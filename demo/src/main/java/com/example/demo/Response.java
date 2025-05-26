package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response <T>{

    private String code;
    private String message;
    private T data;

    public static <T> Response<T> success(T data){
        Response<T> response = new Response<>();
        response.setCode("200");
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    public static <T>  Response<T> success(){
        Response<T> response = new Response<>();
        response.setCode("200");
        response.setMessage("succes");
        response.setData(null);
        return response;
    }

    public static <T> Response<T> error(){
        Response<T> response = new Response<>();
        response.setCode("500");
        response.setMessage("error");
        response.setData(null);
        return response;
    }

    public static<T> Response<T> error(String message){
        Response<T> response = new Response<>();
        response.setCode("500");
        response.setMessage(message);
        response.setData(null);
        return response;
    }
}


