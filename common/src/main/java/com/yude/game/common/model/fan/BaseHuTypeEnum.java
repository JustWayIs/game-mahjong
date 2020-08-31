package com.yude.game.common.model.fan;

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
    平胡("平胡"),
    七对("七对"),
    十三幺("十三幺");

    public String name;

    BaseHuTypeEnum(String name) {
        this.name = name;
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
