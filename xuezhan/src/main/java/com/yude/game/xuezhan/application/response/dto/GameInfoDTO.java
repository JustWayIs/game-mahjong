package com.yude.game.xuezhan.application.response.dto;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/14 19:29
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class GameInfoDTO {
    private Integer gameStatus;
    private List<Integer> dice;
    private Integer bankerPosId;
    private Integer cardWallRemainingSize;
    private Integer currentTookCardPosId;
    private Integer currentOperationPosId;

    public Integer getGameStatus() {
        return gameStatus;
    }

    public GameInfoDTO setGameStatus(Integer gameStatus) {
        this.gameStatus = gameStatus;
        return this;
    }

    public List<Integer> getDice() {
        return dice;
    }

    public GameInfoDTO setDice(List<Integer> dice) {
        this.dice = dice;
        return this;
    }

    public Integer getBankerPosId() {
        return bankerPosId;
    }

    public GameInfoDTO setBankerPosId(Integer bankerPosId) {
        this.bankerPosId = bankerPosId;
        return this;
    }

    public Integer getCardWallRemainingSize() {
        return cardWallRemainingSize;
    }

    public GameInfoDTO setCardWallRemainingSize(Integer cardWallRemainingSize) {
        this.cardWallRemainingSize = cardWallRemainingSize;
        return this;
    }

    public Integer getCurrentTookCardPosId() {
        return currentTookCardPosId;
    }

    public GameInfoDTO setCurrentTookCardPosId(Integer currentTookCardPosId) {
        this.currentTookCardPosId = currentTookCardPosId;
        return this;
    }

    public Integer getCurrentOperationPosId() {
        return currentOperationPosId;
    }

    public GameInfoDTO setCurrentOperationPosId(Integer currentOperationPosId) {
        this.currentOperationPosId = currentOperationPosId;
        return this;
    }

    @Override
    public String toString() {
        return "GameInfoDTO{" +
                "gameStatus=" + gameStatus +
                ", dice=" + dice +
                ", bankerPosId=" + bankerPosId +
                ", cardWallRemainingSize=" + cardWallRemainingSize +
                ", currentTookCardPosId=" + currentTookCardPosId +
                ", currentOperationPosId=" + currentOperationPosId +
                '}';
    }
}
