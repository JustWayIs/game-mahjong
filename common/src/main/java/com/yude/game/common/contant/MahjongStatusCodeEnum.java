package com.yude.game.common.contant;

import com.yude.protocol.common.constant.StatusCodeEnum;
import com.yude.protocol.common.constant.StatusCodeI;

/**
 * @Author: HH
 * @Date: 2020/8/27 20:56
 * @Version: 1.0
 * @Declare:
 */
public enum MahjongStatusCodeEnum implements StatusCodeI {

    /**
     * 成功
     */
    SUCCESS(StatusCodeEnum.SUCCESS.code(), "success"),

    /**
     * 失败
     */
    FAIL(StatusCodeEnum.FAIL.code(), "fail"),

    /**
     *
     */
    MATCH_VALID_FAIL(101,"匹配参数校验失败"),

    MATCH_EXISTS(102,"玩家已在匹配中"),

    MATCH_FAIL(103,"加入匹配队列失败"),

    NO_OUT_CARD_PERMISSION(104,"没有出牌权限"),

    NO_MATCH_OPERATION(105,"没有匹配的操作类型"),

    EXCHANGE_ERROR(1001,"不能交换出自己没有的牌"),;

    private int code;

    private String msg;

    MahjongStatusCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String msg() {
        return msg;
    }
}
