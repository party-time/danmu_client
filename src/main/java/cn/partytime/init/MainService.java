package cn.partytime.init;

import cn.partytime.model.Properties;
import cn.partytime.netty.client.LocalServerWebSocketClient;
import cn.partytime.netty.client.ServerWebSocketClient;
import cn.partytime.service.*;
import cn.partytime.netty.server.ClientServer;
import cn.partytime.netty.server.TmsServer;
import cn.partytime.util.HttpUtils;
import cn.partytime.util.PrintScreenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.URISyntaxException;

/**
 * Created by Administrator on 2017/3/23 0023.
 */

@Service
public class MainService {

    @Autowired
    private ServerStartService serverStartService;

    @Autowired
    private RsyncFileService rsyncFileService;
    /**
     * 启动系统加载项目
     */
    @PostConstruct
    public void init() {
        rsyncFileService.createFlashConfig();
        //serverStartService.projectInit();

    }
}
