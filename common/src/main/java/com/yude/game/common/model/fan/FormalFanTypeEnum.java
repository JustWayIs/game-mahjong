package com.yude.game.common.model.fan;

/**
 * @Author: HH
 * @Date: 2020/8/17 15:25
 * @Version: 1.0
 * @Declare:
 */
public enum FormalFanTypeEnum implements FanType {
    /**
     * 正式的番型
     */
    大对子("碰碰胡",BaseHuTypeEnum.平胡,null),
    清一色("清一色",BaseHuTypeEnum.平胡,null),
    //单吊
    金钩钓("金钩钓",BaseHuTypeEnum.平胡,new FanType[]{AppendedTypeEnum.地胡}),
    //有四条杠的单吊: 有金钩钓再判断有木有十八罗汉，直接在金钩钓里面判断么...
    十八罗汉("十八罗汉",BaseHuTypeEnum.平胡,new FanType[]{金钩钓}),
    清七对("清七对",BaseHuTypeEnum.七对,new FanType[]{清一色}),
    龙七对("龙七对",BaseHuTypeEnum.七对,null);

    public String name;
    public BaseHuTypeEnum baseHu;
    public FanType[] excludeFanType;

    FormalFanTypeEnum(String name, BaseHuTypeEnum baseHu, FanType[] excludeFanType) {
        this.name = name;
        this.baseHu = baseHu;
        this.excludeFanType = excludeFanType;
    }

    @Override
    public int getId() {
        return 3000+this.ordinal();
    }

    @Override
    public FanType[] excludeFan() {
        return null;
    }
}
