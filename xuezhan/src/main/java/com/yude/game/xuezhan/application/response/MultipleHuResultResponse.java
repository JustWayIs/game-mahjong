package com.yude.game.xuezhan.application.response;

import com.yude.protocol.common.response.BaseResponse;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/5 16:49
 * @Version: 1.0
 * @Declare:
 */
public class MultipleHuResultResponse extends BaseResponse {
    private List<Integer> posId;
    private Integer targetCard;
    private Integer cardSourcePosId;

    public List<Integer> getPosId() {
        return posId;
    }

    public MultipleHuResultResponse setPosId(List<Integer> posId) {
        this.posId = posId;
        return this;
    }

    public Integer getTargetCard() {
        return targetCard;
    }

    public MultipleHuResultResponse setTargetCard(Integer targetCard) {
        this.targetCard = targetCard;
        return this;
    }

    public Integer getCardSourcePosId() {
        return cardSourcePosId;
    }

    public MultipleHuResultResponse setCardSourcePosId(Integer cardSourcePosId) {
        this.cardSourcePosId = cardSourcePosId;
        return this;
    }

    @Override
    public String toString() {
        return "MultipleHuResultResponse{" +
                "posId=" + posId +
                ", targetCard=" + targetCard +
                ", cardSourcePosId=" + cardSourcePosId +
                '}';
    }
}
