package com.aj.model.km1face;

import lombok.Data;

@Data
public class FaceBack {
    private String code;
    private String message;
    private float similarity;

    public void fail(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public void fail(String code, String message, float similarity) {
        this.code = code;
        this.message = message;
        this.similarity = similarity;
    }

    public void right(float similarity) {
        this.code = "1";
        this.message = "人脸比对通过！";
        this.similarity = similarity;
    }

    public void right(String message, float similarity) {
        this.code = "1";
        this.message = message;
        this.similarity = similarity;
    }
}
