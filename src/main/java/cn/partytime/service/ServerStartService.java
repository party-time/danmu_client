package cn.partytime.service;

import cn.partytime.init.MainService;
import cn.partytime.model.Properties;
import cn.partytime.netty.client.LocalServerWebSocketClient;
import cn.partytime.netty.client.ServerWebSocketClient;
import cn.partytime.netty.client.TmsTransClient;
import cn.partytime.netty.server.ClientServer;
import cn.partytime.netty.server.TmsServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2017/4/14 0014.
 */

@Service
public class ServerStartService {

    private static final Logger logger = LoggerFactory.getLogger(ServerStartService.class);

    @Value("${netty.port:8081}")
    private int clientSeverPort;

    @Value("${netty.port:2016}")
    private int tmsServerPort;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private LocalServerWebSocketClient localServerWebSocketClient;

    @Autowired
    private ClientServer clientServer;

    @Autowired
    private TmsServer tmsServer;

    @Autowired
    private CommandExecuteService commandExecuteService;

    @Autowired
    private ScriptFileService scriptFileService;

    @Autowired
    private ServerWebSocketClient serverWebSocketClient;

    @Resource(name = "threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private Properties properties;

    @Autowired
    private LogLogicService logLogicService;

    @Autowired
    private TmsTransClient tmsTransClient;

    public void projectInit(){
        //启动netty服务
        startNettyServer();
        startClientServer();

        //启动client1连接远程server
        logLogicService.logUploadHandler("本机编号:"+properties.getMachineNum());
        if("1".equals(properties.getMachineNum())) {
            startClientConnectRemoteServer();
        }else{
            startClientConnectLocalServer();
        }

        if("3".equals(properties.getMachineNum())) {
            setartTmsTransClientServer();
        }

        //加载本地资源
        initResource();
    }

    private void setartTmsTransClientServer(){
        logLogicService.logUploadHandler("向控制机器发送tms指令的服务");
        threadPoolTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    deviceService.findDeviceInfo();
                    tmsTransClient.init();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logLogicService.logUploadHandler("向控制机器发送tms指令的服务client异常:"+e.getMessage());
                }
            }
        });
    }

    /**
     * 启动第一个客户端连接远程server
     */
    private void startClientConnectRemoteServer(){
        logLogicService.logUploadHandler("启动连接远程服务器的client");
        threadPoolTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    deviceService.findDeviceInfo();
                    serverWebSocketClient.init();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logLogicService.logUploadHandler("启动连接远程服务器的client异常:"+e.getMessage());
                }
            }
        });
    }

    /**
     * 启动第一个客户端连接远程server
     */
    private void startClientConnectLocalServer(){
        logLogicService.logUploadHandler("启动连接本地服务器的client");
        threadPoolTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    deviceService.findDeviceInfo();
                    //获取客户端信息
                    localServerWebSocketClient.init();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logLogicService.logUploadHandler("启动连接本地服务器的client线程异常:"+e.getMessage());
                }
            }
        });
    }

    /**
     * 启动netty Server
     */
    private void startClientServer(){
        logLogicService.logUploadHandler("启动服务本地JavaClient的clientServer");
        threadPoolTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    tmsServer.init(tmsServerPort);
                } catch (Exception e) {
                    //e.printStackTrace();
                    logLogicService.logUploadHandler("启动服务本地JavaClient的clientServer异常:"+e.getMessage());
                }
            }
        });
    }
    /**
     * 启动netty Server
     */
    private void startNettyServer(){
        try {
            logLogicService.logUploadHandler("启动TmsServer");
            threadPoolTaskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    clientServer.init(clientSeverPort);
                }
            });
        }catch (Exception e){
            logger.error("线程启动异常:{}",e.getMessage());
        }
    }

    private void initResource(){
        logLogicService.logUploadHandler("重新生成脚本");
        scriptFileService.createShell();
        if(!"3".equals(properties.getMachineNum())) {
            logLogicService.logUploadHandler("启动flash客户端");
            commandExecuteService.executeAppStartCallBack();
        }
    }

}
