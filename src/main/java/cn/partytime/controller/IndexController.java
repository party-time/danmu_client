package cn.partytime.controller;

import cn.partytime.config.ClientCache;
import cn.partytime.config.ClientPartyCache;
import cn.partytime.config.ConfigUtils;
import cn.partytime.json.PartyResourceResult;
import cn.partytime.model.ConfigModel;
import cn.partytime.model.DanmuAddressModel;
import cn.partytime.model.DownloadFileConfig;
import cn.partytime.model.Party;
import cn.partytime.model.common.RestResultModel;
import cn.partytime.model.result.Result;
import cn.partytime.model.result.ResultEnum;
import cn.partytime.service.TmsCommandService;
import cn.partytime.util.*;
import com.alibaba.fastjson.JSON;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Controller
public class IndexController {

    @Autowired
    private ConfigUtils configUtils;


    @Autowired
    private TmsCommandService tmsCommandService;


    @Autowired
    private ClientPartyCache clientPartyCache;

    @RequestMapping("/")
    public String index(Map<String,Object> model,HttpServletResponse response,@CookieValue(value = "command",required=false) String command){

        Integer time = clientPartyCache.getAdTime();

        String content = FileUtils.txt2String(configUtils.findFlashProgramPath() + File.separator + "config");
        List<Party> partyList = new ArrayList<Party>();
        if(!StringUtils.isEmpty(content)) {
            ConfigModel configModel = JSON.parseObject(content, ConfigModel.class);
            List<Party> parties = configModel.getPartys();
            if (ListUtils.checkListIsNotNull(parties)) {
                List<Party> tempPartyList = new ArrayList<Party>();
                for(Party party :parties){
                    if(party.getMovieAlias()!=null){
                        tempPartyList.add(party);
                    }
                }
                model.put("partyList", tempPartyList);
            }
        }


        if(time==null || time==0){
            String url = configUtils.findAddressInfo();
            String result  = HttpUtils.httpRequestStr(url,"GET",null);
            if(!StringUtils.isEmpty(result)){
                RestResultModel restResultModel = JSON.parseObject(result,RestResultModel.class);
                if(restResultModel.getResult()==200){
                    String addressInfoStr = String.valueOf(restResultModel.getData());
                    DanmuAddressModel danmuAddressModel = JSON.parseObject(addressInfoStr,DanmuAddressModel.class);
                    if(danmuAddressModel!=null){
                        //time = (danmuAddressModel.getAdTime() ==null?0:danmuAddressModel.getAdTime());
                        //clientPartyCache.setAdTime(time);
                        if(danmuAddressModel.getAdTime()==null){
                            time = 0;
                        }else{
                            time = danmuAddressModel.getAdTime();
                            clientPartyCache.setAdTime(time);
                        }
                    }
                }
            }
        }

        model.put("command", command==null?"":command);
        model.put("minute", String.valueOf(time / 60));
        model.put("second", String.valueOf(time - (time / 60)*60));
        return "page/index";
    }

    @RequestMapping("/setAdTime")
    @ResponseBody
    public Result setAdTime(HttpServletRequest request, HttpServletResponse response){
        String timeStr  = request.getParameter("time");
        int time = 0;
        if(StringUtils.isEmpty(timeStr)){
            return new Result(501,null);
        }
        time = Integer.parseInt(timeStr);

        String url = configUtils.updateAdTime(time);
        String result = HttpUtils.httpRequestStr(url,"GET",null);
        if(StringUtils.isEmpty(result)){
            return new Result(502,null);
        }
        //将广告时间缓存起来
        clientPartyCache.setAdTime(time);

        //在cookie中缓存电影指令
        addCookie(response,"time", String.valueOf(time), 60*60*24*30);
        return new Result(200,null);
    }

    @RequestMapping("/movieStart")
    @ResponseBody
    public Result movieStart(HttpServletRequest request, HttpServletResponse response){
        String command = request.getParameter("command");
        String timeStr = request.getParameter("time");

        int time = 0;
        if(StringUtils.isEmpty(timeStr)){
            //从服务器获取地址信息
            String url = configUtils.findAddressInfo();
            String result  = HttpUtils.httpRequestStr(url,"GET",null);
            DanmuAddressModel danmuAddressModel = JSON.parseObject(result,DanmuAddressModel.class);
            if(danmuAddressModel!=null){
                time = danmuAddressModel.getAdTime();
            }
        }else{
            time = Integer.parseInt(timeStr);
        }
        if(time==0){
            //提示操作人员没有设置广告时间
            //TODO:
            //同时向服务器发出告警
            return new Result(501,null);
        }

        Date date  = DateUtils.getCurrentDate();
        String result = tmsCommandService.movieHandler(command,date);
        if(StringUtils.isEmpty(result)){
            return new Result(503,null);
        }
        RestResultModel restResultModel = JSON.parseObject(result,RestResultModel.class);
        if(restResultModel!=null){
            if(restResultModel.getResult()==200){
                clientPartyCache.setDanmuStartDate(date.getTime());
                clientPartyCache.setAdTime(time);
                clientPartyCache.setBooleanMovieStart(false);
            }else{
                return new Result(504,null);
            }
        }else{
            return new Result(503,null);
        }
        log.info("request command:{}",command);
        //在cookie中缓存电影指令
        addCookie(response,"command", command, 60*60*24*30);
        return new Result(200,null);
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge){
        Cookie cookie = new Cookie(name,value);
        cookie.setPath("/");
        if(maxAge>0)  cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
}