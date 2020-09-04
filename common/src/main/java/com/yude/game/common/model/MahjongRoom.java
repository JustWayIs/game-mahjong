package com.yude.game.common.model;

import com.yude.game.common.action.CommonAction;
import com.yude.game.common.timeout.TimeoutTaskPool;

/**
 * @Author: HH
 * @Date: 2020/8/3 19:28
 * @Version: 1.0
 * @Declare:
 */
public  class MahjongRoom extends AbstractRoomModel<MahjongZone,MahjongSeat, TimeoutTaskPool> implements CommonAction, Cloneable {
    private MahjongCard[] mahjongCard;

    @Override
    public MahjongRoom cloneData() throws CloneNotSupportedException {
        return null;
    }

    @Override
    public MahjongSeat getPracticalSeatModle(Player player, int posId) {
        return new MahjongSeat(player,posId);
    }

    @Override
    public MahjongZone getPracticalGameZoneModel() {
        //进行下一局的时候
        int gameRound = 1;
        int gameInning = 1;
        if (gameZone != null) {
            if (gameZone.getInning() + 1 > inningLimit) {
                gameRound = gameZone.getRound() + 1;
            } else {
                gameInning = gameZone.getInning() + 1;
            }
        }

        /**
         * 给游戏域 和 位置 建立关联关系：聚合关系。与room中指向的是同一个位置对象
         */
        MahjongSeat[] seatModels = new MahjongSeat[posIdSeatMap.size()];
        int i = 0;
        for (Object seat : posIdSeatMap.values()) {
            MahjongSeat seatModel = (MahjongSeat) seat;
            seatModels[i] = seatModel;
            i++;
        }

        return new MahjongZone(seatModels, gameRound, gameInning);
    }

    @Override
    public void startGame() {

    }

    @Override
    public int getTimeoutLimit() {
        return 0;
    }

    @Override
    public void outCard(Integer card, Integer posId) {

    }

    @Override
    public void hu(Integer card, Integer posId) {

    }

    @Override
    public void cancel(Integer card, Integer podId) {

    }


    @Override
    public void chi(Integer card, int posId) {

    }

    @Override
    public void gang(Integer card, Integer type, Integer posId) {

    }

    @Override
    public void peng(Integer card, Integer posId) {

    }

    public MahjongCard[] getMahjongCard() {
        return mahjongCard;
    }

    public MahjongRoom setMahjongCard(MahjongCard[] mahjongCard) {
        this.mahjongCard = mahjongCard;
        return this;
    }
}
