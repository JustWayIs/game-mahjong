package com.yude.game.xuezhan.application.request;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.yude.protocol.common.request.AbstractRequest;

/**
 * @Author: HH
 * @Date: 2020/9/3 11:09
 * @Version: 1.0
 * @Declare:
 */
@EnableZigZap
@ProtobufClass
public class OperationCardRequest extends AbstractRequest {
    private Integer card;
    private Integer operationType;

    public Integer getCard() {
        return card;
    }

    public void setCard(Integer card) {
        this.card = card;
    }

    public Integer getOperationType() {
        return operationType;
    }

    public OperationCardRequest setOperationType(Integer operationType) {
        this.operationType = operationType;
        return this;
    }

    @Override
    public String toString() {
        return "OperationCardRequest{" +
                "card=" + card +
                ", operationType=" + operationType +
                ", channelUserId=" + channelUserId +
                ", messageType=" + messageType +
                '}';
    }
}
