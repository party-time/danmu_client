package cn.partytime.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by administrator on 2017/2/16.
 */
@ConfigurationProperties
public class Properties {

    @Value("${basePath}")
    private String basePath;

    //0测试环境  1正式环境
    @Value("${env}")
    private Integer env=1;

    @Value("${addressId}")
    private String addressId;

    @Value("${registCode}")
    private String registCode;

    @Value("${machineNum}")
    private String machineNum;

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public Integer getEnv() {
        return env;
    }

    public void setEnv(Integer env) {
        this.env = env;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getMachineNum() {
        return machineNum;
    }

    public void setMachineNum(String machineNum) {
        this.machineNum = machineNum;
    }

    public String getRegistCode() {
        return registCode;
    }

    public void setRegistCode(String registCode) {
        this.registCode = registCode;
    }
}
