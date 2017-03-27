package cn.partytime.service;

import cn.partytime.config.ConfigUtils;
import cn.partytime.util.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2017/3/27 0027.
 */

@Service
public class LogLogicService {

    @Autowired
    private ConfigUtils configUtils;

    public void logUploadHandler(String content) {
        String addressId = configUtils.getAddressId();
        String url = configUtils.getLogUrl()+"?addressId="+addressId+"&param="+content;
        HttpUtils.httpRequestStr(url,"GET",null);
    }
}
