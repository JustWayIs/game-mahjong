package com.yude.game.xuezhan.application.response;

import com.yude.game.xuezhan.application.response.dto.ChaHuaZhuDTO;
import com.yude.protocol.common.response.BaseResponse;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/12 12:35
 * @Version: 1.0
 * @Declare:
 */
public class ChaHuaZhuSettlementResponse extends BaseResponse {
    private List<ChaHuaZhuDTO> list;

    public ChaHuaZhuSettlementResponse() {
    }

    public ChaHuaZhuSettlementResponse(List<ChaHuaZhuDTO> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "ChaHuaZhuSettlementResponse{" +
                "list=" + list +
                "} ";
    }
}
