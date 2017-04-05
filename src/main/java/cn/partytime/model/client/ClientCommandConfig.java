package cn.partytime.model.client;

/**
 * Created by Administrator on 2017/4/5 0005.
 */
public class ClientCommandConfig {
    private String type;

    private ClientCommand  data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ClientCommand getData() {
        return data;
    }

    public void setData(ClientCommand data) {
        this.data = data;
    }
}
