package com.yude.game.xuezhan.application.response.dto;

import com.yude.game.common.application.response.dto.BaseSeatInfo;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/27 10:23
 * @Version: 1.0
 * @Declare:
 */
public class SichuanSeatInfoDTO extends BaseSeatInfo {
    private List<Integer> standCardList;
    /**
     * 不能看到别人的
     */
    private List<Integer> canOperations;
    private List<Integer> recommendExchangeCards;
    private Integer recommendDingQueColor;
    /**
     * 不包含被碰、杠、胡的牌
     */
    private List<Integer> outCardPool;
    /**
     * 副露信息
     */
    private List<ActionDTO> actionDTOList;
    private Integer huCard;
    private List<Integer> status;

    @Override
    public Integer getPosId() {
        return posId;
    }

    @Override
    public SichuanSeatInfoDTO setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }


    public List<Integer> getStandCardList() {
        return standCardList;
    }

    public SichuanSeatInfoDTO setStandCardList(List<Integer> standCardList) {
        this.standCardList = standCardList;
        return this;
    }

    public List<Integer> getCanOperations() {
        return canOperations;
    }

    public SichuanSeatInfoDTO setCanOperations(List<Integer> canOperations) {
        this.canOperations = canOperations;
        return this;
    }

    public List<Integer> getRecommendExchangeCards() {
        return recommendExchangeCards;
    }

    public SichuanSeatInfoDTO setRecommendExchangeCards(List<Integer> recommendExchangeCards) {
        this.recommendExchangeCards = recommendExchangeCards;
        return this;
    }

    public Integer getRecommendDingQueColor() {
        return recommendDingQueColor;
    }

    public SichuanSeatInfoDTO setRecommendDingQueColor(Integer recommendDingQueColor) {
        this.recommendDingQueColor = recommendDingQueColor;
        return this;
    }

    public List<Integer> getOutCardPool() {
        return outCardPool;
    }

    public SichuanSeatInfoDTO setOutCardPool(List<Integer> outCardPool) {
        this.outCardPool = outCardPool;
        return this;
    }

    public List<ActionDTO> getActionDTOList() {
        return actionDTOList;
    }

    public SichuanSeatInfoDTO setActionDTOList(List<ActionDTO> actionDTOList) {
        this.actionDTOList = actionDTOList;
        return this;
    }

    public Integer getHuCard() {
        return huCard;
    }

    public SichuanSeatInfoDTO setHuCard(Integer huCard) {
        this.huCard = huCard;
        return this;
    }

    public List<Integer> getStatus() {
        return status;
    }

    public SichuanSeatInfoDTO setStatus(List<Integer> status) {
        this.status = status;
        return this;
    }

    @Override
    public String toString() {
        return "SichuanSeatInfoDTO{" +
                "posId=" + posId +
                ", playerDTO=" + playerDTO +
                ", standCardList=" + standCardList +
                ", canOperations=" + canOperations +
                ", recommendExchangeCards=" + recommendExchangeCards +
                ", recommendDingQueColor=" + recommendDingQueColor +
                ", outCardPool=" + outCardPool +
                ", actionDTOList=" + actionDTOList +
                ", huCard=" + huCard +
                ", status=" + status +
                '}';
    }
}
