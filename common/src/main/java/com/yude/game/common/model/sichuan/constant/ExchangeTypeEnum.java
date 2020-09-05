package com.yude.game.common.model.sichuan.constant;

/**
 * @Author: HH
 * @Date: 2020/8/20 11:24
 * @Version: 1.0
 * @Declare:
 */
public enum ExchangeTypeEnum {
    /**
     * 顺时针交换
     */
    CLOCKWISE,
    /**
     * 逆时针交换
     */
    ANTICLOCKWISE,
    /**
     * 对家交换
     */
    FACE_TO_FACE;

    public int type(){
        return this.ordinal();
    }
}
