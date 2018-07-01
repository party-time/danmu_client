package cn.partytime.config;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class PjLinkSoftClientCache {

    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
