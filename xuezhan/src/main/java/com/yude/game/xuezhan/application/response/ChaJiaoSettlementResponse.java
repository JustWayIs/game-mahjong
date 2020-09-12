package com.yude.game.xuezhan.application.response;

import com.yude.game.xuezhan.application.response.dto.ChaJiaoDTO;
import com.yude.protocol.common.response.BaseResponse;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/12 12:35
 * @Version: 1.0
 * @Declare:
 */
public class ChaJiaoSettlementResponse extends BaseResponse {
    private List<ChaJiaoDTO> list;

    public ChaJiaoSettlementResponse() {
    }

    public ChaJiaoSettlementResponse(List<ChaJiaoDTO> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "ChaJiaoSettlementResponse{" +
                "list=" + list +
                "} " ;
    }
}
