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
 * @Declare:
 */
public class ChaJiaoStep implements Step {
    private Map<Integer, List<ChaJiaoInfo>> chaJiaoInfoMap;
    private MahjongOperation operation;
    private Status gameStatus = SichuanGameStatusEnum.LIU_JU_SETTLEMENT;

    public Map<Integer, List<ChaJiaoInfo>> getChaJiaoInfoMap() {
        return chaJiaoInfoMap;
    }

    public ChaJiaoStep setChaJiaoInfoMap(Map<Integer, List<ChaJiaoInfo>> chaJiaoInfoMap) {
        this.chaJiaoInfoMap = chaJiaoInfoMap;
        return this;
    }

    public Status getGameStatus() {
        return gameStatus;
    }

    public ChaJiaoStep setGameStatus(Status gameStatus) {
        this.gameStatus = gameStatus;
        return this;
    }

    public MahjongOperation getOperation() {
        return operation;
    }

    public ChaJiaoStep setOperation(MahjongOperation operation) {
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
        return "ChaJiaoStep{" +
                "chaJiaoInfoMap=" + chaJiaoInfoMap +
                ", operation=" + operation +
                ", gameStatus=" + gameStatus +
                '}';
    }
}
