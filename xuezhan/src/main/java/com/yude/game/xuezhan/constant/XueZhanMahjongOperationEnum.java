package com.yude.game.xuezhan.constant;

import com.yude.game.common.contant.OperationEnum;
import com.yude.game.common.model.MahjongOperation;

/**
 * @Author: HH
 * @Date: 2020/8/24 17:13
 * @Version: 1.0
 * @Declare:
 */
public enum XueZhanMahjongOperationEnum implements MahjongOperation {
    /**
     * 从OperationEnum中找出该玩法可以进行的操作，这样的话，似乎OperatioEnum就只有一个限制的作用，OperationEnum本身还是无法摆脱开闭原则
     */
    CANCEL(OperationEnum.CANCEL),
    EXCHANGE_CARD(OperationEnum.EXCHANGE_CARD),
    DING_QUE(OperationEnum.DING_QUE),
    OUT_CARD(OperationEnum.OUT_CARD),
    PENG(OperationEnum.PENG),
    ZHI_GANG(OperationEnum.ZHI_GANG),
    BU_GANG(OperationEnum.BU_GANG),
    AN_GANG(OperationEnum.AN_GANG),
    HU(OperationEnum.HU),
    YI_PAO_DUO_XIANG(OperationEnum.YI_PAO_DUO_XIANG),
    TOOK_CARD(OperationEnum.TOOK_CARD),
    REBATE(OperationEnum.REBATE),
    CHA_JIAO(OperationEnum.CHA_JIAO),
    CHA_HUA_ZHU(OperationEnum.CHA_HUA_ZHU),;


    private OperationEnum operationEnum;

    XueZhanMahjongOperationEnum(OperationEnum operationEnum) {
        this.operationEnum = operationEnum;
    }

    public static XueZhanMahjongOperationEnum matchByValue(Integer value){
        for(XueZhanMahjongOperationEnum operationEnum : XueZhanMahjongOperationEnum.values()){
            if(operationEnum.value().equals(value)){
                return operationEnum;
            }
        }
        return null;
    }

    @Override
    public Integer value() {
        return operationEnum.value();
    }

    @Override
    public boolean canProductFulu() {
        return operationEnum.canProductFulu();
    }

    @Override
    public int priority() {
        return operationEnum.priority();
    }

    @Override
    public String toString() {
        return "XueZhanMahjongOperationEnum{" +
                "operationEnum=" + operationEnum +
                '}';
    }
}
