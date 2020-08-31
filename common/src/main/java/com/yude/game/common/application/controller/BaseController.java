package com.yude.game.common.application.controller;

import com.yude.protocol.common.request.Request;

/**
 * @Author: HH
 * @Date: 2020/7/8 10:37
 * @Version: 1.0
 * @Declare:
 */
public interface BaseController {

    /**
     * 问题在于客户端拿到的可能不是userId 而是 openId、token之类的东西
     * @param request
     * @param userId
     * @return
     */
    default boolean validUser(Request request, Long userId){
        if(userId == null){
            return false;
        }
        return request.getUserIdByChannel().equals(userId);

    }
}
