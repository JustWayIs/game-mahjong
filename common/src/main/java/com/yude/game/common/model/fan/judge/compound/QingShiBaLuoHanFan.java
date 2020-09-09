package com.yude.game.common.model.fan.judge.compound;

import com.yude.game.common.model.fan.BaseHuTypeEnum;
import com.yude.game.common.model.fan.CompoundFanTypeEnum;
import com.yude.game.common.model.fan.FanType;
import com.yude.game.common.model.fan.FormalFanTypeEnum;
import com.yude.game.common.model.fan.judge.Fan;
import com.yude.game.common.model.fan.param.CompoundFanParam;

/**
 * @Author: HH
 * @Date: 2020/8/17 21:20
 * @Version: 1.0
 * @Declare:
 */
public enum QingShiBaLuoHanFan implements Fan<CompoundFanParam> {
    /**
     * 单例
     */
    INSTANCE(CompoundFanTypeEnum.清十八罗汉);

    public FanType fantype;

    QingShiBaLuoHanFan(FanType fantype) {
        this.fantype = fantype;
    }

    @Override
    public FanType judge(CompoundFanParam param) {
        BaseHuTypeEnum baseHuType = param.getBaseHuType();
        if(BaseHuTypeEnum.平胡.equals(baseHuType)){
            int num = 0;
            for(FanType fanType : param.getFanTypeList()){
                if(FormalFanTypeEnum.清一色.equals(fanType)){
                    num++;
                }
                if(FormalFanTypeEnum.十八罗汉.equals(fanType)){
                    num++;
                }
            }
            if(num == 2){
                return fantype;
            }
        }
        return null;
    }
}
