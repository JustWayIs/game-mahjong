package com.yude.game.common.application.controller;

import com.yude.game.common.RoomManager;
import com.yude.game.common.application.request.LoginRequest;
import com.yude.game.common.application.response.LoginResponse;
import com.yude.game.common.command.annotation.RequestCommand;
import com.yude.game.common.command.annotation.RequestController;
import com.yude.game.common.constant.PlayerStatusEnum;
import com.yude.game.common.constant.Status;
import com.yude.game.common.contant.MahjongCommandCode;
import com.yude.game.common.manager.IRoomManager;
import com.yude.game.common.model.Player;
import com.yude.game.communication.tcp.server.session.ISessionManager;
import com.yude.protocol.common.MessageType;
import com.yude.protocol.common.response.Response;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: HH
 * @Date: 2020/6/22 18:41
 * @Version: 1.0
 * @Declare:
 */
@RequestController
public class AuthUserController {

    private static final Logger log = LoggerFactory.getLogger(AuthUserController.class);

    @Autowired
    ISessionManager sessionManager;

    @Autowired
    IRoomManager roomManager;

    @RequestCommand(value = MahjongCommandCode.LOGIN,messageType = MessageType.SERVICE)
    public Response auth(LoginRequest request, ChannelHandlerContext context){
        log.debug("收到登录请求： request={}",request);
        /**
         * 如果验证失败，或者出现了什么异常：
         *
         */
        Long userId = request.getUserId();
        String sessionId = sessionManager.addSession(userId, context);

        Player player = RoomManager.pullPlayer(userId);
        Status playerStatus = player.getStatus();
        LoginResponse response = new LoginResponse(sessionId,playerStatus.status());
        if(PlayerStatusEnum.GAMEMING.equals(playerStatus)){
            Long roomId = roomManager.getRoomIdByUserId(userId);
            response.setRoomId(roomId);
        }
        return response;
    }
}
