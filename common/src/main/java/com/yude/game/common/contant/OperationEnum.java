package com.yude.game.common.contant;

import com.yude.game.common.model.MahjongOperation;

/**
 * @Author: HH
 * @Date: 2020/8/3 14:24
 * @Version: 1.0
 * @Declare:
 */
public enum  OperationEnum implements MahjongOperation {
    /**
     * 过
     */
    CANCEL(false,1),
    /**
     * 补花
     */
    BU_HUA(false,0),
    /**
     * 换牌：可能是换三张、可能是换四张
     */
    EXCHANGE_CARD(false,0),
    /**
     * 定缺
     */
    DING_QUE(false,0),
    /**
     * 出牌
     */
    OUT_CARD(false,0),
    CHI(true,2),
    PENG(true,3),
    ZHI_GANG(true,3),
    BU_GANG(true,3),
    AN_GANG(true,3),
    HU(false,4),
    /**
     * 一炮多响
     */
    YI_PAO_DUO_XIANG(false,4),
    /**
     * 抓牌
     */
    TOOK_CARD(false,0);

    /**
     * 该操作是否能产生副露，就是对操作类型做进一步的区分
     */
    public boolean canProductFulu;

    /**
     * 值为0 代表这个操作不需要优先级这个概念
     */
    public int priority;

    OperationEnum(boolean canProductFulu, int priority) {
        this.canProductFulu = canProductFulu;
        this.priority = priority;
    }

    @Override
    public Integer value() {
        return this.ordinal();
    }

    @Override
    public boolean canProductFulu() {
        return canProductFulu;
    }

    @Override
    public int priority() {
        return priority;
    }
}
