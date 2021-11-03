package com.aj;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ArcFaceApplication implements ApplicationRunner {
    public static void main(String[] args) {
        SpringApplication.run(ArcFaceApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("人脸比对服务已开启...");
    }
}
