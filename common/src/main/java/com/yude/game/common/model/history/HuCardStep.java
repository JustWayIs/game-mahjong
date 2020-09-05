package com.yude.game.common.model.history;

import com.yude.game.common.constant.Status;
import com.yude.game.common.model.StepAction;
import com.yude.game.common.model.fan.FanInfo;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/5 16:19
 * @Version: 1.0
 * @Declare:
 */
public class HuCardStep implements Step {
    private int step;
    private int posId;
    private StepAction action;
    private int remainingCardSize;
    private List<Integer> standCardList;
    private List<String> standCardConvertList;
    private List<FanInfo> fanInfoList;
    private Status gameStatus;

    public int getStep() {
        return step;
    }

    public HuCardStep setStep(int step) {
        this.step = step;
        return this;
    }

    public int getPosId() {
        return posId;
    }

    public HuCardStep setPosId(int posId) {
        this.posId = posId;
        return this;
    }

    public StepAction getAction() {
        return action;
    }

    public HuCardStep setAction(StepAction action) {
        this.action = action;
        return this;
    }

    public int getRemainingCardSize() {
        return remainingCardSize;
    }

    public HuCardStep setRemainingCardSize(int remainingCardSize) {
        this.remainingCardSize = remainingCardSize;
        return this;
    }

    public List<Integer> getStandCardList() {
        return standCardList;
    }

    public HuCardStep setStandCardList(List<Integer> standCardList) {
        this.standCardList = standCardList;
        return this;
    }

    public List<String> getStandCardConvertList() {
        return standCardConvertList;
    }

    public HuCardStep setStandCardConvertList(List<String> standCardConvertList) {
        this.standCardConvertList = standCardConvertList;
        return this;
    }

    public List<FanInfo> getFanInfoList() {
        return fanInfoList;
    }

    public HuCardStep setFanInfoList(List<FanInfo> fanInfoList) {
        this.fanInfoList = fanInfoList;
        return this;
    }

    public Status getGameStatus() {
        return gameStatus;
    }

    public HuCardStep setGameStatus(Status gameStatus) {
        this.gameStatus = gameStatus;
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

    @Override
    public String toString() {
        return "HuCardStep{" +
                "step=" + step +
                ", posId=" + posId +
                ", action=" + action +
                ", remainingCardSize=" + remainingCardSize +
                ", standCardList=" + standCardList +
                ", standCardConvertList=" + standCardConvertList +
                ", fanInfoList=" + fanInfoList +
                ", gameStatus=" + gameStatus +
                '}';
    }
}
