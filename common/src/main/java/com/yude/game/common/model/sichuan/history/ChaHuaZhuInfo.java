package com.yude.game.common.model.sichuan.history;

/**
 * @Author: HH
 * @Date: 2020/9/10 20:29
 * @Version: 1.0
 * @Declare: 同查叫一样，被查花猪的玩家，不可能查别人的花猪，所以这里的是被查花猪，也就是自己的扣分信息
 */
public class ChaHuaZhuInfo {
    private Integer posId;
    /**
     * 如果changeCore是正数，这么这个属性标识的就是赔分给该玩家的玩家
     * 如果是是负数，那么标识的就是要赔付给谁
     */
    private Integer compensationToPosId;
    private int fanNum;
    private long beforeScore;
    private long changeScore;
    private long remainingScore;

    public Integer getPosId() {
        return posId;
    }

    public ChaHuaZhuInfo setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }

    public Integer getCompensationToPosId() {
        return compensationToPosId;
    }

    public ChaHuaZhuInfo setCompensationToPosId(Integer compensationToPosId) {
        this.compensationToPosId = compensationToPosId;
        return this;
    }

    public int getFanNum() {
        return fanNum;
    }

    public ChaHuaZhuInfo setFanNum(int fanNum) {
        this.fanNum = fanNum;
        return this;
    }

    public long getBeforeScore() {
        return beforeScore;
    }

    public ChaHuaZhuInfo setBeforeScore(long beforeScore) {
        this.beforeScore = beforeScore;
        return this;
    }

    public long getChangeScore() {
        return changeScore;
    }

    public ChaHuaZhuInfo setChangeScore(long changeScore) {
        this.changeScore = changeScore;
        return this;
    }

    public long getRemainingScore() {
        return remainingScore;
    }

    public ChaHuaZhuInfo setRemainingScore(long remainingScore) {
        this.remainingScore = remainingScore;
        return this;
    }

    @Override
    public String toString() {
        return "ChaHuaZhuInfo{" +
                "posId=" + posId +
                ", compensationToPosId=" + compensationToPosId +
                ", fanNum=" + fanNum +
                ", beforeScore=" + beforeScore +
                ", changeScore=" + changeScore +
                ", remainingScore=" + remainingScore +
                '}';
    }
}
