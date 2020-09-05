package com.yude.game.common.model.sichuan.history;

import com.yude.game.common.constant.Status;
import com.yude.game.common.model.history.Step;
import com.yude.game.common.model.sichuan.constant.ExchangeTypeEnum;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/28 10:31
 * @Version: 1.0
 * @Declare:
 */
public class GainedCardStep implements Step {
    private int step;
    private int posId;
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
}
