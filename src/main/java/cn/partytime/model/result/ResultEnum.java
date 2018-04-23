package cn.partytime.model.result;

/**
 * Created by admin on 2018/4/4.
 */
public enum ResultEnum {

    OK("发送成功", 200),
    ERROR_TIME_IS_NOT_NULL("请设置广告时间", 501),
    AD_TIME_SET_ERROR_NULL("请重新设置广告时间", 502),
    MOVIE_START_ERROR_NULL("网络异常，请重试", 503),
    MOVIE_REPEAT_START_ERROR_NULL("网络异常，请重试", 504);

    private String message;
    private int code;

    private ResultEnum(String message, int code) {
        this.code = code;
        this.message = message;
    }
    // 普通方法
    public static String getMessage(int code) {
        for (ResultEnum c : ResultEnum.values()) {
            if (c.getCode() == code) {
                return c.getMessage();
            }
        }
        return null;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
