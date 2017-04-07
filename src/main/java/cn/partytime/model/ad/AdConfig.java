package cn.partytime.model.ad;

import cn.partytime.model.client.ClientCommand;

/**
 * Created by Administrator on 2017/4/6 0006.
 */
public class AdConfig {

    private String type;

    private Ad data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Ad getData() {
        return data;
    }

    public void setData(Ad data) {
        this.data = data;
    }
}
