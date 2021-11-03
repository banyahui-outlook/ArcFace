package com.aj.controller;

import com.aj.model.km1face.FaceBack;
import com.aj.model.km1face.FaceParm;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.toolkit.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Base64;

import static com.arcsoft.face.toolkit.ImageFactory.getRGBData;

@Slf4j
@RestController
@RequestMapping("/km1/face")
public class Km1FaceController {
    @Value("${aj.pv}")
    private float faceValue;
    @Autowired
    private FaceEngine faceEngine;

    @PostMapping("/compare")
    public FaceBack compareFace(@RequestBody FaceParm faceParm) {
        var resp = new FaceBack();
        if (faceEngine == null) {
            log.error("k1->人脸比对引擎未开启!");
            resp.fail("-999", "人脸比对引擎未开启！");
            return resp;
        }
        if (faceParm == null) {
            log.error("k1->人脸比对参数为空!");
            resp.fail("-302", "人脸比对参数不能为空！");
            return resp;
        }
        if (StringUtils.isBlank(faceParm.getDb_image())) {
            log.error("k1->人脸比对照片[db_image]为空!");
            resp.fail("-302", "人脸比对照片[db_image]不能为空！");
            return resp;
        }
        if (StringUtils.isBlank(faceParm.getQuery_image1()) && StringUtils.isBlank(faceParm.getQuery_image2()) && StringUtils.isBlank(faceParm.getQuery_image3())) {
            String qpicStr = "";
            if (StringUtils.isBlank(faceParm.getQuery_image1())) qpicStr = "query_image1";
            else if (StringUtils.isBlank(faceParm.getQuery_image1())) qpicStr = "query_image2";
            else qpicStr = "query_image3";
            log.error("k1->人脸比对照片[" + qpicStr + "]为空!");
            resp.fail("-302", "人脸比对照片[" + qpicStr + "]不能为空！");
            return resp;
        }
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] bydbPic = decoder.decode(faceParm.getDb_image());
        log.info("k1->人脸比对[db_image]长度:" + bydbPic.length);
        ImageInfo imageInfo = getRGBData(bydbPic);
        if (imageInfo == null) {
            log.error("k1->人脸比对[db_image]数据有误!");
            resp.fail("-212", "人脸比对[db_image]数据有误！");
            return resp;
        }
        var faceInfoList = new ArrayList<FaceInfo>();
        int errorCode = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
        if (errorCode != 0) {
            log.error("k1->人脸[db_image]检测失败：" + errorCode);
            resp.fail("-210", "人脸[db_image]检测失败！");
            return resp;
        }
        FaceFeature faceFeature = new FaceFeature();
        errorCode = faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList.get(0), faceFeature);
        if (errorCode != 0) {
            log.error("k1->人脸[db_image]特征提取失败：" + errorCode);
            resp.fail("-211", "人脸[db_image]特征提取失败！");
            return resp;
        }
        log.info("k1->db_image特征：" + faceFeature.getFeatureData().length);

        float faceSim = 0f;
        boolean isFacePass = false;
        FaceFeature targetFaceFeature = new FaceFeature();
        targetFaceFeature.setFeatureData(faceFeature.getFeatureData());
        FaceFeature sourceFaceFeature = new FaceFeature();
        FaceSimilar faceSimilar = new FaceSimilar();

        if (!StringUtils.isBlank(faceParm.getQuery_image1())) {
            byte[] byquPic1 = decoder.decode(faceParm.getQuery_image1());
            log.info("k1->人脸比对[query_image1]长度:" + byquPic1.length);
            ImageInfo imageInfo1 = getRGBData(byquPic1);
            if (imageInfo1 == null) {
                log.error("k1->人脸比对[query_image1]数据有误!");
                resp.fail("-212", "人脸比对[query_image1]数据有误！");
                return resp;
            }
            var faceInfoList1 = new ArrayList<FaceInfo>();
            errorCode = faceEngine.detectFaces(imageInfo1.getImageData(), imageInfo1.getWidth(), imageInfo1.getHeight(), imageInfo1.getImageFormat(), faceInfoList1);
            if (errorCode != 0) {
                log.error("k1->人脸[query_image1]检测失败：" + errorCode);
                resp.fail("-210", "人脸[query_image1]检测失败！");
                return resp;
            }
            FaceFeature faceFeature1 = new FaceFeature();
            errorCode = faceEngine.extractFaceFeature(imageInfo1.getImageData(), imageInfo1.getWidth(), imageInfo1.getHeight(), imageInfo1.getImageFormat(), faceInfoList1.get(0), faceFeature1);
            if (errorCode != 0) {
                log.error("k1->人脸[query_image1]特征提取失败：" + errorCode);
                resp.fail("-211", "人脸[query_image1]特征提取失败！");
                return resp;
            }
            log.info("k1->query_image1特征：" + faceFeature1.getFeatureData().length);
            sourceFaceFeature.setFeatureData(faceFeature1.getFeatureData());
            errorCode = faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);
            if (errorCode != 0) {
                log.error("人脸[query_image1]比对失败：" + errorCode);
                resp.fail("-900", "人脸比对失败！");
                return resp;
            }
            faceSim = faceSimilar.getScore();
            log.info("k1->人脸[query_image1]比对相似度：" + faceSim);
            if (faceSim > faceValue) isFacePass = true;
        }
        if (!isFacePass && !StringUtils.isBlank(faceParm.getQuery_image2())) {
            byte[] byquPic1 = decoder.decode(faceParm.getQuery_image2());
            log.info("k1->人脸比对[query_image2]长度:" + byquPic1.length);
            ImageInfo imageInfo1 = getRGBData(byquPic1);
            if (imageInfo1 == null) {
                log.error("k1->人脸比对[query_image2]数据有误!");
                resp.fail("-212", "人脸比对[query_image2]数据有误！");
                return resp;
            }
            var faceInfoList1 = new ArrayList<FaceInfo>();
            errorCode = faceEngine.detectFaces(imageInfo1.getImageData(), imageInfo1.getWidth(), imageInfo1.getHeight(), imageInfo1.getImageFormat(), faceInfoList1);
            if (errorCode != 0) {
                log.error("k1->人脸[query_image2]检测失败：" + errorCode);
                resp.fail("-210", "人脸[query_image2]检测失败！");
                return resp;
            }
            FaceFeature faceFeature1 = new FaceFeature();
            errorCode = faceEngine.extractFaceFeature(imageInfo1.getImageData(), imageInfo1.getWidth(), imageInfo1.getHeight(), imageInfo1.getImageFormat(), faceInfoList1.get(0), faceFeature1);
            if (errorCode != 0) {
                log.error("k1->人脸[query_image2]特征提取失败：" + errorCode);
                resp.fail("-211", "人脸[query_image2]特征提取失败！");
                return resp;
            }
            log.info("k1->query_image2特征：" + faceFeature1.getFeatureData().length);
            sourceFaceFeature.setFeatureData(faceFeature1.getFeatureData());
            errorCode = faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);
            if (errorCode != 0) {
                log.error("人脸[query_image2]比对失败：" + errorCode);
                resp.fail("-900", "人脸比对失败！");
                return resp;
            }
            faceSim = faceSimilar.getScore();
            log.info("k1->人脸[query_image2]比对相似度：" + faceSim);
            if (faceSim > faceValue) isFacePass = true;
        }
        if (!isFacePass && !StringUtils.isBlank(faceParm.getQuery_image3())) {
            byte[] byquPic1 = decoder.decode(faceParm.getQuery_image3());
            log.info("k1->人脸比对[query_image3]长度:" + byquPic1.length);
            ImageInfo imageInfo1 = getRGBData(byquPic1);
            if (imageInfo1 == null) {
                log.error("k1->人脸比对[query_image3]数据有误!");
                resp.fail("-212", "人脸比对[query_image3]数据有误！");
                return resp;
            }
            var faceInfoList1 = new ArrayList<FaceInfo>();
            errorCode = faceEngine.detectFaces(imageInfo1.getImageData(), imageInfo1.getWidth(), imageInfo1.getHeight(), imageInfo1.getImageFormat(), faceInfoList1);
            if (errorCode != 0) {
                log.error("k1->人脸[query_image3]检测失败：" + errorCode);
                resp.fail("-210", "人脸[query_image3]检测失败！");
                return resp;
            }
            FaceFeature faceFeature1 = new FaceFeature();
            errorCode = faceEngine.extractFaceFeature(imageInfo1.getImageData(), imageInfo1.getWidth(), imageInfo1.getHeight(), imageInfo1.getImageFormat(), faceInfoList1.get(0), faceFeature1);
            if (errorCode != 0) {
                log.error("k1->人脸[query_image3]特征提取失败：" + errorCode);
                resp.fail("-211", "人脸[query_image3]特征提取失败！");
                return resp;
            }
            log.info("k1->query_image3特征：" + faceFeature1.getFeatureData().length);
            sourceFaceFeature.setFeatureData(faceFeature1.getFeatureData());
            errorCode = faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);
            if (errorCode != 0) {
                log.error("人脸[query_image3]比对失败：" + errorCode);
                resp.fail("-900", "人脸比对失败！");
                return resp;
            }
            faceSim = faceSimilar.getScore();
            log.info("k1->人脸[query_image3]比对相似度：" + faceSim);
            if (faceSim > faceValue) isFacePass = true;
        }
        log.info("k1->人脸比对结果:" + isFacePass + ",相似度:" + faceSim);
        if (isFacePass) resp.right(faceSim);
        else resp.fail("0", "人脸比对未通过！", faceSim);
        return resp;
    }
}
