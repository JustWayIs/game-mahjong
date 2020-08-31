package com.yude.game.common.model.fan;

/**
 * @Author: HH
 * @Date: 2020/8/17 15:27
 * @Version: 1.0
 * @Declare:
 */
public enum CompoundFanTypeEnum implements FanType{
    /**
     * 复合番型：
     */
    //清一色 + 碰碰胡
    清碰("清碰",new FanType[]{FormalFanTypeEnum.清一色,FormalFanTypeEnum.大对子}),
    /**
     * 附加番 + 番型
     *
     * 金钩钓 + ...
     */
    清金钩钓("清金钩钓",new FanType[]{FormalFanTypeEnum.清一色,FormalFanTypeEnum.金钩钓}),

    清十八罗汉("清十八罗汉",new FanType[]{FormalFanTypeEnum.清一色,FormalFanTypeEnum.十八罗汉});

    public String name;
    public FanType[] fanTypes;

    CompoundFanTypeEnum(String name, FanType[] fanTypes) {
        this.name = name;
        this.fanTypes = fanTypes;
    }

    @Override
    public int getId() {
        return 5000+this.ordinal();
    }

    @Override
    public FanType[] excludeFan() {
        return this.fanTypes;
    }


}
