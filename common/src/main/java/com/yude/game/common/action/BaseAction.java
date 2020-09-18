package com.yude.game.common.action;

/**
 * @Author: HH
 * @Date: 2020/8/3 11:12
 * @Version: 1.0
 * @Declare:
 */
public interface BaseAction {

    void outCard(Integer card,Integer posId);

    /**
     *
     * @param card
     * @param posId
     * @param isRestore 是否是还原操作
     */
    void hu(Integer card,Integer posId,boolean isRestore);

    void cancel(Integer card,Integer podId);
}
