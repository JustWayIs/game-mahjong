package com.yude.game.common.action;

/**
 * @Author: HH
 * @Date: 2020/8/3 11:12
 * @Version: 1.0
 * @Declare:
 */
public interface BaseAction {

    void outCard(Integer card,Integer posId);

    void hu(Integer card,Integer posId);

    void cancel();
}
