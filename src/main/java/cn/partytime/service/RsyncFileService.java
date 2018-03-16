package cn.partytime.service;

import cn.partytime.config.ConfigUtils;
import cn.partytime.json.PartyJson;
import cn.partytime.json.PartyResourceResult;
import cn.partytime.json.Resource;
import cn.partytime.json.VideoResourceJson;
import cn.partytime.model.*;
import cn.partytime.model.Properties;
import cn.partytime.util.Const;
import cn.partytime.util.HttpUtils;
import cn.partytime.util.ListUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;


/**
 * Created by administrator on 2016/12/8.
 */
@Service
@Slf4j
public class RsyncFileService {

    @Autowired
    private Properties properties;

    @Autowired
    private ConfigUtils configUtils;

    @Autowired
    private LogLogicService logLogicService;

    @Autowired
    private CommandExecuteService commandExecuteService;

    public void rsyncFile(){
        commandExecuteService.executeResourceAllDownCallBack();
    }

    public void downloadClient(){
        commandExecuteService.executeUpdateClientDownCallBack();
    }

    public void createFlashConfig() {
        String paramJsonStr = HttpUtils.httpRequestStr(configUtils.getParamUrl()+"?code="+properties.getRegistCode(),"GET",null);
        logLogicService.logUploadHandler("配置参数下载内容:"+paramJsonStr);
        String jsonStr = HttpUtils.httpRequestStr(configUtils.getInitUrl()+"?addressId="+properties.getAddressId(), "GET", null);
        logLogicService.logUploadHandler("配置表生下载内容:"+jsonStr);
        DownloadFileConfig downloadFileConfig = JSON.parseObject(jsonStr,DownloadFileConfig.class);
        if (null != downloadFileConfig) {
            List<PartyResourceResult> partyResourceResultList = downloadFileConfig.getData();
            //获取定时弹幕
            List<TimerDanmuPathModel>  timerDanmuPathModels  = findTimerDanmuFile();
            //获取广告弹幕
            AdTimerFileResource adTimerFileResource = findAdTimerDanmu();
            Map<String,String> adTimerMap = new HashMap<String,String>();
            if(adTimerFileResource!=null){
                List<TimerDanmuFileModel> timerDanmuFileModelList =adTimerFileResource.getTimerDanmuFileLogicModels();
                if(ListUtils.checkListIsNotNull(timerDanmuFileModelList)){
                    for(TimerDanmuFileModel timerDanmuFileModel:timerDanmuFileModelList){
                        String realFilePath = configUtils.realSaveAdtimerFilePath() +timerDanmuFileModel.getPath();
                        File file = new File(realFilePath);
                        if(file.exists()){
                            adTimerMap.put(timerDanmuFileModel.getPartyId(),configUtils.saveFilePath+"/adTimerDanmu"+timerDanmuFileModel.getPath());
                        }
                    }
                }
            }


            if (null != partyResourceResultList) {
                Map<String, Object> resourceFileMap = new HashMap<String, Object>();
                List<PartyResourceModel> partyResourceModelList = new ArrayList<>();
                for (PartyResourceResult partyResourceResult : partyResourceResultList) {
                    String partyId = partyResourceResult.getParty().getId();
                    List<ResourceFile> resourceFileList = partyResourceResult.getResourceFileList();
                    if (null != resourceFileList) {
                        List<ResourceFile> bigExpressionList = new ArrayList<>();
                        List<ResourceFile> specialImageList = new ArrayList<>();
                        List<ResourceFile> specialVideoList = new ArrayList<>();

                        for (ResourceFile resourceFile : resourceFileList) {
                            File file = new File(configUtils.rsyncSaveFilePath()+"/upload"+resourceFile.getFileUrl());
                            if( file.exists()){
                                resourceFile.setLocalFilePath(configUtils.saveFilePath+"/upload"+resourceFile.getFileUrl());
                                if (Const.RESOURCE_EXPRESSIONS == resourceFile.getFileType() || Const.RESOURCE_EXPRESSIONS_CONSTANT == resourceFile.getFileType()) {
                                    bigExpressionList.add(resourceFile);
                                } else if (Const.RESOURCE_SPECIAL_IMAGES == resourceFile.getFileType()) {
                                    specialImageList.add(resourceFile);
                                } else if (Const.RESOURCE_SPECIAL_VIDEOS == resourceFile.getFileType()) {
                                    specialVideoList.add(resourceFile);
                                }
                            }
                        }
                        PartyResourceModel partyResourceModel = new PartyResourceModel();
                        partyResourceModel.setParty(partyResourceResult.getParty());
                        partyResourceModel.setBigExpressionList(bigExpressionList);
                        partyResourceModel.setSpecialImageList(specialImageList);
                        partyResourceModel.setSpecialVideoList(specialVideoList);
                        //填充广告弹幕
                        partyResourceModel.setAdTimerDanmuPath(adTimerMap.get(partyId));

                        if( null != timerDanmuPathModels) {
                            for (TimerDanmuPathModel timerDanmuPathModel : timerDanmuPathModels) {
                                if (partyResourceModel.getParty().getId().equals(timerDanmuPathModel.getPartyId())) {
                                    partyResourceModel.setPathList(timerDanmuPathModel.getPathList());
                                }

                            }
                        }
                        partyResourceModelList.add(partyResourceModel);
                    }
                }
                resourceFileMap.put("partyResourceModelList", partyResourceModelList);

                List<ResourceFile> adExpressionList = new ArrayList<>();
                List<ResourceFile> specialImageList = new ArrayList<>();
                List<ResourceFile> specialVideoList = new ArrayList<>();
                if(adTimerFileResource!=null){
                    List<ResourceFile> resourceFiles = adTimerFileResource.getResourceFileList();
                    if(ListUtils.checkListIsNotNull(resourceFiles)){
                        for (ResourceFile resourceFile : resourceFiles) {
                            File file = new File(configUtils.rsyncSaveFilePath()+"/upload"+resourceFile.getFileUrl());
                            if( file.exists()){
                                resourceFile.setLocalFilePath(configUtils.saveFilePath+"/upload"+resourceFile.getFileUrl());
                                if (Const.RESOURCE_EXPRESSIONS == resourceFile.getFileType() || Const.RESOURCE_EXPRESSIONS_CONSTANT == resourceFile.getFileType()) {
                                    adExpressionList.add(resourceFile);
                                } else if (Const.RESOURCE_SPECIAL_IMAGES == resourceFile.getFileType()) {
                                    specialImageList.add(resourceFile);
                                } else if (Const.RESOURCE_SPECIAL_VIDEOS == resourceFile.getFileType()) {
                                    specialVideoList.add(resourceFile);
                                }
                            }
                        }
                    }
                }
                resourceFileMap.put("adExpressionList", adExpressionList);

                resourceFileMap.put("adSpecialEffectList", specialImageList);

                resourceFileMap.put("adVideoUrlList", specialVideoList);

                Map paramMap = null;

                if(!StringUtils.isEmpty(paramJsonStr)){
                    JSONObject jsonObject = JSON.parseObject(paramJsonStr);
                    String dataStr = (String)jsonObject.get("data");
                    Map<String,Object> temp = (Map) JSON.parse(dataStr);
                    paramMap = new LinkedHashMap<>();
                    if(!StringUtils.isEmpty(dataStr)){
                        String[] dataStrs = dataStr.substring(1,dataStr.length()-1).split(",");
                        for(int i=0;i<dataStrs.length;i++){
                            String[] jsons = dataStrs[i].replaceAll("\"","").split(":");
                            String key = jsons[0];
                            Object obj = temp.get(key);
                            paramMap.put(jsons[0],obj);
                        }
                    }

                }
                createConfigFile(resourceFileMap,paramMap);
            }

        }

    }


