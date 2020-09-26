package com.yude.game.common.model.sichuan.constant;


import com.yude.game.common.constant.Status;

/**
 * @Author: HH
 * @Date: 2020/8/3 11:50
 * @Version: 1.0
 * @Declare:
 */
public enum SichuanGameStatusEnum implements Status {
    /**
     *
     */
    DEAL_CARD(99900000),
    EXCHANGE_CARD(8000),
    DING_QUE(8000),
    OPERATION_CARD(8000),
    SETTLEMENT(99900000),

    /**
     * 血战血流似乎需要一个这样的标识
     */
    GAME_OVER(99900000),
    /**
     * 没有必要设置游戏状态为下面几个，只是在构建Step对象的时候用到
     */
    LIU_JU_SETTLEMENT(99900000),
    OUT_CARD(10000),
    ;

    public int timeout;

    SichuanGameStatusEnum(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public int status() {
        return this.ordinal();
    }

    @Override
    public String toString() {
        return "SichuanGameStatusEnum{" +
                "timeout=" + timeout +
                "} " + super.toString();
    }
}
