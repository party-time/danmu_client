package cn.partytime.model;

import java.util.List;

/**
 * Created by yang on 2017/2/17.
 */
public class UpdatePlanConfig {

    private Integer result;

    private List<VersionInfo> data;

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public List<VersionInfo> getData() {
        return data;
    }

    public void setData(List<VersionInfo> data) {
        this.data = data;
    }
}
