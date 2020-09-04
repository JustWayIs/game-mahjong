package com.yude.game.xuezhan.constant;

import com.yude.game.common.contant.PushCommandCode;

/**
 * @Author: HH
 * @Date: 2020/8/26 11:19
 * @Version: 1.0
 * @Declare:
 */
public interface XueZhanPushCommandCode extends PushCommandCode {
    /**
     * 换三张结果
     */
    int EXCHANGE_RESULT = 0x2003;
    /**
     * 推荐定缺
     */
    int RECOMMEND_DING_QUE = 0x2004;

    /**
     * 通知玩家可以进行什么操作（只通知可以操作的玩家）
     */
    int OPERATION_NOTICE = 0x2005;

    /**
     * 操作结果通知：
     *  1.通知房间 其他 玩家，某个玩
     *  家进行了换牌
     *  2.通知房间所有玩家，某个玩家定缺的结果
     *  3.通知房间所有玩家，某个玩家的出牌、碰、杠、胡
     */
    int OPERATION_RESULT_NOTICE = 0x2006;

    /**
     * 抓牌通知
     */
    int TOOK_CARD_NOTICE = 0x2007;

    /**
     * 进入出牌流程的通知：实质就是通知玩家第一次出牌的是谁，因为第一次出牌是不用先抓牌的
     */
    int FORMAL_PROCESS_NOTICE = 0x2008;
}
