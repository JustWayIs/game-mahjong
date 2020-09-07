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
    private int card;
    private int operationType;

    public int getCard() {
        return card;
    }

    public OperationCardRequest setCard(int card) {
        this.card = card;
        return this;
    }

    public int getOperationType() {
        return operationType;
    }

    public OperationCardRequest setOperationType(int operationType) {
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
