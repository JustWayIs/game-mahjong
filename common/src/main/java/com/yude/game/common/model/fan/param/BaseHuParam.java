package com.yude.game.common.model.fan.param;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/17 16:09
 * @Version: 1.0
 * @Declare:
 */
public class BaseHuParam implements HuFanParam {
    //立牌
    private List<Integer> standCardList;

    /**
     * 将：七对是没有将的，或者说有七对将
     */
    private Integer eyes;

    public BaseHuParam(List<Integer> standCardList, Integer eyes) {
        this.standCardList = standCardList;
        this.eyes = eyes;
    }

    public List<Integer> getStandCardList() {
        return standCardList;
    }

    public BaseHuParam setStandCardList(List<Integer> standCardList) {
        this.standCardList = standCardList;
        return this;
    }

    public Integer getEyes() {
        return eyes;
    }

    public BaseHuParam setEyes(Integer eyes) {
        this.eyes = eyes;
        return this;
    }

    @Override
    public String toString() {
        return "BaseHuParam{" +
                "standCardList=" + standCardList +
                ", eyes=" + eyes +
                '}';
    }
}
