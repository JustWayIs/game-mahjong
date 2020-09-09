package com.yude.game.xuezhan.application.response;

import com.yude.game.xuezhan.application.response.dto.SettlementInfoDTO;
import com.yude.protocol.common.response.BaseResponse;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/9 17:03
 * @Version: 1.0
 * @Declare:
 */
public class SettlementResponse extends BaseResponse {
    private Integer operationPosId;
    private Integer targetCard;
    private Integer cardSourcePosId;
    private Integer operationType;
    private List<SettlementInfoDTO> settlementInfoDTOS;

    public Integer getOperationPosId() {
        return operationPosId;
    }

    public SettlementResponse setOperationPosId(Integer operationPosId) {
        this.operationPosId = operationPosId;
        return this;
    }

    public Integer getTargetCard() {
        return targetCard;
    }

    public SettlementResponse setTargetCard(Integer targetCard) {
        this.targetCard = targetCard;
        return this;
    }

    public Integer getCardSourcePosId() {
        return cardSourcePosId;
    }

    public SettlementResponse setCardSourcePosId(Integer cardSourcePosId) {
        this.cardSourcePosId = cardSourcePosId;
        return this;
    }

    public Integer getOperationType() {
        return operationType;
    }

    public SettlementResponse setOperationType(Integer operationType) {
        this.operationType = operationType;
        return this;
    }

    public List<SettlementInfoDTO> getSettlementInfoDTOS() {
        return settlementInfoDTOS;
    }

    public SettlementResponse setSettlementInfoDTOS(List<SettlementInfoDTO> settlementInfoDTOS) {
        this.settlementInfoDTOS = settlementInfoDTOS;
        return this;
    }

    @Override
    public String toString() {
        return "SettlementResponse{" +
                "operationPosId=" + operationPosId +
                ", targetCard=" + targetCard +
                ", cardSourcePosId=" + cardSourcePosId +
                ", operationType=" + operationType +
                ", settlementInfoDTOS=" + settlementInfoDTOS +
                "} " + super.toString();
    }
}
