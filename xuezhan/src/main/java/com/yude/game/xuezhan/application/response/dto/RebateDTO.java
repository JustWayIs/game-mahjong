package com.yude.game.xuezhan.application.response.dto;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

/**
 * @Author: HH
 * @Date: 2020/9/11 20:08
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class RebateDTO {
    private Integer posId;
    private long beforeScore;
    private long changeScore;
    private long remainingScore;


    public Integer getPosId() {
        return posId;
    }

    public RebateDTO setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }

    public long getBeforeScore() {
        return beforeScore;
    }

    public RebateDTO setBeforeScore(long beforeScore) {
        this.beforeScore = beforeScore;
        return this;
    }

    public long getChangeScore() {
        return changeScore;
    }

    public RebateDTO setChangeScore(long changeScore) {
        this.changeScore = changeScore;
        return this;
    }

    public long getRemainingScore() {
        return remainingScore;
    }

    public RebateDTO setRemainingScore(long remainingScore) {
        this.remainingScore = remainingScore;
        return this;
    }

    @Override
    public String toString() {
        return "RebateDTO{" +
                "posId=" + posId +
                ", beforeScore=" + beforeScore +
                ", changeScore=" + changeScore +
                ", remainingScore=" + remainingScore +
                '}';
    }
}
