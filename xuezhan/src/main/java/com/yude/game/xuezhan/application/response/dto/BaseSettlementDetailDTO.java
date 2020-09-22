package com.yude.game.xuezhan.application.response.dto;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/19 17:15
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class BaseSettlementDetailDTO {
    private Integer posId;
    /**
     * 没听牌就不展示，换而言之，不给立牌数据就是没听牌，但是不直观
     */
    private List<Integer> standCardList;
    private List<ActionDTO> actionDTOList;
    private Integer huCard;
    /**
     * 输赢分
     */
    private Long settlementScore;

    public Integer getPosId() {
        return posId;
    }

    public BaseSettlementDetailDTO setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }

    public List<Integer> getStandCardList() {
        return standCardList;
    }

    public BaseSettlementDetailDTO setStandCardList(List<Integer> standCardList) {
        this.standCardList = standCardList;
        return this;
    }

    public List<ActionDTO> getActionDTOList() {
        return actionDTOList;
    }

    public BaseSettlementDetailDTO setActionDTOList(List<ActionDTO> actionDTOList) {
        this.actionDTOList = actionDTOList;
        return this;
    }

    public Integer getHuCard() {
        return huCard;
    }

    public BaseSettlementDetailDTO setHuCard(Integer huCard) {
        this.huCard = huCard;
        return this;
    }

    public Long getSettlementScore() {
        return settlementScore;
    }

    public BaseSettlementDetailDTO setSettlementScore(Long settlementScore) {
        this.settlementScore = settlementScore;
        return this;
    }

    @Override
    public String toString() {
        return "BaseSettlementDetailDTO{" +
                "posId=" + posId +
                ", standCardList=" + standCardList +
                ", actionDTOList=" + actionDTOList +
                ", huCard=" + huCard +
                ", settlementScore=" + settlementScore +
                '}';
    }
}
