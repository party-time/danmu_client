package cn.partytime.model.client;

import org.omg.CORBA.PRIVATE_MEMBER;

/**
 * Created by Administrator on 2017/4/5 0005.
 */
public class ClientCommand {
    private String name;

    private String bcallBack;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBcallBack() {
        return bcallBack;
    }

    public void setBcallBack(String bcallBack) {
        this.bcallBack = bcallBack;
    }

}
