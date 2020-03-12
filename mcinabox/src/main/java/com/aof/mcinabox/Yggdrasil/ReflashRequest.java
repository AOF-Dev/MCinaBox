package com.aof.mcinabox.Yggdrasil;

public class ReflashRequest {
    private String accessToken;  //有效的accessToken
    private String clientToken;  //客户端标识符
    boolean requestUser;  //默认为false，为true时将响应user信息(可选的)

    public ReflashRequest(String accessToken,String clientToken,boolean requestUser){
        super();
        setAccessToken(accessToken);
        setClientToken(clientToken);
        setRequestUser(requestUser);
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getClientToken() { return clientToken; }
    public void setClientToken(String clientToken) { this.clientToken = clientToken; }
    public boolean isRequestUser() { return requestUser; }
    public void setRequestUser(boolean requestUser) { this.requestUser = requestUser; }
}
