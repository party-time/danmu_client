package cn.partytime.config;

import cn.partytime.model.PartyResourceModel;
import org.springframework.stereotype.Component;

/**
 * Created by admin on 2018/5/30.
 */

@Component
public class FlashCache {

    /**FLASH 打开次数*/
    public int sendFlashOpenCount= 0;

    public int getSendFlashOpenCount() {
        return sendFlashOpenCount;
    }

    public void setSendFlashOpenCount(int sendFlashOpenCount) {
        this.sendFlashOpenCount = sendFlashOpenCount;
    }
}
