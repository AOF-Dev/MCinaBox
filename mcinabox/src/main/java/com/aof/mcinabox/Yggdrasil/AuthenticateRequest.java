package com.aof.mcinabox.Yggdrasil;

import android.bluetooth.le.AdvertisingSetCallback;

public class AuthenticateRequest {
    private Agent agent;
    private String username;  //Mojang账户用户名
    private String password;  //Mojang账户密码
    private String clientToken;  //启动器端标识符(可选的) [请注意，请在第一次运行启动器时生成一个随机UUID.toSring,存储在配置文件中并不要再做任何更改]
    private boolean requestUser;  // 默认为false，若为true则将user对象加入到响应中(可选的)

    public AuthenticateRequest(String name,int version,String username,String password,String clientToken,boolean requestUser){
        super();
        getAgent().setName(name);
        getAgent().setVersion(version);
        setUsername(username);
        setPassword(password);
        setClientToken(clientToken);
        setRequestUser(requestUser);
    }

    private class Agent{
        String name;  //默认为"Minecraft"
        int version;   //请填写 1

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getVersion() { return version; }
        public void setVersion(int version) { this.version = version; }
    }

    public Agent getAgent() { return agent; }
    public void setAgent(Agent agent) { this.agent = agent; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getClientToken() { return clientToken; }
    public void setClientToken(String clientToken) { this.clientToken = clientToken; }
    public boolean isRequestUser() { return requestUser; }
    public void setRequestUser(boolean requestUser) { this.requestUser = requestUser; }
}
