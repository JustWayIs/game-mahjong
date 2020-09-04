package com.yude.game.xuezhan.application.response;

import com.yude.protocol.common.response.BaseResponse;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/26 20:30
 * @Version: 1.0
 * @Declare:
 */
public class ExchangeCardResultResponse extends BaseResponse {
    private Integer exchangeType;
    private List<Integer> gainedCards;

    public ExchangeCardResultResponse() {
    }

    public ExchangeCardResultResponse(Integer exchangeType, List<Integer> gainedCards) {
        this.exchangeType = exchangeType;
        this.gainedCards = gainedCards;
    }

    public Integer getExchangeType() {
        return exchangeType;
    }

    public ExchangeCardResultResponse setExchangeType(Integer exchangeType) {
        this.exchangeType = exchangeType;
        return this;
    }

    public List<Integer> getGainedCards() {
        return gainedCards;
    }

    public ExchangeCardResultResponse setGainedCards(List<Integer> gainedCards) {
        this.gainedCards = gainedCards;
        return this;
    }

    @Override
    public String toString() {
        return "ExchangeCardResultResponse{" +
                "exchangeType=" + exchangeType +
                ", gainedCards=" + gainedCards +
                '}';
    }
}
