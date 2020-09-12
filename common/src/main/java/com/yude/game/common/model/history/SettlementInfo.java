package com.yude.game.common.model.history;

import com.yude.game.common.model.StepAction;
import com.yude.game.common.model.fan.FanInfo;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/9 15:08
 * @Version: 1.0
 * @Declare:
 */
public class SettlementInfo {
    private Integer posId;
    private Long beforeScore;
    private Long changeScore;
    private Long remaningScore;
    private int fanNum;
    private List<Integer> standCards;
    private List<String> standCardsConvertList;
    private List<StepAction> fuluList;
    private List<FanInfo> fanInfoList;

    public Integer getPosId() {
        return posId;
    }

    public SettlementInfo setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }

    public Long getBeforeScore() {
        return beforeScore;
    }

    public SettlementInfo setBeforeScore(Long beforeScore) {
        this.beforeScore = beforeScore;
        return this;
    }

    public Long getChangeScore() {
        return changeScore;
    }

    public SettlementInfo setChangeScore(Long changeScore) {
        this.changeScore = changeScore;
        return this;
    }

    public Long getRemaningScore() {
        return remaningScore;
    }

    public SettlementInfo setRemaningScore(Long remaningScore) {
        this.remaningScore = remaningScore;
        return this;
    }

    public List<Integer> getStandCards() {
        return standCards;
    }

    public SettlementInfo setStandCards(List<Integer> standCards) {
        this.standCards = standCards;
        return this;
    }

    public List<String> getStandCardsConvertList() {
        return standCardsConvertList;
    }

    public SettlementInfo setStandCardsConvertList(List<String> standCardsConvertList) {
        this.standCardsConvertList = standCardsConvertList;
        return this;
    }

    public List<StepAction> getFuluList() {
        return fuluList;
    }

    public SettlementInfo setFuluList(List<StepAction> fuluList) {
        this.fuluList = fuluList;
        return this;
    }

    public List<FanInfo> getFanInfoList() {
        return fanInfoList;
    }

    public SettlementInfo setFanInfoList(List<FanInfo> fanInfoList) {
        this.fanInfoList = fanInfoList;
        return this;
    }

    public int getFanNum() {
        return fanNum;
    }

    public SettlementInfo setFanNum(int fanNum) {
        this.fanNum = fanNum;
        return this;
    }

    @Override
    public String toString() {
        return "SettlementInfo{" +
                "posId=" + posId +
                ", beforeScore=" + beforeScore +
                ", changeScore=" + changeScore +
                ", remaningScore=" + remaningScore +
                ", fanNum=" + fanNum +
                ", standCards=" + standCards +
                ", standCardsConvertList=" + standCardsConvertList +
                ", fuluList=" + fuluList +
                ", fanInfoList=" + fanInfoList +
                '}';
    }
}
