package com.yude.game.xuezhan.application.request;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.yude.protocol.common.request.AbstractRequest;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/26 20:32
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class ExchangeCardRequest extends AbstractRequest {
    private List<Integer> discardCardList;

    public List<Integer> getDiscardCardList() {
        return discardCardList;
    }

    public ExchangeCardRequest setDiscardCardList(List<Integer> discardCardList) {
        this.discardCardList = discardCardList;
        return this;
    }

    @Override
    public String toString() {
        return "ExchangeCardRequest{" +
                "discardCardList=" + discardCardList +
                ", channelUserId=" + channelUserId +
                ", messageType=" + messageType +
                '}';
    }
}
