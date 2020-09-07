package com.yude.game.common.model.fan.judge.base;

import com.yude.game.common.model.fan.BaseHuTypeEnum;
import com.yude.game.common.model.fan.FanType;
import com.yude.game.common.model.fan.judge.Fan;
import com.yude.game.common.model.fan.param.BaseHuParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: HH
 * @Date: 2020/9/5 17:32
 * @Version: 1.0
 * @Declare:
 */
public enum ShiSanYaoFan implements Fan<BaseHuParam> {
    /**
     *
     */
    INSTANCE(BaseHuTypeEnum.十三幺);

    private FanType baseHuType;

    ShiSanYaoFan(FanType baseHuType) {
        this.baseHuType = baseHuType;
    }

    @Override
    public FanType judge(BaseHuParam param) {
        List<Integer> standCardList = new ArrayList<>(param.getStandCardList());
        int size = standCardList.size();
        if(size != 14){
            return null;
        }
        List<Integer> distinctCardList = standCardList.stream().distinct().collect(Collectors.toList());
        if(distinctCardList.size() != 13){
            return null;
        }
        standCardList.removeAll(distinctCardList);
        if(standCardList.size() != 0){
            return null;
        }
        //没必要再判断具体牌值了，14张牌，只有一对，还能在前面的胡牌判断里认定为胡牌，那么基础胡牌就只能是十三幺了
        /*for(Integer card : param.getStandCardList()){
            if(!card.equals()){
                return null;
            }
        }*/
        return baseHuType;
    }
}
