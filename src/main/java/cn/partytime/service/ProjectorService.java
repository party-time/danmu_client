package cn.partytime.service;

import cn.partytime.config.ClientCache;
import cn.partytime.config.ConfigUtils;
import cn.partytime.model.client.ClientCommand;
import cn.partytime.model.client.ClientCommandConfig;
import cn.partytime.model.device.DeviceInfo;
import cn.partytime.util.CommandConst;
import cn.partytime.util.HttpUtils;
import com.alibaba.fastjson.JSON;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2017/3/27 0027.
 */

@Service
public class ProjectorService {

    @Autowired
    private ClientCache clientCache;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private LogLogicService logLogicService;

    @Resource(name = "threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private CommandHandlerService commandHandlerService;

    @Autowired
    private ConfigUtils configUtils;

    /**
     * 投影仪开启和关闭
     *
     * @param type 0开启，1：关闭； 2：切白
     */
    public void projectorHandler(int type) {
        try {

            List<DeviceInfo> deviceInfoList = deviceService.findDeviceInfoList(0);
            String url = "";
            for (DeviceInfo deviceInfo : deviceInfoList) {
                if(deviceInfo.getType()==0){
                    String urlTemp[] = deviceInfo.getUrl().split(";");
                    projectorHandler(urlTemp,type);
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void projectorHandler(String [] urlArrays,int type) throws URISyntaxException {
        String url = "";
        String param= "";
        if(urlArrays!=null && urlArrays.length>0){
            for(int i=0; i<urlArrays.length; i++){
                String tempUrl = urlArrays[i];
                URI uri = new URI(tempUrl);
                QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
                Map<String, List<String>> parameters = queryStringDecoder.parameters();
                String urlType = parameters.get("param").get(0);
                if(type==0 && "start".contentEquals(urlType)){
                    url = tempUrl.substring(0,tempUrl.lastIndexOf("&"));
                    param = url.substring(url.lastIndexOf("?")+1);
                    url = url.substring(0,url.lastIndexOf("?"));
                    logLogicService.logUploadHandler("开启投影仪的url:" + urlArrays[i]);
                }else if(type==1 && "stop".contentEquals(urlType)){
                    url = tempUrl.substring(0,tempUrl.lastIndexOf("&"));
                    param = url.substring(url.lastIndexOf("?")+1);
                    url = url.substring(0,url.lastIndexOf("?"));
                    logLogicService.logUploadHandler("关闭投影仪的url:" + urlArrays[i]);
                }else if(type==2 && "change".contentEquals(urlType)){
                    url = tempUrl.substring(0,tempUrl.lastIndexOf("&"));
                    param = url.substring(url.lastIndexOf("?")+1);
                    url = url.substring(0,url.lastIndexOf("?"));
                    logLogicService.logUploadHandler("切白投影仪的url:" + urlArrays[i]);
                }

            }
            System.out.println("=====url:"+url);
            System.out.println("=====param:"+param);
            executeProjector(url,param);
        }
    }

    private void executeProjector(final String url,String param) {
        threadPoolTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                HttpUtils.httpRequestStr(url, "GET", param);
            }
        });
    }

    /**
     *
     * @param command
     * @param type 0开启，1：关闭； 2：切白
     */
    public void projectSendCommand(String command,int type){
        //logLogicService.logUploadHandler("投影仪关闭");
        projectorHandler(type);
        //http请求
        String url = configUtils.getProjectorRequestUrl(command);
        HttpUtils.repeatRequest(url,"GET",null);

        if(command.contains("-")){
            String[] commadArray = command.split("-");
            command = commadArray[0]+commadArray[1].substring(0, 1).toUpperCase() + commadArray[1].substring(1);
        }
        sendProjectorCommandToOtherServer(command);
    }

    public void sendProjectorCommandToOtherServer(String command){
        ClientCommandConfig<ClientCommand> clientCommandClientCommandConfig = new ClientCommandConfig<ClientCommand>();
        clientCommandClientCommandConfig.setType("clientCommand");
        ClientCommand clientCommand = new ClientCommand();
        clientCommand.setBcallBack(null);
        clientCommand.setName(command);
        clientCommandClientCommandConfig.setData(clientCommand);
        commandHandlerService.pubCommandToOtherServer(JSON.toJSONString(clientCommandClientCommandConfig));
    }

}
