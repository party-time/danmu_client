package cn.partytime.service;

import cn.partytime.config.ClientCache;
import cn.partytime.config.ConfigUtils;
import cn.partytime.model.client.ClientCommand;
import cn.partytime.model.client.ClientCommandConfig;
import cn.partytime.model.common.RestResultModel;
import cn.partytime.util.CommandConst;
import cn.partytime.util.CommonConst;
import cn.partytime.util.DateUtils;
import cn.partytime.util.HttpUtils;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2017/3/27 0027.
 */

@Service
public class TmsCommandService {

    @Autowired
    private LogLogicService logLogicService;

    @Autowired
    private ProjectorService projectorService;


    @Autowired
    private ClientCache clientCache;


    @Autowired
    private ConfigUtils configUtils;

    @Autowired
    private CommandExecuteService commandExecuteService;

    @Autowired
    private CommandHandlerService commandHandlerService;





    /**
     * 投影仪相关的指令
     * @param command
     */
    public void projectorHandler(String command){
        String url ="";
        switch (command){
            case CommandConst.PROJECTOR_START:
                //投影仪开启
                logLogicService.logUploadHandler("投影仪开启");
                projectorService.projectorHandler(0);
                //http请求
                url = configUtils.getProjectorRequestUrl(command);
                HttpUtils.repeatRequest(url,"GET",null);
                sendProjectorCommandToOtherServer(command);
                return;
            case CommandConst.PROJECTOR_CLOSE:
                //投影仪关闭
                logLogicService.logUploadHandler("投影仪关闭");
                projectorService.projectorHandler(1);
                //http请求
                url = configUtils.getProjectorRequestUrl(command);
                HttpUtils.repeatRequest(url,"GET",null);
                sendProjectorCommandToOtherServer(command);
                return;
            default:
                return;
        }
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

    /**
     * 电影相关的指令处理
     * @param command
     */
    public void movieHandler(String command){
        String url="";
        switch (command){
            case CommandConst.MOVIE_START:
                logLogicService.logUploadHandler("电影开始");
                url = configUtils.getPartyRequestUrl(CommandConst.MOVIE_START,command);
                if(clientCache.getPartyInfo()!=null && !StringUtils.isEmpty(clientCache.getPartyInfo().getPartyId())){
                    url = url+ CommonConst.SEPARATOR+clientCache.getPartyInfo().getPartyId()+CommonConst.SEPARATOR+ DateUtils.getCurrentDate().getTime();
                    HttpUtils.repeatRequest(url,"GET",null);
                }
                return;
            case CommandConst.MOVIE_CLOSE:
                logLogicService.logUploadHandler("电影关闭");
                url = configUtils.getPartyRequestUrl(CommandConst.MOVIE_CLOSE,command);
                if(clientCache.getPartyInfo()!=null && !StringUtils.isEmpty(clientCache.getPartyInfo().getPartyId())){
                    url = url+ CommonConst.SEPARATOR+clientCache.getPartyInfo().getPartyId()+CommonConst.SEPARATOR+ DateUtils.getCurrentDate().getTime();
                    String resultStr = HttpUtils.repeatRequest(url,"GET",null);
                    if(!StringUtils.isEmpty(resultStr)){
                        RestResultModel restResultModel = JSON.parseObject(resultStr,RestResultModel.class);
                        if(restResultModel.getResult()==200){
                            commandExecuteService.executeAppRestartCallBack();
                            //执行
                            ClientCommandConfig<ClientCommand> clientCommandClientCommandConfig = new ClientCommandConfig<ClientCommand>();
                            clientCommandClientCommandConfig.setType("clientCommand");
                            ClientCommand clientCommand = new ClientCommand();
                            clientCommand.setBcallBack(null);
                            clientCommand.setName("appRestart");
                            clientCommandClientCommandConfig.setData(clientCommand);
                            commandHandlerService.pubCommandToOtherServer(JSON.toJSONString(clientCommandClientCommandConfig));
                        }
                    }
                }
                return;
            default:
                if(command.startsWith(CommandConst.DANMU_START_PREFIX)){
                    url = configUtils.getPartyRequestUrl(CommandConst.DANMU_START_PREFIX,command)+CommonConst.SEPARATOR+ DateUtils.getCurrentDate().getTime();
                    HttpUtils.repeatRequest(url,"GET",null);
                    logLogicService.logUploadHandler("电影开始");
                }
                return;
        }
    }

    /**
     * 广告相关的指令处理
     */
    public void adHandler(String command){
        String url="";
        switch (command){
            case CommandConst.AD_CLOSE:
                logLogicService.logUploadHandler("广告关闭");
                url = configUtils.getPromotionalFilmUrl(CommandConst.AD_CLOSE,"1");
                HttpUtils.repeatRequest(url,"GET",null);
                //sendAdCommandToServer("promotionalFilm",command,1);
                return;
            default:
                if(command.startsWith(CommandConst.AD_START_PREFIX)){
                    logLogicService.logUploadHandler("广告开始");
                    //sendAdCommandToServer("promotionalFilm",command,0);
                    url = configUtils.getPromotionalFilmUrl(CommandConst.AD_START_PREFIX,"0");
                    url = url +CommonConst.SEPARATOR+command;
                    HttpUtils.repeatRequest(url,"GET",null);
                }
                return;
        }
    }



    //public void commit
    /*public void  sendAdCommandToServer(String type, String name, int status){
        AdConfig adConfig = new AdConfig();
        adConfig.setType(type);
        Ad ad = new Ad();
        ad.setName(name);
        ad.setStatus(status);
        ConcurrentHashMap<Channel,ClientModel> channelClientModelConcurrentHashMap = clientCache.getRemoteserverClientChannelConcurrentHashMap();
        if (channelClientModelConcurrentHashMap != null && channelClientModelConcurrentHashMap.size() > 0) {
            for (ConcurrentHashMap.Entry<Channel, ClientModel> entry : channelClientModelConcurrentHashMap.entrySet()) {
                Channel channel = entry.getKey();
                channel.write()
            }
        }
    }*/
}
