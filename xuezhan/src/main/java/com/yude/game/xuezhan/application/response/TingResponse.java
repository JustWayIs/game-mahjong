package com.yude.game.xuezhan.application.response;

import com.yude.game.xuezhan.application.response.dto.TingInfoDTO;
import com.yude.protocol.common.response.BaseResponse;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/16 18:50
 * @Version: 1.0
 * @Declare:
 */
public class TingResponse extends BaseResponse {
    private List<TingInfoDTO> tingList;

    public List<TingInfoDTO> getTingList() {
        return tingList;
    }

    public TingResponse setTingList(List<TingInfoDTO> tingList) {
        this.tingList = tingList;
        return this;
    }

    @Override
    public String toString() {
        return "TingResponse{" +
                "tingList=" + tingList +
                '}';
    }
}
