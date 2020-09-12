package com.yude.game.xuezhan.application.response.dto;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

/**
 * @Author: HH
 * @Date: 2020/9/12 12:39
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class ChaHuaZhuDTO {

    private Integer posId;
    private long beforeScore;
    private long changeScore;
    private long remainingScore;

    public Integer getPosId() {
        return posId;
    }

    public ChaHuaZhuDTO setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }

    public long getBeforeScore() {
        return beforeScore;
    }

    public ChaHuaZhuDTO setBeforeScore(long beforeScore) {
        this.beforeScore = beforeScore;
        return this;
    }

    public long getChangeScore() {
        return changeScore;
    }

    public ChaHuaZhuDTO setChangeScore(long changeScore) {
        this.changeScore = changeScore;
        return this;
    }

    public long getRemainingScore() {
        return remainingScore;
    }

    public ChaHuaZhuDTO setRemainingScore(long remainingScore) {
        this.remainingScore = remainingScore;
        return this;
    }

    @Override
    public String toString() {
        return "ChaHuaZhuDTO{" +
                "posId=" + posId +
                ", beforeScore=" + beforeScore +
                ", changeScore=" + changeScore +
                ", remainingScore=" + remainingScore +
                '}';
    }
}
