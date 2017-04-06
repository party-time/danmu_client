package cn.partytime.init;

import cn.partytime.model.Properties;
import cn.partytime.netty.client.LocalServerWebSocketClient;
import cn.partytime.netty.client.ServerWebSocketClient;
import cn.partytime.service.DeviceService;
import cn.partytime.service.LogLogicService;
import cn.partytime.service.RsyncFileService;
import cn.partytime.netty.server.ClientServer;
import cn.partytime.netty.server.TmsServer;
import cn.partytime.service.WindowShellService;
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
    private static final Logger logger = LoggerFactory.getLogger(MainService.class);

    @Value("${netty.port:7070}")
    private int clientSeverPort;

    @Value("${netty.port:8080}")
    private int tmsServerPort;

    @Autowired
    private Properties properties;

    @Autowired
    private ClientServer clientServer;

    @Autowired
    private TmsServer tmsServer;

    @Autowired
    private ServerWebSocketClient serverWebSocketClient;

    @Autowired
    private RsyncFileService rsyncFileService;


    @Resource(name = "threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private WindowShellService windowShellService;

    @Autowired
    private LogLogicService logLogicService;


    @Autowired
    private DeviceService deviceService;

    @Autowired
    private LocalServerWebSocketClient localServerWebSocketClient;

    /**
     * 启动系统加载项目
     */
    @PostConstruct
    public void init() {
        //启动netty服务
        startNettyServer();
        startClientServer();
        //TODO:启动client1连接远程server
        startClientConnectRemoteServer();
        //TODO:启动client2连接Javaclient
        startClientConnectLocalServer();

        //TODO:加载本地资源
        initResource();
    }


    private void initResource(){

        threadPoolTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                /**
                if(properties.getIfDownload()==0) {
                    rsyncFileService.rsyncFile();
                    rsyncFileService.downloadClient();
                    //下载执行脚本
                    rsyncFileService.downloadExecuteShell();
                }
                rsyncFileService.createFlashConfig();
                windowShellService.restartTask();

                deviceService.findDeviceInfo();
                **/

                windowShellService.printScreenPic();

            }
        });
    }

    /**
     * 启动第一个客户端连接远程server
     */
    private void startClientConnectRemoteServer(){
        threadPoolTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    serverWebSocketClient.initBootstrap();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * 启动第一个客户端连接远程server
     */
    private void startClientConnectLocalServer(){
        threadPoolTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    localServerWebSocketClient.initBootstrap();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 启动netty Server
     */
    private void startClientServer(){
        try {
            logger.info("启动ClientServer");
            threadPoolTaskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        tmsServer.bind(tmsServerPort);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e){
            logger.error("线程启动异常:{}",e.getMessage());
        }
    }
    /**
     * 启动netty Server
     */
    private void startNettyServer(){
        try {
            logger.info("启动TmsServer");
            logLogicService.logUploadHandler("启动javaClientNettyServer");
            threadPoolTaskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    clientServer.nettyStart(clientSeverPort);
                }
            });
        }catch (Exception e){
            logger.error("线程启动异常:{}",e.getMessage());
        }
    }




    /**
     * 启动第二个客户端连接远程server
     */
    private void startClinetSecondToJavaClient(){}


    /**
     * 加载本地资源
     */
    private void initLocalResource(){

    }
}
