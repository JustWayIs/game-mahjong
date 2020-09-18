package com.yude.game.xuezhan.application.response;

import com.yude.game.xuezhan.application.response.dto.ActionDTO;
import com.yude.game.xuezhan.application.response.dto.SettlementDetailInfoDTO;
import com.yude.protocol.common.response.BaseResponse;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/14 19:44
 * @Version: 1.0
 * @Declare:
 */
public class SettlementDetailResponse extends BaseResponse {
    private Integer posId;
    private List<Integer> standCardList;
    private List<ActionDTO> actionDTOList;
    private Integer huCard;
    /**
     * 输赢分
     */
    private Long settlementScore;
    private List<SettlementDetailInfoDTO> detailList;

    /**
     * 大牌展示：如果有胡过大于 等于16番 的牌再展示
     */
    private Integer bigFanNum;
    private Integer bigFanScore;
    private List<Integer> bigFanIds;

    public Integer getPosId() {
        return posId;
    }

    public SettlementDetailResponse setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }

    public List<Integer> getStandCardList() {
        return standCardList;
    }

    public SettlementDetailResponse setStandCardList(List<Integer> standCardList) {
        this.standCardList = standCardList;
        return this;
    }

    public Long getSettlementScore() {
        return settlementScore;
    }

    public SettlementDetailResponse setSettlementScore(Long settlementScore) {
        this.settlementScore = settlementScore;
        return this;
    }

    public List<SettlementDetailInfoDTO> getDetailList() {
        return detailList;
    }

    public SettlementDetailResponse setDetailList(List<SettlementDetailInfoDTO> detailList) {
        this.detailList = detailList;
        return this;
    }

    public List<ActionDTO> getActionDTOList() {
        return actionDTOList;
    }

    public SettlementDetailResponse setActionDTOList(List<ActionDTO> actionDTOList) {
        this.actionDTOList = actionDTOList;
        return this;
    }

    public Integer getHuCard() {
        return huCard;
    }

    public SettlementDetailResponse setHuCard(Integer huCard) {
        this.huCard = huCard;
        return this;
    }

    public Integer getBigFanNum() {
        return bigFanNum;
    }

    public SettlementDetailResponse setBigFanNum(Integer bigFanNum) {
        this.bigFanNum = bigFanNum;
        return this;
    }

    public List<Integer> getBigFanIds() {
        return bigFanIds;
    }

    public SettlementDetailResponse setBigFanIds(List<Integer> bigFanIds) {
        this.bigFanIds = bigFanIds;
        return this;
    }

    public Integer getBigFanScore() {
        return bigFanScore;
    }

    public SettlementDetailResponse setBigFanScore(Integer bigFanScore) {
        this.bigFanScore = bigFanScore;
        return this;
    }

    @Override
    public String toString() {
        return "SettlementDetailResponse{" +
                "posId=" + posId +
                ", standCardList=" + standCardList +
                ", actionDTOList=" + actionDTOList +
                ", huCard=" + huCard +
                ", settlementScore=" + settlementScore +
                ", detailList=" + detailList +
                ", bigFanNum=" + bigFanNum +
                ", bigFanScore=" + bigFanScore +
                ", bigFanIds=" + bigFanIds +
                '}';
    }
}
