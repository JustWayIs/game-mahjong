package com.yude.game.common.model.sichuan.history;

import com.yude.game.common.constant.Status;
import com.yude.game.common.model.MahjongOperation;
import com.yude.game.common.model.history.Step;
import com.yude.game.common.model.sichuan.constant.SichuanGameStatusEnum;
import com.yude.game.common.model.sichuan.history.info.ChaHuaZhuInfo;

import java.util.List;
import java.util.Map;

/**
 * @Author: HH
 * @Date: 2020/9/10 19:49
 * @Version: 1.0
 * @Declare:
 */
public class ChaHuaZhuStep implements Step {
    private int stepCount;
    private Map<Integer, List<ChaHuaZhuInfo>> chaHuaZhuInfoMap;
    private MahjongOperation operation;
    private Status gameStatus = SichuanGameStatusEnum.LIU_JU_SETTLEMENT;


    public List<ChaHuaZhuInfo> getChaHuaZhuInfoListByPosId(Integer posId){
        List<ChaHuaZhuInfo> chaHuaZhuInfos = chaHuaZhuInfoMap.get(posId);
        return chaHuaZhuInfos;
    }

    public Map<Integer, List<ChaHuaZhuInfo>> getChaHuaZhuInfoMap() {
        return chaHuaZhuInfoMap;
    }

    public ChaHuaZhuStep setChaHuaZhuInfoMap(Map<Integer, List<ChaHuaZhuInfo>> chaHuaZhuInfoMap) {
        this.chaHuaZhuInfoMap = chaHuaZhuInfoMap;
        return this;
    }

    public Status getGameStatus() {
        return gameStatus;
    }

    public ChaHuaZhuStep setGameStatus(Status gameStatus) {
        this.gameStatus = gameStatus;
        return this;
    }

    public MahjongOperation getOperation() {
        return operation;
    }

    public ChaHuaZhuStep setOperation(MahjongOperation operation) {
        this.operation = operation;
        return this;
    }

    public int getStepCount() {
        return stepCount;
    }

    public ChaHuaZhuStep setStepCount(int stepCount) {
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
        return "ChaHuaZhuStep{" +
                "stepCount=" + stepCount +
                ", chaHuaZhuInfoMap=" + chaHuaZhuInfoMap +
                ", operation=" + operation +
                ", gameStatus=" + gameStatus +
                '}';
    }
}
