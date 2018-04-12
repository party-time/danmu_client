package cn.partytime.model;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by liuwei on 16/6/12.
 * 弹幕活动
 */
public class Party  {

    private String id;

    private String partyId;

    private String name;

    private Integer type;

    private Date startTime;

    /**
     * 内容开始时间
     */
    private Date activityStartTime;

    /**
     * 内容结束时间
     */
    private Date activityEndTime;

    private Date endTime;

    private String startTimeStr;

    private String endTimeStr;

    /**
     * 拼音缩写
     */
    private String shortName;

    /**
     * 最后一次更新资源的时间
     */
    private Date lastUpdateResourceTime;

    private String movieAlias;


    public String getJsonStartTime(){
        if( null != this.startTime){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(this.startTime);
            return "{\"year\":"+calendar.get(Calendar.YEAR)+",\"month\":"+(calendar.get(Calendar.MONTH)+1)+",\"day\":"+calendar.get(Calendar.DAY_OF_MONTH)+
                    ",\"hour\":"+calendar.get(Calendar.HOUR_OF_DAY)+",\"minute\":"+calendar.get(Calendar.MINUTE)+"}";
        }
        return null;
    }

    public String getJsonEndTime(){
        if( null != this.endTime){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(this.endTime);
            return "{\"year\":"+calendar.get(Calendar.YEAR)+",\"month\":"+(calendar.get(Calendar.MONTH)+1)+",\"day\":"+calendar.get(Calendar.DAY_OF_MONTH)+
                    ",\"hour\":"+calendar.get(Calendar.HOUR_OF_DAY)+",\"minute\":"+calendar.get(Calendar.MINUTE)+"}";
        }
        return null;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPartyId() {
        return id;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getStartTimeStr() {
        if( null != this.getStartTime()){
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(this.getStartTime());
        }else{
            return this.startTimeStr;
        }

    }

    public String getEndTimeStr() {
        if( null != this.getEndTime()){
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(this.getEndTime());
        }else{
            return this.endTimeStr;
        }

    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Date getLastUpdateResourceTime() {
        return lastUpdateResourceTime;
    }

    public void setLastUpdateResourceTime(Date lastUpdateResourceTime) {
        this.lastUpdateResourceTime = lastUpdateResourceTime;
    }

    public Date getActivityStartTime() {
        return activityStartTime;
    }

    public void setActivityStartTime(Date activityStartTime) {
        this.activityStartTime = activityStartTime;
    }

    public Date getActivityEndTime() {
        return activityEndTime;
    }

    public void setActivityEndTime(Date activityEndTime) {
        this.activityEndTime = activityEndTime;
    }

    public String getMovieAlias() {
        return movieAlias;
    }

    public void setMovieAlias(String movieAlias) {
        this.movieAlias = movieAlias;
    }
}

