package com.yude.game.common.model.history;

import com.yude.game.common.constant.Status;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/20 10:36
 * @Version: 1.0
 * @Declare:
 */
public class GameStartStep implements Step{
    private long roomId;
    private int zoneId;
    private List<Integer> diceList;
    private Integer bankerPosId;
    private int step;
    private int posId;
    //立牌
    private List<Integer> standCards;
    private List<String> standCardCovertList;
    private Status gameStatus;

    @Override
    public Status gameStatus() {
        return gameStatus;
    }

    @Override
    public Integer actionType() {
        return null;
    }

    @Override
    public int posId() {
        return posId;
    }


    public long getRoomId() {
        return roomId;
    }

    public GameStartStep setRoomId(long roomId) {
        this.roomId = roomId;
        return this;
    }

    public int getZoneId() {
        return zoneId;
    }

    public GameStartStep setZoneId(int zoneId) {
        this.zoneId = zoneId;
        return this;
    }

    public List<Integer> getDiceList() {
        return diceList;
    }

    public GameStartStep setDiceList(List<Integer> diceList) {
        this.diceList = diceList;
        return this;
    }

    public Integer getBankerPosId() {
        return bankerPosId;
    }

    public GameStartStep setBankerPosId(Integer bankerPosId) {
        this.bankerPosId = bankerPosId;
        return this;
    }

    public int getStep() {
        return step;
    }

    public GameStartStep setStep(int step) {
        this.step = step;
        return this;
    }


    public int getPosId() {
        return posId;
    }

    public GameStartStep setPosId(int posId) {
        this.posId = posId;
        return this;
    }

    public List<Integer> getStandCards() {
        return standCards;
    }

    public GameStartStep setStandCards(List<Integer> standCards) {
        this.standCards = standCards;
        return this;
    }

    public List<String> getStandCardCovertList() {
        return standCardCovertList;
    }

    public GameStartStep setStandCardCovertList(List<String> standCardCovertList) {
        this.standCardCovertList = standCardCovertList;
        return this;
    }

    public Status getGameStatus() {
        return gameStatus;
    }

    public GameStartStep setGameStatus(Status gameStatus) {
        this.gameStatus = gameStatus;
        return this;
    }

    @Override
    public String toString() {
        return "GameStartStep{" +
                "roomId=" + roomId +
                ", zoneId=" + zoneId +
                ", diceList=" + diceList +
                ", bankerPosId=" + bankerPosId +
                ", step=" + step +
                ", posId=" + posId +
                ", standCards=" + standCards +
                ", standCardCovertList=" + standCardCovertList +
                ", gameStatus=" + gameStatus +
                '}';
    }


}
