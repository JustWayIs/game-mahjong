package com.yude.game.common.model.history;

import com.yude.game.common.model.Player;

/**
 * @Author: HH
 * @Date: 2020/8/20 10:39
 * @Version: 1.0
 * @Declare:
 */
public class GameStepModel<T extends Step> implements GameHistory {
    private Integer zoneId;
    private Player players;
    private T operationStep;

    public GameStepModel(Integer zoneId, Player players, T operationStep) {
        this.zoneId = zoneId;
        this.players = players;
        this.operationStep = operationStep;
    }

    public Integer getZoneId() {
        return zoneId;
    }

    public Player getPlayers() {
        return players;
    }

    public T getOperationStep() {
        return operationStep;
    }

    public GameStepModel<T> setOperationStep(T operationStep) {
        this.operationStep = operationStep;
        return this;
    }

    @Override
    public String toString() {
        return "GameStepModel{" +
                "zoneId=" + zoneId +
                ", players=" + players +
                ", operationStep=" + operationStep +
                '}';
    }
}
