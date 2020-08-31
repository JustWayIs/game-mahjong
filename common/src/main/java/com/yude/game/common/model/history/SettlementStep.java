package com.yude.game.common.model.history;

import com.yude.game.common.constant.Status;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/20 11:12
 * @Version: 1.0
 * @Declare:
 */
public class SettlementStep implements Step {
    private int step;
    private int posId;
    private Long beforeScore;
    private Long changeScore;
    private Long remaningScore;
    private List<Integer> handCards;
    private List<String> handCardsConvertList;
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

    public Long getBeforeScore() {
        return beforeScore;
    }

    public SettlementStep setBeforeScore(Long beforeScore) {
        this.beforeScore = beforeScore;
        return this;
    }

    public Long getChangeScore() {
        return changeScore;
    }

    public SettlementStep setChangeScore(Long changeScore) {
        this.changeScore = changeScore;
        return this;
    }

    public Long getRemaningScore() {
        return remaningScore;
    }

    public SettlementStep setRemaningScore(Long remaningScore) {
        this.remaningScore = remaningScore;
        return this;
    }

    public List<Integer> getHandCards() {
        return handCards;
    }

    public SettlementStep setHandCards(List<Integer> handCards) {
        this.handCards = handCards;
        return this;
    }

    public List<String> getHandCardsConvertList() {
        return handCardsConvertList;
    }

    public SettlementStep setHandCardsConvertList(List<String> handCardsConvertList) {
        this.handCardsConvertList = handCardsConvertList;
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
                ", beforeScore=" + beforeScore +
                ", changeScore=" + changeScore +
                ", remaningScore=" + remaningScore +
                ", handCards=" + handCards +
                ", handCardsConvertList=" + handCardsConvertList +
                ", gameStatus=" + gameStatus +
                '}';
    }
}
