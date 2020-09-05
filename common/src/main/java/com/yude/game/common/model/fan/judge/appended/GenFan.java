package com.yude.game.common.model.fan.judge.appended;

import com.yude.game.common.model.fan.AppendedTypeEnum;
import com.yude.game.common.model.fan.FanType;
import com.yude.game.common.model.fan.judge.Fan;
import com.yude.game.common.model.fan.param.AppendedFanParam;

/**
 * @Author: HH
 * @Date: 2020/9/5 18:39
 * @Version: 1.0
 * @Declare:
 */
public enum GenFan implements Fan<AppendedFanParam> {
    /**
     *
     */
    INSTANCE(AppendedTypeEnum.根);

    public FanType appendedType;

    GenFan(FanType appendedType) {
        this.appendedType = appendedType;
    }

    @Override
    public FanType judge(AppendedFanParam param) {
        return null;
    }
}
