package com.yude.game.common.model.history;

import com.yude.game.common.constant.Status;
import com.yude.game.common.model.CardAction;

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
    private CardAction action;
    private int remainingCardSize;
    private List<Integer> handCards;
    private List<String> handCardConvertList;
    private Status gameStatus;

    public int getStep() {
        return step;
    }

    public OperationCardStep setStep(int step) {
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

    public OperationCardStep setPosId(int posId) {
        this.posId = posId;
        return this;
    }

    public CardAction getAction() {
        return action;
    }

    public OperationCardStep setAction(CardAction action) {
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

    public List<Integer> getHandCards() {
        return handCards;
    }

    public OperationCardStep setHandCards(List<Integer> handCards) {
        this.handCards = handCards;
        return this;
    }

    public List<String> getHandCardConvertList() {
        return handCardConvertList;
    }

    public OperationCardStep setHandCardConvertList(List<String> handCardConvertList) {
        this.handCardConvertList = handCardConvertList;
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
                ", handCards=" + handCards +
                ", handCardConvertList=" + handCardConvertList +
                ", gameStatus=" + gameStatus +
                '}';
    }
}
