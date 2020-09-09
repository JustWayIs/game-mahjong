package com.yude.game.common.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/20 11:54
 * @Version: 1.0
 * @Declare:
 */
public enum CardEnum{
    /**
     * 所有麻将
     */
    万(new ArrayList(Arrays.asList(11, 12, 13, 14, 15, 16, 17, 18, 19))),
    条(new ArrayList(Arrays.asList(21, 22, 23, 24, 25, 26, 27, 28, 29))),
    筒(new ArrayList(Arrays.asList(31, 32, 33, 34, 35, 36, 37, 38, 39))),
    风牌(new ArrayList(Arrays.asList(41,42,43,44))),
    箭牌(new ArrayList<>(Arrays.asList(51,52,53))),
    花牌(new ArrayList<>(Arrays.asList(61,62,63,64,65,66,67,68)));

    public List<Integer> cards;

    CardEnum(List<Integer> cards) {
        this.cards = cards;
    }

    public List<Integer> cards() {
        return cards;
    }

    public Integer getColor(){
        return this.ordinal()+1;
    }

    public static CardEnum judgeCardColor(Integer card){
        for(CardEnum cardEnum : CardEnum.values()){
            if(cardEnum.getColor() == (card/10)){
                return cardEnum;
            }
        }
        return null;
    }
}
