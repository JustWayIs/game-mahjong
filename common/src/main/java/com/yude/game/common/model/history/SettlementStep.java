package com.yude.game.common.model.history;

import com.yude.game.common.constant.Status;
import com.yude.game.common.model.StepAction;

import java.util.Map;

/**
 * @Author: HH
 * @Date: 2020/8/20 11:12
 * @Version: 1.0
 * @Declare:
 */
public class SettlementStep implements Step {
    private int step;
    private int posId;
    private StepAction action;
    private Map<Integer,SettlementInfo> seatSettlementInfoMap;
    private Status gameStatus;

    public int getStep() {
        return step;
    }

    public SettlementStep setStep(int step) {
        this.step = step;
        return this;
    }

    @Override
    public Status stepType() {
        return gameStatus;
    }

    @Override
    public int posId() {
        return posId;
    }

    public int getPosId() {
        return posId;
    }

    public SettlementStep setPosId(int posId) {
        this.posId = posId;
        return this;
    }

    public StepAction getAction() {
        return action;
    }

    public SettlementStep setAction(StepAction action) {
        this.action = action;
        return this;
    }

    public Map<Integer, SettlementInfo> getSeatSettlementInfoMap() {
        return seatSettlementInfoMap;
    }

    public SettlementStep setSeatSettlementInfoMap(Map<Integer, SettlementInfo> seatSettlementInfoMap) {
        this.seatSettlementInfoMap = seatSettlementInfoMap;
        return this;
    }

    public Status getGameStatus() {
        return gameStatus;
    }

    public SettlementStep setGameStatus(Status gameStatus) {
        this.gameStatus = gameStatus;
        return this;
    }

    @Override
    public String toString() {
        return "SettlementStep{" +
                "step=" + step +
                ", posId=" + posId +
                ", action=" + action +
                ", seatSettlementInfoMap=" + seatSettlementInfoMap +
                ", gameStatus=" + gameStatus +
                '}';
    }
}
