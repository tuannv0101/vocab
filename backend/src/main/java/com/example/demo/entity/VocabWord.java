package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vocab_words")
public class VocabWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String word;

    @Column(length = 500)
    private String ipa;

    @Column(length = 100)
    private String partOfSpeech;

    @Column(nullable = false, length = 1000)
    private String meaning;

    @Column(length = 2000)
    private String exampleSentence;

    @Column(length = 2000)
    private String exampleTranslation;

    private Boolean isStarred = false;

    // 0 = chưa học, 1 = đang học, 2 = đã thành thạo
    private Integer masteryLevel = 0;

    private Integer correctCount = 0;
    private Integer incorrectCount = 0;

    private LocalDateTime createdAt;
    private LocalDateTime lastReviewedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vocab_set_id")
    @JsonIgnore
    private VocabSet vocabSet;

    public VocabWord() {
        this.createdAt = LocalDateTime.now();
        this.isStarred = false;
        this.masteryLevel = 0;
        this.correctCount = 0;
        this.incorrectCount = 0;
    }

    public VocabWord(String word, String ipa, String partOfSpeech, String meaning) {
        this();
        this.word = word;
        this.ipa = ipa;
        this.partOfSpeech = partOfSpeech;
        this.meaning = meaning;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getIpa() {
        return ipa;
    }

    public void setIpa(String ipa) {
        this.ipa = ipa;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getExampleSentence() {
        return exampleSentence;
    }

    public void setExampleSentence(String exampleSentence) {
        this.exampleSentence = exampleSentence;
    }

    public String getExampleTranslation() {
        return exampleTranslation;
    }

    public void setExampleTranslation(String exampleTranslation) {
        this.exampleTranslation = exampleTranslation;
    }

    public Boolean getIsStarred() {
        return isStarred != null && isStarred;
    }

    public void setIsStarred(Boolean isStarred) {
        this.isStarred = isStarred;
    }

    public Integer getMasteryLevel() {
        return masteryLevel != null ? masteryLevel : 0;
    }

    public void setMasteryLevel(Integer masteryLevel) {
        this.masteryLevel = masteryLevel;
    }

    public Integer getCorrectCount() {
        return correctCount != null ? correctCount : 0;
    }

    public void setCorrectCount(Integer correctCount) {
        this.correctCount = correctCount;
    }

    public Integer getIncorrectCount() {
        return incorrectCount != null ? incorrectCount : 0;
    }

    public void setIncorrectCount(Integer incorrectCount) {
        this.incorrectCount = incorrectCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastReviewedAt() {
        return lastReviewedAt;
    }

    public void setLastReviewedAt(LocalDateTime lastReviewedAt) {
        this.lastReviewedAt = lastReviewedAt;
    }

    public VocabSet getVocabSet() {
        return vocabSet;
    }

    public void setVocabSet(VocabSet vocabSet) {
        this.vocabSet = vocabSet;
    }
}
