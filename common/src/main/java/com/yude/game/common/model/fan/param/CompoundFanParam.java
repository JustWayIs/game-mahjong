package com.yude.game.common.model.fan.param;

import com.yude.game.common.model.fan.BaseHuTypeEnum;
import com.yude.game.common.model.fan.FanType;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/17 20:47
 * @Version: 1.0
 * @Declare:
 */
public class CompoundFanParam implements HuFanParam {
    private BaseHuTypeEnum baseHuType;
    private List<FanType> fanTypeList;

    public List<FanType> getFanTypeList() {
        return fanTypeList;
    }

    public CompoundFanParam setFanTypeList(List<FanType> fanTypeList) {
        this.fanTypeList = fanTypeList;
        return this;
    }

    public BaseHuTypeEnum getBaseHuType() {
        return baseHuType;
    }

    public CompoundFanParam setBaseHuType(BaseHuTypeEnum baseHuType) {
        this.baseHuType = baseHuType;
        return this;
    }
}
