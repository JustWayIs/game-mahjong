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
    CHI(OperationEnum.CHI),
    PENG(OperationEnum.PENG),
    ZHI_GANG(OperationEnum.ZHI_GANG),
    BU_GANG(OperationEnum.BU_GANG),
    AN_GANG(OperationEnum.AN_GANG),
    HU(OperationEnum.HU),
    YI_PAO_DUO_XIANG(OperationEnum.YI_PAO_DUO_XIANG);

    private OperationEnum operationEnum;

    XueZhanMahjongOperationEnum(OperationEnum operationEnum) {
        this.operationEnum = operationEnum;
    }

    @Override
    public Integer value() {
        return operationEnum.value();
    }
}
