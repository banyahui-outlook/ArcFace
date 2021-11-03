package com.aj.model.apiface;

import lombok.Data;

@Data
public class FaceBack {
    private float faceSim;

    public FaceBack(float faceSim) {
        this.faceSim = faceSim;
    }
}
