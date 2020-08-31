package com.yude.game.common.application.response;

import com.yude.game.common.application.response.dto.BaseSeatInfo;
import com.yude.protocol.common.response.BaseResponse;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/24 14:38
 * @Version: 1.0
 * @Declare:
 */
public class MatchFinishResponse extends BaseResponse {
    private Long roomId;
    private List<BaseSeatInfo> seatInfoList;

    public MatchFinishResponse() {
    }

    public MatchFinishResponse(Long roomId, List<BaseSeatInfo> seatInfoList) {
        this.roomId = roomId;
        this.seatInfoList = seatInfoList;
    }

    @Override
    public String toString() {
        return "MatchFinishResponse{" +
                "roomId=" + roomId +
                ", seatInfoList=" + seatInfoList +
                '}';
    }
}
