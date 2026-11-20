package com.example.demo.dto;

public class StudyAnswerRequest {
    private Long wordId;
    private boolean correct;

    public StudyAnswerRequest() {}

    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
