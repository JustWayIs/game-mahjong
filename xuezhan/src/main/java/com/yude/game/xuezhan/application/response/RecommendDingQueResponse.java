package com.yude.game.xuezhan.application.response;

import com.yude.protocol.common.response.BaseResponse;

/**
 * @Author: HH
 * @Date: 2020/8/26 20:31
 * @Version: 1.0
 * @Declare:
 */
public class RecommendDingQueResponse extends BaseResponse {
    private Integer color;

    public RecommendDingQueResponse() {
    }

    public RecommendDingQueResponse(Integer color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "RecommendDingQueResponse{" +
                "color=" + color +
                '}';
    }
}
