package com.yude.game.xuezhan.application.request;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.yude.protocol.common.request.AbstractRequest;

/**
 * @Author: HH
 * @Date: 2020/9/24 15:08
 * @Version: 1.0
 * @Declare:
 */
@EnableZigZap
@ProtobufClass
public class ReconnectRequest extends AbstractRequest {
    private long roomId;

    public long getRoomId() {
        return roomId;
    }

    public ReconnectRequest setRoomId(long roomId) {
        this.roomId = roomId;
        return this;
    }

    @Override
    public String toString() {
        return "ReconnectRequest{" +
                "roomId=" + roomId +
                '}';
    }
}
