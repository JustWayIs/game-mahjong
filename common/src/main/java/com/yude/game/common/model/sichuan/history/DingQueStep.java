package com.yude.game.common.model.sichuan.history;

import com.yude.game.common.constant.Status;
import com.yude.game.common.model.history.Step;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/20 11:45
 * @Version: 1.0
 * @Declare:
 */
public class DingQueStep implements Step {
    private int step;
    private int posId;
    private int color;
    private List<Integer> handCards;
    private List<String> handCardConvertList;
    private Status gameStatus;

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

    public DingQueStep setPosId(int posId) {
        this.posId = posId;
        return this;
    }

    public int getStep() {
        return step;
    }

    public DingQueStep setStep(int step) {
        this.step = step;
        return this;
    }

    public int getColor() {
        return color;
    }

    public DingQueStep setColor(int color) {
        this.color = color;
        return this;
    }

    public List<Integer> getHandCards() {
        return handCards;
    }

    public DingQueStep setHandCards(List<Integer> handCards) {
        this.handCards = handCards;
        return this;
    }

    public List<String> getHandCardConvertList() {
        return handCardConvertList;
    }

    public DingQueStep setHandCardConvertList(List<String> handCardConvertList) {
        this.handCardConvertList = handCardConvertList;
        return this;
    }

    public Status getGameStatus() {
        return gameStatus;
    }

    public DingQueStep setGameStatus(Status gameStatus) {
        this.gameStatus = gameStatus;
        return this;
    }

    @Override
    public String toString() {
        return "DingQueStep{" +
                "step=" + step +
                ", color=" + color +
                ", handCards=" + handCards +
                ", handCardConvertList=" + handCardConvertList +
                ", gameStatus=" + gameStatus +
                '}';
    }
}
