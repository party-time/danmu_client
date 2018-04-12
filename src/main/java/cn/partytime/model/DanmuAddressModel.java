package cn.partytime.model;


import lombok.Data;

import java.util.Map;

@Data
public class DanmuAddressModel {
    private String id;

    //地址别名
    private String name;

    /**广告名称*/
    private String adName;

    //详细地址
    private String address;

    //所属城市
    private String cityId;

    //所属省份
    private String provinceId;

    //所属区域
    private String areaId;


    //场地的长度
    private Integer length;

    //场地的宽度
    private Integer width;

    //场地的高度
    private Integer height;

    //容纳人数
    private Integer peopleNum;

    //场地的联系人
    private String contacts;

    //场地联系人手机号
    private String phoneNum;

    private int range;

    //该场地的广告时间
    private Integer adTime;
}
