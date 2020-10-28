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
    /**
     * 位置
     */
    private Integer posId;
    /**
     * 结算之前的分
     */
    private Long beforeScore;
    /**
     * 结算变化分
     */
    private int changeScore;
    /**
     * 结算后剩余分
     */
    private Long remaningScore;
    /**
     * 番数
     */
    private int fanNum;
    /**
     * 立牌
     */
    private List<Integer> standCards;
    /**
     * 立牌翻译解析
     */
    private List<String> standCardsConvertList;
    /**
     * 副露
     */
    private List<StepAction> fuluList;
    /**
     * 结算涉及的 番信息
     */
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

    public int getChangeScore() {
        return changeScore;
    }

    public SettlementInfo setChangeScore(int changeScore) {
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
