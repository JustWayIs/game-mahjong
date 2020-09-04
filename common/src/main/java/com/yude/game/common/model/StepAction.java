package com.yude.game.common.model;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/20 11:02
 * @Version: 1.0
 * @Declare:
 */
public class StepAction {
    /**
     * 大众麻将有补花，补花的目标牌是多张 就要用一个新的补花专用step
     * 可以是出的牌、抓的牌、碰的牌、吃的牌、杠的牌、胡的牌
     */
    private Integer targetCard;
    /**
     * 牌来自哪里：posId，有些动作的来源是固定的，就不设值了，比如摸牌、抓牌
     */
    private Integer cardSource;
    private MahjongOperation operationType;
    /**
     * eg: 目标牌:3万   操作类型：吃    组合结果： 2万 3万 4万
     */
    private List<Integer> combinationRsult;

    public StepAction() {
    }

    public StepAction(Integer targetCard, Integer cardSource, MahjongOperation operationType, List<Integer> combinationRsult) {
        this.targetCard = targetCard;
        this.cardSource = cardSource;
        this.operationType = operationType;
        this.combinationRsult = combinationRsult;
    }

    public Integer getTargetCard() {
        return targetCard;
    }

    public StepAction setTargetCard(Integer targetCard) {
        this.targetCard = targetCard;
        return this;
    }


    public MahjongOperation getOperationType() {
        return operationType;
    }

    public StepAction setOperationType(MahjongOperation operationType) {
        this.operationType = operationType;
        return this;
    }

    public List<Integer> getCombinationRsult() {
        return combinationRsult;
    }

    public StepAction setCombinationRsult(List<Integer> combinationRsult) {
        this.combinationRsult = combinationRsult;
        return this;
    }

    public Integer getCardSource() {
        return cardSource;
    }

    public StepAction setCardSource(Integer cardSource) {
        this.cardSource = cardSource;
        return this;
    }


    @Override
    public String toString() {
        return "StepAction{" +
                "targetCard=" + targetCard +
                ", cardSource=" + cardSource +
                ", operationType=" + operationType +
                ", combinationRsult=" + combinationRsult +
                '}';
    }
}
