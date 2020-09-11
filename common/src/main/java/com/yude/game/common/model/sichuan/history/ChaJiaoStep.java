package com.yude.game.common.model.sichuan.history;

import com.yude.game.common.constant.Status;
import com.yude.game.common.model.history.Step;
import com.yude.game.common.model.sichuan.constant.SichuanGameStatusEnum;

import java.util.Map;

/**
 * @Author: HH
 * @Date: 2020/9/10 19:48
 * @Version: 1.0
 * @Declare:
 */
public class ChaJiaoStep implements Step {
    private Map<Integer,ChaJiaoInfo> chaJiaoInfoMap;
    private Status gameStatus = SichuanGameStatusEnum.CHA_JIAO_SETTLEMENT;

    public Map<Integer, ChaJiaoInfo> getChaJiaoInfoMap() {
        return chaJiaoInfoMap;
    }

    public ChaJiaoStep setChaJiaoInfoMap(Map<Integer, ChaJiaoInfo> chaJiaoInfoMap) {
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
