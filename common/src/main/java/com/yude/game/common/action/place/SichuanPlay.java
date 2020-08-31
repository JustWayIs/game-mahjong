package com.yude.game.common.action.place;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/3 11:36
 * @Version: 1.0
 * @Declare:
 */
public interface SichuanPlay {

    void dingQue(Integer color,Integer posId);

    /**
     * 换三张、换四张
     * @param cards
     * @param posId
     */
    void exchangeCard(List<Integer> cards,Integer posId);
}
