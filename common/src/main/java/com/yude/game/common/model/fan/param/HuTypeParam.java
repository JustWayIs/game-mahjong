package com.yude.game.common.model.fan.param;

/**
 * @Author: HH
 * @Date: 2020/8/17 16:25
 * @Version: 1.0
 * @Declare:
 */
public class HuTypeParam implements HuFanParam {
    private Integer selfPosId;
    /**
     * 胡的牌的来源:摸上来的就是自己的位置，否则就是别人的位置
     */
    private Integer cardFrom;

    public boolean cardFromSelf(){
        return selfPosId.equals(cardFrom);
    }
}
