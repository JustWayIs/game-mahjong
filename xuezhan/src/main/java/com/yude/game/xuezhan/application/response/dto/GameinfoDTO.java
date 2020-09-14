package com.yude.game.xuezhan.application.response.dto;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

/**
 * @Author: HH
 * @Date: 2020/9/14 19:29
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class GameinfoDTO {
    private Integer gameStatus;
    private Integer cardWallRemainingSize;
    private Integer currentTookCardPosId;
    private Integer currentOperationPosId;

}
