package com.yude.game.xuezhan.domain;


import com.yude.game.poker.common.model.AbstractSeatModel;
import com.yude.game.poker.common.model.Player;

/**
 * @Author: HH
 * @Date: 2020/8/1 15:29
 * @Version: 1.0
 * @Declare:
 */
public class XueZhanSeat extends AbstractSeatModel {

    public XueZhanSeat(Player player, int posId) {
        super(player, posId);
    }

    @Override
    public void init() {

    }

    @Override
    public void clean() {

    }
}
