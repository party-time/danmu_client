package cn.partytime.service;

import cn.partytime.config.ClientCache;
import cn.partytime.model.Properties;
import cn.partytime.model.client.ClientCommand;
import cn.partytime.model.client.ClientCommandConfig;
import cn.partytime.model.client.ClientModel;
import cn.partytime.model.client.PartyInfo;
import cn.partytime.util.CommonUtil;
import cn.partytime.util.HttpUtils;
import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2017/4/14 0014.
 */
@Service
public class CommandHandlerService {


    @Autowired
    private ClientCache clientCache;

    @Autowired
    private Properties properties;


    @Autowired
    private CommandExecuteService commandExecuteService;


    public void commandHandler(ClientCommandConfig clientCommandConfig){
        if("command".equals(clientCommandConfig.getType())){
            System.out.print(clientCommandConfig.getData());
            String partyInfoStr = String.valueOf(clientCommandConfig.getData());
            PartyInfo partyInfo =  JSON.parseObject(partyInfoStr,PartyInfo.class);
            clientCache.setPartyInfo(partyInfo);

            //活动信息给命令广播到其他服务器
            pubCommandToOtherServer(JSON.toJSONString(clientCommandConfig));

            if(partyInfo.getStatus()==3){
                ClientCommandConfig<ClientCommand> clientCommandClientCommandConfig = new ClientCommandConfig<ClientCommand>();
                clientCommandClientCommandConfig.setType("clientCommand");
                ClientCommand clientCommand = new ClientCommand();
                clientCommand.setBcallBack(null);
                clientCommand.setName("appRestart");
                clientCommandClientCommandConfig.setData(clientCommand);
                commandExecuteService.executeAppRestartCallBack();
            }

        }else if("clientCommand".equals(clientCommandConfig.getType())){
            String clientCommandData = String.valueOf(clientCommandConfig.getData());
            ClientCommand clientCommand = JSON.parseObject(clientCommandData,ClientCommand.class);
            String type = clientCommand.getName();
            new Thread(new Runnable(){
                @Override
                public void run() {
                    //直接脚本
                    execute(type,clientCommandConfig);
                    //执行回调
                    if(!StringUtils.isEmpty(clientCommand.getBcallBack())){
                        HttpUtils.httpRequestStr(clientCommand.getBcallBack(),"GET",null);
                    }
                }
            }).start();
        }
    }


    public void execute(String command,ClientCommandConfig clientCommandConfig){

        if(CommonUtil.hasDigit(command)){
            if(!chckerIsLocalCommand(command)){
                System.out.println("不是本机要执行的命令");
                //通知下个服务器
                pubCommandToOtherServer(JSON.toJSONString(clientCommandConfig));
                return;
            }
        }else{
            pubCommandToOtherServer(JSON.toJSONString(clientCommandConfig));
        }
        command = command.replaceAll("\\d+", "");
        String commandStr = command.substring(0, 1).toUpperCase() + command.substring(1);
        String methodName="execute"+commandStr+"CallBack";
        try {
            Class<CommandExecuteService> clz = CommandExecuteService.class;
            Method method = clz.getMethod(methodName);
            method.invoke(commandExecuteService);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void pubCommandToOtherServer(String message){
        ConcurrentHashMap<Channel,ClientModel> channelClientModelConcurrentHashMap = clientCache.findClientModelConcurrentHashMap();
        if (channelClientModelConcurrentHashMap != null && channelClientModelConcurrentHashMap.size() > 0) {
            for (ConcurrentHashMap.Entry<Channel, ClientModel> entry : channelClientModelConcurrentHashMap.entrySet()) {
                Channel channel = entry.getKey();
                channel.writeAndFlush(new TextWebSocketFrame(message));
            }
        }
    }

    public boolean chckerIsLocalCommand(String command){
        if(command.contains(properties.getMachineNum())){
            return true;
        }
        return false;
    }
}
