package com.yude.game.xuezhan.domain;


import com.yude.game.common.model.AbstractGameZoneModel;
import com.yude.game.common.model.MahjongZone;
import com.yude.game.common.model.history.GameStepModel;
import com.yude.game.common.model.sichuan.SichuanMahjongZone;
import com.yude.game.common.model.sichuan.constant.SichuanGameStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * @Author: HH
 * @Date: 2020/8/1 15:29
 * @Version: 1.0
 * @Declare:
 */
public class XueZhanZone extends AbstractGameZoneModel<XueZhanSeat, SichuanGameStatusEnum> {
    private static final Logger log = LoggerFactory.getLogger(XueZhanZone.class);

    private SichuanMahjongZone sichuanMahjongZone;
    private MahjongZone mahjongZone;

    /**
     *  因为换三张操作 、 定缺操作这种地方麻将玩法，导致，historyList不能放在MahjongZone里面
     *  也就导致多了一层对GameZone的调用。核心问题是，多一重掉用对性能真的有影响么，像现在的Controller省略了Service一样，又对性能提高有多少贡献呢。
     *  如果想要少一层调用，倒是可以把historyList放在RoomModel里面，这里的核心问题是类的职能划分
     *  从代码复用的层面来说，这里面的方法确实应该放在 MahjongZone 和 地方麻将Zone里面
     */

    private List<GameStepModel> historyList;

    public XueZhanZone(XueZhanSeat[] playerSeats, int round, int inning,MahjongZone mahjongZone,SichuanMahjongZone sichuanMahjongZone) {
        super(playerSeats, round, inning);
        /*MahjongSeat[] mahjongSeats = new MahjongSeat[playerSeats.length];
        SichuanMahjongSeat[] sichuanMahjongSeats = new SichuanMahjongSeat[playerSeats.length];
        int i = 0;
        for (XueZhanSeat xueZhanSeat : playerSeats) {
            mahjongSeats[i] = xueZhanSeat.getMahjongSeat();
            sichuanMahjongSeats[i] = xueZhanSeat.getSichuanMahjongSeat();
            sichuanMahjongSeats[i].setMahjongSeat(mahjongSeats[i]);
            ++i;
        }
        mahjongZone = new MahjongZone(mahjongSeats, round, inning);
        sichuanMahjongZone = new SichuanMahjongZone(sichuanMahjongSeats, round, inning);
        sichuanMahjongZone.setMahjongZone(mahjongZone);*/

        this.mahjongZone = mahjongZone;
        this.sichuanMahjongZone = sichuanMahjongZone;
    }

    @Override
    public void init() {
        mahjongZone.init();
        sichuanMahjongZone.init();
        historyList = new ArrayList<>();
    }

    @Override
    public void clean() {

    }


}
