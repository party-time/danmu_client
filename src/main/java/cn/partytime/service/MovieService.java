package cn.partytime.service;

import cn.partytime.config.ClientCache;
import cn.partytime.config.ClientPartyCache;
import cn.partytime.config.ConfigUtils;
import cn.partytime.config.ScriptConfigUtils;
import cn.partytime.model.*;

import cn.partytime.model.common.RestResultModel;
import cn.partytime.service.*;
import cn.partytime.util.DateUtils;
import cn.partytime.util.HttpUtils;
import cn.partytime.util.ListUtils;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;


/**
 * Created by admin on 2018/4/11.
 */

@Service
public class MovieService {

    @Autowired
    private ConfigUtils configUtils;

    @Autowired
    private ClientPartyCache clientPartyCache;

    //场地
    @Value("${addressId}")
    private String addressId;


    public void updateMovieCache() {
        Long danmuStartDate = clientPartyCache.getDanmuStartDate();
        Integer time = clientPartyCache.getAdTime();
        if(time == null){
            //从服务器端获取广告时间
            System.out.println("从缓存中取出广告开始时间为0");
            String url = configUtils.findAddressInfo();
            String result  = HttpUtils.httpRequestStr(url,"GET",null);
            if(!StringUtils.isEmpty(result)){
                RestResultModel restResultModel = JSON.parseObject(result,RestResultModel.class);
                if(restResultModel.getResult()==200){
                    String addressInfoStr = String.valueOf(restResultModel.getData());
                    DanmuAddressModel danmuAddressModel = JSON.parseObject(addressInfoStr,DanmuAddressModel.class);
                    if(danmuAddressModel!=null){
                        if(danmuAddressModel.getAdTime()!=null){
                            clientPartyCache.setAdTime(danmuAddressModel.getAdTime());
                        }
                    }
                }
            }
        }

        if(danmuStartDate==null){
            //从服务器获取弹幕开始时间
            System.out.println("从缓存中取出的电影开始时间为0");
            String url = configUtils.findCurrentMovie(addressId);
            String resultStr  = HttpUtils.httpRequestStr(url,"GET",null);
            if(!StringUtils.isEmpty(resultStr)){
                RestResultModel restResultModel = JSON.parseObject(resultStr,RestResultModel.class);
                if(restResultModel.getResult()==200){
                    String movieScheduleModelStr = String.valueOf(restResultModel.getData());
                    MovieScheduleModel movieScheduleModel = JSON.parseObject(movieScheduleModelStr,MovieScheduleModel.class);
                    if(movieScheduleModel!=null){
                        if(movieScheduleModel.getClientStartTime()!=null){
                            clientPartyCache.setDanmuStartDate(movieScheduleModel.getClientStartTime());
                        }
                    }
                }
            }
        }
    }

}
