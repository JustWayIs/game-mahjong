package com.yude.game.xuezhan.application.response;

import com.yude.protocol.common.response.BaseResponse;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/1 17:52
 * @Version: 1.0
 * @Declare: 通知玩家：谁做了什么操作
 */
public class OperationResultResponse extends BaseResponse {
    private Integer posId;
    private Integer targetCard;
    private Integer cardSource;
    private List<Integer> combinationCards;
    private Integer operationType;

    public OperationResultResponse() {
    }

    public OperationResultResponse(Integer posId, Integer targetCard, Integer cardSource, List<Integer> combinationCards, Integer operationType) {
        this.posId = posId;
        this.targetCard = targetCard;
        this.cardSource = cardSource;
        this.combinationCards = combinationCards;
        this.operationType = operationType;
    }

    public Integer getPosId() {
        return posId;
    }

    public OperationResultResponse setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }

    public Integer getTargetCard() {
        return targetCard;
    }

    public OperationResultResponse setTargetCard(Integer targetCard) {
        this.targetCard = targetCard;
        return this;
    }

    public List<Integer> getCombinationCards() {
        return combinationCards;
    }

    public OperationResultResponse setCombinationCards(List<Integer> combinationCards) {
        this.combinationCards = combinationCards;
        return this;
    }

    public Integer getOperationType() {
        return operationType;
    }

    public OperationResultResponse setOperationType(Integer operationType) {
        this.operationType = operationType;
        return this;
    }

    public Integer getCardSource() {
        return cardSource;
    }

    public OperationResultResponse setCardSource(Integer cardSource) {
        this.cardSource = cardSource;
        return this;
    }

    @Override
    public String toString() {
        return "OperationResultResponse{" +
                "posId=" + posId +
                ", targetCard=" + targetCard +
                ", cardSource=" + cardSource +
                ", combinationCards=" + combinationCards +
                ", operationType=" + operationType +
                '}';
    }
}
