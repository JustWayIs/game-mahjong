package com.yude.game.common.model.fan.judge.formal;

import com.yude.game.common.model.fan.FanType;
import com.yude.game.common.model.fan.FormalFanTypeEnum;
import com.yude.game.common.model.fan.judge.Fan;
import com.yude.game.common.model.fan.param.FormalFanParam;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/17 21:16
 * @Version: 1.0
 * @Declare:
 */
public enum JinGouDiaoFan implements Fan<FormalFanParam> {
    /**
     * 单例
     */
    INSTANCE(FormalFanTypeEnum.金钩钓);

    public FanType fantype;

    JinGouDiaoFan(FanType fantype) {
        this.fantype = fantype;
    }

    @Override
    public FanType judge(FormalFanParam param) {
        List<Integer> wanList = param.getWanList();
        List<Integer> tongList = param.getTongList();
        List<Integer> tiaoList = param.getTiaoList();

        int wanCount = wanList.size();
        int tongCount = tongList.size();
        int tiaoCount = tiaoList.size();
        if (wanCount + tongCount + tiaoCount == 2) {
            return fantype;
        }
        return null;
    }
}
