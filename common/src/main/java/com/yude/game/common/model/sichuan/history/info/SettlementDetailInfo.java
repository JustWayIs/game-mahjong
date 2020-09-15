package com.yude.game.common.model.sichuan.history.info;

import com.yude.game.common.model.StepAction;
import com.yude.game.common.model.fan.FanInfo;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/15 17:04
 * @Version: 1.0
 * @Declare:
 */
public class SettlementDetailInfo {
    private int posId;
    private List<Integer> standCardList;
    private List<String> standCardConvertList;
    private List<StepAction> actionList;
    private Integer huCard;

    /**
     * 带入分
     */
    private long carryScore;
    private long changeScore;
    private long remainingScore;

    /**
     * 知道番数后，通过房间配置的底注就可以算出 分值了
     */
    private List<FanInfo> list;

    public int getPosId() {
        return posId;
    }

    public SettlementDetailInfo setPosId(int posId) {
        this.posId = posId;
        return this;
    }

    public List<Integer> getStandCardList() {
        return standCardList;
    }

    public SettlementDetailInfo setStandCardList(List<Integer> standCardList) {
        this.standCardList = standCardList;
        return this;
    }

    public List<String> getStandCardConvertList() {
        return standCardConvertList;
    }

    public SettlementDetailInfo setStandCardConvertList(List<String> standCardConvertList) {
        this.standCardConvertList = standCardConvertList;
        return this;
    }

    public List<StepAction> getActionList() {
        return actionList;
    }

    public SettlementDetailInfo setActionList(List<StepAction> actionList) {
        this.actionList = actionList;
        return this;
    }

    public Integer getHuCard() {
        return huCard;
    }

    public SettlementDetailInfo setHuCard(Integer huCard) {
        this.huCard = huCard;
        return this;
    }

    public long getCarryScore() {
        return carryScore;
    }

    public SettlementDetailInfo setCarryScore(long carryScore) {
        this.carryScore = carryScore;
        return this;
    }

    public long getChangeScore() {
        return changeScore;
    }

    public SettlementDetailInfo setChangeScore(long changeScore) {
        this.changeScore = changeScore;
        return this;
    }

    public long getRemainingScore() {
        return remainingScore;
    }

    public SettlementDetailInfo setRemainingScore(long remainingScore) {
        this.remainingScore = remainingScore;
        return this;
    }

    public List<FanInfo> getList() {
        return list;
    }

    public SettlementDetailInfo setList(List<FanInfo> list) {
        this.list = list;
        return this;
    }
}
