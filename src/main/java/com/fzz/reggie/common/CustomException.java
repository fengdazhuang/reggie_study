package com.fzz.reggie.common;

public class CustomException extends RuntimeException{

    //重写
    public CustomException(String message){
        super(message);
    }
}
