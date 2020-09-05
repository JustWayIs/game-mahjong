package com.yude.game.xuezhan.application.response;

import com.yude.protocol.common.response.BaseResponse;

/**
 * @Author: HH
 * @Date: 2020/9/2 16:40
 * @Version: 1.0
 * @Declare:
 */
public class TookCardNoticeResponse extends BaseResponse {
    private Integer posId;
    private Integer card;

    public TookCardNoticeResponse() {
    }

    /**
     * 分两步设值，分别给摸牌玩家 和 其他玩家（不能知道别人摸了什么牌）
     * @param posId
     */
    public TookCardNoticeResponse(Integer posId) {
        this.posId = posId;
    }

    public TookCardNoticeResponse(Integer posId, Integer card) {
        this.posId = posId;
        this.card = card;
    }

    public Integer getPosId() {
        return posId;
    }

    public TookCardNoticeResponse setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }

    public Integer getCard() {
        return card;
    }

    public TookCardNoticeResponse setCard(Integer card) {
        this.card = card;
        return this;
    }

    @Override
    public String toString() {
        return "TookCardNoticeResponse{" +
                "posId=" + posId +
                ", card=" + card +
                '}';
    }
}
