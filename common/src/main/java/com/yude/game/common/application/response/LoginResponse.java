package com.yude.game.common.application.response;

import com.yude.protocol.common.constant.StatusCodeEnum;
import com.yude.protocol.common.response.BaseResponse;
import com.yude.protocol.common.response.CommonResponse;

/**
 * @Author: HH
 * @Date: 2020/6/22 20:14
 * @Version: 1.0
 * @Declare:
 */

public class LoginResponse extends BaseResponse {
    /**
     * 如果是Controllerd的方法里面直接返回的Response对象就需要组合CommonResponse
     */
    private CommonResponse commonResponse;
    private String sessionId;

    private Integer status;
    /**
     * 有值，就说明该用户在游戏中，需要走断线重连
     */
    private Long roomId;



    public LoginResponse() {
    }

    public LoginResponse(String sessionId, Integer status){
        this.sessionId = sessionId;
        this.status = status;
        commonResponse = new CommonResponse(StatusCodeEnum.SUCCESS);
    }

    public LoginResponse(String sessionId, Integer status, Long roomId){
        this.sessionId = sessionId;
        this.status = status;
        this.roomId = roomId;
        commonResponse = new CommonResponse(StatusCodeEnum.SUCCESS);
    }

    public LoginResponse(StatusCodeEnum status, String sessionId) {
        super();
        this.commonResponse = new CommonResponse(status);
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public LoginResponse setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public LoginResponse setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public Long getRoomId() {
        return roomId;
    }

    public LoginResponse setRoomId(Long roomId) {
        this.roomId = roomId;
        return this;
    }

    public CommonResponse getCommonResponse() {
        return commonResponse;
    }

    public void setCommonResponse(CommonResponse commonResponse) {
        this.commonResponse = commonResponse;
    }



    public int getCode(){
        return commonResponse.getCode();
    }

    public void setCode(int code){
        commonResponse.setCode(code);
    }

    public String getMsg(){
        return commonResponse.getMsg();
    }

    public void setMsg(String massage){
        commonResponse.setMsg(massage);
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "commonResponse=" + commonResponse +
                ", sessionId='" + sessionId + '\'' +
                ", status=" + status +
                ", roomId=" + roomId +
                '}';
    }

    @Override
    public boolean isSuccess() {
        return commonResponse.isSuccess();
    }
}
