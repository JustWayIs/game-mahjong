package com.yude.game.common.application.controller;

import com.yude.game.common.application.request.MatchRequest;
import com.yude.game.common.command.annotation.RequestCommand;
import com.yude.game.common.command.annotation.RequestController;
import com.yude.game.common.contant.MahjongCommandCode;
import com.yude.game.common.contant.MahjongStatusCodeEnum;
import com.yude.game.common.manager.IRoomManager;
import com.yude.game.exception.BizException;
import com.yude.protocol.common.constant.StatusCodeEnum;
import com.yude.protocol.common.response.CommonResponse;
import com.yude.protocol.common.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: HH
 * @Date: 2020/6/22 16:56
 * @Version: 1.0
 * @Declare:
 */
@RequestController
public class JoinRoomController implements BaseController{
    private static final Logger log = LoggerFactory.getLogger(JoinRoomController.class);

    @Autowired
    IRoomManager roomManager;


    @RequestCommand(MahjongCommandCode.MATCH)
    public Response match(MatchRequest request){
        if(!validUser(request,request.getUserId())){
            throw new BizException("匹配用户校验失败,channel保存的玩家标识 与请求参数中的玩家标识不一致", MahjongStatusCodeEnum.MATCH_VALID_FAIL);
        }
        roomManager.match(request.getUserId());
        CommonResponse commonResponse = new CommonResponse(StatusCodeEnum.SUCCESS);
        return commonResponse;
    }
}
