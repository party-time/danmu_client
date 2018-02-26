package cn.partytime.service;


import cn.partytime.config.ConfigUtils;
import cn.partytime.util.HttpUtils;
import cn.partytime.util.PrintScreenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by administrator on 2016/12/9.
 */
@Service
@Slf4j
@EnableScheduling
public class WindowShellService {


    @Autowired
    private LogLogicService logLogicService;


    @Autowired
    private ConfigUtils configUtils;


    private static String FIND_TASK = "tasklist | find ";

    public String execShell(String shellString) {
        log.info(shellString);
        Process process = null;
        StringBuffer sb = new StringBuffer();
        try {
            process = Runtime.getRuntime().exec(shellString);
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = input.readLine()) != null) {
                sb.append(line);
            }
            input.close();
            int exitValue = process.waitFor();
            if (0 != exitValue) {
                log.info("call shell failed. error code is :" + exitValue);
            } else {
                log.info("shell exec success");
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return sb.toString();
    }

    public String execExe(String shellString) {
        logLogicService.logUploadHandler("执行脚本:"+shellString);
        Process process = null;
        StringBuffer sb = new StringBuffer();
        try {
            Runtime.getRuntime().exec(shellString);
        } catch (Exception e) {
            //log.error("", e);
            logLogicService.logUploadHandler("执行脚本异常:"+e.getMessage());
        }
        return sb.toString();
    }

    public String execExeVBS(String shellString) {
        logLogicService.logUploadHandler("执行脚本:"+shellString);
        Process process = null;
        StringBuffer sb = new StringBuffer();
        try {
            Runtime.getRuntime().exec("cscript "+shellString);
        } catch (Exception e) {
            //log.error("", e);
            logLogicService.logUploadHandler("执行脚本异常:"+e.getMessage());
        }
        return sb.toString();
    }

    public void printScreenPic(){
        PrintScreenUtils.screenShotAsFile(configUtils.screenSavePath(),configUtils.getScreenSaveFile());
        HttpUtils.postFile(configUtils.screenSavePath()+"/"+configUtils.getScreenSaveFile(), configUtils.getSaveScreenPicUrl());
    }

}
