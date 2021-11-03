package com.aj.controller;

import com.aj.model.ComResp;
import com.aj.model.oldface.FaceBack;
import com.aj.model.oldface.FaceParm;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.toolkit.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Base64;

import static com.arcsoft.face.toolkit.ImageFactory.getRGBData;

@Slf4j
@RestController
@RequestMapping(path = "/old/face", produces = "application/json;charset=UTF-8")
public class OldFaceController {
    @Autowired
    private FaceEngine faceEngine;

    @PostMapping("/compare")
    public ComResp<FaceBack> compareFace(@RequestBody FaceParm faceParm) {
        var resp = new ComResp<FaceBack>();
        if (faceEngine == null) {
            log.error("old->人脸比对引擎未开启!");
            resp.fail("人脸比对引擎未开启！");
            return resp;
        }
        if (faceParm == null) {
            log.error("old->人脸比对参数为空!");
            resp.fail("人脸比对参数不能为空！");
            return resp;
        }
        if (StringUtils.isBlank( faceParm.getPic1()) || StringUtils.isBlank( faceParm.getPic2())) {
            log.error("old->人脸比对照片为空!");
            resp.fail("人脸比对照片不能为空！");
            return resp;
        }
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] byPic1 = decoder.decode(faceParm.getPic1());
        log.info("old->人脸比对[照片1]长度:" + byPic1.length);
        ImageInfo imageInfo = getRGBData(byPic1);
        if (imageInfo == null) {
            log.error("old->人脸比对[照片1]数据有误!");
            resp.fail("人脸比对[照片1]数据有误！");
            return resp;
        }
        var faceInfoList = new ArrayList<FaceInfo>();
        int errorCode = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
        if (errorCode != 0) {
            log.error("old->人脸[照片1]检测失败：" + errorCode);
            resp.fail("人脸[照片1]检测失败！");
            return resp;
        }
        FaceFeature faceFeature = new FaceFeature();
        errorCode = faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList.get(0), faceFeature);
        if (errorCode != 0) {
            log.error("old->人脸[照片1]特征提取失败：" + errorCode);
            resp.fail("人脸[照片1]特征提取失败！");
            return resp;
        }
        log.info("old->图片1特征：" + faceFeature.getFeatureData().length);

        byte[] byPic2 = decoder.decode(faceParm.getPic2());
        log.info("old->人脸比对[照片2]长度:" + byPic2.length);
        ImageInfo imageInfo2 = getRGBData(byPic2);
        if (imageInfo == null) {
            log.error("old->人脸比对[照片2]数据有误!");
            resp.fail("人脸比对[照片2]数据有误！");
            return resp;
        }
        var faceInfoList2 = new ArrayList<FaceInfo>();
        errorCode = faceEngine.detectFaces(imageInfo2.getImageData(), imageInfo2.getWidth(), imageInfo2.getHeight(), imageInfo2.getImageFormat(), faceInfoList2);
        if (errorCode != 0) {
            log.error("old->人脸[照片2]检测失败：" + errorCode);
            resp.fail("人脸[照片2]检测失败！");
            return resp;
        }
        FaceFeature faceFeature2 = new FaceFeature();
        errorCode = faceEngine.extractFaceFeature(imageInfo2.getImageData(), imageInfo2.getWidth(), imageInfo2.getHeight(), imageInfo2.getImageFormat(), faceInfoList2.get(0), faceFeature2);
        if (errorCode != 0) {
            log.error("old->人脸[照片2]特征提取失败：" + errorCode);
            resp.fail("人脸[照片2]特征提取失败！");
            return resp;
        }
        log.info("old->图片2特征：" + faceFeature2.getFeatureData().length);

        FaceFeature targetFaceFeature = new FaceFeature();
        targetFaceFeature.setFeatureData(faceFeature.getFeatureData());
        FaceFeature sourceFaceFeature = new FaceFeature();
        sourceFaceFeature.setFeatureData(faceFeature2.getFeatureData());
        FaceSimilar faceSimilar = new FaceSimilar();
        errorCode = faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);
        if (errorCode != 0) {
            log.error("old->人脸比对失败：" + errorCode);
            resp.fail("人脸比对失败！");
            return resp;
        }
        var faceSim = faceSimilar.getScore();
        log.info("old->人脸比对相似度：" + faceSim);
        resp.right(new FaceBack(faceSim));
        return resp;
    }
}
