package com.yude.game.common.model.sichuan.constant;


import com.yude.game.common.constant.Status;

/**
 * @Author: HH
 * @Date: 2020/8/3 11:44
 * @Version: 1.0
 * @Declare:
 */
public enum SeatStatusEnum implements Status {
    /**
     * 由于不能看见其他玩家的操作权限，但是断线重连回来，或者其他的什么场景，可能需要知道别人的当前的状态。
     */
    DEAL_CARD,
    /**
     * 有这个状态意味着 还没有进行换牌请求
     */
    EXCHANGE_CARD,
    DING_QUE,
    OPERATION_CARD,
    AUTO,
    ALREADY_HU;


    @Override
    public int status() {
        return this.ordinal();
    }
}
