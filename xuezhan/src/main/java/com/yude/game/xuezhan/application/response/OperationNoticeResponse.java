package com.yude.game.xuezhan.application.response;

import com.yude.game.xuezhan.application.response.dto.OperationDTO;
import com.yude.protocol.common.response.BaseResponse;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/2 16:43
 * @Version: 1.0
 * @Declare:
 */
public class OperationNoticeResponse extends BaseResponse {
    private Integer posId;
    private List<OperationDTO> operations;
    private Integer remainingTime;

    public OperationNoticeResponse() {
    }

    public OperationNoticeResponse(Integer posId, List<OperationDTO> operations, Integer remainingTime) {
        this.posId = posId;
        this.operations = operations;
        this.remainingTime = remainingTime;
    }

    public Integer getPosId() {
        return posId;
    }

    public OperationNoticeResponse setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }

    public List<OperationDTO> getOperations() {
        return operations;
    }

    public OperationNoticeResponse setOperations(List<OperationDTO> operations) {
        this.operations = operations;
        return this;
    }

    public Integer getRemainingTime() {
        return remainingTime;
    }

    public OperationNoticeResponse setRemainingTime(Integer remainingTime) {
        this.remainingTime = remainingTime;
        return this;
    }

    @Override
    public String toString() {
        return "OperationNoticeResponse{" +
                "posId=" + posId +
                ", operations=" + operations +
                ", remainingTime=" + remainingTime +
                "} ";
    }
}
