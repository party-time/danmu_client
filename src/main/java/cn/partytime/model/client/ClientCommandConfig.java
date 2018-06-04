package cn.partytime.model.client;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/5 0005.
 */
public class ClientCommandConfig<T> implements Serializable {
    private String type;

    private String code;

    private T data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
