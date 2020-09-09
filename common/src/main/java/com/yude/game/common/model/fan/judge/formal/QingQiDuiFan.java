package com.yude.game.common.model.fan.judge.formal;

import com.yude.game.common.model.fan.BaseHuTypeEnum;
import com.yude.game.common.model.fan.FanType;
import com.yude.game.common.model.fan.FormalFanTypeEnum;
import com.yude.game.common.model.fan.judge.Fan;
import com.yude.game.common.model.fan.param.FormalFanParam;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/17 21:08
 * @Version: 1.0
 * @Declare:
 */
public enum QingQiDuiFan implements Fan<FormalFanParam> {
    /**
     * 单例
     */
    INSTANCE(FormalFanTypeEnum.清七对);

    public FanType fantype;

    QingQiDuiFan(FanType fantype) {
        this.fantype = fantype;
    }

    @Override
    public FanType judge(FormalFanParam param) {
        BaseHuTypeEnum baseHuType = param.getBaseHuType();
        if(!BaseHuTypeEnum.七对.equals(baseHuType)){
            return null;
        }
        List<Integer> wanList = param.getWanList();
        List<Integer> tiaoList = param.getTiaoList();
        List<Integer> tongList = param.getTongList();
        if(wanList.size() > 0 && tiaoList.size() == 0 && tongList.size() == 0){
            return fantype;
        }
        if(tiaoList.size() > 0 && wanList.size() == 0 && tongList.size() == 0){
           return fantype;
        }
        if(tongList.size() > 0 && wanList.size() == 0 && tiaoList.size() == 0){
            return fantype;
        }
        return null;
    }
}
