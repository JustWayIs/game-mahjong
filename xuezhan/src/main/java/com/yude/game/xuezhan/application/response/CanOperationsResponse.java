package com.yude.game.xuezhan.application.response;

import com.yude.protocol.common.response.BaseResponse;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/31 20:43
 * @Version: 1.0
 * @Declare:
 */
public class CanOperationsResponse extends BaseResponse {
    private Integer operationPosId;
    private Integer operationCard;
    private List<Integer> operations;

    public CanOperationsResponse() {
    }

    public CanOperationsResponse(Integer operationPosId, Integer operationCard, List<Integer> operations) {
        this.operationPosId = operationPosId;
        this.operationCard = operationCard;
        this.operations = operations;
    }

    public Integer getOperationPosId() {


        return operationPosId;
    }

    public CanOperationsResponse setOperationPosId(Integer operationPosId) {
        this.operationPosId = operationPosId;
        return this;
    }

    public Integer getOperationCard() {
        return operationCard;
    }

    public CanOperationsResponse setOperationCard(Integer operationCard) {
        this.operationCard = operationCard;
        return this;
    }

    public List<Integer> getOperations() {
        return operations;
    }

    public CanOperationsResponse setOperations(List<Integer> operations) {
        this.operations = operations;
        return this;
    }
}
