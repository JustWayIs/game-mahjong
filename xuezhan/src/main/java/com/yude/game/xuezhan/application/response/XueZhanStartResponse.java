package com.yude.game.xuezhan.application.response;

import com.yude.game.common.application.response.GameStartResponse;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/26 20:20
 * @Version: 1.0
 * @Declare:
 */
public class XueZhanStartResponse extends GameStartResponse {
    private List<Integer> recommendExchangeList;

    public XueZhanStartResponse() {
    }

    public XueZhanStartResponse(List<Integer> recommendExchangeList) {
        this.recommendExchangeList = recommendExchangeList;
    }

    public List<Integer> getRecommendExchangeList() {
        return recommendExchangeList;
    }

    public XueZhanStartResponse setRecommendExchangeList(List<Integer> recommendExchangeList) {
        this.recommendExchangeList = recommendExchangeList;
        return this;
    }

    @Override
    public String toString() {
        return "XueZhanStartResponse{" +
                "recommendExchangeList=" + recommendExchangeList +
                ", roomId=" + roomId +
                ", zoneId=" + zoneId +
                ", step=" + step +
                ", diceList=" + diceList +
                ", bankerPosId=" + bankerPosId +
                ", posId=" + posId +
                ", standCardList=" + standCardList +
                ", gameStatus=" + gameStatus +
                '}';
    }
}
