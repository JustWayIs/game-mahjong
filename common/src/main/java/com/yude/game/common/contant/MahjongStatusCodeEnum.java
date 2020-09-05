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
    MATCH_VALID_FAIL(101, "匹配参数校验失败"),

    MATCH_EXISTS(102, "玩家已在匹配中"),

    MATCH_FAIL(103, "加入匹配队列失败"),

    NO_OUT_CARD_PERMISSION(104, "没有出牌权限"),

    NO_MATCH_OPERATION(105, "没有匹配的操作类型"),

    /**
     * 两种情况：一种是非法的请求、一种是由于多操作回合中，高优先级的操作先被玩家执行了，这个时候低优先级操作会被直接清除 这个时候低优先级操作 就没有操作记录了
     */
    NOT_CANCEL_OPERATION(106, "没有 过 的操作权限"),

    NOT_PENG_OPERATION(107, "没有 碰 的操作权限"),
    PENG_PARAM_ERROR(108, "碰牌参数错误"),

    NOT_ZHI_GANG_OPERATION(109, "没有 直杠 的操作权限"),
    ZHI_GANG_PARAM_ERROR(110, "直杠参数错误"),

    NOT_BU_GANG_OPERATION(111, "没有 补杠 的操作权限"),
    BU_GANG_PARAM_ERROR(112, "补杠参数错误"),

    NOT_AN_GANG_OPERATION(113, "没有 暗杠 的操作权限"),
    AN_GANG_PARAM_ERROR(114, "暗杠参数错误"),

    NOT_HU_OPERATION(115, "没有 胡 的操作权限"),
    HU_PARAM_ERROR(116, "胡牌参数错误"),

    NOT_CHI_OPERATION(117, "没有 吃 的操作权限"),
    CHI_PARAM_ERROR(118, "吃牌参数错误"),

    EXCHANGE_ERROR(1001, "不能交换出自己没有的牌"),
    ;

    private int code;

    private String msg;

    MahjongStatusCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static MahjongStatusCodeEnum matchParamErrorByOperation(Integer operation) {
        OperationEnum value = OperationEnum.values()[operation];
        switch (value) {
            case PENG:
                return PENG_PARAM_ERROR;
            case AN_GANG:
                return AN_GANG_PARAM_ERROR;
            case BU_GANG:
                return BU_GANG_PARAM_ERROR;
            case ZHI_GANG:
                return ZHI_GANG_PARAM_ERROR;
            case CHI:
                return CHI_PARAM_ERROR;
            default:;
        }
        return null;
    }

    public static MahjongStatusCodeEnum matchNotOperationErrorByOperation(Integer operation) {
        OperationEnum value = OperationEnum.values()[operation];
        switch (value) {
            case PENG:
                return NOT_PENG_OPERATION;
            case AN_GANG:
                return NOT_AN_GANG_OPERATION;
            case BU_GANG:
                return NOT_BU_GANG_OPERATION;
            case ZHI_GANG:
                return NOT_ZHI_GANG_OPERATION;
            case CHI:
                return NOT_CHI_OPERATION;
            default:;
        }
        return null;
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
