package cn.partytime.model.device;

/**
 * Created by Administrator on 2017/3/27 0027.
 */
public class DeviceInfo {

    private String id;

    /**
     * 场地id
     */
    private String addressId;

    /**
     * ip信息
     */
    private String ip;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 类型 0 投影ip  1 javaClient ip
     */
    private Integer type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
