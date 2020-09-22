package com.yude.game.common.model.sichuan.history.info;

import com.yude.game.common.model.fan.FanInfo;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/18 15:53
 * @Version: 1.0
 * @Declare:
 */
public class SingleSettlementInfo {
    private List<FanInfo> fanInfoList;
    private Integer fanScore;
    private Integer sumFanNum;
    private Integer targetPosId;

    public List<FanInfo> getFanInfoList() {
        return fanInfoList;
    }

    public SingleSettlementInfo setFanInfoList(List<FanInfo> fanInfoList) {
        this.fanInfoList = fanInfoList;
        return this;
    }

    public Integer getFanScore() {
        return fanScore;
    }

    public SingleSettlementInfo setFanScore(Integer fanScore) {
        this.fanScore = fanScore;
        return this;
    }

    public Integer getTargetPosId() {
        return targetPosId;
    }

    public SingleSettlementInfo setTargetPosId(Integer targetPosId) {
        this.targetPosId = targetPosId;
        return this;
    }

    public Integer getSumFanNum() {
        return sumFanNum;
    }

    public SingleSettlementInfo setSumFanNum(Integer sumFanNum) {
        this.sumFanNum = sumFanNum;
        return this;
    }

    @Override
    public String toString() {
        return "SingleSettlementInfo{" +
                "fanInfoList=" + fanInfoList +
                ", fanScore=" + fanScore +
                ", sumFanNum=" + sumFanNum +
                ", targetPosId=" + targetPosId +
                '}';
    }
}
