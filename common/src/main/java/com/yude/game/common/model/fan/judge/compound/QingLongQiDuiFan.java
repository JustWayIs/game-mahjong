package com.yude.game.common.model.fan.judge.compound;

import com.yude.game.common.model.fan.BaseHuTypeEnum;
import com.yude.game.common.model.fan.CompoundFanTypeEnum;
import com.yude.game.common.model.fan.FanType;
import com.yude.game.common.model.fan.FormalFanTypeEnum;
import com.yude.game.common.model.fan.judge.Fan;
import com.yude.game.common.model.fan.param.CompoundFanParam;

/**
 * @Author: HH
 * @Date: 2020/9/25 15:36
 * @Version: 1.0
 * @Declare:
 */
public enum  QingLongQiDuiFan implements Fan<CompoundFanParam> {
    /**
     *
     */
    INSTANCE(CompoundFanTypeEnum.清龙七对);

    public FanType fanType;

    QingLongQiDuiFan(FanType fanType) {
        this.fanType = fanType;
    }

    @Override
    public FanType judge(CompoundFanParam param) {
        BaseHuTypeEnum baseHuType = param.getBaseHuType();
        if(BaseHuTypeEnum.七对.equals(baseHuType)){
            int num = 0;
            for(FanType fanType : param.getFanTypeList()){
                if(FormalFanTypeEnum.清一色.equals(fanType)){
                    num++;
                }
                if(FormalFanTypeEnum.龙七对.equals(fanType)){
                    num++;
                }
            }
            if(num == 2){
                return fanType;
            }
        }
        return null;
    }
}
