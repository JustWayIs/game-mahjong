package com.yude.game.common.contant;

import com.yude.game.common.model.MahjongOperation;

/**
 * @Author: HH
 * @Date: 2020/8/3 14:24
 * @Version: 1.0
 * @Declare:
 */
public enum  OperationEnum implements MahjongOperation {
    /**
     *
     */
    CANCEL,
    BU_HUA,
    EXCHANGE_CARD,
    DING_QUE,
    OUT_CARD,
    CHI,
    PENG,
    ZHI_GANG,
    BU_GANG,
    AN_GANG,
    HU,
    YI_PAO_DUO_XIANG;

    /*public List<Integer> getOperationList(){
        List<Integer> operationList = new ArrayList<>();
        //逻辑上来说是不会直接使用CANCEL
        if(!OperationEnum.BU_HUA.equals(this) && !OperationEnum.OUT_CARD.equals(this)){
            operationList.add(CANCEL.ordinal());
        }
        operationList.add(this.ordinal());
        return operationList;
    }

    *//**
     * 存在一个问题，这里返回是带cancel操作的权限集合了，如果同时可以吃、碰 这种多操作呢。集合里就会有两个cancel这个时候又得做一次去重操作。  这样来看的话，倒不如临时决定要不要增加cancel操作，这个方法也就失去了意义。
     * @return
     *//*
    public List<OperationEnum> getOperationEnumList(){
        List<OperationEnum> operationList = new ArrayList<>();
        //逻辑上来说是不会直接使用CANCEL
        if(!OperationEnum.BU_HUA.equals(this) && !OperationEnum.OUT_CARD.equals(this)){
            operationList.add(CANCEL);
        }
        operationList.add(this);
        return operationList;
    }*/

    @Override
    public Integer value() {
        return this.ordinal();
    }
}
