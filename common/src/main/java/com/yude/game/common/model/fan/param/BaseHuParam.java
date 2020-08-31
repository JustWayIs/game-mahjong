package com.yude.game.common.model.fan.param;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/17 16:09
 * @Version: 1.0
 * @Declare:
 */
public class BaseHuParam implements HuFanParam {
    //立牌 + 副露
    private List<Integer> handCardList;
    //将
    private Integer eyes;

    public BaseHuParam(List<Integer> handCardList) {
        this.handCardList = handCardList;
    }

    public BaseHuParam(List<Integer> handCardList, Integer eyes) {
        this.handCardList = handCardList;
        this.eyes = eyes;
    }

    public List<Integer> getHandCardList() {
        return handCardList;
    }

    public Integer getEyes() {
        return eyes;
    }

    public BaseHuParam setEyes(Integer eyes) {
        this.eyes = eyes;
        return this;
    }
}
