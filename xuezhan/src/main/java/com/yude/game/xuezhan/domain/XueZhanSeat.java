package com.yude.game.xuezhan.domain;


import com.yude.game.common.model.AbstractSeatModel;
import com.yude.game.common.model.MahjongOperation;
import com.yude.game.common.model.MahjongSeat;
import com.yude.game.common.model.Player;
import com.yude.game.common.model.sichuan.SichuanMahjongSeat;
import com.yude.game.common.model.sichuan.constant.SeatStatusEnum;


/**
 * @Author: HH
 * @Date: 2020/8/1 15:29
 * @Version: 1.0
 * @Declare:
 */
public class XueZhanSeat extends AbstractSeatModel implements Cloneable{
    private SichuanMahjongSeat sichuanMahjongSeat;
    private MahjongSeat mahjongSeat;

    private SeatStatusEnum seatStatusEnum;

    public XueZhanSeat(Player player, int posId) {
        super(player, posId);
        mahjongSeat = new MahjongSeat(player,posId);
        sichuanMahjongSeat = new SichuanMahjongSeat(player,posId);
    }

    @Override
    public void init() {

    }

    @Override
    public void clean() {

    }


    public SichuanMahjongSeat getSichuanMahjongSeat() {
        return sichuanMahjongSeat;
    }

    public MahjongSeat getMahjongSeat() {
        return mahjongSeat;
    }

    public SeatStatusEnum getSeatStatusEnum() {
        return seatStatusEnum;
    }

    public void removeCardFromStandCards(Integer card){
        mahjongSeat.removeCardFromStandCards(card);
    }

    public boolean canOperation(MahjongOperation operation) {
        return mahjongSeat.canOperation(operation);
    }

    public void appendCard(Integer card){
        mahjongSeat.appendCard(card);
    }

    @Override
    public XueZhanSeat clone() throws CloneNotSupportedException {
        final XueZhanSeat xueZhanSeat = (XueZhanSeat) super.clone();
        xueZhanSeat.mahjongSeat = xueZhanSeat.mahjongSeat.clone();
        xueZhanSeat.sichuanMahjongSeat = xueZhanSeat.sichuanMahjongSeat.clone();
        xueZhanSeat.sichuanMahjongSeat.setMahjongSeat(xueZhanSeat.mahjongSeat);
        return xueZhanSeat;
    }
}
