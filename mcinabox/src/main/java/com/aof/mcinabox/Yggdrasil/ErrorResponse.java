package com.aof.mcinabox.Yggdrasil;

import java.util.HashMap;

public class ErrorResponse {
        //specification
    private String error; //错误的简短描述
    private String errorMessage; //用于向用户显示的更长的描述
    private String cause; //错误原因

        //customize
    public static HashMap<String,String> errorMap = new HashMap<String,String>();
    public static String TranslateError(String error){
        if(errorMap.size() == 0) {
            errorMap.put("Method Not Allowed", "不允许的请求");
            errorMap.put("Not Found", "不存在的端点");
            errorMap.put("ForbiddenOperationException", "无效的凭据");
            errorMap.put("IllegalArgumentException", "不合法的参数");
            errorMap.put("Unsupported Media Type", "不受支持的对象格式");
        }
        if(errorMap.get(error) == null){
            return error;
        }else {
            return errorMap.get(error);
        }
    }

}
