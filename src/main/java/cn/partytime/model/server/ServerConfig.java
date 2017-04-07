package cn.partytime.model.server;

/**
 * Created by Administrator on 2017/4/7 0007.
 */
public class ServerConfig {
    private int result;
    private ServerInfo serverInfo;
    private String message;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
