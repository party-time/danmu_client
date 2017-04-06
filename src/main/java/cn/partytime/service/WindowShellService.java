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

    private String execExe(String shellString) {
        log.info(shellString);
        Process process = null;
        StringBuffer sb = new StringBuffer();
        try {
            Runtime.getRuntime().exec(shellString);
        } catch (Exception e) {
            log.error("", e);
        }

        return sb.toString();

    }


    public String findTask() {
        return execShell(FIND_TASK + configUtils.appName);
    }

    public String killTask() {
        return execShell(configUtils.shellPath() + "/killFlash.bat");
    }

    public void restartTask() {
        killTask();
        String result = findTask();
        //TODO:增加验证，判断进程确实kill掉了
        startTask();
    }

    public void startTask() {
        execExe(configUtils.findFlashProgramPath()+"/"+configUtils.appName+".exe");
        PrintScreenUtils.open2Screen();
    }

    public void startTeamViewer() throws InterruptedException {
        execExe(configUtils.shellPath() + "/startTeamViewer.bat");
        killTask();
    }

    public void printScreenPic(){
        log.info("save screen");
        PrintScreenUtils.screenShotAsFile(configUtils.screenSavePath(),configUtils.getScreenSaveFile());
        log.info("save screen success");
        HttpUtils.postFile(configUtils.screenSavePath()+"/"+configUtils.getScreenSaveFile(), configUtils.getSaveScreenPicUrl());
        log.info("post success");
    }

    public void killTeamViewer() {
        execShell(configUtils.shellPath() + "/killTeamViewer.bat");
    }

    public void javaClientUpdate() {
        //execShell(configUtils.getJaveUpdateVbsPath());
        //execShell("cscript "+configUtils.getJaveUpdateVbsPath());
    }

    public void flashClientUpdate() {
        //execShell("cscript "+configUtils.getFlashUpdateVbsPath());
    }

    public void javaClientRollback(){
        //String shellCommand="cscript "+configUtils.getJavaRollbackVbsPath();
        //log.info("current command is:{}",shellCommand);
        //execShell(shellCommand);
    }

    public void flashClientRollback(){
        //String shellCommand="cscript " +configUtils.getFlashRollbackVbsPath();
       // log.info("current command is:{}",shellCommand);
        //execShell(shellCommand);
    }


}
