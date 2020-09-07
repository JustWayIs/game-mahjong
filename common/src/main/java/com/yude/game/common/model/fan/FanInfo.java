package com.yude.game.common.model.fan;

import com.yude.game.common.model.fan.judge.Fan;
import com.yude.game.common.model.fan.param.HuFanParam;

/**
 * @Author: HH
 * @Date: 2020/8/18 11:45
 * @Version: 1.0
 * @Declare:
 */
public  class FanInfo<T extends FanType> {
    private T fanType;
    /**
     * 番数
     */
    private Integer fanScore;
    //乘法:0 或者 加法:1
    private Integer calculationType;

    private Fan fan;

    public FanInfo() {
    }

    public FanInfo(T fanType, Integer fanScore, Integer calculationType, Fan fan) {
        this.fanType = fanType;
        this.fanScore = fanScore;
        this.calculationType = calculationType;
        this.fan = fan;
    }

    public boolean judgeFan(HuFanParam param){
        FanType fanType = fan.judge(param);
        return fanType == null ? false : true;
    }

    @Override
    public String toString() {
        return "FanInfo{" +
                "fanType=" + fanType +
                ", fanScore=" + fanScore +
                ", calculationType=" + calculationType +
                ", fan=" + fan +
                '}';
    }
}
