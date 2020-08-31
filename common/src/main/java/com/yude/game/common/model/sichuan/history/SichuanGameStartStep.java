package com.yude.game.common.model.sichuan.history;

import com.yude.game.common.constant.Status;
import com.yude.game.common.model.history.GameStartStep;
import com.yude.game.common.model.history.Step;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/28 15:03
 * @Version: 1.0
 * @Declare:
 */
public class SichuanGameStartStep implements Step {
    private GameStartStep gameStartStep;
    private List<Integer> recommendedCardList;

    public GameStartStep getGameStartStep() {
        return gameStartStep;
    }

    public SichuanGameStartStep setGameStartStep(GameStartStep gameStartStep) {
        this.gameStartStep = gameStartStep;
        return this;
    }

    public List<Integer> getRecommendedCardList() {
        return recommendedCardList;
    }

    public SichuanGameStartStep setRecommendedCardList(List<Integer> recommendedCardList) {
        this.recommendedCardList = recommendedCardList;
        return this;
    }

    @Override
    public String toString() {
        return "SichuanGameStartStep{" +
                "gameStartStep=" + gameStartStep +
                ", recommendedCardList=" + recommendedCardList +
                '}';
    }

    @Override
    public Status stepType() {
        return gameStartStep.getGameStatus();
    }

    @Override
    public int posId() {
        return gameStartStep.getPosId();
    }
}
