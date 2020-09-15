package com.yude.game.common.model.sichuan.history;

import com.yude.game.common.constant.Status;
import com.yude.game.common.model.history.Step;
import com.yude.game.common.model.sichuan.history.info.SettlementDetailInfo;

import java.util.Map;

/**
 * @Author: HH
 * @Date: 2020/9/15 11:16
 * @Version: 1.0
 * @Declare:
 */
public class SettlementDetailStep implements Step {
    private int stepCount;
    private Map<Integer, SettlementDetailInfo> map;

    @Override
    public Status gameStatus() {
        return null;
    }

    @Override
    public Integer actionType() {
        return null;
    }

    @Override
    public int posId() {
        return 0;
    }

    public int getStepCount() {
        return stepCount;
    }

    public SettlementDetailStep setStepCount(int stepCount) {
        this.stepCount = stepCount;
        return this;
    }

    public Map<Integer, SettlementDetailInfo> getMap() {
        return map;
    }

    public SettlementDetailStep setMap(Map<Integer, SettlementDetailInfo> map) {
        this.map = map;
        return this;
    }

    @Override
    public String toString() {
        return "SettlementDetailStep{" +
                "stepCount=" + stepCount +
                ", map=" + map +
                '}';
    }
}
