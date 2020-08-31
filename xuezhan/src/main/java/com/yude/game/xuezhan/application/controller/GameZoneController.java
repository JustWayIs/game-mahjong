package com.yude.game.xuezhan.application.controller;

import com.yude.game.common.application.controller.BaseController;
import com.yude.game.common.command.annotation.RequestCommand;
import com.yude.game.common.command.annotation.RequestController;
import com.yude.game.common.manager.IRoomManager;
import com.yude.game.xuezhan.application.request.DingQueRequest;
import com.yude.game.xuezhan.application.request.ExchangeCardRequest;
import com.yude.game.xuezhan.constant.XueZhanCommandCode;
import com.yude.game.xuezhan.domain.XueZhanRoom;
import com.yude.protocol.common.constant.StatusCodeEnum;
import com.yude.protocol.common.response.CommonResponse;
import com.yude.protocol.common.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;



/**
 * @Author: HH
 * @Date: 2020/8/27 15:09
 * @Version: 1.0
 * @Declare:
 */
@RequestController
public class GameZoneController implements BaseController {
    private static final Logger log = LoggerFactory.getLogger(GameZoneController.class);

    @Autowired
    IRoomManager<XueZhanRoom> roomManager;

    @RequestCommand(value = XueZhanCommandCode.EXCHANGE_CARD)
    public Response exchangeCard(ExchangeCardRequest request){
        Long userId = request.getUserIdByChannel();
        XueZhanRoom room = roomManager.getRoomByUserId(userId);
        if(room == null){
            log.warn("玩家已经不在游戏中: userId={}",userId);
            return  new CommonResponse(StatusCodeEnum.FAIL);
        }
        room.exchangeCard(request.getDiscardCardList(),room.getPosId(userId));
        Response commonResponse = new CommonResponse(StatusCodeEnum.SUCCESS);
        return commonResponse;
    }

    @RequestCommand(value = XueZhanCommandCode.DING_QUE)
    public void dingQue(DingQueRequest request){
        Long userId = request.getUserIdByChannel();
        XueZhanRoom room = roomManager.getRoomByUserId(userId);
        if(room == null){
            log.warn("玩家已经不在游戏中: userId={}",userId);
            return;
        }
        room.dingQue(request.getColor(),room.getPosId(userId));
    }
}
