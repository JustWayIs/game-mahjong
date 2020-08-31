package com.yude.game.common.model.sichuan;

import com.yude.game.common.action.place.SichuanPlay;
import com.yude.game.common.model.AbstractRoomModel;
import com.yude.game.common.model.Player;
import com.yude.game.common.timeout.MahjongTimeoutTaskPool;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/4 14:54
 * @Version: 1.0
 * @Declare:
 */
public class SichuanMahjongRoom extends AbstractRoomModel<SichuanMahjongZone,SichuanMahjongSeat, MahjongTimeoutTaskPool> implements SichuanPlay {


    @Override
    public SichuanMahjongRoom cloneData() throws CloneNotSupportedException {
        return null;
    }

    @Override
    public SichuanMahjongSeat getPracticalSeatModle(Player player, int posId) {
        return null;
    }

    @Override
    public SichuanMahjongZone getPracticalGameZoneModel() {
        return null;
    }

    @Override
    public void startGame() {
    }

    @Override
    public int getTimeoutLimit() {
        return 0;
    }

    @Override
    public void dingQue(Integer color, Integer posId) {

    }

    @Override
    public void exchangeCard(List<Integer> cards, Integer posId) {

    }
}
