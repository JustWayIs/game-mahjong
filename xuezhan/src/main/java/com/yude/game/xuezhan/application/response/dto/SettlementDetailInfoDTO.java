package com.yude.game.xuezhan.application.response.dto;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

/**
 * @Author: HH
 * @Date: 2020/9/14 19:45
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class SettlementDetailInfoDTO {
    private Integer fanNum;
    private Integer fanScore;
    private Integer actionType;
    private Integer fanId;

    public Integer getFanNum() {
        return fanNum;
    }

    public SettlementDetailInfoDTO setFanNum(Integer fanNum) {
        this.fanNum = fanNum;
        return this;
    }

    public Integer getFanScore() {
        return fanScore;
    }

    public SettlementDetailInfoDTO setFanScore(Integer fanScore) {
        this.fanScore = fanScore;
        return this;
    }

    public Integer getActionType() {
        return actionType;
    }

    public SettlementDetailInfoDTO setActionType(Integer actionType) {
        this.actionType = actionType;
        return this;
    }

    public Integer getFanId() {
        return fanId;
    }

    public SettlementDetailInfoDTO setFanId(Integer fanId) {
        this.fanId = fanId;
        return this;
    }
}