    private void createConfigFile(Map<String, Object> model,Map<String,Object> paramMap) {
        File saveDir = new File(configUtils.findFlashProgramPath());
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        File file = new File(configUtils.findFlashProgramPath() + File.separator + "config");
        JSONObject jsonObject = jsonObject = new JSONObject(true);
        jsonObject.put("result",200);
        //放入服务器端自定义配置表
        if( null != paramMap){
            Iterator<String> iter = paramMap.keySet().iterator();
            while(iter.hasNext()){
                String key = iter.next();
                jsonObject.put(key,paramMap.get(key));
            }
        }

        Object object = model.get("partyResourceModelList");

        if( null != object){
            List<PartyResourceModel> partyResourceModelList = (List<PartyResourceModel>)object;
            List<PartyJson> partyJsonList = new ArrayList<>();
            for(PartyResourceModel partyResourceModel : partyResourceModelList){
                PartyJson partyJson = new PartyJson();
                partyJson.setName(partyResourceModel.getParty().getName());
                partyJson.setPartyId(partyResourceModel.getParty().getId());
                partyJson.setMovieAlias(partyResourceModel.getParty().getMovieAlias());
                List<ResourceFile> expressionList = partyResourceModel.getBigExpressionList();

                if( null != expressionList && expressionList.size() > 0){
                    List<Resource> resourceList = new ArrayList<>();
                    for(ResourceFile resourceFile : expressionList){
                        Resource resource = new Resource();
                        resource.setId(resourceFile.getId());
                        resource.setUrl(resourceFile.getLocalFilePath());
                        resourceList.add(resource);
                    }
                    partyJson.setExpressions(resourceList);
                }else{
                    partyJson.setExpressions(new ArrayList<>());
                }

                List<ResourceFile> specialImageList= partyResourceModel.getSpecialImageList();
                if( null != specialImageList && specialImageList.size() > 0){
                    List<Resource> resourceList = new ArrayList<>();
                    for(ResourceFile resourceFile : specialImageList){
                        Resource resource = new Resource();
                        resource.setId(resourceFile.getId());
                        resource.setUrl(resourceFile.getLocalFilePath());
                        resourceList.add(resource);
                    }
                    partyJson.setSpecialEffects(resourceList);
                }else{
                    partyJson.setSpecialEffects(new ArrayList<>());
                }

                List<ResourceFile> specialVideoList = partyResourceModel.getSpecialVideoList();

                if(partyResourceModel.getAdTimerDanmuPath()!=null){
                    partyJson.setAdTimerDanmuUrl(partyResourceModel.getAdTimerDanmuPath());
                }


                if( null != specialVideoList && specialVideoList.size() > 0){
                    List<VideoResourceJson> resourceList = new ArrayList<>();
                    for(ResourceFile resourceFile : specialVideoList){
                        VideoResourceJson videoResourceJson = new VideoResourceJson();
                        videoResourceJson.setId(resourceFile.getId());
                        videoResourceJson.setUrl(resourceFile.getLocalFilePath());
                        videoResourceJson.setType("click");
                        resourceList.add(videoResourceJson);
                    }
                    partyJson.setLocalVideoUrl(resourceList);

                }else{
                    partyJson.setLocalVideoUrl(new ArrayList<>());
                }

                List<String> pathList = partyResourceModel.getPathList();

                if( null != pathList && pathList.size() > 0){
                    List<Resource> resourceList = new ArrayList<>();
                    for(String url : pathList){
                        Resource resource = new Resource();
                        resource.setUrl(url);
                        resourceList.add(resource);
                    }
                    partyJson.setTimerDanmuUrl(resourceList);

                }else{
                    partyJson.setTimerDanmuUrl(new ArrayList<>());
                }
                partyJsonList.add(partyJson);

            }
            jsonObject.put("partys",partyJsonList);
        }

        Object objectAdExpression = model.get("adExpressionList");
        Object objectAdSpecialEffects = model.get("adSpecialEffectList");
        Object objectAdVideoUrl = model.get("adVideoUrlList");
        List<Resource> adExpressionList = new ArrayList<>();
        if(objectAdExpression!=null){
            List<ResourceFile> expressionList = (List<ResourceFile>)objectAdExpression;
            if(ListUtils.checkListIsNotNull(expressionList)){
                for(ResourceFile resourceFile : expressionList){
                    Resource resource = new Resource();
                    resource.setId(resourceFile.getId());
                    resource.setUrl(resourceFile.getLocalFilePath());
                    adExpressionList.add(resource);
                }
            }
        }
        jsonObject.put("adExpressions",adExpressionList);

        List<Resource> adSpecialEffectList = new ArrayList<>();
        if(objectAdSpecialEffects!=null){
            List<ResourceFile> specialEffectsList = (List<ResourceFile>)objectAdSpecialEffects;
            if(ListUtils.checkListIsNotNull(specialEffectsList)){

                for(ResourceFile resourceFile : specialEffectsList){
                    Resource resource = new Resource();
                    resource.setId(resourceFile.getId());
                    resource.setUrl(resourceFile.getLocalFilePath());
                    adSpecialEffectList.add(resource);
                }
            }
        }
        jsonObject.put("adSpecialEffects",adSpecialEffectList);

        List<VideoResourceJson> adVideoList = new ArrayList<>();
        if(objectAdVideoUrl!=null){
            List<ResourceFile> specialEffectsList = (List<ResourceFile>)objectAdVideoUrl;
            if(ListUtils.checkListIsNotNull(specialEffectsList)){
                for(ResourceFile resourceFile : specialEffectsList){
                    VideoResourceJson videoResourceJson = new VideoResourceJson();
                    videoResourceJson.setId(resourceFile.getId());
                    videoResourceJson.setUrl(resourceFile.getLocalFilePath());
                    videoResourceJson.setType("click");
                    adVideoList.add(videoResourceJson);
                }
            }
        }
        jsonObject.put("adVideoUrl",adVideoList);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(JSON.toJSONString(jsonObject).getBytes());
        }catch (FileNotFoundException e) {
            log.error("", e);
        } catch (IOException e) {
            log.error("", e);
        }

        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                log.error("", e);
            }
        }


    }



    private TimerDanmuFileConfig initTimerDanmuFileConfig(){
        String jsonStr = HttpUtils.httpRequestStr(configUtils.getTimerDanmuNetUrl()+"?addressId="+properties.getAddressId(), "GET", null);
        TimerDanmuFileConfig timerDanmuFileConfig = JSON.parseObject(jsonStr, TimerDanmuFileConfig.class);
        return timerDanmuFileConfig;
    }


    /**
     * 获取广告弹幕
     * @return
     */
    public AdTimerFileResource findAdTimerDanmu () {
        String jsonStr = HttpUtils.httpRequestStr(configUtils.getAdTimerDanmuNetUrl()+"?addressId="+properties.getAddressId(), "GET", null);
        AdTimerFileConfig adTimerFileConfig = JSON.parseObject(jsonStr, AdTimerFileConfig.class);
        AdTimerFileResource adTimerFileResource = adTimerFileConfig.getData();
        return adTimerFileResource;

    }

    public List<TimerDanmuPathModel> findTimerDanmuFile () {
        log.info("exectue download timerdanmu command:");
        TimerDanmuFileConfig timerDanmuFileConfig = initTimerDanmuFileConfig();
        if( null == timerDanmuFileConfig){
            return null;
        }
        List<TimerDanmuFileModel> timerDanmuFileConfigList = timerDanmuFileConfig.getData();
        Map<String,List<TimerDanmuFileModel>> map = new HashMap<String,List<TimerDanmuFileModel>>();

        List<TimerDanmuPathModel> timerDanmuPathModels = new ArrayList<>();
        if (ListUtils.checkListIsNotNull(timerDanmuFileConfigList)) {
            //timerDanmuPathMap = new HashMap<String,List<String>>();
            for (TimerDanmuFileModel timerDanmuFileModel : timerDanmuFileConfigList) {
                //String fileName = getFileName(timerDanmuFileModel.getPath());
                String partyId = timerDanmuFileModel.getPartyId();
                if (map.containsKey(partyId)) {
                    List<TimerDanmuFileModel> timerDanmuFileModels = map.get(partyId);
                    timerDanmuFileModels.add(timerDanmuFileModel);
                    map.put(partyId, timerDanmuFileModels);
                } else {
                    List<TimerDanmuFileModel> timerDanmuFileModels = new ArrayList<TimerDanmuFileModel>();
                    timerDanmuFileModels.add(timerDanmuFileModel);
                    map.put(partyId, timerDanmuFileModels);
                }
            }
            for (Map.Entry<String, List<TimerDanmuFileModel>> entry : map.entrySet()) {
                TimerDanmuPathModel timerDanmuPathModel = new TimerDanmuPathModel();
                List<TimerDanmuFileModel> pathList = entry.getValue();
                String key = entry.getKey();
                List<String> nameList = new ArrayList<String>();
                for (TimerDanmuFileModel timerDanmuFileModel : pathList) {
                    String path = timerDanmuFileModel.getPath();
                    String realFilePath = configUtils.realSaveTimerFilePath() +path;
                    File file = new File(realFilePath);
                    if(file.exists()){
                        nameList.add(configUtils.saveFilePath+"/timerDanmu"+path);
                    }
                }
                timerDanmuPathModel.setPartyId(key);
                timerDanmuPathModel.setPathList(nameList);
                timerDanmuPathModels.add(timerDanmuPathModel);

            }
            log.info(JSON.toJSONString(timerDanmuPathModels));
            return timerDanmuPathModels;

        }
        return null;
    }
}
