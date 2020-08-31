package com.yude.game.xuezhan.domain.status;


import com.yude.game.common.constant.Status;

/**
 * @Author: HH
 * @Date: 2020/8/3 11:44
 * @Version: 1.0
 * @Declare:
 */
public enum SeatStatusEnum implements Status {
    /**
     *
     */
    DEAL_CARD,
    CHANGE_CARD,
    DING_QUE,
    OPERATION_CARD;


    @Override
    public int status() {
        return this.ordinal();
    }
}
