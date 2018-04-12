package cn.partytime.config;

import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by admin on 2018/4/8.
 */

@Component
public class ClientPartyCache {

    public Long danmuStartDate;

    public Integer adTime;

    public boolean booleanMovieStart = false;


    public boolean isBooleanMovieStart() {
        return booleanMovieStart;
    }

    public void setBooleanMovieStart(boolean booleanMovieStart) {
        this.booleanMovieStart = booleanMovieStart;
    }


    public Integer getAdTime() {
        return adTime;
    }

    public void setAdTime(Integer adTime) {
        this.adTime = adTime;
    }

    public Long getDanmuStartDate() {
        return danmuStartDate;
    }

    public void setDanmuStartDate(long danmuStartDate) {
        this.danmuStartDate = danmuStartDate;
    }
}
