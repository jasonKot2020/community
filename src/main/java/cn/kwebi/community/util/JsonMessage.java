package cn.kwebi.community.util;

public class JsonMessage {
    /**
     * 0000 注册成功
     * 0001 登录成功
     */
    private static final int SUCCESS_CORD = 200;
    private static final int ERROR_CORD = 502;
    private static final String SUCCESS_MESSAGE = "操作成功";
    private static final String ERROR_MESSAGE = "操作失败";

    private int code;
    private String message;
    private Object data;

    private JsonMessage(int code,String message,Object data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Object success(){
        return JsonUtil.obj2String(new JsonMessage(SUCCESS_CORD,SUCCESS_MESSAGE,null));
    }

    public static Object success(int code){
        return JsonUtil.obj2String(new JsonMessage(code,SUCCESS_MESSAGE,null));
    }

    public static Object success(Object obj){
        return JsonUtil.obj2String(new JsonMessage(SUCCESS_CORD,SUCCESS_MESSAGE,obj));
    }

    public static Object success(String message){
        return JsonUtil.obj2String(new JsonMessage(SUCCESS_CORD,message,null));
    }

    public static Object success(String message, Object obj){
        return JsonUtil.obj2String(new JsonMessage(SUCCESS_CORD,message,obj));
    }

    public static Object error(){
        return JsonUtil.obj2String(new JsonMessage(ERROR_CORD,ERROR_MESSAGE,null));
    }

    public static Object error(int code){
        return JsonUtil.obj2String(new JsonMessage(code,ERROR_MESSAGE,null));
    }

    public static Object error(Object obj){
        return JsonUtil.obj2String(new JsonMessage(ERROR_CORD,ERROR_MESSAGE,obj));
    }

    public static Object error(String message){
        return JsonUtil.obj2String(new JsonMessage(ERROR_CORD,message,null));
    }

    public static Object error(String message, Object obj){
        return JsonUtil.obj2String(new JsonMessage(ERROR_CORD,message,obj));
    }

    public static Object error(int code,String message, Object obj){
        return JsonUtil.obj2String(new JsonMessage(code,message,obj));
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
