package com.yude.game.xuezhan.application.response.dto;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/27 10:23
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class SichuanGameZoneInfoDTO {
    protected Long roomId;
    protected Integer zoneId;
    protected Integer step;
    protected List<Integer> diceList;
    protected Integer bankerPosId;
    protected Integer posId;
    protected Integer gameStatus;
    /**
     * 当前操作人如果不是当前出牌玩家的话，是不能告诉其他玩家的
     */
    private Integer currentOperatorPosId;
    private Integer currentOutCardPosId;

    private Integer beforeOperatorPosId;
    private Integer beforeOutCardPosId;
    private Integer beforeOutCard;

    private Integer exchangeType;
}
