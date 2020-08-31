package com.yude.game.common.application.response.dto;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

/**
 * @Author: HH
 * @Date: 2020/6/29 20:54
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class PlayerDTO {
    private Long userId;

    private String name;
    /**
     * 头像地址
     */
    private String headUrl;
    /**
     * 玩家积分
     */
    private Long score;


    public PlayerDTO() {
    }

    public PlayerDTO(Long userId, String name, String headUrl, Long score) {
        this.userId = userId;
        this.name = name;
        this.headUrl = headUrl;
        this.score = score;
    }

    public Long getUserId() {
        return userId;
    }

    public PlayerDTO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getName() {
        return name;
    }

    public PlayerDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public PlayerDTO setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
        return this;
    }

    public Long getScore() {
        return score;
    }

    public PlayerDTO setScore(Long score) {
        this.score = score;
        return this;
    }

    @Override
    public String toString() {
        return "PlayerDTO{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", headUrl='" + headUrl + '\'' +
                ", score=" + score +
                '}';
    }
}
