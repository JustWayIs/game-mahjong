package com.yude.game.xuezhan.application.response.dto;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/9 17:26
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class SettlementInfoDTO {
    private Integer posId;
    private long beforeScore;
    private long changeScore;
    private long remaningScore;
    private List<Integer> standCards;
    private List<ActionDTO> fuluList;
    private List<Integer> fanInfoList;

    public Integer getPosId() {
        return posId;
    }

    public SettlementInfoDTO setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }

    public long getBeforeScore() {
        return beforeScore;
    }

    public SettlementInfoDTO setBeforeScore(long beforeScore) {
        this.beforeScore = beforeScore;
        return this;
    }

    public long getChangeScore() {
        return changeScore;
    }

    public SettlementInfoDTO setChangeScore(long changeScore) {
        this.changeScore = changeScore;
        return this;
    }

    public long getRemaningScore() {
        return remaningScore;
    }

    public SettlementInfoDTO setRemaningScore(long remaningScore) {
        this.remaningScore = remaningScore;
        return this;
    }

    public List<Integer> getStandCards() {
        return standCards;
    }

    public SettlementInfoDTO setStandCards(List<Integer> standCards) {
        this.standCards = standCards;
        return this;
    }

    public List<ActionDTO> getFuluList() {
        return fuluList;
    }

    public SettlementInfoDTO setFuluList(List<ActionDTO> fuluList) {
        this.fuluList = fuluList;
        return this;
    }

    public List<Integer> getFanInfoList() {
        return fanInfoList;
    }

    public SettlementInfoDTO setFanInfoList(List<Integer> fanInfoList) {
        this.fanInfoList = fanInfoList;
        return this;
    }

    @Override
    public String toString() {
        return "SettlementInfoDTO{" +
                "posId=" + posId +
                ", beforeScore=" + beforeScore +
                ", changeScore=" + changeScore +
                ", remaningScore=" + remaningScore +
                ", standCards=" + standCards +
                ", fuluList=" + fuluList +
                ", fanInfoList=" + fanInfoList +
                '}';
    }
}
