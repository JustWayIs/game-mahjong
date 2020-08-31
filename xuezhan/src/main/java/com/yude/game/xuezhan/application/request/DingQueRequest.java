package com.yude.game.xuezhan.application.request;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.yude.protocol.common.request.AbstractRequest;

/**
 * @Author: HH
 * @Date: 2020/8/27 15:08
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class DingQueRequest extends AbstractRequest {
    private Integer color;

    public Integer getColor() {
        return color;
    }

    public DingQueRequest setColor(Integer color) {
        this.color = color;
        return this;
    }
}
