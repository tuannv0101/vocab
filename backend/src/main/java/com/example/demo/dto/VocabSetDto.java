package com.example.demo.dto;

import com.example.demo.entity.VocabSet;
import com.example.demo.entity.VocabWord;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VocabSetDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int totalWords;
    private int masteredWords;
    private int learningWords;
    private int newWords;
    private int starredWords;
    private List<VocabWordDto> words = new ArrayList<>();

    public VocabSetDto() {}

    public static VocabSetDto fromEntity(VocabSet entity, boolean includeWords) {
        if (entity == null) return null;
        VocabSetDto dto = new VocabSetDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        List<VocabWord> wordList = entity.getWords() != null ? entity.getWords() : new ArrayList<>();
        dto.setTotalWords(wordList.size());

        int mastered = 0;
        int learning = 0;
        int unlearned = 0;
        int starred = 0;

        for (VocabWord w : wordList) {
            if (Boolean.TRUE.equals(w.getIsStarred())) starred++;
            int ml = w.getMasteryLevel() != null ? w.getMasteryLevel() : 0;
            if (ml == 2) mastered++;
            else if (ml == 1) learning++;
            else unlearned++;
        }

        dto.setMasteredWords(mastered);
        dto.setLearningWords(learning);
        dto.setNewWords(unlearned);
        dto.setStarredWords(starred);

        if (includeWords) {
            dto.setWords(wordList.stream().map(VocabWordDto::fromEntity).collect(Collectors.toList()));
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getTotalWords() {
        return totalWords;
    }

    public void setTotalWords(int totalWords) {
        this.totalWords = totalWords;
    }

    public int getMasteredWords() {
        return masteredWords;
    }

    public void setMasteredWords(int masteredWords) {
        this.masteredWords = masteredWords;
    }

    public int getLearningWords() {
        return learningWords;
    }

    public void setLearningWords(int learningWords) {
        this.learningWords = learningWords;
    }

    public int getNewWords() {
        return newWords;
    }

    public void setNewWords(int newWords) {
        this.newWords = newWords;
    }

    public int getStarredWords() {
        return starredWords;
    }

    public void setStarredWords(int starredWords) {
        this.starredWords = starredWords;
    }

    public List<VocabWordDto> getWords() {
        return words;
    }

    public void setWords(List<VocabWordDto> words) {
        this.words = words;
    }
}
