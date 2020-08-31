package com.yude.game.common.model.fan.judge.formal;

import com.yude.game.common.model.fan.FanType;
import com.yude.game.common.model.fan.FormalFanTypeEnum;
import com.yude.game.common.model.fan.judge.Fan;
import com.yude.game.common.model.fan.param.FormalFanParam;

/**
 * @Author: HH
 * @Date: 2020/8/17 21:17
 * @Version: 1.0
 * @Declare:
 */
public enum ShiBaLuoHanFan implements Fan<FormalFanParam> {
    /**
     * 单例
     */
    INSTANCE(FormalFanTypeEnum.十八罗汉);

    public FanType fantype;

    ShiBaLuoHanFan(FanType fantype) {
        this.fantype = fantype;
    }

    @Override
    public FanType judge(FormalFanParam param) {
        return null;
    }
}
