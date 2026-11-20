package com.example.demo.dto;

import com.example.demo.entity.VocabWord;

public class VocabWordDto {
    private Long id;
    private String word;
    private String ipa;
    private String partOfSpeech;
    private String meaning;
    private String exampleSentence;
    private String exampleTranslation;
    private Boolean isStarred;
    private Integer masteryLevel;
    private Integer correctCount;
    private Integer incorrectCount;
    private Long setId;
    private String setTitle;

    public VocabWordDto() {}

    public static VocabWordDto fromEntity(VocabWord entity) {
        if (entity == null) return null;
        VocabWordDto dto = new VocabWordDto();
        dto.setId(entity.getId());
        dto.setWord(entity.getWord());
        dto.setIpa(entity.getIpa());
        dto.setPartOfSpeech(entity.getPartOfSpeech());
        dto.setMeaning(entity.getMeaning());
        dto.setExampleSentence(entity.getExampleSentence());
        dto.setExampleTranslation(entity.getExampleTranslation());
        dto.setIsStarred(entity.getIsStarred());
        dto.setMasteryLevel(entity.getMasteryLevel());
        dto.setCorrectCount(entity.getCorrectCount());
        dto.setIncorrectCount(entity.getIncorrectCount());
        if (entity.getVocabSet() != null) {
            dto.setSetId(entity.getVocabSet().getId());
            dto.setSetTitle(entity.getVocabSet().getTitle());
        }
        return dto;
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
        return isStarred;
    }

    public void setIsStarred(Boolean isStarred) {
        this.isStarred = isStarred;
    }

    public Integer getMasteryLevel() {
        return masteryLevel;
    }

    public void setMasteryLevel(Integer masteryLevel) {
        this.masteryLevel = masteryLevel;
    }

    public Integer getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(Integer correctCount) {
        this.correctCount = correctCount;
    }

    public Integer getIncorrectCount() {
        return incorrectCount;
    }

    public void setIncorrectCount(Integer incorrectCount) {
        this.incorrectCount = incorrectCount;
    }

    public Long getSetId() {
        return setId;
    }

    public void setSetId(Long setId) {
        this.setId = setId;
    }

    public String getSetTitle() {
        return setTitle;
    }

    public void setSetTitle(String setTitle) {
        this.setTitle = setTitle;
    }
}
