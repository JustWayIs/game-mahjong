package com.yude.game.common.model.fan.judge;

import com.yude.game.common.model.fan.FanType;
import com.yude.game.common.model.fan.param.HuFanParam;

/**
 * @Author: HH
 * @Date: 2020/8/17 11:20
 * @Version: 1.0
 * @Declare:
 */
public interface Fan<T extends HuFanParam> {
    FanType judge(T param);
}
