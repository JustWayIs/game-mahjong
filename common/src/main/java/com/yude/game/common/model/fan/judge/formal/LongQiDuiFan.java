package com.yude.game.common.model.fan.judge.formal;

import com.yude.game.common.model.fan.BaseHuTypeEnum;
import com.yude.game.common.model.fan.FanType;
import com.yude.game.common.model.fan.FormalFanTypeEnum;
import com.yude.game.common.model.fan.judge.Fan;
import com.yude.game.common.model.fan.param.FormalFanParam;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/17 21:09
 * @Version: 1.0
 * @Declare:
 */
public enum LongQiDuiFan implements Fan<FormalFanParam> {
    /**
     * 单例
     */
    INSTANCE(FormalFanTypeEnum.龙七对);

    public FanType fantype;

    LongQiDuiFan(FanType fantype) {
        this.fantype = fantype;
    }

    @Override
    public FanType judge(FormalFanParam param) {
        BaseHuTypeEnum baseHuType = param.getBaseHuType();
        /**
         * 更合理的方式，应该是如果基础胡不是七对，压根就不应该进入这个方法，而不是在方法内部判断。这个可以在规则设置的时候做，以基础胡牌类型 为根节点，对需要判断的番进行分类
         */
        if(!BaseHuTypeEnum.七对.equals(baseHuType)){
            return null;
        }
        List<Integer> genList = param.getGenList();
        if(genList.size() == 1){
            return fantype;
        }
        return null;
    }
}
