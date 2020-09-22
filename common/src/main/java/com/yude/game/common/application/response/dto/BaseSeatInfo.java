package com.yude.game.common.application.response.dto;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

/**
 * @Author: HH
 * @Date: 2020/8/24 14:43
 * @Version: 1.0
 * @Declare:
 */

@ProtobufClass
@EnableZigZap
public class BaseSeatInfo{
    protected Integer posId;
    protected PlayerDTO playerDTO;

    public BaseSeatInfo() {
    }

    public BaseSeatInfo(Integer posId, PlayerDTO playerDTO) {
        this.posId = posId;
        this.playerDTO = playerDTO;
    }

    public Integer getPosId() {
        return posId;
    }

    public BaseSeatInfo setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }

    public PlayerDTO getPlayerDTO() {
        return playerDTO;
    }

    public BaseSeatInfo setPlayerDTO(PlayerDTO playerDTO) {
        this.playerDTO = playerDTO;
        return this;
    }

    @Override
    public String toString() {
        return "BaseSeatInfo{" +
                "posId=" + posId +
                ", playerDTO=" + playerDTO +
                '}';
    }
}
