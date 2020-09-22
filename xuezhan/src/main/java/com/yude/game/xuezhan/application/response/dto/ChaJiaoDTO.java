package com.yude.game.xuezhan.application.response.dto;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

/**
 * @Author: HH
 * @Date: 2020/9/12 12:38
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class ChaJiaoDTO {
    private Integer posId;
    private long beforeScore;
    private long changeScore;
    private long remainingScore;

    public Integer getPosId() {
        return posId;
    }

    public ChaJiaoDTO setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }

    public long getBeforeScore() {
        return beforeScore;
    }

    public ChaJiaoDTO setBeforeScore(long beforeScore) {
        this.beforeScore = beforeScore;
        return this;
    }

    public long getChangeScore() {
        return changeScore;
    }

    public ChaJiaoDTO setChangeScore(long changeScore) {
        this.changeScore = changeScore;
        return this;
    }

    public long getRemainingScore() {
        return remainingScore;
    }

    public ChaJiaoDTO setRemainingScore(long remainingScore) {
        this.remainingScore = remainingScore;
        return this;
    }

    @Override
    public String toString() {
        return "ChaJiaoDTO{" +
                "posId=" + posId +
                ", beforeScore=" + beforeScore +
                ", changeScore=" + changeScore +
                ", remainingScore=" + remainingScore +
                '}';
    }
}
