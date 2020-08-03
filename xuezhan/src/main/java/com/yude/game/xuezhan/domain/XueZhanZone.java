package com.yude.game.xuezhan.domain;


import com.yude.game.poker.common.model.AbstractGameZoneModel;

/**
 * @Author: HH
 * @Date: 2020/8/1 15:29
 * @Version: 1.0
 * @Declare:
 */
public class XueZhanZone extends AbstractGameZoneModel<XueZhanSeat> {

    public XueZhanZone(XueZhanSeat[] playerSeats, int round, int inning) {
        super(playerSeats, round, inning);
    }

    @Override
    public void init() {

    }

    @Override
    public void clean() {

    }
}
