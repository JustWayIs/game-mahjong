package com.yude.game.common.application.response;

import com.yude.protocol.common.response.BaseResponse;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/20 14:50
 * @Version: 1.0
 * @Declare:
 */
public class GameStartResponse extends BaseResponse {
    protected Long roomId;
    protected Integer zoneId;
    protected Integer step;
    protected List<Integer> diceList;
    protected Integer bankerPosId;
    protected Integer posId;
    protected List<Integer> standCardList;
    protected Integer gameStatus;

    public GameStartResponse setRoomId(Long roomId) {
        this.roomId = roomId;
        return this;
    }

    public GameStartResponse setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
        return this;
    }

    public GameStartResponse setStep(Integer step) {
        this.step = step;
        return this;
    }

    public GameStartResponse setDiceList(List<Integer> diceList) {
        this.diceList = diceList;
        return this;
    }

    public GameStartResponse setBankerPosId(Integer bankerPosId) {
        this.bankerPosId = bankerPosId;
        return this;
    }

    public GameStartResponse setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }

    public GameStartResponse setStandCardList(List<Integer> standCardList) {
        this.standCardList = standCardList;
        return this;
    }

    public GameStartResponse setGameStatus(Integer gameStatus) {
        this.gameStatus = gameStatus;
        return this;
    }
}
