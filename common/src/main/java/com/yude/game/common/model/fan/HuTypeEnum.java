package com.yude.game.common.model.fan;

/**
 * @Author: HH
 * @Date: 2020/8/17 15:12
 * @Version: 1.0
 * @Declare:
 */
public enum HuTypeEnum implements FanType {
    /**
     * 胡牌方式
     */
    自摸("自摸"),
    点炮胡("点炮胡");

    public String name;

    HuTypeEnum(String name) {
        this.name = name;
    }

    @Override
    public int getId() {
        return 1000+this.ordinal();
    }

    @Override
    public FanType[] excludeFan() {
        return null;
    }
}
