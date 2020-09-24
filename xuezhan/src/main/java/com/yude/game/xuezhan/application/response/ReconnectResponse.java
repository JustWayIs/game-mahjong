package com.yude.game.xuezhan.application.response;

import com.yude.game.xuezhan.application.response.dto.GameZoneInfoDTO;
import com.yude.game.xuezhan.application.response.dto.SeatInfoDTO;
import com.yude.protocol.common.response.BaseResponse;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/22 11:19
 * @Version: 1.0
 * @Declare:
 */
public class ReconnectResponse extends BaseResponse {
    private Integer posId;
    private List<SeatInfoDTO> seatInfoDTOList;
    private GameZoneInfoDTO gameZoneInfoDTO;

    public ReconnectResponse() {
    }

    /**
     * 通知其他玩家
     * @param posId
     */
    public ReconnectResponse(Integer posId) {
        this.posId = posId;
    }

    /**
     * 通知重连的玩家
     * @param posId
     * @param seatInfoDTOList
     * @param gameZoneInfoDTO
     */
    public ReconnectResponse(Integer posId, List<SeatInfoDTO> seatInfoDTOList, GameZoneInfoDTO gameZoneInfoDTO) {
        this.posId = posId;
        this.seatInfoDTOList = seatInfoDTOList;
        this.gameZoneInfoDTO = gameZoneInfoDTO;
    }

    public List<SeatInfoDTO> getSeatInfoDTOList() {
        return seatInfoDTOList;
    }

    public ReconnectResponse setSeatInfoDTOList(List<SeatInfoDTO> seatInfoDTOList) {
        this.seatInfoDTOList = seatInfoDTOList;
        return this;
    }

    public GameZoneInfoDTO getGameZoneInfoDTO() {
        return gameZoneInfoDTO;
    }

    public ReconnectResponse setGameZoneInfoDTO(GameZoneInfoDTO gameZoneInfoDTO) {
        this.gameZoneInfoDTO = gameZoneInfoDTO;
        return this;
    }

    public Integer getPosId() {
        return posId;
    }

    public ReconnectResponse setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }

    @Override
    public String toString() {
        return "SelfReconnectResponse{" +
                "posId=" + posId +
                ", seatInfoDTOList=" + seatInfoDTOList +
                ", gameZoneInfoDTO=" + gameZoneInfoDTO +
                '}';
    }
}
