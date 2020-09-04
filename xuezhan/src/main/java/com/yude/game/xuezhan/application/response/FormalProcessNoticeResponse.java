package com.yude.game.xuezhan.application.response;

import com.yude.protocol.common.response.BaseResponse;

/**
 * @Author: HH
 * @Date: 2020/9/3 16:57
 * @Version: 1.0
 * @Declare:
 */
public class FormalProcessNoticeResponse extends BaseResponse {
    private Integer firstOutCardPosId;
    private Integer remainingTime;

    public FormalProcessNoticeResponse() {
    }

    public FormalProcessNoticeResponse(Integer firstOutCardPosId, Integer remainingTime) {
        this.firstOutCardPosId = firstOutCardPosId;
        this.remainingTime = remainingTime;
    }


    @Override
    public String toString() {
        return "FormalProcessNoticeResponse{" +
                "firstOutCardPosId=" + firstOutCardPosId +
                ", remainingTime=" + remainingTime +
                '}';
    }
}
