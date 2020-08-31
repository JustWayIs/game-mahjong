package com.yude.game.common.model.fan.judge;

import com.yude.game.common.model.fan.FanType;
import com.yude.game.common.model.fan.HuTypeEnum;
import com.yude.game.common.model.fan.param.HuTypeParam;

/**
 * @Author: HH
 * @Date: 2020/8/18 10:50
 * @Version: 1.0
 * @Declare:
 */
public enum  HuTypeFan implements Fan<HuTypeParam> {
    /**
     * 单例
     */
    INSTANCE;

    @Override
    public FanType judge(HuTypeParam param) {
        if(param.cardFromSelf()){
            return HuTypeEnum.自摸;
        }
        return HuTypeEnum.点炮胡;
    }
}
