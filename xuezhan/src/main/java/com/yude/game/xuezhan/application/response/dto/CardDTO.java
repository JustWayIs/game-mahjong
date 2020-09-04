package com.yude.game.xuezhan.application.response.dto;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/1 17:55
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class CardDTO {
    private List<Integer> cards;
    private Integer type;
}
