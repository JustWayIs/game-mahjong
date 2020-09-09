package com.yude.game.common.model.fan.judge.formal;

import com.yude.game.common.model.fan.FanType;
import com.yude.game.common.model.fan.FormalFanTypeEnum;
import com.yude.game.common.model.fan.judge.Fan;
import com.yude.game.common.model.fan.param.FormalFanParam;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/17 20:52
 * @Version: 1.0
 * @Declare:
 */
public enum  BigDoubleFan implements Fan<FormalFanParam> {
    /**
     * 单例
     */
    INSTANCE(FormalFanTypeEnum.大对子);

    public FanType fanType;

    BigDoubleFan(FanType fanType) {
        this.fanType = fanType;
    }

    @Override
    public FanType judge(FormalFanParam param) {
        List<Integer> duiziList = param.getDuiziList();
        if(duiziList == null){
            return null;
        }
        if(duiziList.size() != 1){
            return null;
        }
        List<Integer> pengList = param.getPengList();
        List<Integer> keZiList = param.getKeziList();
        int pengCount = pengList.size();
        int keZiCount = keZiList.size();
        if(pengCount + keZiCount != 4){
            return null;
        }

        return fanType;
    }
}
