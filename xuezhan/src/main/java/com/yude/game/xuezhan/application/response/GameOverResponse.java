package com.yude.game.xuezhan.application.response;

import com.yude.game.xuezhan.application.response.dto.SeatInfoDTO;
import com.yude.protocol.common.response.BaseResponse;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/14 17:29
 * @Version: 1.0
 * @Declare:
 */
public class GameOverResponse extends BaseResponse {
    private List<SeatInfoDTO> list;
    private Boolean isLiuJu;

    public GameOverResponse() {
    }

    public GameOverResponse(List<SeatInfoDTO> list, Boolean isLiuJu) {
        this.list = list;
        this.isLiuJu = isLiuJu;
    }

    public List<SeatInfoDTO> getList() {
        return list;
    }

    public GameOverResponse setList(List<SeatInfoDTO> list) {
        this.list = list;
        return this;
    }

    public Boolean getLiuJu() {
        return isLiuJu;
    }

    public GameOverResponse setLiuJu(Boolean liuJu) {
        isLiuJu = liuJu;
        return this;
    }

    @Override
    public String toString() {
        return "GameOverResponse{" +
                "list=" + list +
                ", isLiuJu=" + isLiuJu +
                '}';
    }
}
