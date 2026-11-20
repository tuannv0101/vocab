package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;

public class BatchAddWordsRequest {
    private Long setId;
    private String setName;
    private String setDescription;
    private List<CreateWordRequest> words = new ArrayList<>();

    public BatchAddWordsRequest() {}

    public Long getSetId() {
        return setId;
    }

    public void setSetId(Long setId) {
        this.setId = setId;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public String getSetDescription() {
        return setDescription;
    }

    public void setSetDescription(String setDescription) {
        this.setDescription = setDescription;
    }

    public List<CreateWordRequest> getWords() {
        return words;
    }

    public void setWords(List<CreateWordRequest> words) {
        this.words = words;
    }
}
