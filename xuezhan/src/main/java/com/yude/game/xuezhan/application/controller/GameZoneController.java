package com.yude.game.xuezhan.application.controller;

import com.yude.game.common.application.controller.BaseController;
import com.yude.game.common.command.annotation.RequestCommand;
import com.yude.game.common.command.annotation.RequestController;
import com.yude.game.common.contant.MahjongStatusCodeEnum;
import com.yude.game.common.manager.IRoomManager;
import com.yude.game.xuezhan.application.request.DingQueRequest;
import com.yude.game.xuezhan.application.request.ExchangeCardRequest;
import com.yude.game.xuezhan.application.request.OperationCardRequest;
import com.yude.game.xuezhan.application.request.ReconnectRequest;
import com.yude.game.xuezhan.constant.XueZhanCommandCode;
import com.yude.game.xuezhan.domain.XueZhanRoom;
import com.yude.protocol.common.MessageType;
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

    /**
     * 其实吧，这里都不应该感知Room这个东西，应该面向Room提供的接口.
     * 对于应用层的而言，我管你是游戏服务器，还是什么服务
     */
    @Autowired
    IRoomManager<XueZhanRoom> roomManager;

    @RequestCommand(value = XueZhanCommandCode.EXCHANGE_CARD)
    public void exchangeCard(ExchangeCardRequest request) {
        log.debug("玩家请求换三张：{}", request);
        Long userId = request.getUserIdByChannel();
        XueZhanRoom room = roomManager.getRoomByUserId(userId);
        if (room == null) {
            log.warn("玩家已经不在游戏中: userId={}", userId);
            return;
        }
        room.exchangeCard(request.getDiscardCardList(), room.getPosId(userId));

    }

    @RequestCommand(value = XueZhanCommandCode.DING_QUE)
    public void dingQue(DingQueRequest request) {
        log.debug("玩家请求定缺：{}", request);
        Long userId = request.getUserIdByChannel();
        XueZhanRoom room = roomManager.getRoomByUserId(userId);
        if (room == null) {
            log.warn("玩家已经不在游戏中: userId={}", userId);
            return;
        }
        room.dingQue(request.getColor(), room.getPosId(userId));
    }

    @RequestCommand(value = XueZhanCommandCode.OPERATION_CARD)
    public Response operation(OperationCardRequest request) {
        log.debug("玩家请求操作：{}", request);
        Long userId = request.getUserIdByChannel();
        XueZhanRoom room = roomManager.getRoomByUserId(userId);
        if (room == null) {
            log.warn("玩家已经不在游戏中: userId={}", userId);
            CommonResponse commonResponse = new CommonResponse(MahjongStatusCodeEnum.PLAYER_NOT_GAMEMING);
            return commonResponse;
        }
        final MessageType messageType = request.getMessageType();
        if(!MessageType.TIMEOUT.equals(messageType)){
            room.resetSerialTimeoutCount(room.getPosId(userId));
        }
        room.operation(request.getCard(), request.getOperationType(), userId,false);
        CommonResponse response = new CommonResponse(MahjongStatusCodeEnum.SUCCESS);
        return response;
       /* Integer operationType = request.getOperationType();
        XueZhanMahjongOperationEnum operationEnum = XueZhanMahjongOperationEnum.matchByValue(request.getOperationType());
        switch (operationEnum) {
            case OUT_CARD:
                room.outCard(request.getCard(), room.getPosId(userId));
                break;
            case PENG:
                room.peng(request.getCard(), room.getPosId(userId));
                break;
            case ZHI_GANG:
            case BU_GANG:
            case AN_GANG:
                room.gang(request.getCard(), operationType, room.getPosId(userId));
                break;
            case HU:
                room.hu(request.getCard(), room.getPosId(userId));
                break;
            case CANCEL:
                room.cancel(request.getCard(), room.getPosId(userId));
                break;
            default:
                log.error("没有匹配的操作类型 request={}", request);
                throw new BizException(MahjongStatusCodeEnum.NO_MATCH_OPERATION);*/
    }

    @RequestCommand(value = XueZhanCommandCode.RECONNECT)
    public Response reconnect(ReconnectRequest request){
        log.debug("玩家请求重连： request={}",request);
        Long userId = request.getUserIdByChannel();
        XueZhanRoom room = roomManager.getRoomByUserId(userId);
        if (room == null) {
            log.warn("玩家已经不在游戏中: userId={}", userId);
            CommonResponse commonResponse = new CommonResponse(MahjongStatusCodeEnum.PLAYER_NOT_GAMEMING);
            return commonResponse;
        }
        room.reconnect(userId);
        CommonResponse response = new CommonResponse(MahjongStatusCodeEnum.SUCCESS);
        return response;
    }

}


