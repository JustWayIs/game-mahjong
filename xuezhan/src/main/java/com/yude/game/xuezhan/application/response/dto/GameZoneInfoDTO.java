package com.yude.game.xuezhan.application.response.dto;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/27 10:23
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class GameZoneInfoDTO {
    protected Long roomId;
    protected Integer zoneId;
    protected Integer step;
    protected List<Integer> diceList;
    protected Integer bankerPosId;
    private Integer cardWallRemainingSize;
    protected Integer gameStatus;
    /**
     * 当前操作人如果不是当前出牌玩家的话，是不能告诉其他玩家的
     */
    private Integer currentOperatorPosId;
    private Integer currentTookCardPosId;

    private Integer exchangeType;

    public Long getRoomId() {
        return roomId;
    }

    public GameZoneInfoDTO setRoomId(Long roomId) {
        this.roomId = roomId;
        return this;
    }

    public Integer getZoneId() {
        return zoneId;
    }

    public GameZoneInfoDTO setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
        return this;
    }

    public Integer getStep() {
        return step;
    }

    public GameZoneInfoDTO setStep(Integer step) {
        this.step = step;
        return this;
    }

    public List<Integer> getDiceList() {
        return diceList;
    }

    public GameZoneInfoDTO setDiceList(List<Integer> diceList) {
        this.diceList = diceList;
        return this;
    }

    public Integer getBankerPosId() {
        return bankerPosId;
    }

    public GameZoneInfoDTO setBankerPosId(Integer bankerPosId) {
        this.bankerPosId = bankerPosId;
        return this;
    }

    public Integer getGameStatus() {
        return gameStatus;
    }

    public GameZoneInfoDTO setGameStatus(Integer gameStatus) {
        this.gameStatus = gameStatus;
        return this;
    }

    public Integer getCurrentOperatorPosId() {
        return currentOperatorPosId;
    }

    public GameZoneInfoDTO setCurrentOperatorPosId(Integer currentOperatorPosId) {
        this.currentOperatorPosId = currentOperatorPosId;
        return this;
    }

    public Integer getCurrentTookCardPosId() {
        return currentTookCardPosId;
    }

    public GameZoneInfoDTO setCurrentTookCardPosId(Integer currentTookCardPosId) {
        this.currentTookCardPosId = currentTookCardPosId;
        return this;
    }

    public Integer getExchangeType() {
        return exchangeType;
    }

    public GameZoneInfoDTO setExchangeType(Integer exchangeType) {
        this.exchangeType = exchangeType;
        return this;
    }

    public Integer getCardWallRemainingSize() {
        return cardWallRemainingSize;
    }

    public GameZoneInfoDTO setCardWallRemainingSize(Integer cardWallRemainingSize) {
        this.cardWallRemainingSize = cardWallRemainingSize;
        return this;
    }

    @Override
    public String toString() {
        return "GameZoneInfoDTO{" +
                "roomId=" + roomId +
                ", zoneId=" + zoneId +
                ", step=" + step +
                ", diceList=" + diceList +
                ", bankerPosId=" + bankerPosId +
                ", cardWallRemainingSize=" + cardWallRemainingSize +
                ", gameStatus=" + gameStatus +
                ", currentOperatorPosId=" + currentOperatorPosId +
                ", currentTookCardPosId=" + currentTookCardPosId +
                ", exchangeType=" + exchangeType +
                '}';
    }
}
