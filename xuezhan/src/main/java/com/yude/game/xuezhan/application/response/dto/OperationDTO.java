package com.yude.game.xuezhan.application.response.dto;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.yude.game.xuezhan.constant.XueZhanMahjongOperationEnum;

/**
 * @Author: HH
 * @Date: 2020/9/3 11:27
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class OperationDTO {
    private Integer opreation;
    private Integer targetCard;

    public OperationDTO() {
    }

    public OperationDTO(Integer opreation, Integer targetCard) {
        this.opreation = opreation;
        this.targetCard = targetCard;
    }

    public Integer getOpreation() {
        return opreation;
    }

    public OperationDTO setOpreation(Integer opreation) {
        this.opreation = opreation;
        return this;
    }

    public Integer getTargetCard() {
        return targetCard;
    }

    public OperationDTO setTargetCard(Integer targetCard) {
        this.targetCard = targetCard;
        return this;
    }


    @Override
    public String toString() {
        return "OperationDTO{" +
                "opreation=" + XueZhanMahjongOperationEnum.matchByValue(opreation) +
                ", targetCard=" + targetCard +
                '}';
    }
}
