package com.yude.game.common.model.fan.judge.base;

import com.yude.game.common.model.fan.BaseHuTypeEnum;
import com.yude.game.common.model.fan.FanType;
import com.yude.game.common.model.fan.judge.Fan;
import com.yude.game.common.model.fan.param.BaseHuParam;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/5 17:33
 * @Version: 1.0
 * @Declare:
 */
public enum QiDuiFan implements Fan<BaseHuParam> {
    /**
     *
     */
    INSTANCE(BaseHuTypeEnum.七对);

    private FanType baseHuType;

    QiDuiFan(FanType baseHuType) {
        this.baseHuType = baseHuType;
    }

    @Override
    public FanType judge(BaseHuParam param) {
        List<Integer> standCardList = param.getStandCardList();
        int beforeCard = 0;
        int cardNum = 1;
        int doubleCardNum = 0;

        int size = standCardList.size();
        if(size != 14){
            return null;
        }

        for(int i = 0 ; i < size ; ++i){
            Integer card = standCardList.get(i);
            if(beforeCard != card){
                beforeCard = card;
            }else{
                doubleCardNum++;
                ++i;
                if(i < size){
                    beforeCard = standCardList.get(i);
                }
            }
            cardNum = 1;
        }
        if(doubleCardNum !=  7){
            return null;
        }
        return baseHuType;
    }
}
