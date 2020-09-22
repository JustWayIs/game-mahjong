package com.yude.game.xuezhan.application.response;

import com.yude.game.xuezhan.application.response.dto.SettlementDetailInfoDTO;
import com.yude.protocol.common.response.BaseResponse;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/22 10:27
 * @Version: 1.0
 * @Declare:
 */
public class WaterFlowResponse extends BaseResponse {
    /**
     * 总的变动分
     */
    private Integer changeScore;
    private List<SettlementDetailInfoDTO> waterFlowList;

    public WaterFlowResponse() {
    }

    public WaterFlowResponse(Integer changeScore, List<SettlementDetailInfoDTO> waterFlowList) {
        this.changeScore = changeScore;
        this.waterFlowList = waterFlowList;
    }

    public Integer getChangeScore() {
        return changeScore;
    }

    public WaterFlowResponse setChangeScore(Integer changeScore) {
        this.changeScore = changeScore;
        return this;
    }

    public List<SettlementDetailInfoDTO> getWaterFlowList() {
        return waterFlowList;
    }

    public WaterFlowResponse setWaterFlowList(List<SettlementDetailInfoDTO> waterFlowList) {
        this.waterFlowList = waterFlowList;
        return this;
    }

    @Override
    public String toString() {
        return "WaterFlowResponse{" +
                "changeScore=" + changeScore +
                ", waterFlowList=" + waterFlowList +
                '}';
    }
}
