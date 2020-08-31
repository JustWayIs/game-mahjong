package com.yude.game.common.model.fan.judge.appended;

import com.yude.game.common.model.fan.AppendedTypeEnum;
import com.yude.game.common.model.fan.FanType;
import com.yude.game.common.model.fan.judge.Fan;
import com.yude.game.common.model.fan.param.AppendedFanParam;

/**
 * @Author: HH
 * @Date: 2020/8/17 21:12
 * @Version: 1.0
 * @Declare:
 */
public enum HaiDiPaoFan implements Fan<AppendedFanParam> {
    /**
     * 单例
     */
    INSTANCE(AppendedTypeEnum.海底炮);

    public FanType fantype;

    HaiDiPaoFan(FanType fantype) {
        this.fantype = fantype;
    }

    @Override
    public FanType judge(AppendedFanParam param) {
        int cardWallRemainingCount = param.getCardWallRemainingCount();
        if(cardWallRemainingCount == 0 && !param.isZiMo()){
            return fantype;
        }
        return null;
    }
}
