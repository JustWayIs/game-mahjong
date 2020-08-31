package com.yude.game.common.model.sichuan;

import com.yude.game.common.model.CardEnum;
import com.yude.game.common.model.MahjongCard;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/18 17:44
 * @Version: 1.0
 * @Declare:
 */
public enum SichuanMahjongCard implements MahjongCard {
    /**
     * 四川麻将：
     */
    万(CardEnum.万.cards,4),
    条(CardEnum.条.cards,4),
    筒(CardEnum.筒.cards,4);


    public List<Integer> cards;
    public int cardCount;

    SichuanMahjongCard(List<Integer> cards,int cardCount) {
        this.cards = cards;
        this.cardCount = cardCount;
    }


    @Override
    public List<Integer> getCards() {
        return cards;
    }

    @Override
    public int getCardCount() {
        return cardCount;
    }
}
