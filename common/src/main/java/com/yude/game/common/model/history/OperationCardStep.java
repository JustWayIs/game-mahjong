package com.yude.game.common.model.history;

import com.yude.game.common.constant.Status;
import com.yude.game.common.model.StepAction;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/20 10:57
 * @Version: 1.0
 * @Declare:
 */
public class OperationCardStep implements Step {

    private int step;
    private int posId;
    private StepAction action;
    private int remainingCardSize;
    private List<Integer> standCardList;
    private List<String> standCardConvertList;
    private Status gameStatus;

    public int getStep() {
        return step;
    }

    public OperationCardStep setStep(int step) {
        this.step = step;
        return this;
    }

    @Override
    public Status gameStatus() {
        return gameStatus;
    }

    @Override
    public Integer actionType() {
        return action.getOperationType().value();
    }

    @Override
    public int posId() {
        return posId;
    }

    public int getPosId() {
        return posId;
    }

    public OperationCardStep setPosId(int posId) {
        this.posId = posId;
        return this;
    }

    public StepAction getAction() {
        return action;
    }

    public OperationCardStep setAction(StepAction action) {
        this.action = action;
        return this;
    }

    public int getRemainingCardSize() {
        return remainingCardSize;
    }

    public OperationCardStep setRemainingCardSize(int remainingCardSize) {
        this.remainingCardSize = remainingCardSize;
        return this;
    }

    public List<Integer> getStandCardList() {
        return standCardList;
    }

    public OperationCardStep setStandCardList(List<Integer> standCardList) {
        this.standCardList = standCardList;
        return this;
    }

    public List<String> getStandCardConvertList() {
        return standCardConvertList;
    }

    public OperationCardStep setStandCardConvertList(List<String> standCardConvertList) {
        this.standCardConvertList = standCardConvertList;
        return this;
    }

    public Status getGameStatus() {
        return gameStatus;
    }

    public OperationCardStep setGameStatus(Status gameStatus) {
        this.gameStatus = gameStatus;
        return this;
    }

    @Override
    public String toString() {
        return "OperationCardStep{" +
                "step=" + step +
                ", posId=" + posId +
                ", action=" + action +
                ", remainingCardSize=" + remainingCardSize +
                ", standCardList=" + standCardList +
                ", standCardConvertList=" + standCardConvertList +
                ", gameStatus=" + gameStatus +
                '}';
    }
}
