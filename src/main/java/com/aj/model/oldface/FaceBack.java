package com.aj.model.oldface;

import lombok.Data;

@Data
public class FaceBack {
    private float faceSim;

    public FaceBack(float faceSim) {
        this.faceSim = faceSim;
    }
}
