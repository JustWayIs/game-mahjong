package com.yude.game.common.model.fan;

import com.yude.game.common.contant.OperationEnum;

/**
 * @Author: HH
 * @Date: 2020/9/9 15:00
 * @Version: 1.0
 * @Declare:
 */
public enum SpecificFanTypeEnum implements FanType {
    /**
     * 杠番id
     */
    直杠(OperationEnum.ZHI_GANG.value()),
    补杠(OperationEnum.BU_GANG.value()),
    暗杠(OperationEnum.AN_GANG.value()),

    退税(OperationEnum.REBATE.value()),
    查叫(OperationEnum.CHA_JIAO.value()),
    查花猪(OperationEnum.CHA_HUA_ZHU.value()),
    ;

    private Integer actionType;

    SpecificFanTypeEnum(Integer actionType) {
        this.actionType = actionType;
    }

    public Integer getActionType() {
        return actionType;
    }

    @Override
    public int getId() {
        return 6000 + this.ordinal();
    }

    @Override
    public FanType[] excludeFan() {
        return new FanType[0];
    }
}
