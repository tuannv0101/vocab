package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.VocabService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class VocabController {

    private final VocabService vocabService;

    public VocabController(VocabService vocabService) {
        this.vocabService = vocabService;
    }

    // Set endpoints
    @GetMapping("/sets")
    public ResponseEntity<List<VocabSetDto>> getAllSets() {
        return ResponseEntity.ok(vocabService.getAllSets());
    }

    @GetMapping("/sets/{id}")
    public ResponseEntity<VocabSetDto> getSetById(@PathVariable Long id) {
        return ResponseEntity.ok(vocabService.getSetById(id));
    }

    @PostMapping("/sets")
    public ResponseEntity<VocabSetDto> createSet(@RequestBody CreateSetRequest req) {
        return ResponseEntity.ok(vocabService.createSet(req));
    }

    @PutMapping("/sets/{id}")
    public ResponseEntity<VocabSetDto> updateSet(@PathVariable Long id, @RequestBody CreateSetRequest req) {
        return ResponseEntity.ok(vocabService.updateSet(id, req));
    }

    @DeleteMapping("/sets/{id}")
    public ResponseEntity<Map<String, String>> deleteSet(@PathVariable Long id) {
        vocabService.deleteSet(id);
        return ResponseEntity.ok(Map.of("message", "Đã xóa bộ từ vựng thành công"));
    }

    @PostMapping("/sets/{id}/reset")
    public ResponseEntity<Map<String, String>> resetSetProgress(@PathVariable Long id) {
        vocabService.resetSetProgress(id);
        return ResponseEntity.ok(Map.of("message", "Đã đặt lại tiến độ học"));
    }

    @GetMapping("/sets/{id}/duplicates")
    public ResponseEntity<Map<String, Object>> getDuplicates(@PathVariable Long id) {
        return ResponseEntity.ok(vocabService.getDuplicates(id));
    }

    @PostMapping("/sets/{id}/deduplicate")
    public ResponseEntity<Map<String, Object>> deduplicateSet(@PathVariable Long id) {
        return ResponseEntity.ok(vocabService.deduplicateSet(id));
    }

    @PostMapping("/sets/clean-pos")
    public ResponseEntity<Map<String, Object>> cleanPartOfSpeech() {
        return ResponseEntity.ok(vocabService.cleanPartOfSpeech());
    }

    // Word endpoints
    @GetMapping("/sets/{id}/words")
    public ResponseEntity<List<VocabWordDto>> getWordsBySet(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "false") Boolean starred) {
        return ResponseEntity.ok(vocabService.getWordsBySet(id, starred));
    }

    @PostMapping("/words")
    public ResponseEntity<VocabWordDto> addWord(@RequestBody CreateWordRequest req) {
        return ResponseEntity.ok(vocabService.addWord(req));
    }

    @PutMapping("/words/{id}")
    public ResponseEntity<VocabWordDto> updateWord(@PathVariable Long id, @RequestBody CreateWordRequest req) {
        return ResponseEntity.ok(vocabService.updateWord(id, req));
    }

    @DeleteMapping("/words/{id}")
    public ResponseEntity<Map<String, String>> deleteWord(@PathVariable Long id) {
        vocabService.deleteWord(id);
        return ResponseEntity.ok(Map.of("message", "Đã xóa từ vựng thành công"));
    }

    @PostMapping("/words/{id}/star")
    public ResponseEntity<VocabWordDto> toggleStar(@PathVariable Long id) {
        return ResponseEntity.ok(vocabService.toggleStar(id));
    }

    @PostMapping("/words/{id}/mastery")
    public ResponseEntity<VocabWordDto> updateMastery(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer level = body.getOrDefault("masteryLevel", 0);
        return ResponseEntity.ok(vocabService.updateMastery(id, level));
    }

    @PostMapping("/study/answer")
    public ResponseEntity<VocabWordDto> recordStudyAnswer(@RequestBody StudyAnswerRequest req) {
        return ResponseEntity.ok(vocabService.recordStudyAnswer(req));
    }

    // Import endpoints
    @PostMapping("/import")
    public ResponseEntity<VocabSetDto> bulkImport(@RequestBody BulkImportRequest req) {
        return ResponseEntity.ok(vocabService.bulkImport(req));
    }

    @PostMapping("/words/batch")
    public ResponseEntity<VocabSetDto> batchAddWords(@RequestBody BatchAddWordsRequest req) {
        return ResponseEntity.ok(vocabService.batchAddWords(req));
    }

    @PostMapping("/import/preview")
    public ResponseEntity<List<VocabWordDto>> parsePreview(@RequestBody Map<String, String> body) {
        String text = body.getOrDefault("rawText", "");
        return ResponseEntity.ok(vocabService.parsePreview(text));
    }

    // Search
    @GetMapping("/words/search")
    public ResponseEntity<List<VocabWordDto>> searchWords(@RequestParam String q) {
        return ResponseEntity.ok(vocabService.searchWords(q));
    }
}
