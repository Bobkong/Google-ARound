package com.google.ar.core.codelabs.arlocalizer.netservice.Exception;


import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.net.ConnectException;
import java.text.ParseException;

import retrofit2.HttpException;

public class ExceptionEngine {
    //对应HTTP的状态码
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;
    //服务器约定异常
    private static final int UNKNOWN = 1000;
    private static final int PARSE_ERROR = 1001;
    private static final int NETWORD_ERROR = 1002;
    private static final int HTTP_ERROR = 1003;


    public static ApiException handleException(Throwable throwable){
        ApiException exception;
        if(throwable instanceof ServerException){//服务器端返回的错误
            ServerException serverException=(ServerException)throwable;
            exception=new ApiException(serverException,serverException.getCode());
            exception.setDisplayMessage(serverException.getMessage());
            return exception;
        }else if(throwable instanceof HttpException){ //http错误
            HttpException httpException=(HttpException)throwable;
            exception=new ApiException(httpException,HTTP_ERROR);
            switch (httpException.code()){
                case UNAUTHORIZED:
                case FORBIDDEN:
                case NOT_FOUND:
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                default:
                    exception.setDisplayMessage("Network Error");
                    break;
            }
            return exception;
        }else if(throwable instanceof JsonParseException
                ||throwable instanceof JSONException
                ||throwable instanceof ParseException){
            exception=new ApiException(throwable,PARSE_ERROR);
            exception.setDisplayMessage("Parse Error");
            return exception;
        }else if(throwable instanceof ConnectException){
            exception=new ApiException(throwable,NETWORD_ERROR);
            exception.setDisplayMessage("Connection Failed");
            return exception;
        }else{
            exception=new ApiException(throwable,UNKNOWN);
            exception.setDisplayMessage("Unknown Error");
            return exception;
        }
    }

}
