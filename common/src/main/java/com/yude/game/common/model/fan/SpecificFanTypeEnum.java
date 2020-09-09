package com.yude.game.common.model.fan;

/**
 * @Author: HH
 * @Date: 2020/9/9 15:00
 * @Version: 1.0
 * @Declare:
 */
public enum SpecificFanTypeEnum implements FanType {
    直杠,
    补杠,
    暗杠;

    @Override
    public int getId() {
        return 6000+this.ordinal();
    }

    @Override
    public FanType[] excludeFan() {
        return new FanType[0];
    }
}
