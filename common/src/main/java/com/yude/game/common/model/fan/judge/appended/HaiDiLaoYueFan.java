package com.yude.game.common.model.fan.judge.appended;

import com.yude.game.common.model.fan.AppendedTypeEnum;
import com.yude.game.common.model.fan.FanType;
import com.yude.game.common.model.fan.judge.Fan;
import com.yude.game.common.model.fan.param.AppendedFanParam;

/**
 * @Author: HH
 * @Date: 2020/8/17 21:13
 * @Version: 1.0
 * @Declare:
 */
public enum HaiDiLaoYueFan implements Fan<AppendedFanParam> {
    /**
     * 单例
     */
    INSTANCE(AppendedTypeEnum.海底捞月);

    public FanType fantype;

    HaiDiLaoYueFan(FanType fantype) {
        this.fantype = fantype;
    }

    @Override
    public FanType judge(AppendedFanParam param) {
        int cardWallRemainingCount = param.getCardWallRemainingCount();
        if(cardWallRemainingCount == 0 && param.isZiMo()){
            return fantype;
        }
        return null;
    }
}
