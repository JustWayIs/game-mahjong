package com.yude.game.xuezhan.application.response.dto;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

import java.util.List;

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
    private List<Integer> fanIdList;
    private Integer targetPosId;

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


    public List<Integer> getFanIdList() {
        return fanIdList;
    }

    public SettlementDetailInfoDTO setFanIdList(List<Integer> fanIdList) {
        this.fanIdList = fanIdList;
        return this;
    }

    public Integer getTargetPosId() {
        return targetPosId;
    }

    public SettlementDetailInfoDTO setTargetPosId(Integer targetPosId) {
        this.targetPosId = targetPosId;
        return this;
    }

    @Override
    public String toString() {
        return "SettlementDetailInfoDTO{" +
                "fanNum=" + fanNum +
                ", fanScore=" + fanScore +
                ", fanIdList=" + fanIdList +
                ", targetPosId=" + targetPosId +
                '}';
    }
}
