package com.yude.game.xuezhan.application.response.dto;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.yude.game.common.application.response.dto.PlayerDTO;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/14 18:23
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class SeatInfoDTO {
    private PlayerDTO playerDTO;
    private Integer posId;
    private Integer queColor;
    private List<Integer> recommendExchangeCards;
    private Integer recommendDingQueColor;
    private List<Integer> standCardList;
    private Integer standCardRemaining;
    private List<ActionDTO> actionDTOList;
    /**
     * 不包含被碰、杠、胡的牌
     */
    private List<Integer> outCardPool;
    /**
     * 不能看到别人的
     */
    private List<Integer> canOperationList;
    private Integer huCard;
    private List<Integer> seatStatus;

    public PlayerDTO getPlayerDTO() {
        return playerDTO;
    }

    public SeatInfoDTO setPlayerDTO(PlayerDTO playerDTO) {
        this.playerDTO = playerDTO;
        return this;
    }

    public Integer getPosId() {
        return posId;
    }

    public SeatInfoDTO setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }

    public Integer getQueColor() {
        return queColor;
    }

    public SeatInfoDTO setQueColor(Integer queColor) {
        this.queColor = queColor;
        return this;
    }

    public List<Integer> getRecommendExchangeCards() {
        return recommendExchangeCards;
    }

    public SeatInfoDTO setRecommendExchangeCards(List<Integer> recommendExchangeCards) {
        this.recommendExchangeCards = recommendExchangeCards;
        return this;
    }

    public Integer getRecommendDingQueColor() {
        return recommendDingQueColor;
    }

    public SeatInfoDTO setRecommendDingQueColor(Integer recommendDingQueColor) {
        this.recommendDingQueColor = recommendDingQueColor;
        return this;
    }

    public List<Integer> getStandCardList() {
        return standCardList;
    }

    public SeatInfoDTO setStandCardList(List<Integer> standCardList) {
        this.standCardList = standCardList;
        return this;
    }

    public Integer getStandCardRemaining() {
        return standCardRemaining;
    }

    public SeatInfoDTO setStandCardRemaining(Integer standCardRemaining) {
        this.standCardRemaining = standCardRemaining;
        return this;
    }

    public List<ActionDTO> getActionDTOList() {
        return actionDTOList;
    }

    public SeatInfoDTO setActionDTOList(List<ActionDTO> actionDTOList) {
        this.actionDTOList = actionDTOList;
        return this;
    }

    public List<Integer> getOutCardPool() {
        return outCardPool;
    }

    public SeatInfoDTO setOutCardPool(List<Integer> outCardPool) {
        this.outCardPool = outCardPool;
        return this;
    }

    public List<Integer> getCanOperationList() {
        return canOperationList;
    }

    public SeatInfoDTO setCanOperationList(List<Integer> canOperationList) {
        this.canOperationList = canOperationList;
        return this;
    }

    public Integer getHuCard() {
        return huCard;
    }

    public SeatInfoDTO setHuCard(Integer huCard) {
        this.huCard = huCard;
        return this;
    }

    public List<Integer> getSeatStatus() {
        return seatStatus;
    }

    public SeatInfoDTO setSeatStatus(List<Integer> seatStatus) {
        this.seatStatus = seatStatus;
        return this;
    }

    @Override
    public String toString() {
        return "SeatInfoDTO{" +
                "playerDTO=" + playerDTO +
                ", posId=" + posId +
                ", queColor=" + queColor +
                ", recommendExchangeCards=" + recommendExchangeCards +
                ", recommendDingQueColor=" + recommendDingQueColor +
                ", standCardList=" + standCardList +
                ", standCardRemaining=" + standCardRemaining +
                ", actionDTOList=" + actionDTOList +
                ", outCardPool=" + outCardPool +
                ", canOperationList=" + canOperationList +
                ", huCard=" + huCard +
                ", seatStatus=" + seatStatus +
                '}';
    }
}
