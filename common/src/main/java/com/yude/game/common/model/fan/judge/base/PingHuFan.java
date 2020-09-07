package com.yude.game.common.model.fan.judge.base;

import com.yude.game.common.model.fan.BaseHuTypeEnum;
import com.yude.game.common.model.fan.FanType;
import com.yude.game.common.model.fan.judge.Fan;
import com.yude.game.common.model.fan.param.BaseHuParam;

/**
 * @Author: HH
 * @Date: 2020/9/5 17:31
 * @Version: 1.0
 * @Declare: 比起去用是否满足 2333 来判断是不是平胡， 判断不是七对 并且 不是 十三幺 更加简单。唯一的问题在于如果 当前玩法 加入了新的基础胡，需要再次修改这个类。得益于基础胡的判断方式在所有地方玩法上几乎不会有不同，所以可以把所有基础胡的算法枚举 与 基础胡枚举 建立映射关系.
 *
 */
public enum PingHuFan implements Fan<BaseHuParam> {
    /**
     *
     */
    INSTANCE(BaseHuTypeEnum.平胡);

    private FanType baseHuType;

    PingHuFan(BaseHuTypeEnum baseHuType) {
        this.baseHuType = baseHuType;
    }


    @Override
    public FanType judge(BaseHuParam param) {
        /**
         * 因为现有的胡牌工具类里，判断能否胡牌里包含了 平胡 和 七对，但是数据结构里没有说明胡的什么
         *
         * 在判定能胡牌的时候 找出 基础胡牌类型 记录下来是最合理的。应该说必须要判断出来，因为有可能该基础胡牌类型 不是房间规则所支持的，所以必须记录下来。下面的代码逻辑可以不用走了
         */
        BaseHuTypeEnum[] values = BaseHuTypeEnum.values();
        for(BaseHuTypeEnum baseHuTypeEnum : values){
            if(baseHuTypeEnum.fan.judge(param) != null){
                return  null;
            }
        }
       return baseHuType;
    }
}
