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
    private List<Integer> hszList;
    private List<Integer> standCardList;
    private List<ActionDTO> actionDTOList;
    private List<Integer> cardPool;
    /**
     * 不能看到别人的
     */
    private List<Integer> canOperationList;
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

    public List<Integer> getHszList() {
        return hszList;
    }

    public SeatInfoDTO setHszList(List<Integer> hszList) {
        this.hszList = hszList;
        return this;
    }

    public List<Integer> getStandCardList() {
        return standCardList;
    }

    public SeatInfoDTO setStandCardList(List<Integer> standCardList) {
        this.standCardList = standCardList;
        return this;
    }

    public List<ActionDTO> getActionDTOList() {
        return actionDTOList;
    }

    public SeatInfoDTO setActionDTOList(List<ActionDTO> actionDTOList) {
        this.actionDTOList = actionDTOList;
        return this;
    }

    public List<Integer> getCardPool() {
        return cardPool;
    }

    public SeatInfoDTO setCardPool(List<Integer> cardPool) {
        this.cardPool = cardPool;
        return this;
    }

    public List<Integer> getCanOperationList() {
        return canOperationList;
    }

    public SeatInfoDTO setCanOperationList(List<Integer> canOperationList) {
        this.canOperationList = canOperationList;
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
                ", hszList=" + hszList +
                ", standCardList=" + standCardList +
                ", actionDTOList=" + actionDTOList +
                ", cardPool=" + cardPool +
                ", canOperationList=" + canOperationList +
                ", seatStatus=" + seatStatus +
                '}';
    }
}
