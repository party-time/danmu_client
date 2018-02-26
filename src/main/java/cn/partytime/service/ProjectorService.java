package cn.partytime.service;

import cn.partytime.config.ClientCache;
import cn.partytime.config.ConfigUtils;
import cn.partytime.config.ScriptConfigUtils;
import cn.partytime.model.client.ClientCommand;
import cn.partytime.model.client.ClientCommandConfig;
import cn.partytime.model.device.DeviceInfo;
import cn.partytime.util.CommandConst;
import cn.partytime.util.HttpUtils;
import com.alibaba.fastjson.JSON;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
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
    private WindowShellService windowShellService;

    @Autowired
    private CommandExecuteService commandExecuteService;


    //场地
    @Value("${addressId}")
    private String addressId;

    @Autowired
    private ConfigUtils configUtils;

    @Autowired
    private ScriptConfigUtils scriptConfigUtils;

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

    public void
    executePJLINKCommand(int type){
        List<DeviceInfo> deviceInfoList = deviceService.findDeviceInfoList(0);
        for (DeviceInfo deviceInfo : deviceInfoList) {

            String ip = deviceInfo.getUrl();
            if(!StringUtils.isEmpty(ip)){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        logLogicService.logUploadHandler("地址:"+ip+"投影操作:"+(type==0?"关闭":"开启"));
                        executeSendPJlinkCommand(ip,type,0);
                    }
                }).start();
            }
        }
    }

    private String parseResponse(String response)
    {
        if (response != null)
        {
            if (response.contains(" ERRA"))
            {
                response = "Authentication error.";
            }
            else if (response.endsWith("ERR1"))
            {
                response = "Unknown Command.";
            }
            else if (response.endsWith("ERR2"))
            {
                response = "Wrong Parameter.";
            }
            else if (response.endsWith("ERR3"))
            {
                response = "Device is not responding.";
            }
            else if (response.endsWith("ERR4"))
            {
                response = "Device has internal error.";
            }
            else
            {
                response = response.substring(7);
            }
        }

        return response;
    }

    private void executeSendPJlinkCommand(String ip,int type,int count){
        if(count==4){
            String url= configUtils.getJavaClientAlarmUlr()+"/"+"projector"+"/"+configUtils.getAddressId();
            threadPoolTaskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    HttpUtils.httpRequestStr(url, "GET", null);
                }
            });
            logLogicService.logUploadHandler("投影仪:"+ip+",socket 进行重新连接超过"+4+"次,结束");
            return;
        }
        count++;
        OutputStreamWriter osw =null;
        BufferedReader br=null;
        Socket socket = null;
        try {
            socket = new Socket(ip, 4352);
            String authPass = "";
            String command = "%1POWR "+type+"\r";
            osw = new OutputStreamWriter(socket.getOutputStream());
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            logLogicService.logUploadHandler("发送命令:"+command);
            osw.write(command);
            osw.flush();
            String response = parseResponse(br.readLine());
            logLogicService.logUploadHandler("接收投影返回的值: " + response);
        } catch (IOException e) {
            e.printStackTrace();
            logLogicService.logUploadHandler("投影仪:"+ip+",socket 连接异常");
            logLogicService.logUploadHandler("投影仪:"+ip+",socket 进行重新连接"+(count)+"次");
            executeSendPJlinkCommand(ip,type,count);
        }finally
        {
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                }
            }

            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    logLogicService.logUploadHandler("投影仪:"+ip+",socket 连接异常");
                }
            }
        }
    }

    /**
     *
     * @param command
     * @param type 0开启，1：关闭； 2：切白
     */
    public void projectSendCommand(String command,int type){
        //logLogicService.logUploadHandler("投影仪关闭");

        if("584a1a9a0cf2fdb8406efdce".equals(addressId)){
            projectorHandler(type);
        }else{
            //0关闭，1:开启
            executePJLINKCommand(type==0?1:0);
            if(type==0){
                //windowShellService.execExe(scriptConfigUtils.findScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.PJLINKSTART_VBS));
                try {
                    newPjLinkStartOperate();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else if(type==1){
                //executePJLINKCommand(0);
                windowShellService.execExeVBS(scriptConfigUtils.findScriptPath(scriptConfigUtils.VBS_TYPE, scriptConfigUtils.PJLINKSTOP_VBS));
            }

        }

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


    public void newPjLinkStartOperate() throws InterruptedException {

        commandExecuteService.executeAppCloseCallBack();

        Thread.sleep(2000);
        //开启本地投影软件
        windowShellService.execExeVBS(scriptConfigUtils.findScriptPath(scriptConfigUtils.VBS_TYPE, scriptConfigUtils.PJLINKSTART_VBS));

        //Thread.sleep(2000);

        //commandExecuteService.executeAppStartCallBack();
    }
}
