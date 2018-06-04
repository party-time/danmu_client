package cn.partytime.service;

import cn.partytime.config.ConfigUtils;
import cn.partytime.config.FlashCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2018/5/30.
 */
@Service
public class FlashBussinessHandlerService {


    @Autowired
    private MessageSendToCollectorService messageSendToCollectorService;

    @Autowired
    private ConfigUtils configUtils;

    @Autowired
    private FlashCache flashCache;

    public void flashFullHandler(){


        if(flashCache.getSendFlashOpenCount()>0){
            return;
        }
        flashCache.setSendFlashOpenCount(1);


        Map<String,Object> map = new HashMap<String,Object>();
        map.put("type","screenMove");
        map.put("clientType","2");
        map.put("isCallBack","true");
        map.put("code",configUtils.getRegisterCode());
        Map<String,Object> dataMap = new HashMap<String,Object>();
        dataMap.put("screenMove",true);
        dataMap.put("isCallBack",true);
        map.put("data",dataMap);
        messageSendToCollectorService.sendMessageToCollectorServer(map);
    }
}
