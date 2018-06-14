package com.tracy.agent;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AgentApp {
    private static Logger logger = LoggerFactory.getLogger(AgentApp.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(AgentApp.class, args);
        byte[] bo = new byte[9999999];
        String[] cmd = {"bash", "-c", "ps -ef|grep java"};
        Process p = Runtime.getRuntime().exec(cmd);
        p.getInputStream().read(bo);
        String processInfo = new String(bo);
        int saltStart = processInfo.indexOf("-Dsalt=");
        int saltEnd = processInfo.indexOf(" ", saltStart);
        String slat = processInfo.substring(saltStart, saltEnd).replace("-Dsalt=", "");
        logger.info("hello salt : " + slat);
        if (StringUtils.isEmpty(slat)) {
            slat = "1234";
        }
        System.setProperty("salt", slat);
    }
}
