package cn.partytime.model.result;

/**
 * Created by admin on 2018/4/4.
 */
public class Result {

    private String message;
    private int code;
    private Object data;

    public Result( int code, Object data) {
        this.message = ResultEnum.getMessage(code);
        this.code = code;
        this.data = data;
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
