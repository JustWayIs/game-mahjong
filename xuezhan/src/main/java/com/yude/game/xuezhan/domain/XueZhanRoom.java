package com.yude.game.xuezhan.domain;



import com.yude.game.poker.common.model.AbstractRoomModel;
import com.yude.game.poker.common.model.Player;
import com.yude.game.poker.common.timeout.TimeoutTaskPool;

/**
 * @Author: HH
 * @Date: 2020/8/1 15:29
 * @Version: 1.0
 * @Declare:
 */
public class XueZhanRoom extends AbstractRoomModel<XueZhanZone,XueZhanSeat, TimeoutTaskPool> {

    @Override
    public XueZhanSeat getPracticalSeatModle(Player player, int posId) {
        return new XueZhanSeat(player,posId);
    }

    @Override
    public XueZhanZone getPracticalGameZoneModel() {
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
        XueZhanSeat[] seatModels = new XueZhanSeat[posIdSeatMap.size()];
        int i = 0;
        for (Object seat : posIdSeatMap.values()) {
            XueZhanSeat seatModel = (XueZhanSeat) seat;
            seatModels[i] = seatModel;
            i++;
        }

        return new XueZhanZone(seatModels, gameRound, gameInning);
    }

    @Override
    public void startGame() {

    }
}
