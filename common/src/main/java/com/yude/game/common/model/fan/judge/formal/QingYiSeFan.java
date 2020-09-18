package com.yude.game.common.model.fan.judge.formal;

import com.yude.game.common.model.CardEnum;
import com.yude.game.common.model.fan.FanType;
import com.yude.game.common.model.fan.FormalFanTypeEnum;
import com.yude.game.common.model.fan.judge.Fan;
import com.yude.game.common.model.fan.param.FormalFanParam;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/17 20:58
 * @Version: 1.0
 * @Declare:
 */
public enum  QingYiSeFan implements Fan<FormalFanParam> {
    /**
     * 单例
     */
    INSTANCE(FormalFanTypeEnum.清一色);

    public FanType fanType;

    QingYiSeFan(FanType fanType) {
        this.fanType = fanType;
    }

    @Override
    public FanType judge(FormalFanParam param) {
        List<Integer> wanList = param.getWanList();
        List<Integer> tiaoList = param.getTiaoList();
        List<Integer> tongList = param.getTongList();

        List<Integer> chiList = param.getChiList();
        List<Integer> pengList = param.getPengList();
        List<Integer> zhiGangList = param.getZhiGangList();
        List<Integer> buGangList = param.getBuGangList();
        List<Integer> anGangList = param.getAnGangList();

        List<Integer> allFuluList = new ArrayList<>();
        allFuluList.addAll(chiList);
        allFuluList.addAll(pengList);
        allFuluList.addAll(zhiGangList);
        allFuluList.addAll(buGangList);
        allFuluList.addAll(anGangList);

        if(wanList.size() > 0 && tiaoList.size() == 0 && tongList.size() == 0){
            for(Integer card : allFuluList){
                CardEnum cardEnum = CardEnum.judgeCardColor(card);
                if(!CardEnum.万.equals(cardEnum)){
                    return null;
                }
            }
            return fanType;
        }
        if(tiaoList.size() > 0 && wanList.size() == 0 && tongList.size() == 0){
            for(Integer card : allFuluList){
                CardEnum cardEnum = CardEnum.judgeCardColor(card);
                if(!CardEnum.条.equals(cardEnum)){
                    return null;
                }
            }
            return fanType;
        }
        if(tongList.size() > 0 && wanList.size() == 0 && tiaoList.size() == 0){
            for(Integer card : allFuluList){
                CardEnum cardEnum = CardEnum.judgeCardColor(card);
                if(!CardEnum.筒.equals(cardEnum)){
                    return null;
                }
            }
            return fanType;
        }
        return null;
    }
}
