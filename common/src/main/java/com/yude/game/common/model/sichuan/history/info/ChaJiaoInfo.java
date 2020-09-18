package com.yude.game.common.model.sichuan.history.info;

import com.yude.game.common.model.fan.FanInfo;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/10 21:09
 * @Version: 1.0
 * @Declare: 玩家不可能被查叫了，还能查叫别人,所以这个应该是被查叫信息
 */
public class ChaJiaoInfo {
    private Integer posId;
    private Integer compensationToPosId;
    private long beforeScore;
    private int changeScore;
    private long remainingScore;
    private List<FanInfo> fanInfoList;
    private int fanNum;


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

    public int getChangeScore() {
        return changeScore;
    }

    public ChaJiaoInfo setChangeScore(int changeScore) {
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

    public List<FanInfo> getFanInfoList() {
        return fanInfoList;
    }

    public ChaJiaoInfo setFanInfoList(List<FanInfo> fanInfoList) {
        this.fanInfoList = fanInfoList;
        return this;
    }

    @Override
    public String toString() {
        return "ChaJiaoInfo{" +
                "posId=" + posId +
                ", compensationToPosId=" + compensationToPosId +
                ", beforeScore=" + beforeScore +
                ", changeScore=" + changeScore +
                ", remainingScore=" + remainingScore +
                ", fanInfoList=" + fanInfoList +
                ", fanNum=" + fanNum +
                '}';
    }
}
