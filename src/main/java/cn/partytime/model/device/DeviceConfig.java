package cn.partytime.model.device;

import cn.partytime.model.AdTimerFileResource;

import java.util.List;

/**
 * Created by Administrator on 2017/3/27 0027.
 */
public class DeviceConfig {

    private Integer result;

    private List<DeviceInfo> data;

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public List<DeviceInfo> getData() {
        return data;
    }

    public void setData(List<DeviceInfo> data) {
        this.data = data;
    }
}
