package com.yude.game.common.model.fan;

/**
 * @Author: HH
 * @Date: 2020/8/17 15:15
 * @Version: 1.0
 * @Declare:
 */
public enum  AppendedTypeEnum implements FanType {
    /**
     * 附加番:达成某些条件
     */
    根("根",null),
    海底炮("海底炮",null),
    海底捞月("海底捞月",null),
    //发生在补杠阶段
    抢杠胡("抢杠胡",null),
    天胡("天胡",null),
    地胡("地胡",null),
    //还没有加param 和 实现
    杠上开花("杠上开花",null),
    杠上炮("杠上炮",null);

    public String name;
    private FanType[] execludeFanTypes;

    AppendedTypeEnum(String name, FanType[] execludeFanTypes) {
        this.name = name;
        this.execludeFanTypes = execludeFanTypes;
    }

    @Override
    public int getId() {
        return 4000+this.ordinal();
    }

    @Override
    public FanType[] excludeFan() {
        return this.execludeFanTypes;
    }

}
