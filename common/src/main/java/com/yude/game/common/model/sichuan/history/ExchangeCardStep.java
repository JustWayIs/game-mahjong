package com.yude.game.common.model.sichuan.history;

import com.yude.game.common.constant.Status;
import com.yude.game.common.model.history.Step;
import com.yude.game.common.model.sichuan.ExchangeTypeEnum;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/20 11:20
 * @Version: 1.0
 * @Declare:
 */
public class ExchangeCardStep implements Step {
    private int step;
    private int posId;
    private List<Integer> discardCards;
    private List<Integer> gainedCards;
    private ExchangeTypeEnum exchangeType;
    private List<Integer> standCards;
    private List<String> standCardConvertList;
    private Status gameStatus;

    @Override
    public Status stepType() {
        return gameStatus;
    }

    @Override
    public int posId() {
        return posId;
    }


    public int getStep() {
        return step;
    }

    public ExchangeCardStep setStep(int step) {
        this.step = step;
        return this;
    }

    public int getPosId() {
        return posId;
    }

    public ExchangeCardStep setPosId(int posId) {
        this.posId = posId;
        return this;
    }

    public List<Integer> getDiscardCards() {
        return discardCards;
    }

    public ExchangeCardStep setDiscardCards(List<Integer> discardCards) {
        this.discardCards = discardCards;
        return this;
    }

    public List<Integer> getGainedCards() {
        return gainedCards;
    }

    public ExchangeCardStep setGainedCards(List<Integer> gainedCards) {
        this.gainedCards = gainedCards;
        return this;
    }

    public ExchangeTypeEnum getExchangeType() {
        return exchangeType;
    }

    public ExchangeCardStep setExchangeType(ExchangeTypeEnum exchangeType) {
        this.exchangeType = exchangeType;
        return this;
    }

    public List<Integer> getStandCards() {
        return standCards;
    }

    public ExchangeCardStep setStandCards(List<Integer> standCards) {
        this.standCards = standCards;
        return this;
    }

    public List<String> getStandCardConvertList() {
        return standCardConvertList;
    }

    public ExchangeCardStep setStandCardConvertList(List<String> standCardConvertList) {
        this.standCardConvertList = standCardConvertList;
        return this;
    }

    public Status getGameStatus() {
        return gameStatus;
    }

    public ExchangeCardStep setGameStatus(Status gameStatus) {
        this.gameStatus = gameStatus;
        return this;
    }

    @Override
    public String toString() {
        return "ExchangeCardStep{" +
                "step=" + step +
                ", posId=" + posId +
                ", discardCards=" + discardCards +
                ", gainedCards=" + gainedCards +
                ", exchangeType=" + exchangeType +
                ", standCards=" + standCards +
                ", standCardConvertList=" + standCardConvertList +
                ", gameStatus=" + gameStatus +
                '}';
    }
}
