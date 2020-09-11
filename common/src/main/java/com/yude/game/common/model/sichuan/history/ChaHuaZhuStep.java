package com.yude.game.common.model.sichuan.history;

import com.yude.game.common.constant.Status;
import com.yude.game.common.model.history.Step;
import com.yude.game.common.model.sichuan.constant.SichuanGameStatusEnum;

import java.util.Map;

/**
 * @Author: HH
 * @Date: 2020/9/10 19:49
 * @Version: 1.0
 * @Declare:
 */
public class ChaHuaZhuStep implements Step {
    private Map<Integer,ChaHuaZhuInfo> chaHuaZhuInfoMap;
    private Status gameStatus = SichuanGameStatusEnum.CHA_HUA_ZHU_SETTLEMENT;

    public Map<Integer, ChaHuaZhuInfo> getChaHuaZhuInfoMap() {
        return chaHuaZhuInfoMap;
    }

    public ChaHuaZhuStep setChaHuaZhuInfoMap(Map<Integer, ChaHuaZhuInfo> chaHuaZhuInfoMap) {
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
}
