package com.yude.game.xuezhan.domain.status;


import com.yude.game.common.constant.Status;

/**
 * @Author: HH
 * @Date: 2020/8/3 11:50
 * @Version: 1.0
 * @Declare:
 */
public enum GameStatusEnum implements Status {
    /**
     *
     */
    DEAL_CARD,
    /**
     *
     */
    CHANGE_CARD,
    /**
     *
     */
    DING_QUE,
    OPERATION_CARD,
    SETTLEMENT;


    @Override
    public int status() {
        return this.ordinal();
    }
}
