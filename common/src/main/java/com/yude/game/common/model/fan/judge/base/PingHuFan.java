package com.yude.game.common.model.fan.judge.base;

import com.yude.game.common.model.fan.BaseHuTypeEnum;
import com.yude.game.common.model.fan.FanType;
import com.yude.game.common.model.fan.judge.Fan;
import com.yude.game.common.model.fan.param.BaseHuParam;

/**
 * @Author: HH
 * @Date: 2020/9/5 17:31
 * @Version: 1.0
 * @Declare:
 */
public enum PingHuFan implements Fan<BaseHuParam> {
    /**
     *
     */
    INSTANCE(BaseHuTypeEnum.平胡);

    private FanType baseHuType;

    PingHuFan(BaseHuTypeEnum baseHuType) {
        this.baseHuType = baseHuType;
    }


    @Override
    public FanType judge(BaseHuParam param) {
        return null;
    }
}
