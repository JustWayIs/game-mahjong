package com.yude.game.common.model;

import com.yude.game.common.contant.OperationEnum;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/20 11:02
 * @Version: 1.0
 * @Declare:
 */
public class CardAction {
    /**
     * 目标牌/操作牌
     */
    private List<Integer> operationCard;
    private OperationEnum operationType;
    /**
     * eg: 目标牌:3万   操作类型：吃    组合结果： 2万 3万 4万
     */
    private List<Integer> combinationRsult;
}
