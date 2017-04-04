package cn.partytime.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2017/3/27 0027.
 */

@Service
public class TmsCommandService {

    @Autowired
    private LogLogicService logLogicService;

    @Autowired
    private ProjectorService projectorService;

    private final String PROJECTOR_START="projector-start";
    private final String PROJECTOR_CLOSE="projector-close";
    private final String DANMU_START_PREFIX="danmu-start";
    private final String MOVIE_START="movie-start";
    private final String MOVIE_CLOSE="movie-close";
    private final String AD_START_PREFIX="ad-start";
    private final String AD_CLOSE="ad-close";


    /**
     * 投影仪相关的指令
     * @param command
     */
    public void projectorHandler(String command){
        switch (command){
            case PROJECTOR_START:
                //投影仪开启
                logLogicService.logUploadHandler("投影仪开启");
                projectorService.projectorHandler(0);
                return;
            case PROJECTOR_CLOSE:
                //投影仪关闭
                logLogicService.logUploadHandler("投影仪关闭");
                projectorService.projectorHandler(1);
                return;
            default:
                return;
        }
    }

    /**
     * 电影相关的指令处理
     * @param command
     */
    public void movieHandler(String command){
        switch (command){
            case MOVIE_START:
                logLogicService.logUploadHandler("弹幕开始");

                return;
            case MOVIE_CLOSE:
                logLogicService.logUploadHandler("电影关闭");

                return;
            default:
                if(command.startsWith(DANMU_START_PREFIX)){
                    logLogicService.logUploadHandler("电影开始");
                    //TODO:弹幕开始处理
                }
                return;
        }
    }

    /**
     * 广告相关的指令处理
     */
    public void adHandler(String command){
        switch (command){
            case AD_CLOSE:
                logLogicService.logUploadHandler("广告关闭");

                return;
            default:
                if(command.startsWith(AD_START_PREFIX)){
                    //TODO:广告开始处理
                    logLogicService.logUploadHandler("广告开始");
                }
                return;
        }
    }
}
