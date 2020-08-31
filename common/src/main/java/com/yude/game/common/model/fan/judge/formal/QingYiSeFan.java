package com.yude.game.common.model.fan.judge.formal;

import com.yude.game.common.model.fan.FanType;
import com.yude.game.common.model.fan.FormalFanTypeEnum;
import com.yude.game.common.model.fan.judge.Fan;
import com.yude.game.common.model.fan.param.FormalFanParam;

/**
 * @Author: HH
 * @Date: 2020/8/17 20:58
 * @Version: 1.0
 * @Declare:
 */
public enum  QingYiSeFan implements Fan<FormalFanParam> {
    /**
     * 单例
     */
    INSTANCE(FormalFanTypeEnum.清一色);

    public FanType fanType;

    QingYiSeFan(FanType fanType) {
        this.fanType = fanType;
    }

    @Override
    public FanType judge(FormalFanParam param) {
        return null;
    }
}
