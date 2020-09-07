package com.yude.game.common.model.fan;

import com.yude.game.common.model.fan.judge.Fan;
import com.yude.game.common.model.fan.judge.base.PingHuFan;
import com.yude.game.common.model.fan.judge.base.QiDuiFan;
import com.yude.game.common.model.fan.judge.base.ShiSanYaoFan;
import com.yude.game.common.model.fan.param.BaseHuParam;

/**
 * @Author: HH
 * @Date: 2020/8/17 15:10
 * @Version: 1.0
 * @Declare:
 */
public enum BaseHuTypeEnum implements FanType{
    /**
     * 基础胡
     */
    平胡("平胡", PingHuFan.INSTANCE),
    七对("七对", QiDuiFan.INSTANCE),
    十三幺("十三幺", ShiSanYaoFan.INSTANCE);

    public String name;
    /**
     * 不应该在这里绑定具体算法，因为对于不同地方麻将而言可能是不一样的
     * 但是，可能性非常小。具体番型判断，出现不同的可能性会更高，但是对于基础胡的牌型判断而言，
     * 几乎不会有不同的地方
     */
    public Fan<BaseHuParam> fan;

    BaseHuTypeEnum(String name) {
        this.name = name;
    }

    BaseHuTypeEnum(String name, Fan fan) {
        this.name = name;
        this.fan = fan;
    }

    @Override
    public int getId(){
        return 1000+this.ordinal();
    }

    @Override
    public FanType[] excludeFan() {
        return null;
    }

}
