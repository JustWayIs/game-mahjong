package com.yude.game.common.model.sichuan.history;

import com.yude.game.common.constant.Status;
import com.yude.game.common.contant.OperationEnum;
import com.yude.game.common.model.MahjongOperation;
import com.yude.game.common.model.history.Step;
import com.yude.game.common.model.sichuan.constant.SichuanGameStatusEnum;
import com.yude.game.common.model.sichuan.history.info.ChaJiaoInfo;

import java.util.List;
import java.util.Map;

/**
 * @Author: HH
 * @Date: 2020/9/10 19:48
 * @Version: 1.0
 * @Declare:
 */
public class ChaJiaoStep implements Step {
    private int stepCount;
    private Map<Integer, List<ChaJiaoInfo>> chaJiaoInfoMap;
    private Status gameStatus = SichuanGameStatusEnum.LIU_JU_SETTLEMENT;
    private MahjongOperation operation = OperationEnum.CHA_JIAO;

    public List<ChaJiaoInfo> getChaJiaoInfoListByPosId(Integer posId){
        return chaJiaoInfoMap.get(posId);
    }

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

    public int getStepCount() {
        return stepCount;
    }

    public ChaJiaoStep setStepCount(int stepCount) {
        this.stepCount = stepCount;
        return this;
    }

    @Override
    public Status gameStatus() {
        return gameStatus;
    }

    @Override
    public Integer actionType() {
        return operation.value();
    }

    @Override
    public int posId() {
        return 0;
    }

    @Override
    public String toString() {
        return "ChaJiaoStep{" +
                "stepCount=" + stepCount +
                ", chaJiaoInfoMap=" + chaJiaoInfoMap +
                ", operation=" + operation +
                ", gameStatus=" + gameStatus +
                '}';
    }
}
