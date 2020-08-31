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
    protected Long userId;
    protected String nickName;
    protected String headUrl;
    protected Long score;
    protected Integer posId;

    public Long getUserId() {
        return userId;
    }

    public BaseSeatInfo setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getNickName() {
        return nickName;
    }

    public BaseSeatInfo setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public BaseSeatInfo setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
        return this;
    }

    public Long getScore() {
        return score;
    }

    public BaseSeatInfo setScore(Long score) {
        this.score = score;
        return this;
    }

    public Integer getPosId() {
        return posId;
    }

    public BaseSeatInfo setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }

    @Override
    public String toString() {
        return "BaseSeatInfo{" +
                "userId=" + userId +
                ", nickName='" + nickName + '\'' +
                ", headUrl='" + headUrl + '\'' +
                ", score=" + score +
                ", posId=" + posId +
                '}';
    }
}
