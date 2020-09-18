package com.yude.game.xuezhan.application.response.dto;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

/**
 * @Author: HH
 * @Date: 2020/9/16 18:52
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class TingInfoDTO {
    private Integer card;
    private Integer fanNum;
    private Integer remainingSize;

    public Integer getCard() {
        return card;
    }

    public TingInfoDTO setCard(Integer card) {
        this.card = card;
        return this;
    }

    public Integer getFanNum() {
        return fanNum;
    }

    public TingInfoDTO setFanNum(Integer fanNum) {
        this.fanNum = fanNum;
        return this;
    }

    public Integer getRemainingSize() {
        return remainingSize;
    }

    public TingInfoDTO setRemainingSize(Integer remainingSize) {
        this.remainingSize = remainingSize;
        return this;
    }

    @Override
    public String toString() {
        return "TingInfoDTO{" +
                "card=" + card +
                ", fanNum=" + fanNum +
                ", remainingSize=" + remainingSize +
                '}';
    }
}
