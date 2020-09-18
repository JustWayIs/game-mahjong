package com.yude.game.common.model.sichuan.history.info;

import com.yude.game.common.model.StepAction;

/**
 * @Author: HH
 * @Date: 2020/9/10 20:14
 * @Version: 1.0
 * @Declare: 应该表述为自己相关的退税信息
 */
public class RebateInfo {
    private Integer posId;
    private StepAction rebateActions;
    private int fanNum;
    private long beforeScore;
    private int changeScore;
    private long remainingScore;
    private Integer compensationToPosId;

    public Integer getPosId() {
        return posId;
    }

    public RebateInfo setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }

    public StepAction getRebateActions() {
        return rebateActions;
    }

    public RebateInfo setRebateActions(StepAction rebateActions) {
        this.rebateActions = rebateActions;
        return this;
    }

    public int getFanNum() {
        return fanNum;
    }

    public RebateInfo setFanNum(int fanNum) {
        this.fanNum = fanNum;
        return this;
    }

    public long getBeforeScore() {
        return beforeScore;
    }

    public RebateInfo setBeforeScore(long beforeScore) {
        this.beforeScore = beforeScore;
        return this;
    }

    public int getChangeScore() {
        return changeScore;
    }

    public RebateInfo setChangeScore(int changeScore) {
        this.changeScore = changeScore;
        return this;
    }

    public long getRemainingScore() {
        return remainingScore;
    }

    public RebateInfo setRemainingScore(long remainingScore) {
        this.remainingScore = remainingScore;
        return this;
    }

    public Integer getCompensationToPosId() {
        return compensationToPosId;
    }

    public RebateInfo setCompensationToPosId(Integer compensationToPosId) {
        this.compensationToPosId = compensationToPosId;
        return this;
    }

    @Override
    public String toString() {
        return "RebateInfo{" +
                "posId=" + posId +
                ", rebateActions=" + rebateActions +
                ", fanNum=" + fanNum +
                ", beforeScore=" + beforeScore +
                ", changeScore=" + changeScore +
                ", remainingScore=" + remainingScore +
                ", compensationToPosId=" + compensationToPosId +
                '}';
    }
}
