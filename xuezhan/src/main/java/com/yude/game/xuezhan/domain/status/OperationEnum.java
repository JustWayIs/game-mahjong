package com.yude.game.xuezhan.domain.status;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/3 14:24
 * @Version: 1.0
 * @Declare:
 */
public enum  OperationEnum {
    /**
     *
     */
    CANCEL,
    OUT_CARD,
    PENG,
    ZHI_GANG,
    BU_GANG,
    AN_GANG,
    HU;

    public List<Integer> getOperationList(){
        List<Integer> operationList = new ArrayList<>();
        //逻辑上来说是不会直接使用CANCEL
        if(!OperationEnum.CANCEL.equals(this) && !OperationEnum.OUT_CARD.equals(this)){
            operationList.add(CANCEL.ordinal());
        }
        operationList.add(this.ordinal());
        return operationList;
    }
}
