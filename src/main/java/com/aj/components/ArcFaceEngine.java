package com.aj.components;

import com.arcsoft.face.EngineConfiguration;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FunctionConfiguration;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.enums.ErrorInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Slf4j
@Configuration
public class ArcFaceEngine {
    @Value("${aj.id}")
    private String appId;

    @Value("${aj.key}")
    private String sdkKey;

    @Bean
    public FaceEngine faceEngine() {
        log.info("开启人脸比对引擎...");
        ApplicationHome appHome = new ApplicationHome(getClass());
        String dirPath = appHome.getSource().getParentFile().toString();
        var libPath = dirPath + File.separator + "depends";
        log.info("人脸比对库地址：" + libPath);
        FaceEngine faceEngine = new FaceEngine(libPath);
        int errorCode = faceEngine.activeOnline(appId, sdkKey);
        if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            log.error("激活人脸比对引擎失败:" + errorCode);
            return null;
        }
        log.info("人脸比对激活成功..." );
        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_ALL_OUT);
        engineConfiguration.setDetectFaceMaxNum(10);
        engineConfiguration.setDetectFaceScaleVal(16);

        FunctionConfiguration functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setSupportAge(true);
        functionConfiguration.setSupportFace3dAngle(true);
        functionConfiguration.setSupportFaceDetect(true);
        functionConfiguration.setSupportFaceRecognition(true);
        functionConfiguration.setSupportGender(true);
        functionConfiguration.setSupportIRLiveness(true);
        functionConfiguration.setSupportLiveness(true);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);
        errorCode = faceEngine.init(engineConfiguration);
        if (errorCode != ErrorInfo.MOK.getValue()) {
            log.error("初始化人脸比对引擎失败：" + errorCode);
            return null;
        }
        log.info("人脸比对引擎开启成功！");
        return faceEngine;
    }
}
