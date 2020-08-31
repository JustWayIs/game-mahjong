package com.yude.game.common.model.sichuan.history;

import com.yude.game.common.constant.Status;
import com.yude.game.common.model.history.Step;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/28 10:26
 * @Version: 1.0
 * @Declare:
 */
public class DiscardCardStep implements Step {
    private int step;
    private int posId;
    /**
     * 给出去的牌
     */
    private List<Integer> discardCards;
    /**
     * 排除了给出去的牌的剩余立牌
     */
    private List<Integer> standCardList;
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

    public void setStep(int step) {
        this.step = step;
    }

    public int getPosId() {
        return posId;
    }

    public void setPosId(int posId) {
        this.posId = posId;
    }

    public List<Integer> getDiscardCards() {
        return discardCards;
    }

    public void setDiscardCards(List<Integer> discardCards) {
        this.discardCards = discardCards;
    }

    public List<Integer> getStandCardList() {
        return standCardList;
    }

    public void setStandCardList(List<Integer> standCardList) {
        this.standCardList = standCardList;
    }

    public List<String> getStandCardConvertList() {
        return standCardConvertList;
    }

    public void setStandCardConvertList(List<String> standCardConvertList) {
        this.standCardConvertList = standCardConvertList;
    }

    public Status getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(Status gameStatus) {
        this.gameStatus = gameStatus;
    }
}
