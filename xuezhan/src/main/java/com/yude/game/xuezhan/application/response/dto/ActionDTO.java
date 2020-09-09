package com.yude.game.xuezhan.application.response.dto;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

/**
 * @Author: HH
 * @Date: 2020/9/9 19:28
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class ActionDTO {
    private Integer operationType;
    private Integer targetCard;

    public Integer getOperationType() {
        return operationType;
    }

    public ActionDTO setOperationType(Integer operationType) {
        this.operationType = operationType;
        return this;
    }

    public Integer getTargetCard() {
        return targetCard;
    }

    public ActionDTO setTargetCard(Integer targetCard) {
        this.targetCard = targetCard;
        return this;
    }

    @Override
    public String toString() {
        return "ActionDTO{" +
                "operationType=" + operationType +
                ", targetCard=" + targetCard +
                '}';
    }
}
