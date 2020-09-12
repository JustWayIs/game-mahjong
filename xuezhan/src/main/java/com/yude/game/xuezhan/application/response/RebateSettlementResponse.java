package com.yude.game.xuezhan.application.response;

import com.yude.game.xuezhan.application.response.dto.RebateDTO;
import com.yude.protocol.common.response.BaseResponse;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/11 17:48
 * @Version: 1.0
 * @Declare:
 */
public class RebateSettlementResponse extends BaseResponse {
    private List<RebateDTO> list;

    public RebateSettlementResponse() {
    }

    public RebateSettlementResponse(List<RebateDTO> list) {
        this.list = list;
    }

    public List<RebateDTO> getList() {
        return list;
    }

    public RebateSettlementResponse setList(List<RebateDTO> list) {
        this.list = list;
        return this;
    }

    @Override
    public String toString() {
        return "RebateSettlementResponse{" +
                "list=" + list +
                "} ";
    }
}
