package com.yude.game.xuezhan.domain.action;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/3 11:36
 * @Version: 1.0
 * @Declare:
 */
public interface SichuanPlay {

    void dingQue(Integer color,int posId);

    /**
     * 换三张、换四张
     * @param cards
     * @param posId
     */
    void changeCard(List<Integer> cards,int posId);
}
