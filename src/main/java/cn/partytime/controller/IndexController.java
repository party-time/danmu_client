package cn.partytime.controller;

import cn.partytime.config.ClientCache;
import cn.partytime.config.ClientPartyCache;
import cn.partytime.config.ConfigUtils;
import cn.partytime.json.PartyResourceResult;
import cn.partytime.model.*;
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

    //场地
    @Value("${addressId}")
    private String addressId;


    @RequestMapping("/")
    public String index(Map<String,Object> model,HttpServletResponse response,@CookieValue(value = "command",required=false) String command){

        Integer time = clientPartyCache.getAdTime();

        Long danmuStartDate = clientPartyCache.getDanmuStartDate();

        String content = FileUtils.txt2String(configUtils.findFlashProgramPath() + File.separator + "config");

        boolean isNetException = false;
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
        }else{
            isNetException = true;
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
            }else{
                time = 0;
                isNetException = true;
            }
        }

        if(danmuStartDate==null){
            //从服务器获取弹幕开始时间
            System.out.println("从缓存中取出的电影开始时间为0");
            String url = configUtils.findCurrentMovie(addressId);
            String resultStr  = HttpUtils.httpRequestStr(url,"GET",null);
            if(!StringUtils.isEmpty(resultStr)){
                RestResultModel restResultModel = JSON.parseObject(resultStr,RestResultModel.class);
                if(restResultModel.getResult()==200){
                    String movieScheduleModelStr = String.valueOf(restResultModel.getData());
                    MovieScheduleModel movieScheduleModel = JSON.parseObject(movieScheduleModelStr,MovieScheduleModel.class);
                    if(movieScheduleModel!=null){
                        if(movieScheduleModel.getClientStartTime()!=null){
                            clientPartyCache.setDanmuStartDate(movieScheduleModel.getClientStartTime());
                        }
                    }
                }
            }else{
                isNetException = true;
            }
        }
        model.put("isNetException",isNetException);
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

        log.info("弹幕开始时间：{}",DateUtils.dateToString(date,"yyyy-MM-dd hh:mm:ss"));
        if(StringUtils.isEmpty(result)){
            return new Result(503,null);
        }
        RestResultModel restResultModel = JSON.parseObject(result,RestResultModel.class);
        if(restResultModel!=null){
            if(restResultModel.getResult()==200){
                clientPartyCache.setDanmuStartDate(date.getTime());
                clientPartyCache.setAdTime(time);
                clientPartyCache.setBooleanMovieStart(false);
            }else if(restResultModel.getResult()==201){
                ;
                Date danmuStart =  DateUtils.transferLongToDate(clientPartyCache.getDanmuStartDate());
                long seconds = DateUtils.subSecond(danmuStart,new Date());
                long subSecond = 600 - seconds;

                if(subSecond > 0){
                    StringBuffer messsage = new StringBuffer("请");
                    long minute = subSecond / 60;
                    long second = 0;
                    if(minute>0){
                        messsage.append(minute+"分");
                        second =subSecond - minute * 60;
                        messsage.append(second+"秒");
                    }else{
                        messsage.append(subSecond+"秒");
                    }
                    messsage.append("后重新尝试!");
                    return new Result(201,messsage.toString(),null);
                }
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