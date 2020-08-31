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
public enum QiangGangHuFan implements Fan<AppendedFanParam> {
    /**
     * 单例
     */
    INSTANCE(AppendedTypeEnum.抢杠胡);

    public FanType fantype;

    QiangGangHuFan(FanType fantype) {
        this.fantype = fantype;
    }

    @Override
    public FanType judge(AppendedFanParam param) {
        if(param.isQiangGang()){
            return fantype;
        }
        return null;
    }
}
