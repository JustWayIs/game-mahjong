package com.yude.game.xuezhan.application.response;

import com.yude.game.xuezhan.application.response.dto.GameInfoDTO;
import com.yude.game.xuezhan.application.response.dto.SichuanSeatInfoDTO;
import com.yude.protocol.common.response.BaseResponse;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/22 11:19
 * @Version: 1.0
 * @Declare:
 */
public class SelfReconnectResponse extends BaseResponse {
    private Integer posId;
    private List<SichuanSeatInfoDTO> seatInfoDTOList;
    private GameInfoDTO gameinfoDTO;

    public SelfReconnectResponse() {
    }

    public SelfReconnectResponse(Integer posId, List<SichuanSeatInfoDTO> seatInfoDTOList, GameInfoDTO gameinfoDTO) {
        this.posId = posId;
        this.seatInfoDTOList = seatInfoDTOList;
        this.gameinfoDTO = gameinfoDTO;
    }

    public List<SichuanSeatInfoDTO> getSeatInfoDTOList() {
        return seatInfoDTOList;
    }

    public SelfReconnectResponse setSeatInfoDTOList(List<SichuanSeatInfoDTO> seatInfoDTOList) {
        this.seatInfoDTOList = seatInfoDTOList;
        return this;
    }

    public GameInfoDTO getGameinfoDTO() {
        return gameinfoDTO;
    }

    public SelfReconnectResponse setGameinfoDTO(GameInfoDTO gameinfoDTO) {
        this.gameinfoDTO = gameinfoDTO;
        return this;
    }

    public Integer getPosId() {
        return posId;
    }

    public SelfReconnectResponse setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }

    @Override
    public String toString() {
        return "SelfReconnectResponse{" +
                "posId=" + posId +
                ", seatInfoDTOList=" + seatInfoDTOList +
                ", gameinfoDTO=" + gameinfoDTO +
                '}';
    }
}
