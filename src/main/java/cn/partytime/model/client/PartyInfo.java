package cn.partytime.model.client;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/7 0007.
 */
public class PartyInfo implements Serializable{

    private long partyTime;

    private String partyId;

    private String type;

    private Integer status;

    public long getPartyTime() {
        return partyTime;
    }

    public void setPartyTime(long partyTime) {
        this.partyTime = partyTime;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
