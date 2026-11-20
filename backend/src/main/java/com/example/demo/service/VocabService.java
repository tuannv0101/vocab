package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.VocabSet;
import com.example.demo.entity.VocabWord;
import com.example.demo.repository.VocabSetRepository;
import com.example.demo.repository.VocabWordRepository;
import com.example.demo.util.TextParserUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class VocabService {

    private final VocabSetRepository setRepository;
    private final VocabWordRepository wordRepository;

    public VocabService(VocabSetRepository setRepository, VocabWordRepository wordRepository) {
        this.setRepository = setRepository;
        this.wordRepository = wordRepository;
    }

    @PostConstruct
    public void autoCleanOnStartup() {
        try {
            cleanPartOfSpeech();
        } catch (Exception ignored) {
        }
    }

    public List<VocabSetDto> getAllSets() {
        return setRepository.findAllWithWords()
                .stream()
                .map(s -> VocabSetDto.fromEntity(s, false))
                .collect(Collectors.toList());
    }

    public VocabSetDto getSetById(Long id) {
        VocabSet set = setRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bộ từ vựng với id: " + id));
        return VocabSetDto.fromEntity(set, true);
    }

    public VocabSetDto createSet(CreateSetRequest req) {
        String title = (req.getTitle() != null && !req.getTitle().isBlank())
                ? req.getTitle().trim()
                : "Bộ từ vựng mới " + LocalDateTime.now().toLocalDate();
        VocabSet set = new VocabSet(title, req.getDescription());
        VocabSet saved = setRepository.save(set);
        return VocabSetDto.fromEntity(saved, true);
    }

    public VocabSetDto updateSet(Long id, CreateSetRequest req) {
        VocabSet set = setRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bộ từ vựng với id: " + id));
        if (req.getTitle() != null && !req.getTitle().isBlank()) {
            set.setTitle(req.getTitle().trim());
        }
        if (req.getDescription() != null) {
            set.setDescription(req.getDescription().trim());
        }
        VocabSet updated = setRepository.save(set);
        return VocabSetDto.fromEntity(updated, true);
    }

    public void deleteSet(Long id) {
        if (!setRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy bộ từ vựng với id: " + id);
        }
        setRepository.deleteById(id);
    }

    public List<VocabWordDto> getWordsBySet(Long setId, Boolean starredOnly) {
        List<VocabWord> words;
        if (Boolean.TRUE.equals(starredOnly)) {
            words = wordRepository.findStarredBySetId(setId);
        } else {
            words = wordRepository.findByVocabSetIdOrderByIdAsc(setId);
        }
        return words.stream().map(VocabWordDto::fromEntity).collect(Collectors.toList());
    }

    public VocabWordDto addWord(CreateWordRequest req) {
        if (req.getWord() == null || req.getWord().isBlank()) {
            throw new IllegalArgumentException("Từ vựng không được để trống");
        }
        VocabSet set;
        if (req.getSetId() != null) {
            set = setRepository.findById(req.getSetId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bộ từ vựng với id: " + req.getSetId()));
        } else {
            List<VocabSet> existing = setRepository.findAll();
            if (existing.isEmpty()) {
                set = setRepository.save(new VocabSet("Kho từ vựng chung", "Kho lưu trữ từ vựng hàng ngày"));
            } else {
                set = existing.get(0);
            }
        }

        String normalizedWord = req.getWord().trim();
        TextParserUtil.CleanedPosMeaning cleaned = TextParserUtil.cleanPosAndMeaning(req.getPartOfSpeech(), req.getMeaning());
        String cleanPos = cleaned.getPartOfSpeech();
        String cleanMeaning = cleaned.getMeaning();

        Optional<VocabWord> existingOpt = set.getWords().stream()
                .filter(w -> w.getWord().trim().equalsIgnoreCase(normalizedWord))
                .findFirst();

        if (existingOpt.isPresent()) {
            VocabWord existing = existingOpt.get();
            if (req.getIpa() != null && !req.getIpa().isBlank() && (existing.getIpa() == null || existing.getIpa().isBlank())) {
                existing.setIpa(req.getIpa().trim());
            }
            if (!cleanPos.isBlank() && (existing.getPartOfSpeech() == null || existing.getPartOfSpeech().isBlank())) {
                existing.setPartOfSpeech(cleanPos);
            }
            if (!cleanMeaning.isBlank()) {
                if (existing.getMeaning() == null || existing.getMeaning().isBlank() || cleanMeaning.length() > existing.getMeaning().length()) {
                    existing.setMeaning(cleanMeaning);
                }
            }
            if (req.getExampleSentence() != null && !req.getExampleSentence().isBlank()) {
                existing.setExampleSentence(req.getExampleSentence().trim());
            }
            if (req.getExampleTranslation() != null && !req.getExampleTranslation().isBlank()) {
                existing.setExampleTranslation(req.getExampleTranslation().trim());
            }
            VocabWord updated = wordRepository.save(existing);
            return VocabWordDto.fromEntity(updated);
        }

        VocabWord word = new VocabWord(
                normalizedWord,
                req.getIpa() != null ? req.getIpa().trim() : "",
                cleanPos,
                cleanMeaning
        );
        word.setExampleSentence(req.getExampleSentence());
        word.setExampleTranslation(req.getExampleTranslation());

        set.addWord(word);
        VocabWord saved = wordRepository.save(word);
        return VocabWordDto.fromEntity(saved);
    }

    public VocabWordDto updateWord(Long id, CreateWordRequest req) {
        VocabWord word = wordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy từ với id: " + id));

        if (req.getWord() != null && !req.getWord().isBlank()) {
            word.setWord(req.getWord().trim());
        }
        if (req.getIpa() != null) {
            word.setIpa(req.getIpa().trim());
        }

        String candPos = req.getPartOfSpeech() != null ? req.getPartOfSpeech() : word.getPartOfSpeech();
        String candMeaning = req.getMeaning() != null ? req.getMeaning() : word.getMeaning();
        TextParserUtil.CleanedPosMeaning cleaned = TextParserUtil.cleanPosAndMeaning(candPos, candMeaning);
        word.setPartOfSpeech(cleaned.getPartOfSpeech());
        word.setMeaning(cleaned.getMeaning());

        if (req.getExampleSentence() != null) {
            word.setExampleSentence(req.getExampleSentence().trim());
        }
        if (req.getExampleTranslation() != null) {
            word.setExampleTranslation(req.getExampleTranslation().trim());
        }

        if (req.getSetId() != null && (word.getVocabSet() == null || !req.getSetId().equals(word.getVocabSet().getId()))) {
            VocabSet newSet = setRepository.findById(req.getSetId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bộ từ vựng đích"));
            if (word.getVocabSet() != null) {
                word.getVocabSet().getWords().remove(word);
            }
            newSet.addWord(word);
        }

        VocabWord updated = wordRepository.save(word);
        return VocabWordDto.fromEntity(updated);
    }

    public void deleteWord(Long id) {
        VocabWord word = wordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy từ với id: " + id));
        if (word.getVocabSet() != null) {
            word.getVocabSet().getWords().remove(word);
        }
        wordRepository.delete(word);
    }

    public VocabWordDto toggleStar(Long id) {
        VocabWord word = wordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy từ với id: " + id));
        word.setIsStarred(!word.getIsStarred());
        VocabWord updated = wordRepository.save(word);
        return VocabWordDto.fromEntity(updated);
    }

    public VocabWordDto updateMastery(Long id, Integer masteryLevel) {
        VocabWord word = wordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy từ với id: " + id));
        word.setMasteryLevel(Math.max(0, Math.min(2, masteryLevel)));
        word.setLastReviewedAt(LocalDateTime.now());
        VocabWord updated = wordRepository.save(word);
        return VocabWordDto.fromEntity(updated);
    }

    public VocabWordDto recordStudyAnswer(StudyAnswerRequest req) {
        VocabWord word = wordRepository.findById(req.getWordId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy từ với id: " + req.getWordId()));

        word.setLastReviewedAt(LocalDateTime.now());
        if (req.isCorrect()) {
            word.setCorrectCount(word.getCorrectCount() + 1);
            if (word.getMasteryLevel() < 2) {
                word.setMasteryLevel(word.getMasteryLevel() + 1);
            }
        } else {
            word.setIncorrectCount(word.getIncorrectCount() + 1);
            word.setMasteryLevel(1);
        }

        VocabWord updated = wordRepository.save(word);
        return VocabWordDto.fromEntity(updated);
    }

    public VocabSetDto bulkImport(BulkImportRequest req) {
        VocabSet targetSet;
        if (req.getSetId() != null) {
            targetSet = setRepository.findById(req.getSetId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bộ từ vựng với id: " + req.getSetId()));
        } else {
            String title = (req.getSetName() != null && !req.getSetName().isBlank())
                    ? req.getSetName().trim()
                    : "Bộ từ nhập ngày " + LocalDateTime.now().toLocalDate();
            targetSet = new VocabSet(title, req.getSetDescription());
            targetSet = setRepository.save(targetSet);
        }

        Map<String, VocabWord> existingMap = new HashMap<>();
        for (VocabWord ew : targetSet.getWords()) {
            existingMap.put(ew.getWord().trim().toLowerCase(), ew);
        }

        List<VocabWord> parsed = TextParserUtil.parseRawText(req.getRawText());
        for (VocabWord w : parsed) {
            if (w.getWord() == null || w.getWord().isBlank()) continue;
            String key = w.getWord().trim().toLowerCase();
            VocabWord existing = existingMap.get(key);
            if (existing != null) {
                if ((existing.getIpa() == null || existing.getIpa().isBlank()) && w.getIpa() != null && !w.getIpa().isBlank()) {
                    existing.setIpa(w.getIpa().trim());
                }
                if ((existing.getPartOfSpeech() == null || existing.getPartOfSpeech().isBlank()) && w.getPartOfSpeech() != null && !w.getPartOfSpeech().isBlank()) {
                    existing.setPartOfSpeech(w.getPartOfSpeech().trim());
                }
                if (w.getMeaning() != null && !w.getMeaning().isBlank()) {
                    if (existing.getMeaning() == null || existing.getMeaning().isBlank() || w.getMeaning().trim().length() > existing.getMeaning().length()) {
                        existing.setMeaning(w.getMeaning().trim());
                    }
                }
                wordRepository.save(existing);
            } else {
                targetSet.addWord(w);
                existingMap.put(key, w);
            }
        }
        VocabSet saved = setRepository.save(targetSet);
        return VocabSetDto.fromEntity(saved, true);
    }

    public VocabSetDto batchAddWords(BatchAddWordsRequest req) {
        VocabSet targetSet;
        if (req.getSetId() != null) {
            targetSet = setRepository.findById(req.getSetId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bộ từ vựng với id: " + req.getSetId()));
        } else {
            String title = (req.getSetName() != null && !req.getSetName().isBlank())
                    ? req.getSetName().trim()
                    : "Bộ từ nhập ngày " + LocalDateTime.now().toLocalDate();
            targetSet = new VocabSet(title, req.getSetDescription());
            targetSet = setRepository.save(targetSet);
        }

        if (req.getWords() != null) {
            Map<String, VocabWord> existingMap = new HashMap<>();
            for (VocabWord ew : targetSet.getWords()) {
                existingMap.put(ew.getWord().trim().toLowerCase(), ew);
            }

            for (CreateWordRequest wReq : req.getWords()) {
                if (wReq.getWord() == null || wReq.getWord().isBlank()) {
                    continue;
                }
                String normalized = wReq.getWord().trim();
                String key = normalized.toLowerCase();

                TextParserUtil.CleanedPosMeaning cleaned = TextParserUtil.cleanPosAndMeaning(wReq.getPartOfSpeech(), wReq.getMeaning());
                String cleanPos = cleaned.getPartOfSpeech();
                String cleanMeaning = cleaned.getMeaning();

                VocabWord existing = existingMap.get(key);
                if (existing != null) {
                    if (wReq.getIpa() != null && !wReq.getIpa().isBlank() && (existing.getIpa() == null || existing.getIpa().isBlank())) {
                        existing.setIpa(wReq.getIpa().trim());
                    }
                    if (!cleanPos.isBlank() && (existing.getPartOfSpeech() == null || existing.getPartOfSpeech().isBlank())) {
                        existing.setPartOfSpeech(cleanPos);
                    }
                    if (!cleanMeaning.isBlank()) {
                        if (existing.getMeaning() == null || existing.getMeaning().isBlank() || cleanMeaning.length() > existing.getMeaning().length()) {
                            existing.setMeaning(cleanMeaning);
                        }
                    }
                    if (wReq.getExampleSentence() != null && !wReq.getExampleSentence().isBlank()) {
                        existing.setExampleSentence(wReq.getExampleSentence().trim());
                    }
                    if (wReq.getExampleTranslation() != null && !wReq.getExampleTranslation().isBlank()) {
                        existing.setExampleTranslation(wReq.getExampleTranslation().trim());
                    }
                    wordRepository.save(existing);
                } else {
                    VocabWord word = new VocabWord(
                            normalized,
                            wReq.getIpa() != null ? wReq.getIpa().trim() : "",
                            cleanPos,
                            cleanMeaning
                    );
                    if (wReq.getExampleSentence() != null) {
                        word.setExampleSentence(wReq.getExampleSentence().trim());
                    }
                    if (wReq.getExampleTranslation() != null) {
                        word.setExampleTranslation(wReq.getExampleTranslation().trim());
                    }
                    targetSet.addWord(word);
                    existingMap.put(key, word);
                }
            }
        }
        VocabSet saved = setRepository.save(targetSet);
        return VocabSetDto.fromEntity(saved, true);
    }

    public Map<String, Object> deduplicateSet(Long setId) {
        VocabSet set = setRepository.findById(setId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bộ từ vựng với id: " + setId));

        List<VocabWord> words = new ArrayList<>(set.getWords());
        Map<String, List<VocabWord>> grouped = words.stream()
                .collect(Collectors.groupingBy(w -> w.getWord().trim().toLowerCase()));

        List<VocabWord> toDelete = new ArrayList<>();
        List<VocabWord> toSave = new ArrayList<>();
        int removedCount = 0;

        for (Map.Entry<String, List<VocabWord>> entry : grouped.entrySet()) {
            List<VocabWord> list = entry.getValue();
            if (list.size() <= 1) {
                continue;
            }

            // Pick the best primary word
            VocabWord primary = list.stream().max((a, b) -> {
                if (Boolean.TRUE.equals(a.getIsStarred()) && !Boolean.TRUE.equals(b.getIsStarred())) return 1;
                if (!Boolean.TRUE.equals(a.getIsStarred()) && Boolean.TRUE.equals(b.getIsStarred())) return -1;
                int masteryDiff = Integer.compare(
                        a.getMasteryLevel() != null ? a.getMasteryLevel() : 0,
                        b.getMasteryLevel() != null ? b.getMasteryLevel() : 0
                );
                if (masteryDiff != 0) return masteryDiff;
                int meaningDiff = Integer.compare(
                        a.getMeaning() != null ? a.getMeaning().length() : 0,
                        b.getMeaning() != null ? b.getMeaning().length() : 0
                );
                if (meaningDiff != 0) return meaningDiff;
                return Long.compare(b.getId() != null ? b.getId() : 0L, a.getId() != null ? a.getId() : 0L);
            }).orElse(list.get(0));

            // Merge properties from other duplicate entries into primary
            for (VocabWord dup : list) {
                if (dup.getId() != null && dup.getId().equals(primary.getId())) {
                    continue;
                }
                if (Boolean.TRUE.equals(dup.getIsStarred())) {
                    primary.setIsStarred(true);
                }
                int dupMastery = dup.getMasteryLevel() != null ? dup.getMasteryLevel() : 0;
                int priMastery = primary.getMasteryLevel() != null ? primary.getMasteryLevel() : 0;
                if (dupMastery > priMastery) {
                    primary.setMasteryLevel(dupMastery);
                }
                int dupCorrect = dup.getCorrectCount() != null ? dup.getCorrectCount() : 0;
                int priCorrect = primary.getCorrectCount() != null ? primary.getCorrectCount() : 0;
                primary.setCorrectCount(Math.max(priCorrect, dupCorrect));

                int dupIncorrect = dup.getIncorrectCount() != null ? dup.getIncorrectCount() : 0;
                int priIncorrect = primary.getIncorrectCount() != null ? primary.getIncorrectCount() : 0;
                primary.setIncorrectCount(Math.max(priIncorrect, dupIncorrect));

                if ((primary.getIpa() == null || primary.getIpa().isBlank()) && dup.getIpa() != null && !dup.getIpa().isBlank()) {
                    primary.setIpa(dup.getIpa().trim());
                }
                if ((primary.getPartOfSpeech() == null || primary.getPartOfSpeech().isBlank()) && dup.getPartOfSpeech() != null && !dup.getPartOfSpeech().isBlank()) {
                    primary.setPartOfSpeech(dup.getPartOfSpeech().trim());
                }
                if (dup.getMeaning() != null && !dup.getMeaning().isBlank()) {
                    if (primary.getMeaning() == null || primary.getMeaning().isBlank() || dup.getMeaning().trim().length() > primary.getMeaning().length()) {
                        primary.setMeaning(dup.getMeaning().trim());
                    }
                }
                if ((primary.getExampleSentence() == null || primary.getExampleSentence().isBlank()) && dup.getExampleSentence() != null && !dup.getExampleSentence().isBlank()) {
                    primary.setExampleSentence(dup.getExampleSentence().trim());
                }
                if ((primary.getExampleTranslation() == null || primary.getExampleTranslation().isBlank()) && dup.getExampleTranslation() != null && !dup.getExampleTranslation().isBlank()) {
                    primary.setExampleTranslation(dup.getExampleTranslation().trim());
                }

                toDelete.add(dup);
                set.getWords().remove(dup);
                removedCount++;
            }
            toSave.add(primary);
        }

        wordRepository.saveAll(toSave);
        wordRepository.deleteAll(toDelete);
        VocabSet savedSet = setRepository.save(set);

        return Map.of(
                "removedCount", removedCount,
                "totalWords", savedSet.getWords().size(),
                "message", "Đã dọn dẹp và gộp " + removedCount + " từ vựng bị trùng lặp thành công!"
        );
    }

    public Map<String, Object> getDuplicates(Long setId) {
        VocabSet set = setRepository.findById(setId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bộ từ vựng với id: " + setId));

        Map<String, List<VocabWord>> grouped = set.getWords().stream()
                .collect(Collectors.groupingBy(w -> w.getWord().trim().toLowerCase()));

        List<Map<String, Object>> duplicateGroups = new ArrayList<>();
        int totalDuplicateItems = 0;

        for (Map.Entry<String, List<VocabWord>> entry : grouped.entrySet()) {
            List<VocabWord> list = entry.getValue();
            if (list.size() > 1) {
                totalDuplicateItems += (list.size() - 1);
                List<String> meanings = list.stream()
                        .map(VocabWord::getMeaning)
                        .filter(m -> m != null && !m.isBlank())
                        .distinct()
                        .collect(Collectors.toList());
                List<Long> ids = list.stream().map(VocabWord::getId).collect(Collectors.toList());

                duplicateGroups.add(Map.of(
                        "word", list.get(0).getWord(),
                        "count", list.size(),
                        "meanings", meanings,
                        "ids", ids
                ));
            }
        }

        return Map.of(
                "totalDuplicates", totalDuplicateItems,
                "duplicateGroupsCount", duplicateGroups.size(),
                "groups", duplicateGroups
        );
    }

    public List<VocabWordDto> parsePreview(String rawText) {
        List<VocabWord> parsed = TextParserUtil.parseRawText(rawText);
        return parsed.stream().map(VocabWordDto::fromEntity).collect(Collectors.toList());
    }

    public List<VocabWordDto> searchWords(String query) {
        if (query == null || query.isBlank()) {
            return new ArrayList<>();
        }
        return wordRepository.searchWords(query.trim())
                .stream()
                .map(VocabWordDto::fromEntity)
                .collect(Collectors.toList());
    }

    public void resetSetProgress(Long setId) {
        List<VocabWord> words = wordRepository.findByVocabSetId(setId);
        for (VocabWord w : words) {
            w.setMasteryLevel(0);
            w.setCorrectCount(0);
            w.setIncorrectCount(0);
        }
        wordRepository.saveAll(words);
    }

    public Map<String, Object> cleanPartOfSpeech() {
        List<VocabWord> allWords = wordRepository.findAll();
        List<VocabWord> toSave = new ArrayList<>();
        int cleanedCount = 0;

        for (VocabWord w : allWords) {
            TextParserUtil.CleanedPosMeaning res = TextParserUtil.cleanPosAndMeaning(w.getPartOfSpeech(), w.getMeaning());
            boolean changed = false;
            if (!Objects.equals(w.getMeaning(), res.getMeaning())) {
                w.setMeaning(res.getMeaning());
                changed = true;
            }
            if (!Objects.equals(w.getPartOfSpeech(), res.getPartOfSpeech())) {
                w.setPartOfSpeech(res.getPartOfSpeech());
                changed = true;
            }
            if (changed) {
                toSave.add(w);
                cleanedCount++;
            }
        }

        if (!toSave.isEmpty()) {
            wordRepository.saveAll(toSave);
        }

        return Map.of(
                "cleanedCount", cleanedCount,
                "totalWords", allWords.size(),
                "message", "Đã dọn dẹp và chuẩn hóa từ loại cho " + cleanedCount + " từ vựng thành công!"
        );
    }
}
