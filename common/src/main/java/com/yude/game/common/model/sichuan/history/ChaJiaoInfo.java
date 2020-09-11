package com.yude.game.common.model.sichuan.history;

/**
 * @Author: HH
 * @Date: 2020/9/10 21:09
 * @Version: 1.0
 * @Declare: 玩家不可能被查叫了，还能查叫别人,所以这个应该是被查叫信息
 */
public class ChaJiaoInfo {
    private Integer posId;
    private Integer compensationToPosId;
    private int fanNum;
    private long beforeScore;
    private long changeScore;
    private long remainingScore;


    public Integer getPosId() {
        return posId;
    }

    public ChaJiaoInfo setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }

    public Integer getCompensationToPosId() {
        return compensationToPosId;
    }

    public ChaJiaoInfo setCompensationToPosId(Integer compensationToPosId) {
        this.compensationToPosId = compensationToPosId;
        return this;
    }

    public int getFanNum() {
        return fanNum;
    }

    public ChaJiaoInfo setFanNum(int fanNum) {
        this.fanNum = fanNum;
        return this;
    }

    public long getBeforeScore() {
        return beforeScore;
    }

    public ChaJiaoInfo setBeforeScore(long beforeScore) {
        this.beforeScore = beforeScore;
        return this;
    }

    public long getChangeScore() {
        return changeScore;
    }

    public ChaJiaoInfo setChangeScore(long changeScore) {
        this.changeScore = changeScore;
        return this;
    }

    public long getRemainingScore() {
        return remainingScore;
    }

    public ChaJiaoInfo setRemainingScore(long remainingScore) {
        this.remainingScore = remainingScore;
        return this;
    }

    @Override
    public String toString() {
        return "ChaJiaoInfo{" +
                "posId=" + posId +
                ", compensationToPosId=" + compensationToPosId +
                ", fanNum=" + fanNum +
                ", beforeScore=" + beforeScore +
                ", changeScore=" + changeScore +
                ", remainingScore=" + remainingScore +
                '}';
    }
}
