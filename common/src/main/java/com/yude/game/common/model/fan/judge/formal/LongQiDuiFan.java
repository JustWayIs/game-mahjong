package com.yude.game.common.model.fan.judge.formal;

import com.yude.game.common.model.fan.FanType;
import com.yude.game.common.model.fan.FormalFanTypeEnum;
import com.yude.game.common.model.fan.judge.Fan;
import com.yude.game.common.model.fan.param.FormalFanParam;

/**
 * @Author: HH
 * @Date: 2020/8/17 21:09
 * @Version: 1.0
 * @Declare:
 */
public enum LongQiDuiFan implements Fan<FormalFanParam> {
    /**
     * 单例
     */
    INSTANCE(FormalFanTypeEnum.龙七对);

    public FanType fantype;

    LongQiDuiFan(FanType fantype) {
        this.fantype = fantype;
    }

    @Override
    public FanType judge(FormalFanParam param) {
        return null;
    }
}
