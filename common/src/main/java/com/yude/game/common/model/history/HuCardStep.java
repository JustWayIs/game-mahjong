package com.yude.game.common.model.history;

import com.yude.game.common.model.fan.FanInfo;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/5 16:19
 * @Version: 1.0
 * @Declare:
 */
public class HuCardStep extends OperationCardStep {

    private List<FanInfo> fanInfoList;

    public List<FanInfo> getFanInfoList() {
        return fanInfoList;
    }

    public HuCardStep setFanInfoList(List<FanInfo> fanInfoList) {
        this.fanInfoList = fanInfoList;
        return this;
    }

    @Override
    public String toString() {
        return "HuCardStep{" +
                "fanInfoList=" + fanInfoList +
                "} " + super.toString();
    }
}
