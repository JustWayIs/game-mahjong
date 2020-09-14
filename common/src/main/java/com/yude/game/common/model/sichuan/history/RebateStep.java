package com.yude.game.common.model.sichuan.history;

import com.yude.game.common.constant.Status;
import com.yude.game.common.model.MahjongOperation;
import com.yude.game.common.model.history.Step;
import com.yude.game.common.model.sichuan.constant.SichuanGameStatusEnum;

import java.util.List;
import java.util.Map;

/**
 * @Author: HH
 * @Date: 2020/9/10 19:48
 * @Version: 1.0
 * @Declare: 所有玩家的退税步骤。跟结算还不太一样，胡牌（不是一炮多响）结算、杠牌结算，收益方只会有一个。而退税记录的是相对的东西，既有自己退给别人的，也有别人退给自己的
 */
public class RebateStep implements Step {
    private Map<Integer, List<RebateInfo>> seatRebateMap;
    private MahjongOperation operation;
    private Status gameStatus = SichuanGameStatusEnum.LIU_JU_SETTLEMENT;

    public Map<Integer, List<RebateInfo>> getSeatRebateMap() {
        return seatRebateMap;
    }

    public RebateStep setSeatRebateMap(Map<Integer, List<RebateInfo>> seatRebateMap) {
        this.seatRebateMap = seatRebateMap;
        return this;
    }

    public Status getGameStatus() {
        return gameStatus;
    }

    public RebateStep setGameStatus(Status gameStatus) {
        this.gameStatus = gameStatus;
        return this;
    }

    public MahjongOperation getOperation() {
        return operation;
    }

    public RebateStep setOperation(MahjongOperation operation) {
        this.operation = operation;
        return this;
    }

    @Override
    public Status gameStatus() {
        return null;
    }

    @Override
    public Integer actionType() {
        return null;
    }

    @Override
    public int posId() {
        return 0;
    }

    @Override
    public String toString() {
        return "RebateStep{" +
                "seatRebateMap=" + seatRebateMap +
                ", operation=" + operation +
                ", gameStatus=" + gameStatus +
                '}';
    }
}
