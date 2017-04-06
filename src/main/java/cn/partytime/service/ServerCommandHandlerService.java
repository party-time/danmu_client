package cn.partytime.service;

import cn.partytime.model.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2017/4/6 0006.
 */

@Service
public class ServerCommandHandlerService {

    @Autowired
    private Properties properties;

    public boolean chckerIsLocalCommand(String command){
        if(command.contains(properties.getMachineNum())){
            return true;
        }
        return false;
    }
}
