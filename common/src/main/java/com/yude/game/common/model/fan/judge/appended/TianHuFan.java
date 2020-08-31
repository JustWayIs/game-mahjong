package com.yude.game.common.model.fan.judge.appended;

import com.yude.game.common.model.fan.AppendedTypeEnum;
import com.yude.game.common.model.fan.FanType;
import com.yude.game.common.model.fan.judge.Fan;
import com.yude.game.common.model.fan.param.AppendedFanParam;

/**
 * @Author: HH
 * @Date: 2020/8/17 21:15
 * @Version: 1.0
 * @Declare:
 */
public enum TianHuFan implements Fan<AppendedFanParam> {
    /**
     * 单例
     */
    INSTANCE(AppendedTypeEnum.天胡);

    public FanType fantype;

    TianHuFan(FanType fantype) {
        this.fantype = fantype;
    }

    @Override
    public FanType judge(AppendedFanParam param) {
        if(param.isBanker() && param.getMocardNum() == 0){
            return fantype;
        }
        return null;
    }
}
