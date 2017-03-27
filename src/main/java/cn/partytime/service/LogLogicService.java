package cn.partytime.service;

import cn.partytime.config.ConfigUtils;
import cn.partytime.util.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2017/3/27 0027.
 */

@Service
public class LogLogicService {


    @Resource(name = "threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    private ConfigUtils configUtils;

    public void logUploadHandler(String content) {

        threadPoolTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String addressId = configUtils.getAddressId();
                String url = configUtils.getLogUrl()+"?addressId="+addressId+"&param="+content;
                HttpUtils.httpRequestStr(url,"GET",null);
            }
        });
    }
}
