package com.yude.game.xuezhan.domain;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baidu.bjf.remoting.protobuf.Any;
import com.yude.game.common.application.response.dto.PlayerDTO;
import com.yude.game.common.constant.PlayerStatusEnum;
import com.yude.game.common.contant.MahjongStatusCodeEnum;
import com.yude.game.common.contant.OperationEnum;
import com.yude.game.common.contant.PushCommandCode;
import com.yude.game.common.dispatcher.event.DisruptorRegistrar;
import com.yude.game.common.mahjong.PlayerHand;
import com.yude.game.common.manager.IPushManager;
import com.yude.game.common.manager.IRoomManager;
import com.yude.game.common.model.*;
import com.yude.game.common.model.fan.*;
import com.yude.game.common.model.fan.judge.HuTypeFan;
import com.yude.game.common.model.fan.judge.appended.*;
import com.yude.game.common.model.fan.judge.base.PingHuFan;
import com.yude.game.common.model.fan.judge.base.QiDuiFan;
import com.yude.game.common.model.fan.judge.compound.QingJinGouDiaoFan;
import com.yude.game.common.model.fan.judge.compound.QingPengFan;
import com.yude.game.common.model.fan.judge.compound.QingShiBaLuoHanFan;
import com.yude.game.common.model.fan.judge.formal.*;
import com.yude.game.common.model.history.*;
import com.yude.game.common.model.sichuan.*;
import com.yude.game.common.model.sichuan.constant.ExchangeTypeEnum;
import com.yude.game.common.model.sichuan.constant.SeatStatusEnum;
import com.yude.game.common.model.sichuan.constant.SichuanGameStatusEnum;
import com.yude.game.common.model.sichuan.history.*;
import com.yude.game.common.model.sichuan.history.info.*;
import com.yude.game.common.timeout.MahjongTimeoutTaskPool;
import com.yude.game.communication.dispatcher.IProducerWithTranslator;
import com.yude.game.exception.BizException;
import com.yude.game.xuezhan.application.request.DingQueRequest;
import com.yude.game.xuezhan.application.request.ExchangeCardRequest;
import com.yude.game.xuezhan.application.request.OperationCardRequest;
import com.yude.game.xuezhan.application.response.*;
import com.yude.game.xuezhan.application.response.dto.*;
import com.yude.game.xuezhan.constant.XueZhanCommandCode;
import com.yude.game.xuezhan.constant.XueZhanMahjongOperationEnum;
import com.yude.game.xuezhan.constant.XueZhanPushCommandCode;
import com.yude.game.xuezhan.domain.action.BaseRoomProcess;
import com.yude.game.xuezhan.domain.action.XueZhanAction;
import com.yude.protocol.common.MessageType;
import com.yude.protocol.common.message.GameRequestMessage;
import com.yude.protocol.common.message.GameRequestMessageHead;
import com.yude.protocol.common.request.Request;
import com.yude.protocol.common.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * @Author: HH
 * @Date: 2020/8/1 15:29
 * @Version: 1.0
 * @Declare:
 */
public class XueZhanRoom extends AbstractRoomModel<XueZhanZone, XueZhanSeat, MahjongTimeoutTaskPool> implements XueZhanAction, BaseRoomProcess {

    private static final Logger log = LoggerFactory.getLogger(XueZhanRoom.class);
    private SichuanMahjongZone sichuanMahjongZone;
    private MahjongZone mahjongZone;
    private SiChuanMahjongRule mahjongRule;

    /**
     * 从XueZhanZone挪到外层
     */
    private List<GameStepModel> historyList;


    @Override
    public void init(IRoomManager roomManager, Long roomId, List<Player> playerList, int roundLimit, int inningLimit) {
        ruleInit();
        historyList = new ArrayList<>();
        super.init(roomManager, roomId, playerList, roundLimit, inningLimit);
        //存储到分布式缓存中
    }


    @Override
    public void clean() {
        for (Map.Entry<Long, Integer> entry : userPosIdMap.entrySet()) {
            Integer posId = entry.getValue();
            XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
            Player player = xueZhanSeat.getPlayer();
            roomManager.changePlayerStatus(player, PlayerStatusEnum.FREE_TIME);
        }
        destroy();
    }

    private static final AtomicInteger roomCount = new AtomicInteger(0);

    @Override
    public void destroy() {
        log.debug("完成第 {} 局游戏", roomCount.incrementAndGet());
        int gameInning = gameZone.getInning();
        boolean isFinish = gameInning >= inningLimit;
        if (isFinish) {
            log.info("房间销毁： roomId={} ", roomId);
            //房间销毁
            posIdSeatMap = null;
            roomManager.destroyRoom(roomId);
        }
    }

    @Override
    public XueZhanSeat getPracticalSeatModle(Player player, int posId) {
        return new XueZhanSeat(player, posId);
    }

    @Override
    public XueZhanZone getPracticalGameZoneModel() {
        //进行下一局的时候
        int gameRound = 1;
        int gameInning = 1;
        if (gameZone != null) {
            if (gameZone.getInning() + 1 > inningLimit) {
                gameRound = gameZone.getRound() + 1;
            } else {
                gameInning = gameZone.getInning() + 1;
            }
        }

        /**
         * 给游戏域 和 位置 建立关联关系：聚合关系。与room中指向的是同一个位置对象
         *
         * 数组下标位置就是 posId 【斗地主应该也像这样设置】
         */
        XueZhanSeat[] playerSeats = new XueZhanSeat[posIdSeatMap.size()];
        for (int i = 0; i < playerSeats.length; i++) {
            playerSeats[i] = posIdSeatMap.get(i);
        }

        /**
         * 这一段的作用是废弃 对XueZhanZone的调用，直接让XueZhanRoom来判断应该调用 mahjongZone 还是 siChuanZone 当有特殊需求时，再选择调用 xueZhanZone.
         */
        MahjongSeat[] mahjongSeats = new MahjongSeat[playerSeats.length];
        SichuanMahjongSeat[] sichuanMahjongSeats = new SichuanMahjongSeat[playerSeats.length];
        int i = 0;
        for (XueZhanSeat xueZhanSeat : playerSeats) {
            mahjongSeats[i] = xueZhanSeat.getMahjongSeat();
            sichuanMahjongSeats[i] = xueZhanSeat.getSichuanMahjongSeat();
            sichuanMahjongSeats[i].setMahjongSeat(mahjongSeats[i]);
            ++i;
        }
        mahjongZone = new MahjongZone(mahjongSeats, gameRound, gameInning);
        sichuanMahjongZone = new SichuanMahjongZone(sichuanMahjongSeats, gameRound, gameInning);
        sichuanMahjongZone.setMahjongZone(mahjongZone);

        return new XueZhanZone(playerSeats, gameRound, gameInning, mahjongZone, sichuanMahjongZone);
    }

    @Override
    public void startGame() {
        gameZone = getPracticalGameZoneModel();
        log.debug("血战到底 游戏开始 roomId={}  gameId={}", roomId, gameZone.getZoneId());
        gameZone.init();
        mahjongZone.deal(SichuanMahjongCard.values(), SichuanGameStatusEnum.DEAL_CARD, roomId, historyList);
        //0号玩家在该游戏没有任何操作的情况下重连了


        noticePlayersDealCardsResult();

        //noticePlayersChangeCard();
    }

    public void operation(Integer card, Integer operationType, Long userId, boolean isRestore) {
        Integer operationPosId = userPosIdMap.get(userId);
        XueZhanMahjongOperationEnum operationEnum = XueZhanMahjongOperationEnum.matchByValue(operationType);
        if (isRestore) {
            log.info("多操作的最终执行： roomId={} zoneId={} card={} operation={} 方位=[{},posId={}]", roomId, gameZone.getZoneId(), card, operationEnum, getSeatDirection(operationPosId), operationPosId);
        }

        switch (operationEnum) {
            case OUT_CARD:
                outCard(card, operationPosId);
                break;
            case PENG:

            case ZHI_GANG:

            case BU_GANG:

            case AN_GANG:
                operation(card, operationPosId, operationEnum, isRestore);
                break;

            case HU:
                hu(card, operationPosId, isRestore);
                break;
            case CANCEL:
                cancel(card, operationPosId);
                break;
            default:
                log.error("没有匹配的操作类型 roomId={} zoneId={} posId={} card={} operationType={}", roomId, gameZone.getZoneId(), operationPosId, card, operationEnum);
                throw new BizException(MahjongStatusCodeEnum.NO_MATCH_OPERATION);
        }
    }

    @Override
    public void outCard(Integer card, Integer posId) {
        XueZhanSeat seat = posIdSeatMap.get(posId);
        final MahjongSeat mahjongSeat = seat.getMahjongSeat();
        log.info("出牌：roomId={} zoneId={} 方位=[{},posId={}] card={} 立牌：{}", roomId, gameZone.getZoneId(), getSeatDirection(posId), posId, card,mahjongSeat.getStandCardList());

        if (!seat.canOperation(XueZhanMahjongOperationEnum.OUT_CARD)) {
            log.warn("玩家没有出牌权限：roomId={} zoneId=={} userId={}  方位=[{},posId={}] card={}", roomId, gameZone.getZoneId(), seat.getUserId(), getSeatDirection(posId), posId, card);
            throw new BizException(MahjongStatusCodeEnum.NO_OUT_CARD_PERMISSION);
        }

        GameStepModel<OperationCardStep> gameStepModel = mahjongZone.outCard(card, posId);
        historyList.add(gameStepModel);
        OperationCardStep step = gameStepModel.getOperationStep();
        StepAction action = step.getAction();

        OperationResultResponse response = new OperationResultResponse();
        response.setPosId(step.getPosId())
                .setTargetCard(action.getTargetCard())
                .setOperationType(action.getOperationType().value());
        pushToRoomUser(XueZhanPushCommandCode.OPERATION_RESULT_NOTICE, response);

        noticePlayerCurrentTingInfo(seat);

        /**
         * 判断有没有玩家可以针对这次出牌进行操作
         */
        final SichuanRoomConfig ruleConfig = mahjongRule.getRuleConfig();
        List<MahjongSeat> canOperationSeats =
                sichuanMahjongZone.otherPalyerCanDo(seat.getSichuanMahjongSeat(), card, ruleConfig);
        if (canOperationSeats.size() > 0) {
            for (MahjongSeat canOperationSeat : canOperationSeats) {
                List<OperationDTO> operationList = new ArrayList<>();
                List<StepAction> canOperations = canOperationSeat.getCanOperations();
                canOperations.stream().forEach(operation -> {
                    OperationDTO operationDTO = new OperationDTO(operation.getOperationType().value(), card);
                    operationList.add(operationDTO);
                });

                final int timeoutTime = getTimeoutTime(canOperationSeat,false);
                OperationNoticeResponse noticeResponse = new OperationNoticeResponse(canOperationSeat.getPosId(), operationList,timeoutTime);
                roomManager.pushToUser(XueZhanPushCommandCode.OPERATION_NOTICE, canOperationSeat.getUserId(), noticeResponse, roomId);

                refreshTimeout(SichuanGameStatusEnum.OPERATION_CARD);
            }
        } else {
            Integer needTookCardPosId = sichuanMahjongZone.refreshTookPlayer();
            nextPalyerTookCard(needTookCardPosId);
        }

    }

    /**
     *
     * @param mahjongSeat
     * @param isOutCard
     * @return
     */
    private int getTimeoutTime(MahjongSeat mahjongSeat,boolean isOutCard){
        final int serialTimeoutCount = mahjongSeat.getSerialTimeoutCount();
        final SichuanRoomConfig ruleConfig = mahjongRule.getRuleConfig();
        int timeoutTime = ruleConfig.getTimeoutTimeByGameStatus((SichuanGameStatusEnum) mahjongZone.getGameStatus());
        if(isOutCard){
            timeoutTime = ruleConfig.getTimeoutTimeByGameStatus(SichuanGameStatusEnum.OUT_CARD);
        }
        if(serialTimeoutCount >= ruleConfig.getSerialTimeoutCountLimit()){
            timeoutTime = 5000;
        }
        return timeoutTime;
    }

    private void noticePlayerCurrentTingInfo(XueZhanSeat xueZhanSeat) {
        log.debug("查找玩家的听牌信息: roomId={} gameZoneId={} 方位[{},posId={}]", roomId, gameZone.getZoneId(),getSeatDirection(xueZhanSeat.getPosId()), xueZhanSeat.getPosId());
        XueZhanSeat tempSeat;
        try {
            tempSeat = xueZhanSeat.clone();
        } catch (CloneNotSupportedException e) {
            log.error("获取停牌信息时，克隆XueZhanSeat 失败： roomId={} zoneId={} seat={}", roomId, gameZone.getZoneId(), xueZhanSeat);
            return;
        }
        MahjongSeat tempMahjongSeat = tempSeat.getMahjongSeat();

        final List<Integer> standCardList = tempMahjongSeat.getStandCardList();
        final List<Integer> tempStandCardList = new ArrayList<>(standCardList);
        final PlayerHand playerHand = tempMahjongSeat.getPlayerHand();

        List<TingInfoDTO> tingList = new ArrayList<>();
        TingResponse tingResponse = new TingResponse(tingList);
        if (playerHand.isTing()) {
            final Set<Integer> tingCards = playerHand.getTingCards();
            Integer beforeCard = null;
            for (Integer card : tingCards) {
                if (beforeCard != null) {
                    standCardList.remove(beforeCard);
                }
                standCardList.add(card);
                final List<FanInfo> fanInfos = sichuanMahjongZone.checkFan(tempMahjongSeat, card, XueZhanMahjongOperationEnum.HU, -1, mahjongRule);
                final int sumFanNum = sichuanMahjongZone.calculateFanNumByFanInfo(fanInfos);
                TingInfoDTO tingInfoDTO = new TingInfoDTO();
                tingInfoDTO.setCard(card)
                        .setFanNum(sumFanNum);
                Integer cardRemainingNum = mahjongZone.getCardRemainingNum(card);
                for (Integer allCard : tempStandCardList) {
                    if (allCard.equals(card)) {
                        cardRemainingNum--;
                    }
                }
                tingInfoDTO.setRemainingSize(cardRemainingNum);
                tingList.add(tingInfoDTO);
            }
        }
        roomManager.pushToUser(XueZhanPushCommandCode.TING_CARDS, tempMahjongSeat.getUserId(), tingResponse, roomId);
    }

    public void nextPalyerTookCard(Integer needTookCardPosId) {
        /**
         * 没有玩家可以操作，下一个玩家摸牌
         */
        if (sichuanMahjongZone.gameover()) {
            mahjongZone.setGameStatus(SichuanGameStatusEnum.GAME_OVER);
            if (isLiuJu()) {
                sichuanMahjongZone.setLiuJu(true);
            } else {
                sichuanMahjongZone.setLiuJu(false);
            }
            List<SeatInfoDTO> seatInfoDTOS = new ArrayList<>();
            for (Map.Entry<Integer, XueZhanSeat> entry : posIdSeatMap.entrySet()) {
                final Integer posId = entry.getKey();
                final XueZhanSeat value = entry.getValue();
                MahjongSeat mahjongSeat = value.getMahjongSeat();
                SeatInfoDTO seatInfoDTO = new SeatInfoDTO();
                seatInfoDTO.setPosId(posId)
                        .setStandCardList(mahjongSeat.getStandCardList());
                seatInfoDTOS.add(seatInfoDTO);
            }
            GameOverResponse gameOverResponse = new GameOverResponse(seatInfoDTOS, sichuanMahjongZone.getLiuJu());
            pushToRoomUser(XueZhanPushCommandCode.GAME_OVER, gameOverResponse);
            settlement();
            clean();
            return;
        }

        GameStepModel<OperationCardStep> tookCardStep = sichuanMahjongZone.tookCardStep(needTookCardPosId);
        historyList.add(tookCardStep);
        mahjongZone.stepAdd();
        TookCardNoticeResponse tookCardNoticeResponse = new TookCardNoticeResponse(needTookCardPosId);
        tookCardNoticeResponse.setPosId(needTookCardPosId)
                .setCardWallRemaining(mahjongZone.getCardWall().size());

        XueZhanSeat needTookCardSeat = posIdSeatMap.get(needTookCardPosId);
        final MahjongSeat mahjongSeat = needTookCardSeat.getMahjongSeat();
        Long userId = needTookCardSeat.getUserId();
        pushToRoomUser(XueZhanPushCommandCode.TOOK_CARD_NOTICE, tookCardNoticeResponse, userId);

        Integer tookCard = tookCardStep.getOperationStep().getAction().getTargetCard();
        log.info("玩家摸牌：roomId={} 方位=[{},posId={}] 摸的牌={}  摸牌前的立牌={}",roomId,getSeatDirection(needTookCardPosId),needTookCardPosId,tookCard,mahjongSeat.getStandCardList());
        //因为在pushToRoomUser方法中，需要推送的数据已经被序列化了，所以这里修改相同的response不会影响之前的推送
        tookCardNoticeResponse.setCard(tookCard);
        roomManager.pushToUser(XueZhanPushCommandCode.TOOK_CARD_NOTICE, userId, tookCardNoticeResponse, roomId);

        //sichuanMahjongZone.whatCanYouDo(tookCard);
        noticePlayersOperation(tookCard);

        refreshTimeout(SichuanGameStatusEnum.OUT_CARD);
    }


    @Override
    public void hu(Integer card, Integer posId, boolean isRestore) {

        log.info("请求胡操作： roomId={} zoneId={} 方位=[{},posId={}] card={} isRestore={}", roomId, gameZone.getZoneId(), getSeatDirection(posId), posId, card, isRestore);
        XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
        boolean canOperation = xueZhanSeat.canOperation(XueZhanMahjongOperationEnum.HU);
        if (!canOperation) {
            log.warn("玩家没有 胡 的操作权限：roomId={} userId={} 方位=[{},posId={}]", roomId, xueZhanSeat.getUserId(), getSeatDirection(posId), posId);
            throw new BizException(MahjongStatusCodeEnum.NOT_HU_OPERATION);
        }
        MahjongSeat huSeat = xueZhanSeat.getMahjongSeat();
        //一定得确保执行的时候，玩家还有操作权限
        StepAction action = huSeat.getDesignateOperationCardSource(XueZhanMahjongOperationEnum.HU.value(), card);
        if (action == null) {
            //既然目标牌需要校验，并且服务器的seat存储了这个操作的具体信息，那么客户端似乎没有必要再传 目标牌 这个属性上来了
            log.error("玩家没有胡这张牌的权限: roomId={} zoneId={} posId={} card={}", roomId, gameZone.getZoneId(), posId, card);
            throw new BizException(MahjongStatusCodeEnum.HU_PARAM_ERROR);
        }
        if (!isRestore) {
            final Integer cardSourcePosId = action.getCardSource();
            boolean isZiMo = cardSourcePosId.equals(posId);
            final SichuanMahjongSeat sichuanMahjongSeat = xueZhanSeat.getSichuanMahjongSeat();
            sichuanMahjongSeat.setHuCard(card);
            GameStepModel<HuCardStep> stepModel = mahjongZone.hu(card, posId, isZiMo);
            final Integer cardSource = action.getCardSource();
            final XueZhanSeat loserXueZhanSeat = posIdSeatMap.get(cardSource);
            final MahjongSeat loserSeat = loserXueZhanSeat.getMahjongSeat();
            final boolean isQiangGangHu = loserSeat.judgeIsQiangGang();
            if (isQiangGangHu) {
                final HuCardStep operationStep = stepModel.getOperationStep();
                final StepAction operationStepAction = operationStep.getAction();
                operationStepAction.setOperationType(XueZhanMahjongOperationEnum.QIANG_GANG_HU);
                final OperationCardStep buGangStep = loserSeat.getDesinateStep(XueZhanMahjongOperationEnum.QIANG_GANG_HU.value(), card);
                buGangStep.setEffective(false);
                final OperationCardStep pengStep = loserSeat.getDesinateStep(OperationEnum.PENG.value(), card);
                pengStep.setEffective(true);
            }
            historyList.add(stepModel);
        }


        boolean needWait = mahjongZone.existsCanOperation(XueZhanMahjongOperationEnum.HU,posId);
        if (needWait) {
            log.info("玩家胡牌：需要等他其他可胡牌玩家操作 roomId={} zoneId={} 方位=[{},posId={}]  card={}", roomId, gameZone.getZoneId(), getSeatDirection(posId),posId, card);
            return;
        }
        /**
         * 还原临时区的最高优先级操作
         * 或者下一个玩家摸牌
         */
        if (!restoreAction()) {
            //如果不是还原出来的操作，这里的清理其实是第二次执行了，但是多清理一次也没有影响
            //huSeat.clearOperation();
            //把其他玩家所拥有的权限也要清理一遍，因为做了优先级判断。 玩家胡是不需要等待 碰杠玩家的操作的
            for (Map.Entry<Integer, XueZhanSeat> entry : posIdSeatMap.entrySet()) {
                final XueZhanSeat currentXueZhanSeat = entry.getValue();
                final MahjongSeat mahjongSeat = currentXueZhanSeat.getMahjongSeat();
                mahjongSeat.clearOperation();
            }
            mahjongZone.cleanTempAction();

            OperationResultResponse operationResultResponse = new OperationResultResponse();
            operationResultResponse
                    .setPosId(huSeat.getPosId())
                    .setTargetCard(card)
                    .setOperationType(action.getOperationType().value())
                    .setCardSourcePosId(action.getCardSource())
                    .setCombinationCards(huSeat.getStandCardList());

            pushToRoomUser(XueZhanPushCommandCode.OPERATION_RESULT_NOTICE, operationResultResponse);

            HuCardStep huStep = (HuCardStep) huSeat.getOperationHistoryByTypeAndCard(XueZhanMahjongOperationEnum.HU.value(), card);
            StepAction huAction = huStep.getAction();
            Integer cardSourcePosId = huAction.getCardSource();

            //移除放炮玩家的出牌池里 放炮的那张牌
            final XueZhanSeat outCardSeat = posIdSeatMap.get(cardSourcePosId);
            final MahjongSeat outCardMahjongSeat = outCardSeat.getMahjongSeat();
            outCardMahjongSeat.removeLastOutCard();

            List<FanInfo> fanInfos = sichuanMahjongZone.checkFan(huSeat, action.getTargetCard(), XueZhanMahjongOperationEnum.HU, cardSourcePosId, mahjongRule);
            huStep.setFanInfoList(fanInfos);

            huSettlement(xueZhanSeat.getMahjongSeat(), action, fanInfos);
            final List<Integer> standCardList = huSeat.getStandCardList();
            standCardList.remove(card);
            mahjongZone.stepAdd();

            mahjongZone.refreshCurrentPosId(huSeat.getPosId());
            Integer needTookCardPosId = sichuanMahjongZone.refreshTookPlayer();
            nextPalyerTookCard(needTookCardPosId);
        }
    }

    /*public List<FanInfo> checkFan(XueZhanSeat seat, Integer card, Integer operationType) {
        MahjongSeat huPlayerSeat = seat.getMahjongSeat();
        huPlayerSeat.solution();

        StepAction huAction = huPlayerSeat.getDesignateOperationCardSource(operationType, card);
        Integer cardSourcePosId = huAction.getCardSource();
        boolean cardFromSelf = cardSourcePosId.equals(seat.getPosId());

        List<Integer> standCardList = huPlayerSeat.getStandCardList();
        List<Solution> solutions = huPlayerSeat.getPlayerHand().canHu(card, cardFromSelf);
        //血战（无赖子）可能没有要找最大番型的需要
        //可能会出现多个solution能胡，但是番型完全一致的情况（没有被优化掉），比如面子 334455 的理牌
        *//*for(Solution solution : solutions){

        }*//*
        if (solutions.size() == 0) {
            log.error("不可思议的异常 玩家有胡牌权限，但是没有找出能胡牌的 理牌 roomId={} zoneId={} 方位=[{}] seat={}", roomId, gameZone.getZoneId(), getSeatDirection(seat.getPosId()), seat);
            throw new SystemException(StatusCodeEnum.FAIL);
        }

        Solution solution = solutions.get(0);
        BaseHuTypeEnum certaintyBaseHuType = solution.getBaseHuType();
        HuTypeEnum huType = cardFromSelf ? HuTypeEnum.自摸 : HuTypeEnum.点炮胡;

        List<FanInfo<FormalFanTypeEnum>> allFormalFanList = rule.getFormalFanTypeEnumList();
        //这里传的是原立牌，涉及到修改立牌的番型判断，应该先copy一次
        FormalFanParam formalFanParam = FormalFanParam.build(standCardList, huPlayerSeat.getFuLu(), solution, certaintyBaseHuType);

        List<FanInfo> certaintyFanList = new ArrayList<>();
        for (FanInfo<FormalFanTypeEnum> fanInfo : allFormalFanList) {
            boolean flag = fanInfo.judgeFan(formalFanParam);
            if (flag) {
                certaintyFanList.add(fanInfo);
            }
        }

        List<FanType> collect = certaintyFanList.stream().map(fanInfo ->
                fanInfo.getFanType()
        ).collect(Collectors.toList());
        CompoundFanParam compoundFanParam = new CompoundFanParam();
        compoundFanParam.setBaseHuType(certaintyBaseHuType)
                .setFanTypeList(collect);
        for (FanInfo<CompoundFanTypeEnum> fanInfo : rule.getCompoundFanTypeEnumList()) {
            boolean flag = fanInfo.judgeFan(compoundFanParam);
            if (flag) {
                certaintyFanList.add(fanInfo);
                CompoundFanTypeEnum fanType = fanInfo.getFanType();
                FanType[] fanTypes = fanType.excludeFan();
                for (FanType excludeFan : fanTypes) {
                    certaintyFanList.remove(excludeFan);
                }
            }
        }


        XueZhanSeat cardRourceXueZhanSeat = posIdSeatMap.get(cardSourcePosId);
        MahjongSeat cardRourceSeat = cardRourceXueZhanSeat.getMahjongSeat();

        AppendedFanParam appendedFanParam = new AppendedFanParam();
        appendedFanParam.setBanker(huPlayerSeat.getPosId() == mahjongZone.getBankerPosId())
                .setCardWallRemainingCount(mahjongZone.getCardWall().size())
                .setMocardNum(huPlayerSeat.getTookCardCount())
                .setQiangGang(cardRourceSeat.judgeIsQiangGang())
                .setBeforeOperationIsGang(cardRourceSeat.judgeIsGangShangOperation())
                .setStandCardCount(huPlayerSeat.getStandCardList().size())
                .setZiMo(cardFromSelf)
                .setGenCountBySolution(solution);
        for (FanInfo<AppendedTypeEnum> fanInfo : rule.getAppendedTypeEnumList()) {
            boolean b = fanInfo.judgeFan(appendedFanParam);
            if (b) {
                certaintyFanList.add(fanInfo);
            }
        }
        //这里要求一炮多响，不能立马修改step的Action类型，要走完这里，要不然checkFan方法没法区分是普通胡牌，还是一炮多响
        HuCardStep huStep = (HuCardStep) huPlayerSeat.getOperationHistoryByTypeAndCard(XueZhanMahjongOperationEnum.HU.value(), card);
        huStep.setFanInfoList(certaintyFanList);
        return certaintyFanList;
    }*/

    public void multiplePlayerHu(Integer card, Integer cardSourcePosId, List<Integer> posIds) {
        log.info("请求一炮多响操作： roomId={} zoneId={} posIds={} card={}", roomId, gameZone.getZoneId(), posIds, card);
        //一炮多响理论上 只是内部的调用，只调用一次，所以应该不用校验权限，也没有添加权限
        MultipleHuCardStep multipleHuCardStep = new MultipleHuCardStep();

        MultipleHuResultResponse response = new MultipleHuResultResponse();
        response.setPosId(posIds)
                .setTargetCard(card)
                .setCardSourcePosId(cardSourcePosId);
        pushToRoomUser(XueZhanPushCommandCode.YI_PAO_DUO_XIANG, response);

        multipleHuSettlement(card, cardSourcePosId, posIds);
        for (Integer posId : posIds) {
            final XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
            final MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();
            mahjongSeat.removeCard(card);
        }

        final XueZhanSeat outCardSeat = posIdSeatMap.get(cardSourcePosId);
        final MahjongSeat outCardMahjongSeat = outCardSeat.getMahjongSeat();
        outCardMahjongSeat.removeLastOutCard();

        Integer tookCardPosId = null;
        Integer tempCardSourcePosId = cardSourcePosId;
        for (int i = 0; i < 3; ++i) {
            tempCardSourcePosId = (tempCardSourcePosId + 1) % posIdSeatMap.size();
            if (posIds.contains(tempCardSourcePosId)) {
                tookCardPosId = (tempCardSourcePosId + 1) % posIdSeatMap.size();
            }
        }
        nextPalyerTookCard(tookCardPosId);
    }


    public void multipleHuSettlement(Integer card, Integer cardSourcePosId, List<Integer> posIds) {
        log.info("一炮多响结算：roomId={} zoneId={} posIds={} card={}", roomId, gameZone.getZoneId(), posIds, card);
        SettlementResponse settlementResponse = new SettlementResponse();

        SettlementStep settlementStep = new SettlementStep();
        Map<Integer, SettlementInfo> settlementInfoMap = new HashMap<>();
        StepAction stepAction = new StepAction();
        stepAction.setOperationType(XueZhanMahjongOperationEnum.YI_PAO_DUO_XIANG)
                .setTargetCard(card)
                .setCardSource(cardSourcePosId);
        settlementStep.setStep(mahjongZone.getStepCount())
                .setGameStatus(SichuanGameStatusEnum.SETTLEMENT)
                .setAction(stepAction)
                .setSeatSettlementInfoMap(settlementInfoMap);

        List<SettlementInfoDTO> settlementInfoDTOS = new ArrayList<>();
        final SichuanRoomConfig ruleConfig = mahjongRule.getRuleConfig();
        final int baseScoreFactor = ruleConfig.getBaseScoreFactor();

        final XueZhanSeat loserXueZhanSeat = posIdSeatMap.get(cardSourcePosId);
        final MahjongSeat loserSeat = loserXueZhanSeat.getMahjongSeat();
        final Player loserPlayer = loserSeat.getPlayer();

        int loserSumScore = 0;
        int loserSumFanNum = 0;
        for (Integer posId : posIds) {
            final XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
            final SichuanMahjongSeat sichuanMahjongSeat = xueZhanSeat.getSichuanMahjongSeat();
            final MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();
            final Player player = mahjongSeat.getPlayer();
            final List<FanInfo> fanInfos = sichuanMahjongZone.checkFan(mahjongSeat, card, XueZhanMahjongOperationEnum.YI_PAO_DUO_XIANG, cardSourcePosId, mahjongRule);
            final int sumFan = sichuanMahjongZone.calculateFanNumByFanInfo(fanInfos);
            int changeScore = sumFan * baseScoreFactor;

            long beforeScore = player.getScore();
            player.scoreSettle(changeScore);
            long remainingScore = player.getScore();

            final List<StepAction> fuLu = mahjongSeat.getFuLu();
            SettlementInfo settlementInfo = new SettlementInfo();
            settlementInfo.setPosId(posId)
                    .setFanNum(sumFan)
                    .setFanInfoList(fanInfos)
                    .setBeforeScore(beforeScore)
                    .setChangeScore(changeScore)
                    .setRemaningScore(remainingScore)
                    .setFuluList(new ArrayList<>(fuLu))
                    .setStandCards(new ArrayList<>(mahjongSeat.getStandCardList()))
                    .setStandCardsConvertList(MahjongProp.cardConvertName(mahjongSeat.getStandCardList()));
            settlementInfoMap.put(posId, settlementInfo);

            SettlementInfoDTO winInfoDTO = new SettlementInfoDTO();
            winInfoDTO.setPosId(mahjongSeat.getPosId())
                    .setFanInfoList(fanInfos.stream().map(fanInfo -> fanInfo.getFanType().getId()).collect(Collectors.toList()))
                    .setBeforeScore(beforeScore)
                    .setChangeScore(changeScore)
                    .setRemaningScore(remainingScore);
            settlementInfoDTOS.add(winInfoDTO);

            loserSumScore += changeScore;
            loserSumFanNum += sumFan;
        }

        long loserBeforeScore = loserPlayer.getScore();
        loserPlayer.scoreSettle(-loserSumScore);
        long loserRemainingScore = loserPlayer.getScore();

        final List<StepAction> fuLu = loserSeat.getFuLu();
        SettlementInfo settlementInfo = new SettlementInfo();
        settlementInfo.setPosId(cardSourcePosId)
                .setFanNum(loserSumFanNum)
                .setBeforeScore(loserBeforeScore)
                .setChangeScore(-loserSumScore)
                .setRemaningScore(loserRemainingScore)
                .setFuluList(new ArrayList<>(fuLu))
                .setStandCards(new ArrayList<>(loserSeat.getStandCardList()))
                .setStandCardsConvertList(MahjongProp.cardConvertName(loserSeat.getStandCardList()));
        settlementInfoMap.put(cardSourcePosId, settlementInfo);

        SettlementInfoDTO loserInfoDTO = new SettlementInfoDTO();
        loserInfoDTO.setPosId(cardSourcePosId)
                .setBeforeScore(loserBeforeScore)
                .setChangeScore(-loserSumScore)
                .setRemaningScore(loserRemainingScore);
        settlementInfoDTOS.add(loserInfoDTO);

        settlementResponse.setCardSourcePosId(cardSourcePosId)
                .setOperationType(XueZhanMahjongOperationEnum.YI_PAO_DUO_XIANG.value())
                .setTargetCard(card)
                .setSettlementInfoDTOS(settlementInfoDTOS);


        for(Integer posId : posIds){
            final XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
            final MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();
            final List<Integer> standCardList = mahjongSeat.getStandCardList();
            standCardList.remove(card);
        }
        mahjongZone.stepAdd();
        pushToRoomUser(XueZhanPushCommandCode.SETTLEMENT_NOTICE, settlementResponse);
    }

    @Override
    public void cancel(Integer card, Integer posId) {
        log.info("请求过 roomId={} 方位=[{},posId={}]", roomId, getSeatDirection(posId), posId);
        XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
        boolean canCancel = xueZhanSeat.canOperation(XueZhanMahjongOperationEnum.CANCEL);
        if (!canCancel) {
            log.warn("玩家没有 过 的操作权限：roomId={} userId={} 方位=[{},posId={}]", roomId, xueZhanSeat.getUserId(), getSeatDirection(posId), posId);
            throw new BizException(MahjongStatusCodeEnum.NOT_CANCEL_OPERATION);
        }
        //过操作不用通知玩家，要不要响应给本人呢
        //接下来要判断其他用户可不可以操作，可能会涉及到还原操作、通知下一个玩家摸牌
        GameStepModel<OperationCardStep> cancelStep = mahjongZone.cancel(card, posId);
        historyList.add(cancelStep);

        OperationResultResponse operationResultResponse = new OperationResultResponse();
        operationResultResponse
                .setPosId(posId)
                .setOperationType(XueZhanMahjongOperationEnum.CANCEL.value())
                .setTargetCard(card);
        roomManager.pushToUser(XueZhanPushCommandCode.OPERATION_RESULT_NOTICE, xueZhanSeat.getUserId(), operationResultResponse, roomId);

        boolean needWait = mahjongZone.existsCanOperation(XueZhanMahjongOperationEnum.CANCEL);
        if (needWait) {
            log.info("玩家过 需要等待其他玩家操作  roomId={} 方位=[{},posId={}]", roomId, getSeatDirection(posId), posId);
            return;
        }
        /**
         * 还原临时区的最高优先级操作
         * 或者下一个玩家摸牌
         */
        if (!restoreAction()) {
            final OperationCardStep buGangStep = judgeQiangGang();
            if (buGangStep != null) {
                final int buGangPosId = buGangStep.getPosId();
                final XueZhanSeat seat = posIdSeatMap.get(buGangPosId);
                //抢杠胡实际上不是多操作，所以选择执行过的话，是不需要等待玩家操作，或者还原操作
                otherGangSettlement(seat.getMahjongSeat(), buGangStep.getAction());
                //杠完要摸牌
                mahjongZone.refreshCurrentPosId(buGangPosId);
                nextPalyerTookCard(buGangPosId);
                return;
            }
            //多人操作时，前面的玩家点过操作并不会改变当前操作人
            Integer needTookCardPosId = sichuanMahjongZone.refreshTookPlayer();
            nextPalyerTookCard(needTookCardPosId);
        }

    }

    /**
     * 看起来有点抽象，如果倒数第二个操作是补杠。倒数第一个操作是胡，那么就是抢杠胡。如果可以胡的玩家选择了过，
     * 那么需要进行 补杠结算.
     *
     * @return
     */
    public OperationCardStep judgeQiangGang() {
        final int size = historyList.size();
        if (size - 2 > 0) {
            final GameStepModel gameStepModel = historyList.get(size - 2);
            final Step operationStep = gameStepModel.getOperationStep();
            if (XueZhanMahjongOperationEnum.BU_GANG.value().equals(operationStep.actionType())) {
                OperationCardStep buGangSetp = (OperationCardStep) operationStep;
                return buGangSetp;
            }

        }
        return null;
    }

    /**
     * 能产生副露的操作
     *
     * @param card
     * @param posId
     * @param operationType
     */
    public void operation(Integer card, Integer posId, MahjongOperation operationType, boolean isRestore) {
        XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
        final MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();
        log.info("请求操作：  roomId={} zoneId={} 方位=[{},posId={}] type={} card={}  立牌：{}", roomId, gameZone.getZoneId(), getSeatDirection(posId), posId, operationType, card,mahjongSeat.getStandCardList());

        boolean canCancel = xueZhanSeat.canOperation(operationType);
        if (!canCancel) {
            log.warn("玩家没有该的操作权限：operation={} roomId={} userId={} 方位=[{},posId={}]", operationType, roomId, xueZhanSeat.getUserId(), getSeatDirection(posId), posId);
            throw new BizException(MahjongStatusCodeEnum.matchNotOperationErrorByOperation(operationType.value()));
        }

        //一定得确保执行的时候，玩家还有操作权限
        StepAction action = mahjongSeat.getDesignateOperationCardSource(operationType.value(), card);
        if (action == null) {
            //既然目标牌需要校验，并且服务器的seat存储了这个操作的具体信息，那么客户端似乎没有必要再传 目标牌 这个属性上来了
            log.error("操作类型{} 没有操作这张牌的权限: roomId={} zoneId={} posId={} card={}", operationType, roomId, gameZone.getZoneId(), posId, card);
            throw new BizException(MahjongStatusCodeEnum.matchParamErrorByOperation(operationType.value()));
        }
        if (!isRestore) {
            GameStepModel<OperationCardStep> stepModel = mahjongZone.operation(card, posId, operationType);
            historyList.add(stepModel);
        }

        nextProcess(xueZhanSeat, card, action);
    }

    private void refreshTimeout(SichuanGameStatusEnum sichuanGameStatusEnum){
        final SichuanRoomConfig roomConfig = mahjongRule.getRuleConfig();
        final int timeout= roomConfig.getTimeoutTimeByGameStatus(sichuanGameStatusEnum);
        final long currentTimeMillis = System.currentTimeMillis();
        refreshTimeout(timeout + currentTimeMillis);
    }

    private void refreshTimeout(long time){
        final SichuanRoomConfig roomConfig = mahjongRule.getRuleConfig();
        final long currentTimeMillis = System.currentTimeMillis();
        final int timeout= roomConfig.getTimeoutTimeByGameStatus((SichuanGameStatusEnum) mahjongZone.getGameStatus());
        mahjongZone.setTimeoutTime(currentTimeMillis+timeout);

        for(Map.Entry<Integer,XueZhanSeat> entry : posIdSeatMap.entrySet()){
            final XueZhanSeat value = entry.getValue();
            final MahjongSeat mahjongSeat = value.getMahjongSeat();
            long timeoutTime = time;

            if(mahjongSeat.getSerialTimeoutCount() >= roomConfig.getSerialTimeoutCountLimit()){
                timeoutTime = currentTimeMillis + 5000;
            }
            mahjongSeat.setTimeoutTime(timeoutTime);
        }
    }

    @Override
    public void gang(Integer card, Integer type, Integer posId) {
        log.info("请求杠操作： roomId={} 方位=[{},posId={}] type={} card={} posId={}", roomId, getSeatDirection(posId), posId, type, posId);
    }

    public void zhiGang(Integer card, Integer posId) {
        log.info("请求直杠操作： roomId={} zoneId={} 方位=[{},posId={}] type={} card={} posId={}", roomId, gameZone.getZoneId(), getSeatDirection(posId), posId, posId);
        XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
        boolean canCancel = xueZhanSeat.canOperation(XueZhanMahjongOperationEnum.ZHI_GANG);
        if (!canCancel) {
            log.warn("玩家没有 直杠 的操作权限：roomId={} userId={} 方位=[{},posId={}]", roomId, xueZhanSeat.getUserId(), getSeatDirection(posId), posId);
            throw new BizException(MahjongStatusCodeEnum.NOT_ZHI_GANG_OPERATION);
        }
        MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();
        //一定得确保执行的时候，玩家还有操作权限
        StepAction action = mahjongSeat.getDesignateOperationCardSource(XueZhanMahjongOperationEnum.ZHI_GANG.value(), card);
        if (action == null) {
            //既然目标牌需要校验，并且服务器的seat存储了这个操作的具体信息，那么客户端似乎没有必要再传 目标牌 这个属性上来了
            log.error("玩家没有直杠这张牌的权限: roomId={} zoneId={} posId={} card={}", roomId, gameZone.getZoneId(), posId, card);
            throw new BizException(MahjongStatusCodeEnum.ZHI_GANG_PARAM_ERROR);
        }
        GameStepModel<OperationCardStep> stepModel = mahjongZone.zhiGang(card, posId);
        historyList.add(stepModel);
        nextProcess(xueZhanSeat, card, action);
    }

    /**
     * 吃碰杠 操作后 流程应该怎么走
     *
     * @param xueZhanSeat
     * @param card
     * @param action
     */
    private void nextProcess(XueZhanSeat xueZhanSeat, final Integer card, StepAction action) {
        boolean needWait = mahjongZone.existsCanOperation(action.getOperationType(),xueZhanSeat.getPosId());
        if (needWait) {
            log.debug("玩家需要等待操作： roomId={} zoneId={} 方位=[{},posId={}] action={}", roomId, gameZone.getZoneId(), getSeatDirection(xueZhanSeat.getPosId()), xueZhanSeat.getPosId(), action);
            return;
        }
        /**
         * 还原临时区的最高优先级操作
         * 或者下一个玩家摸牌
         */
        if (!restoreAction()) {
            log.info("不需要还原操作： roomId={} zoneId={} 方位=[{},posId={}] action={}", roomId, gameZone.getZoneId(), getSeatDirection(xueZhanSeat.getPosId()), xueZhanSeat.getPosId(), action);
            MahjongSeat operationSeat = xueZhanSeat.getMahjongSeat();
            //historyList.add(stepModel);
            OperationResultResponse operationResultResponse = new OperationResultResponse();
            operationResultResponse
                    .setPosId(operationSeat.getPosId())
                    .setTargetCard(card)
                    .setOperationType(action.getOperationType().value())
                    .setCardSourcePosId(action.getCardSource());

            pushToRoomUser(XueZhanPushCommandCode.OPERATION_RESULT_NOTICE, operationResultResponse);
            mahjongZone.setCurOperatorPosId(operationSeat.getPosId());
            mahjongZone.cleanTempAction();
            operationSeat.clearOperation();

            //H2 如果是杠操作 还要触发小结算。 既然要在这里做具体类型判断，那么其实胡牌也可以直接用operation方法
            //一堆的if-else 中间还夹杂着return，使得逻辑变动混乱
            Integer type = action.getOperationType().value();
            if (XueZhanMahjongOperationEnum.PENG.value().equals(type)) {
                //碰完要出牌
                operationSeat.addOperation(XueZhanMahjongOperationEnum.OUT_CARD);
                OperationDTO operationDTO = new OperationDTO();
                operationDTO.setOpreation(XueZhanMahjongOperationEnum.OUT_CARD.value());
                List<OperationDTO> operationList = new ArrayList<>();
                operationList.add(operationDTO);

                final int timeoutTime = getTimeoutTime(xueZhanSeat.getMahjongSeat(),true);
                OperationNoticeResponse noticeResponse = new OperationNoticeResponse(operationSeat.getPosId(), operationList,timeoutTime);
                roomManager.pushToUser(XueZhanPushCommandCode.OPERATION_NOTICE, operationSeat.getUserId(), noticeResponse, roomId);


                refreshTimeout(SichuanGameStatusEnum.OUT_CARD);
                return;
            } else if (XueZhanMahjongOperationEnum.AN_GANG.value().equals(type)) {
                otherGangSettlement(operationSeat, action);

            } else if (XueZhanMahjongOperationEnum.BU_GANG.value().equals(type)) {
                //由于是补杠，理论上来说，只有可能别的玩家能胡，不可能是直杠、碰
                final List<MahjongSeat> canOperationSeats = sichuanMahjongZone.otherPalyerCanDo(xueZhanSeat.getSichuanMahjongSeat(), card, mahjongRule.getRuleConfig());
                if (canOperationSeats.isEmpty()) {
                    //能够补杠，并且其他玩家不能抢杠胡，前一个操作一定是该玩家的摸牌-- 这里的写法不能体现这个领域知识
                    final GameStepModel gameStepModel = historyList.get(historyList.size() - 2);
                    final Step operationStep = gameStepModel.getOperationStep();
                    if(XueZhanMahjongOperationEnum.TOOK_CARD.value().equals(operationStep.actionType())){
                           OperationCardStep operationCardStep = (OperationCardStep) operationStep;
                        final StepAction tookCardAction = operationCardStep.getAction();
                        if(tookCardAction.getTargetCard().equals(card)){
                            otherGangSettlement(operationSeat, action);
                        }
                    }

                } else {
                    //H2 这段代码跟出牌后的判断是一样的
                    for (MahjongSeat canOperationSeat : canOperationSeats) {
                        List<OperationDTO> operationList = new ArrayList<>();
                        List<StepAction> canOperations = canOperationSeat.getCanOperations();
                        canOperations.stream().forEach(operation -> {
                            OperationDTO operationDTO = new OperationDTO(operation.getOperationType().value(), card);
                            operationList.add(operationDTO);
                        });

                        final int timeoutTime = getTimeoutTime(canOperationSeat,false);
                        OperationNoticeResponse noticeResponse = new OperationNoticeResponse(canOperationSeat.getPosId(), operationList,timeoutTime);
                        roomManager.pushToUser(XueZhanPushCommandCode.OPERATION_NOTICE, canOperationSeat.getUserId(), noticeResponse, roomId);

                    }
                    //方案1: 用一个属性临时存储补杠结算需要的属性 方案2：在cancel方法用其他的方式判断要不要还原补杠结算
                    refreshTimeout(SichuanGameStatusEnum.OPERATION_CARD);
                    return;
                }

            } else if (XueZhanMahjongOperationEnum.ZHI_GANG.value().equals(type)) {
                zhiGangSettlement(operationSeat, action);
            } else if (XueZhanMahjongOperationEnum.HU.equals(type)) {
                //在这里实现的话  hu()方法还有存在必要么

            }
            mahjongZone.stepAdd();
            mahjongZone.refreshCurrentPosId(operationSeat.getPosId());
            //杠完要摸牌
            nextPalyerTookCard(operationSeat.getPosId());
        }
    }

    /**
     * 可以放在四川麻将的游戏域里
     *
     * @param operationSeat
     * @param action
     * @param fanInfos
     */
    private void huSettlement(MahjongSeat operationSeat, final StepAction action, final List<FanInfo> fanInfos) {
        log.info("胡牌结算： roomId={} zoneId={} 方位=[{},posId={}] action={} fanInfos={}", roomId, gameZone.getZoneId(), getSeatDirection(operationSeat.getPosId()), operationSeat.getPosId(), action, fanInfos);
        List<MahjongSeat> neededutionSeats;
        //自摸
        Integer cardSourcePosId = action.getCardSource();
        int huPosId = operationSeat.getPosId();
        if (huPosId == action.getCardSource()) {
            neededutionSeats = sichuanMahjongZone.findNotHuSeat();
        } else {
            neededutionSeats = new ArrayList<>();
            XueZhanSeat xueZhanSeat = posIdSeatMap.get(cardSourcePosId);
            neededutionSeats.add(xueZhanSeat.getMahjongSeat());
        }

        final int sumFan = sichuanMahjongZone.calculateFanNumByFanInfo(fanInfos);
        SichuanRoomConfig ruleConfig = mahjongRule.getRuleConfig();
        int huScore = ruleConfig.getBaseScoreFactor() * sumFan;

        SettlementStep settlementStep = new SettlementStep();
        Map<Integer, SettlementInfo> seatSettlementInfoMap = new HashMap<>();
        settlementStep.setPosId(huPosId)
                .setStep(mahjongZone.getStepCount())
                .setSeatSettlementInfoMap(seatSettlementInfoMap)
                .setGameStatus(SichuanGameStatusEnum.SETTLEMENT)
                //这里的action来自可操作权限
                .setAction(action);

        List<SettlementInfoDTO> settlementInfoDTOList = new ArrayList<>();
        for (MahjongSeat curSeat : neededutionSeats) {
            Integer curPosId = curSeat.getPosId();

            Player player = curSeat.getPlayer();
            long beforeScore = player.getScore();
            player.scoreSettle(-huScore);
            long remainintScore = player.getScore();


            SettlementInfo settlementInfo = new SettlementInfo();
            settlementInfo.setPosId(curPosId)
                    .setBeforeScore(beforeScore)
                    .setChangeScore(-huScore)
                    .setRemaningScore(remainintScore)
                    .setFanNum(sumFan)
                    .setFanInfoList(fanInfos);
            seatSettlementInfoMap.put(curPosId, settlementInfo);

            SettlementInfoDTO settlementInfoDTO = new SettlementInfoDTO();
            settlementInfoDTO.setPosId(curPosId)
                    .setBeforeScore(beforeScore)
                    .setChangeScore(-huScore)
                    .setRemaningScore(remainintScore)
                    .setFanInfoList(fanInfos.stream().map(fanInfo -> fanInfo.getFanType().getId()).collect(Collectors.toList()));
            settlementInfoDTOList.add(settlementInfoDTO);
        }
        Player player = operationSeat.getPlayer();
        int winScore = huScore * neededutionSeats.size();
        long beforeScore = player.getScore();
        player.scoreSettle(winScore);
        long remainintScore = player.getScore();

        SettlementInfo settlementInfo = new SettlementInfo();
        settlementInfo.setPosId(huPosId)
                .setBeforeScore(beforeScore)
                .setChangeScore(winScore)
                .setRemaningScore(remainintScore)
                .setFanNum(sumFan)
                .setFanInfoList(fanInfos);

        seatSettlementInfoMap.put(huPosId, settlementInfo);
        GameStepModel<SettlementStep> huStepModel = new GameStepModel<>(gameZone.getZoneId(), operationSeat.getPlayer(), settlementStep);
        historyList.add(huStepModel);

        SettlementInfoDTO settlementInfoDTO = new SettlementInfoDTO();
        settlementInfoDTO.setPosId(huPosId)
                .setBeforeScore(beforeScore)
                .setChangeScore(winScore)
                .setRemaningScore(remainintScore)
                .setFanInfoList(fanInfos.stream().map(fanInfo -> fanInfo.getFanType().getId()).collect(Collectors.toList()));
        settlementInfoDTOList.add(settlementInfoDTO);

        SettlementResponse settlementResponse = new SettlementResponse();
        settlementResponse.setSettlementInfoDTOS(settlementInfoDTOList)
                .setTargetCard(action.getTargetCard())
                .setOperationType(action.getOperationType().value())
                .setOperationPosId(huPosId)
                .setCardSourcePosId(action.getTargetCard());

        pushToRoomUser(XueZhanPushCommandCode.SETTLEMENT_NOTICE, settlementResponse);

        List<MahjongSeat> needNoticeSeats = new ArrayList<>();
        needNoticeSeats.add(operationSeat);
        needNoticeSeats.addAll(neededutionSeats);
        noticePlayersWaterFlow(needNoticeSeats);
        /*if(action.getCardSource().equals(operationSeat.getPosId())){
            ziMoHuSettlement(operationSeat,type,action,fanInfos);
        }else{
            dianPaoHuSettlement(operationSeat,type,action,fanInfos);
        }*/
    }

    private void ziMoHuSettlement(MahjongSeat operationSeat, Integer type, StepAction action, List<FanInfo> fanInfos) {
        /*int sumFan = 1;
        List<FanInfo> additionFan = new ArrayList<>();
        for(FanInfo fanInfo : fanInfos){
            if(FanInfo.MULTIPLICATION == fanInfo.getCalculationType()){
                sumFan *= fanInfo.getFanScore();
            }
        }
        for(FanInfo fanInfo : additionFan){
            sumFan += fanInfo.getFanScore();
        }
        SichuanRoomConfig ruleConfig = rule.getRuleConfig();
        int huScore = ruleConfig.getBaseScoreFactor() * sumFan;
        sichuanMahjongZone.findNotHuSeat(operationSeat.getPosId());
        for(Map.Entry<Integer,XueZhanSeat> entry : posIdSeatMap.entrySet()){
            Integer curPosId = entry.getKey();
            XueZhanSeat curXueZhanSeat = entry.getValue();
            MahjongSeat mahjongSeat = curXueZhanSeat.getMahjongSeat();
            if(mahjongSeat.existsStatus(SeatStatusEnum.ALREADY_HU)){
                continue;
            }
            Player player = curXueZhanSeat.getPlayer();
            long beforeScore = player.getScore();
            player.scoreSettle();

            SettlementStep settlementStep = new SettlementStep();
            SettlementInfo settlementInfo = new SettlementInfo();
            settlementInfo.setPosId(curPosId)
                    .setBeforeScore(beforeScore)
                    .setChangeScore(huScore)
                    .setRemaningScore()

        }*/
    }

    private void dianPaoHuSettlement(MahjongSeat operationSeat, Integer type, StepAction action, List<FanInfo> fanInfos) {

    }

    private void zhiGangSettlement(MahjongSeat operationSeat, StepAction action) {
        log.info("直杠结算：roomId={} zoneId={} 方位=[{},posId={}] action={}", roomId, gameZone.getZoneId(), getSeatDirection(operationSeat.getPosId()), operationSeat.getPosId(), action);
        SichuanRoomConfig ruleConfig = mahjongRule.getRuleConfig();
        Integer gangFan = ruleConfig.getGangFan(action.getOperationType().value());
        Integer baseScoreFactor = ruleConfig.getBaseScoreFactor();
        int changeScore = gangFan * baseScoreFactor;

        SettlementStep settlementStep = new SettlementStep();
        settlementStep.setStep(mahjongZone.getStepCount())
                .setPosId(operationSeat.getPosId())
                .setGameStatus(SichuanGameStatusEnum.SETTLEMENT)
                .setAction(action);

        Map<Integer, SettlementInfo> seatSettlementInfoMap = new HashMap<>();
        settlementStep.setSeatSettlementInfoMap(seatSettlementInfoMap);

        List<SettlementInfoDTO> settlementInfoDTOS = new ArrayList<>();
        Integer fangGangPosId = action.getCardSource();
        XueZhanSeat fangGangXueZhanSeat = posIdSeatMap.get(fangGangPosId);
        MahjongSeat fangGangSeat = fangGangXueZhanSeat.getMahjongSeat();

        changeGangSocre(fangGangSeat, -changeScore, gangFan, seatSettlementInfoMap, settlementInfoDTOS, XueZhanMahjongOperationEnum.ZHI_GANG.value());
        changeGangSocre(operationSeat, changeScore, gangFan, seatSettlementInfoMap, settlementInfoDTOS, XueZhanMahjongOperationEnum.ZHI_GANG.value());

        GameStepModel<SettlementStep> gameStepModel = new GameStepModel<>(gameZone.getZoneId(), operationSeat.getPlayer(), settlementStep);
        historyList.add(gameStepModel);

        SettlementResponse settlementResponse = new SettlementResponse();
        settlementResponse.setCardSourcePosId(action.getCardSource())
                .setOperationPosId(operationSeat.getPosId())
                .setOperationType(action.getOperationType().value())
                .setTargetCard(action.getTargetCard())
                .setSettlementInfoDTOS(settlementInfoDTOS);
        pushToRoomUser(XueZhanPushCommandCode.SETTLEMENT_NOTICE, settlementResponse);

        List<MahjongSeat> needNoticeSeats = new ArrayList<>();
        needNoticeSeats.add(operationSeat);
        needNoticeSeats.add(fangGangSeat);
        noticePlayersWaterFlow(needNoticeSeats);
    }

    private void changeGangSocre(MahjongSeat mahjongSeat, int changeScore, Integer gangFangNum, Map<Integer, SettlementInfo> seatSettlementInfoMap, List<SettlementInfoDTO> settlementInfoDTOS, Integer operationType) {
        List<StepAction> fuLu = mahjongSeat.getFuLu();
        Integer posId = mahjongSeat.getPosId();

        Player player = mahjongSeat.getPlayer();
        long beforeScore = player.getScore();
        player.scoreSettle(changeScore);
        long remainingScore = player.getScore();

        ArrayList<Integer> standCardList = new ArrayList<>(mahjongSeat.getStandCardList());
        SettlementInfo settlementInfo = new SettlementInfo();
        settlementInfo.setPosId(posId)
                .setBeforeScore(beforeScore)
                .setChangeScore(changeScore)
                .setRemaningScore(remainingScore)
                .setFanNum(gangFangNum)
                .setStandCards(standCardList)
                .setStandCardsConvertList(MahjongProp.cardConvertName(standCardList))
                .setFuluList(new ArrayList<>(fuLu));

        List<FanInfo> fanInfoList = new ArrayList<>();
        final List<FanInfo<SpecificFanTypeEnum>> specificFanTypeList = mahjongRule.getSpecificFanTypeList();
        for (FanInfo<SpecificFanTypeEnum> fanInfo : specificFanTypeList) {
            final SpecificFanTypeEnum fanType = fanInfo.getFanType();
            if (fanType.getActionType().equals(operationType)) {
                FanInfo<SpecificFanTypeEnum> specificFanInfo = new FanInfo<>(fanType, gangFangNum, null, null);
                fanInfoList.add(specificFanInfo);
                break;
            }
        }
        settlementInfo.setFanInfoList(fanInfoList);
        seatSettlementInfoMap.put(posId, settlementInfo);

        SettlementInfoDTO settlementInfoDTO = new SettlementInfoDTO();
        settlementInfoDTO.setPosId(posId)
                .setBeforeScore(beforeScore)
                .setChangeScore(changeScore)
                .setRemaningScore(remainingScore);
        settlementInfoDTOS.add(settlementInfoDTO);
    }

    private void otherGangSettlement(MahjongSeat operationSeat, final StepAction action) {
        log.info("杠分结算：roomId={} zoneId={} 方位=[{},posId={}] action={}", roomId, gameZone.getZoneId(), getSeatDirection(operationSeat.getPosId()), operationSeat.getPosId(), action);
        List<MahjongSeat> needNoticeScoreWaterFlowSeats = new ArrayList<>();
        needNoticeScoreWaterFlowSeats.add(operationSeat);

        SichuanRoomConfig ruleConfig = mahjongRule.getRuleConfig();
        final Integer operationType = action.getOperationType().value();
        Integer gangFan = ruleConfig.getGangFan(operationType);
        Integer baseScoreFactor = ruleConfig.getBaseScoreFactor();
        int changeScore = gangFan * baseScoreFactor;
        int sumScore = 0;

        SettlementStep settlementStep = new SettlementStep();
        settlementStep.setStep(mahjongZone.getStepCount())
                .setPosId(operationSeat.getPosId())
                .setGameStatus(SichuanGameStatusEnum.SETTLEMENT)
                .setAction(action);

        Map<Integer, SettlementInfo> seatSettlementInfoMap = new HashMap<>();
        List<SettlementInfoDTO> settlementInfoDTOS = new ArrayList<>();
        for (Map.Entry<Integer, XueZhanSeat> entry : posIdSeatMap.entrySet()) {
            Integer xueZhanPosId = entry.getKey();
            if (xueZhanPosId.equals(operationSeat.getPosId())) {
                continue;
            }
            XueZhanSeat seat = entry.getValue();
            MahjongSeat currentSeat = seat.getMahjongSeat();
            if (currentSeat.existsStatus(SeatStatusEnum.ALREADY_HU)) {
                continue;
            }
            needNoticeScoreWaterFlowSeats.add(currentSeat);

            sumScore += changeScore;
            List<StepAction> fuLu = currentSeat.getFuLu();

            Player player = currentSeat.getPlayer();
            long beforeScore = player.getScore();
            player.scoreSettle(-changeScore);
            long remainingScore = player.getScore();

            ArrayList<Integer> standCardList = new ArrayList<>(currentSeat.getStandCardList());
            SettlementInfo settlementInfo = new SettlementInfo();
            settlementInfo.setPosId(xueZhanPosId)
                    .setBeforeScore(beforeScore)
                    .setChangeScore(-changeScore)
                    .setRemaningScore(remainingScore)
                    .setFanNum(gangFan)
                    .setStandCards(standCardList)
                    .setStandCardsConvertList(MahjongProp.cardConvertName(standCardList))
                    .setFuluList(new ArrayList<>(fuLu));

            List<FanInfo> fanInfoList = new ArrayList<>();
            final List<FanInfo<SpecificFanTypeEnum>> specificFanTypeList = mahjongRule.getSpecificFanTypeList();

            for (FanInfo<SpecificFanTypeEnum> fanInfo : specificFanTypeList) {
                final SpecificFanTypeEnum fanType = fanInfo.getFanType();
                if (fanType.getActionType().equals(operationType)) {
                    FanInfo<SpecificFanTypeEnum> specificFanInfo = new FanInfo<>(fanType, gangFan, null, null);
                    fanInfoList.add(specificFanInfo);
                    break;
                }
            }
            settlementInfo.setFanInfoList(fanInfoList);

            seatSettlementInfoMap.put(currentSeat.getPosId(), settlementInfo);

            SettlementInfoDTO settlementInfoDTO = new SettlementInfoDTO();
            settlementInfoDTO.setPosId(xueZhanPosId)
                    .setBeforeScore(beforeScore)
                    .setChangeScore(-changeScore)
                    .setRemaningScore(remainingScore);
            settlementInfoDTOS.add(settlementInfoDTO);
        }

        ArrayList<Integer> standCardList = new ArrayList<>(operationSeat.getStandCardList());
        Player player = operationSeat.getPlayer();
        long beforeScore = player.getScore();
        player.scoreSettle(sumScore);
        long remainingScore = player.getScore();

        List<StepAction> fuLu = operationSeat.getFuLu();
        List<FanInfo> fanInfos = new ArrayList<>();

        final List<FanInfo<SpecificFanTypeEnum>> specificFanTypeList = mahjongRule.getSpecificFanTypeList();
        for (FanInfo<SpecificFanTypeEnum> fanInfo : specificFanTypeList) {
            final Integer actionType = fanInfo.getFanType().getActionType();
            if (operationType.equals(actionType)) {
                fanInfos.add(fanInfo);
            }
        }

        SettlementInfo settlementInfo = new SettlementInfo();
        settlementInfo.setPosId(operationSeat.getPosId())
                .setBeforeScore(beforeScore)
                .setChangeScore(sumScore)
                .setRemaningScore(remainingScore)
                .setFanNum(gangFan)
                .setStandCards(standCardList)
                .setStandCardsConvertList(MahjongProp.cardConvertName(standCardList))
                .setFuluList(new ArrayList<>(fuLu))
                .setFanInfoList(fanInfos);

        seatSettlementInfoMap.put(operationSeat.getPosId(), settlementInfo);
        settlementStep.setSeatSettlementInfoMap(seatSettlementInfoMap);
        GameStepModel<SettlementStep> gameStepModel = new GameStepModel<>(gameZone.getZoneId(), operationSeat.getPlayer(), settlementStep);
        historyList.add(gameStepModel);

        SettlementInfoDTO settlementInfoDTO = new SettlementInfoDTO();
        settlementInfoDTO.setPosId(operationSeat.getPosId())
                .setBeforeScore(beforeScore)
                .setChangeScore(sumScore)
                .setRemaningScore(remainingScore);
        settlementInfoDTOS.add(settlementInfoDTO);

        SettlementResponse settlementResponse = new SettlementResponse();
        settlementResponse.setCardSourcePosId(action.getCardSource())
                .setOperationPosId(operationSeat.getPosId())
                .setOperationType(action.getOperationType().value())
                .setTargetCard(action.getTargetCard())
                .setSettlementInfoDTOS(settlementInfoDTOS);
        pushToRoomUser(XueZhanPushCommandCode.SETTLEMENT_NOTICE, settlementResponse);

        noticePlayersWaterFlow(needNoticeScoreWaterFlowSeats);
    }

    private void checkGangFan(Integer operationType) {
        SichuanRoomConfig ruleConfig = mahjongRule.getRuleConfig();
        Integer gangFan = ruleConfig.getGangFan(operationType);
    }

    public void buGang(Integer card, Integer posId) {
        log.info("请求补杠操作： roomId={} zoneId={} 方位=[{},posId={}] type={} card={} posId={}", roomId, gameZone.getZoneId(), getSeatDirection(posId), posId, posId);
        XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
        boolean canCancel = xueZhanSeat.canOperation(XueZhanMahjongOperationEnum.BU_GANG);
        if (!canCancel) {
            log.warn("玩家没有 补杠 的操作权限：roomId={} userId={} 方位=[{},posId={}]", roomId, xueZhanSeat.getUserId(), getSeatDirection(posId), posId);
            throw new BizException(MahjongStatusCodeEnum.NOT_BU_GANG_OPERATION);
        }
        MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();
        //一定得确保执行的时候，玩家还有操作权限
        StepAction action = mahjongSeat.getDesignateOperationCardSource(XueZhanMahjongOperationEnum.BU_GANG.value(), card);
        if (action == null) {
            //既然目标牌需要校验，并且服务器的seat存储了这个操作的具体信息，那么客户端似乎没有必要再传 目标牌 这个属性上来了
            log.error("玩家没有补杠这张牌的权限: roomId={} zoneId={} posId={} card={}", roomId, gameZone.getZoneId(), posId, card);
            throw new BizException(MahjongStatusCodeEnum.BU_GANG_PARAM_ERROR);
        }
        GameStepModel<OperationCardStep> stepModel = mahjongZone.buGang(card, posId);
        historyList.add(stepModel);
        nextProcess(xueZhanSeat, card, action);
    }

    public void anGang(Integer card, Integer posId) {
        log.info("请求暗杠操作： roomId={} zoneId={} 方位=[{},posId={}] type={} card={} posId={}", roomId, gameZone.getZoneId(), getSeatDirection(posId), posId, posId);
        XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
        boolean canCancel = xueZhanSeat.canOperation(XueZhanMahjongOperationEnum.AN_GANG);
        if (!canCancel) {
            log.warn("玩家没有 暗杠 的操作权限：roomId={} userId={} 方位=[{},posId={}]", roomId, xueZhanSeat.getUserId(), getSeatDirection(posId), posId);
            throw new BizException(MahjongStatusCodeEnum.NOT_AN_GANG_OPERATION);
        }
        MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();
        //一定得确保执行的时候，玩家还有操作权限
        StepAction action = mahjongSeat.getDesignateOperationCardSource(XueZhanMahjongOperationEnum.AN_GANG.value(), card);
        if (action == null) {
            //既然目标牌需要校验，并且服务器的seat存储了这个操作的具体信息，那么客户端似乎没有必要再传 目标牌 这个属性上来了
            log.error("玩家没有暗杠这张牌的权限: roomId={} zoneId={} posId={} card={}", roomId, gameZone.getZoneId(), posId, card);
            throw new BizException(MahjongStatusCodeEnum.AN_GANG_PARAM_ERROR);
        }
        GameStepModel<OperationCardStep> stepModel = mahjongZone.anGang(card, posId);
        historyList.add(stepModel);
        nextProcess(xueZhanSeat, card, action);
    }

    @Override
    public void peng(Integer card, Integer posId) {
        log.info("请求碰操作： roomId={} 方位=[{},posId={}] card={} posId={}", roomId, getSeatDirection(posId), posId, posId);
        XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
        boolean canCancel = xueZhanSeat.canOperation(XueZhanMahjongOperationEnum.PENG);
        if (!canCancel) {
            log.warn("玩家没有 碰 的操作权限：roomId={} userId={} 方位=[{},posId={}]", roomId, xueZhanSeat.getUserId(), getSeatDirection(posId), posId);
            throw new BizException(MahjongStatusCodeEnum.NOT_PENG_OPERATION);
        }
        MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();
        //一定得确保执行的时候，玩家还有操作权限
        StepAction action = mahjongSeat.getDesignateOperationCardSource(XueZhanMahjongOperationEnum.PENG.value(), card);
        if (action == null) {
            //既然目标牌需要校验，并且服务器的seat存储了这个操作的具体信息，那么客户端似乎没有必要再传 目标牌 这个属性上来了
            log.error("玩家没有碰这张牌的权限: roomId={} zoneId={} posId={} card={}", roomId, gameZone.getZoneId(), posId, card);
            throw new BizException(MahjongStatusCodeEnum.PENG_PARAM_ERROR);
        }
        GameStepModel<OperationCardStep> stepModel = mahjongZone.peng(card, posId);
        historyList.add(stepModel);
        nextProcess(xueZhanSeat, card, action);
    }

    @Override
    public void exchangeCard(List<Integer> cards, Integer posId) {
        log.info("换三张操作： roomId={} zoneId={} 方位=[{},posId={}] 拿出去的牌：{}", roomId, gameZone.getZoneId(), getSeatDirection(posId), posId, cards);
        XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
        if (!mahjongRule.getRuleConfig().getCanHsz()) {
            log.error("当前游戏不支持换三张： roomId={} zoneId={} userId={} 方位=[{},posId={}]", roomId, gameZone.getZoneId(), xueZhanSeat.getUserId(), getSeatDirection(posId), posId);
            return;
        }
        if (!mahjongZone.checkCurrentGameStatus(SichuanGameStatusEnum.EXCHANGE_CARD)) {
            log.error("当前游戏阶段不是换三张：roomId={} zoneId={} userId={} 方位=[{},posId={}]", roomId, gameZone.getZoneId(), xueZhanSeat.getUserId(), getSeatDirection(posId), posId);
            return;
        }

        MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();
        if (!mahjongSeat.canOperation(XueZhanMahjongOperationEnum.EXCHANGE_CARD)) {
            log.warn("该玩家没有进行换牌的权限： roomId={} zoneId={} 方位=[{},posId={}] 要交换的牌：{}", roomId, gameZone.getZoneId(), getSeatDirection(posId), posId, cards);
            return;
        }
        boolean isFinishExchange = sichuanMahjongZone.exchangeCard(cards, xueZhanSeat.getSichuanMahjongSeat(), historyList);
        mahjongSeat.removeStatus(SeatStatusEnum.EXCHANGE_CARD.status());
        mahjongSeat.clearOperation();
        noticePlayersWhoFinishChangeCard(xueZhanSeat);
        if (isFinishExchange) {
            log.info("开始换牌 roomId={} zoneId={}", roomId, gameZone.getZoneId());
            List<Step> exchangeHistory = getHistoryByGameStatus(SichuanGameStatusEnum.EXCHANGE_CARD);
            sichuanMahjongZone.executeExchange(exchangeHistory);

            noticePlayersExchangeResult();
            if (mahjongRule.getRuleConfig().getCanDingQue()) {
                //H2 似乎存在一个顺序问题,refreshTimeout（）应该在noticePlayerDingQue()前面.因为没有刷新时间，就为玩家赋予权限。在定时任务线程会有个时间差
                noticePlayersDingQue();

                final SichuanRoomConfig roomConfig = mahjongRule.getRuleConfig();
                final int timeout= roomConfig.getTimeoutTimeByGameStatus(SichuanGameStatusEnum.DING_QUE);
                final long currentTimeMillis = System.currentTimeMillis();
                //实际上有定缺不一定有换三张，有换三张不一定有定缺
                final int animationTime = roomConfig.getFinishExchangeCardAnimationTime();
                refreshTimeout(currentTimeMillis + animationTime + timeout);
            }else{
                noticePlayersOperation(null);

                final SichuanRoomConfig roomConfig = mahjongRule.getRuleConfig();
                final int timeout= roomConfig.getTimeoutTimeByGameStatus(SichuanGameStatusEnum.OPERATION_CARD);
                final long currentTimeMillis = System.currentTimeMillis();
                refreshTimeout(currentTimeMillis + timeout);
            }
        }
    }

    @Override
    public void dingQue(Integer color, Integer posId) {
        log.info("玩家定缺： color={}", color);
        XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
        if (!mahjongRule.getRuleConfig().getCanDingQue()) {
            log.error("当前游戏不支持定缺: roomId={} zoneId={} posId={}", roomId, gameZone.getZoneId(), posId);
            return;
        }
        if (!mahjongZone.checkCurrentGameStatus(SichuanGameStatusEnum.DING_QUE)) {
            log.error("当前游戏阶段不是定缺：roomId={} zoneId={} userId={} 方位=[{},posId={}]", roomId, gameZone.getZoneId(), xueZhanSeat.getUserId(), getSeatDirection(posId), posId);
            return;
        }

        MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();
        if (!mahjongSeat.canOperation(XueZhanMahjongOperationEnum.DING_QUE)) {
            log.warn("该玩家没有进行定缺的权限： roomId={} zoneId={} 方位=[{},posId={}] 定缺的花色：{}", roomId, gameZone.getZoneId(), getSeatDirection(posId), posId, color);
            return;
        }
        final boolean isFinishDingQue = sichuanMahjongZone.dingQue(color, posIdSeatMap.get(posId).getSichuanMahjongSeat(), historyList);
        mahjongSeat.clearOperation();
        mahjongSeat.removeStatus(SeatStatusEnum.DING_QUE.status());
        noticePlayersWhoFinishDingQue(xueZhanSeat);
        if (isFinishDingQue) {
            log.info("定缺流程完成： roomId={} zoneId={}", roomId, gameZone.getZoneId());
            FormalProcessNoticeResponse response = new FormalProcessNoticeResponse(mahjongZone.getBankerPosId(), 10);
            pushToRoomUser(XueZhanPushCommandCode.FORMAL_PROCESS_NOTICE, response);

            //定缺完成后，没有摸牌，庄家直接出牌
            noticePlayersOperation(null);

            refreshTimeout(SichuanGameStatusEnum.OUT_CARD);
        }

    }

    private boolean isLiuJu() {
        if (mahjongZone.cardWallHasCard()) {
            return true;
        }
        /**
         * 特殊的非流局情况（牌摸完了）： 摸最后一张的玩家胡牌了，并且胡牌人数达到了3个
         */
        int huNum = 0;
        for (Map.Entry<Integer, XueZhanSeat> entry : posIdSeatMap.entrySet()) {
            final Integer posId = entry.getKey();
            final XueZhanSeat value = entry.getValue();
            MahjongSeat mahjongSeat = value.getMahjongSeat();
            if (mahjongSeat.existsStatus(SeatStatusEnum.ALREADY_HU)) {
                huNum++;
            }
        }
        if (huNum >= posIdSeatMap.size() - 1) {
            return false;
        }
        return true;
    }

    private void settlement() {
        if (sichuanMahjongZone.getLiuJu()) {
            chaHuazhu();
            chaJiao();
            rebate();
            List<MahjongSeat> allSeat = new ArrayList<>();
            for (Map.Entry<Integer, XueZhanSeat> entry : posIdSeatMap.entrySet()) {
                final XueZhanSeat value = entry.getValue();
                allSeat.add(value.getMahjongSeat());
            }
            noticePlayersWaterFlow(allSeat);
        }
        /**
         * 分别记录 立牌信息、胡牌信息、副露信息、单次结算详情
         */
        SettlementDetailStep settlementDetailStep = new SettlementDetailStep();
        settlementDetailStep.setStepCount(mahjongZone.getStepCount());
        Map<Integer, SettlementDetailInfo> map = new HashMap<>();
        settlementDetailStep.setMap(map);

        List<BaseSettlementDetailDTO> baseSettlementDetailDTOList = new ArrayList<>();
        for (Map.Entry<Integer, XueZhanSeat> entry : posIdSeatMap.entrySet()) {
            final Integer posId = entry.getKey();
            final XueZhanSeat value = entry.getValue();
            final SichuanMahjongSeat sichuanMahjongSeat = value.getSichuanMahjongSeat();
            final MahjongSeat mahjongSeat = value.getMahjongSeat();
            final Player player = mahjongSeat.getPlayer();
            final PlayerHand playerHand = mahjongSeat.getPlayerHand();

            final SettlementDetailInfo settlementDetailInfo = new SettlementDetailInfo();


            BaseSettlementDetailDTO baseSettlementDetailDTO = new BaseSettlementDetailDTO();
            if (playerHand.isTing()) {
                baseSettlementDetailDTO.setStandCardList(mahjongSeat.getStandCardList());
            }
            List<ActionDTO> actionDTOList = new ArrayList<>();
            baseSettlementDetailDTO.setPosId(posId)
                    .setActionDTOList(actionDTOList)
                    .setHuCard(sichuanMahjongSeat.getHuCard())
                    .setSettlementScore(player.getScore() - mahjongSeat.getCarryScore());
            baseSettlementDetailDTOList.add(baseSettlementDetailDTO);

            final List<StepAction> fuLu = mahjongSeat.getFuLu();
            settlementDetailInfo.setPosId(posId)
                    .setActionList(fuLu)
                    .setHuCard(sichuanMahjongSeat.getHuCard())
                    .setCarryScore(mahjongSeat.getCarryScore())
                    .setChangeScore(player.getScore() - mahjongSeat.getCarryScore())
                    .setRemainingScore(player.getScore());

            for (StepAction stepAction : fuLu) {
                ActionDTO actionDTO = new ActionDTO();
                actionDTO.setTargetCard(stepAction.getTargetCard())
                        .setCardSourcePosId(stepAction.getCardSource())
                        .setOperationType(stepAction.getOperationType().value());
                actionDTOList.add(actionDTO);
            }

            //List<SettlementDetailInfoDTO> settlementDetailInfoDTOS = new ArrayList<>();
            for (GameStepModel gameStepModel : historyList) {
                final Step operationStep = gameStepModel.getOperationStep();


                if (SichuanGameStatusEnum.SETTLEMENT.status() == operationStep.gameStatus().status()) {
                    SettlementStep settlementStep = (SettlementStep) operationStep;
                    final StepAction action = settlementStep.getAction();


                    final SettlementInfo settlementInfo = settlementStep.getSettlementInfoByPosId(posId);
                    if (settlementInfo == null) {
                        continue;
                    }
                    Integer targetPosId;
                    if (settlementStep.posId() == mahjongSeat.getPosId()) {
                        targetPosId = action.getCardSource();
                    } else {
                        targetPosId = settlementStep.getPosId();
                    }

                    SingleSettlementInfo singleSettlementInfo = new SingleSettlementInfo();
                    List<FanInfo> fanInfoList = new ArrayList<>();
                    singleSettlementInfo.setFanInfoList(fanInfoList)
                            .setFanScore(settlementInfo.getChangeScore())
                            .setSumFanNum(settlementInfo.getFanNum())
                            .setTargetPosId(targetPosId);
                    settlementDetailInfo.addSingleSttlementInfo(singleSettlementInfo);
                    for (FanInfo fanInfo : settlementInfo.getFanInfoList()) {

                        fanInfoList.add(fanInfo);

                    }

                    if (settlementStep.actionType().equals(XueZhanMahjongOperationEnum.HU.value()) || settlementStep.actionType().equals(XueZhanMahjongOperationEnum.YI_PAO_DUO_XIANG.value())) {
                        /**
                         * 大牌展示
                         */
                        if (settlementInfo.getFanNum() >= 16 && settlementInfo.getChangeScore() > 0) {
                            settlementDetailInfo.setBigFan(settlementInfo.getFanInfoList())
                                    .setBigFanNum(settlementInfo.getFanNum())
                                    .setBigFanScore(settlementInfo.getChangeScore());

                        }
                    }

                }
                if (XueZhanMahjongOperationEnum.CHA_HUA_ZHU.value().equals(operationStep.actionType())) {
                    ChaHuaZhuStep chaHuaZhuStep = (ChaHuaZhuStep) operationStep;
                    final List<ChaHuaZhuInfo> chaHuaZhuInfoList = chaHuaZhuStep.getChaHuaZhuInfoListByPosId(posId);
                    if (chaHuaZhuInfoList == null) {
                        continue;
                    }
                    for (ChaHuaZhuInfo chaHuaZhuInfo : chaHuaZhuInfoList) {
                        SingleSettlementInfo singleSettlementInfo = new SingleSettlementInfo();
                        FanInfo<SpecificFanTypeEnum> fanInfo = new FanInfo<>(SpecificFanTypeEnum.查花猪, chaHuaZhuInfo.getFanNum(), null, null);
                        List<FanInfo> fanInfoList = new ArrayList<>();
                        fanInfoList.add(fanInfo);
                        singleSettlementInfo.setTargetPosId(chaHuaZhuInfo.getCompensationToPosId())
                                .setFanInfoList(fanInfoList)
                                .setFanScore(chaHuaZhuInfo.getChangeScore())
                                .setSumFanNum(chaHuaZhuInfo.getFanNum());
                        settlementDetailInfo.addSingleSttlementInfo(singleSettlementInfo);
                    }
                }
                if (XueZhanMahjongOperationEnum.CHA_JIAO.value().equals(operationStep.actionType())) {
                    ChaJiaoStep chaJiaoStep = (ChaJiaoStep) operationStep;
                    final List<ChaJiaoInfo> chaJiaoInfoList = chaJiaoStep.getChaJiaoInfoListByPosId(posId);
                    if (chaJiaoInfoList == null) {
                        continue;
                    }
                    for (ChaJiaoInfo chaJiaoInfo : chaJiaoInfoList) {
                        SingleSettlementInfo singleSettlementInfo = new SingleSettlementInfo();
                        FanInfo<SpecificFanTypeEnum> fanInfo = new FanInfo<>(SpecificFanTypeEnum.查叫, chaJiaoInfo.getFanNum(), null, null);
                        List<FanInfo> fanInfoList = new ArrayList<>();
                        fanInfoList.add(fanInfo);
                        singleSettlementInfo.setTargetPosId(chaJiaoInfo.getCompensationToPosId())
                                .setFanInfoList(fanInfoList)
                                .setFanScore(chaJiaoInfo.getChangeScore())
                                .setSumFanNum(chaJiaoInfo.getFanNum());
                        settlementDetailInfo.addSingleSttlementInfo(singleSettlementInfo);
                    }
                }
                if (XueZhanMahjongOperationEnum.REBATE.value().equals(operationStep.actionType())) {
                    RebateStep rebateStep = (RebateStep) operationStep;

                    final List<RebateInfo> rebateInfoList = rebateStep.getRebateInfoListByPosId(posId);
                    if (rebateInfoList == null) {
                        continue;
                    }
                    for (RebateInfo rebateInfo : rebateInfoList) {
                        SingleSettlementInfo singleSettlementInfo = new SingleSettlementInfo();
                        FanInfo<SpecificFanTypeEnum> fanInfo = new FanInfo<>(SpecificFanTypeEnum.退税, rebateInfo.getFanNum(), null, null);
                        List<FanInfo> fanInfoList = new ArrayList<>();
                        fanInfoList.add(fanInfo);
                        singleSettlementInfo.setTargetPosId(rebateInfo.getCompensationToPosId())
                                .setFanInfoList(fanInfoList)
                                .setFanScore(rebateInfo.getChangeScore())
                                .setSumFanNum(rebateInfo.getFanNum());
                        settlementDetailInfo.addSingleSttlementInfo(singleSettlementInfo);
                    }
                }

            }
            map.put(posId, settlementDetailInfo);


        }
        for (Map.Entry<Integer, SettlementDetailInfo> entryInfo : map.entrySet()) {
            SettlementDetailResponse settlementDetailResponse = new SettlementDetailResponse();
            final Integer posId = entryInfo.getKey();
            final XueZhanSeat seat = posIdSeatMap.get(posId);
            final MahjongSeat mahjongSeat = seat.getMahjongSeat();
            final SettlementDetailInfo settlementDetailInfo = entryInfo.getValue();

            final List<FanInfo> bigFan = settlementDetailInfo.getBigFan();
            if (bigFan != null) {
                final List<Integer> bigFanIds = bigFan.stream().map(fanInfo -> fanInfo.getFanType().getId()).collect(Collectors.toList());
                settlementDetailResponse.setBigFanIds(bigFanIds)
                        .setBigFanNum(settlementDetailInfo.getBigFanNum())
                        .setBigFanScore(settlementDetailInfo.getBigFanScore());
            }
            final List<SettlementDetailInfoDTO> singleSettlementInfoDTOs = new ArrayList<>();
            settlementDetailResponse.setPosId(posId)
                    .setBaseSettlementDetailList(baseSettlementDetailDTOList)
                    .setDetailList(singleSettlementInfoDTOs);


            final List<SingleSettlementInfo> singleSettlementInfos = settlementDetailInfo.getSingleSettlementInfos();
            if (singleSettlementInfos != null) {
                singleSettlementInfos.stream().forEach(singleSettlementInfo -> {
                    final List<FanInfo> fanInfoList = singleSettlementInfo.getFanInfoList();

                    SettlementDetailInfoDTO settlementDetailInfoDTO = new SettlementDetailInfoDTO();
                    settlementDetailInfoDTO.setFanIdList(fanInfoList == null ? null : fanInfoList.stream().map(fanInfo -> fanInfo.getFanType().getId()).collect(Collectors.toList()))
                            .setFanNum(singleSettlementInfo.getSumFanNum())
                            .setFanScore(singleSettlementInfo.getFanScore())
                            .setTargetPosId(singleSettlementInfo.getTargetPosId());
                    singleSettlementInfoDTOs.add(settlementDetailInfoDTO);
                });
            }

            roomManager.pushToUser(XueZhanPushCommandCode.SETTLEMENT_DETAIL, mahjongSeat.getUserId(), settlementDetailResponse, roomId);
        }
        GameStepModel<SettlementDetailStep> gameStepModel = new GameStepModel<>(gameZone.getZoneId(), null, settlementDetailStep);
        historyList.add(gameStepModel);

        log.info("当局结算已完成： history = {}", JSONObject.toJSONString(historyList, SerializerFeature.DisableCircularReferenceDetect));
    }

    private void rebate() {
        log.info("退税结算： roomId={} zoneId={}", roomId, gameZone.getZoneId());
        List<SettlementStep> settlementStepList = new ArrayList<>();
        for (GameStepModel gameStepModel : historyList) {
            Step operationStep = gameStepModel.getOperationStep();
            if (operationStep.gameStatus().status() != SichuanGameStatusEnum.SETTLEMENT.status()) {
                continue;
            }
            Integer actionType = operationStep.actionType();
            if (XueZhanMahjongOperationEnum.ZHI_GANG.value().equals(actionType) || XueZhanMahjongOperationEnum.BU_GANG.value().equals(actionType) || XueZhanMahjongOperationEnum.AN_GANG.value().equals(actionType)) {
                settlementStepList.add((SettlementStep) operationStep);
            }
        }
        GameStepModel<RebateStep> rebateStepModel = sichuanMahjongZone.rebate(settlementStepList, XueZhanMahjongOperationEnum.REBATE);
        historyList.add(rebateStepModel);

        RebateStep operationStep = rebateStepModel.getOperationStep();

        List<RebateDTO> list = new ArrayList<>();
        RebateSettlementResponse rebateSettlementResponse = new RebateSettlementResponse(list);

        Map<Integer, List<RebateInfo>> seatRebateMap = operationStep.getSeatRebateMap();
        for (Map.Entry<Integer, List<RebateInfo>> entry : seatRebateMap.entrySet()) {
            Integer posId = entry.getKey();
            List<RebateInfo> rebateInfoList = entry.getValue();
            RebateDTO rebateDTO = new RebateDTO();
            long changeScore = 0;
            for (RebateInfo rebateInfo : rebateInfoList) {
                //因为退税就是 之前杠 赢的分，现在要变成 输的分
                changeScore += rebateInfo.getChangeScore();
            }
            XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
            MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();
            Player player = mahjongSeat.getPlayer();

            final long remainingScore = player.getScore();
            rebateDTO.setBeforeScore(remainingScore - changeScore)
                    .setChangeScore(changeScore)
                    .setRemainingScore(remainingScore)
                    .setPosId(posId);
            list.add(rebateDTO);
        }
        pushToRoomUser(XueZhanPushCommandCode.REBATE_SETTLEMENT, rebateSettlementResponse);
    }

    private void chaJiao() {
        log.info("查叫结算： roomId={} zoneId={}", roomId, gameZone.getZoneId());
        GameStepModel<ChaJiaoStep> chaJiaoStepGameStepModel = sichuanMahjongZone.chaJiao(mahjongRule, XueZhanMahjongOperationEnum.CHA_JIAO);
        historyList.add(chaJiaoStepGameStepModel);
        ChaJiaoStep operationStep = chaJiaoStepGameStepModel.getOperationStep();

        Map<Integer, List<ChaJiaoInfo>> chaJiaoInfoMap = operationStep.getChaJiaoInfoMap();

        List<ChaJiaoDTO> chaJiaoDTOList = new ArrayList<>();
        for (Map.Entry<Integer, List<ChaJiaoInfo>> entry : chaJiaoInfoMap.entrySet()) {
            final Integer posId = entry.getKey();
            XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
            Player player = xueZhanSeat.getPlayer();
            final List<ChaJiaoInfo> currentChajiaoInfos = entry.getValue();

            long changeScore = 0;
            for (ChaJiaoInfo chaJiaoInfo : currentChajiaoInfos) {
                changeScore += chaJiaoInfo.getChangeScore();
            }
            long remainingScore = player.getScore();
            ChaJiaoDTO chaJiaoDTO = new ChaJiaoDTO();
            chaJiaoDTO.setBeforeScore(remainingScore - changeScore)
                    .setChangeScore(changeScore)
                    .setRemainingScore(remainingScore)
                    .setPosId(posId);
            chaJiaoDTOList.add(chaJiaoDTO);

        }
        ChaJiaoSettlementResponse response = new ChaJiaoSettlementResponse(chaJiaoDTOList);
        pushToRoomUser(XueZhanPushCommandCode.CHA_JIAO, response);

    }

    private void chaHuazhu() {
        log.info("查花猪结算： roomId={} zoneId={}", roomId, gameZone.getZoneId());
        final GameStepModel<ChaHuaZhuStep> gameStepModel = sichuanMahjongZone.chaHuazhu(mahjongRule, XueZhanMahjongOperationEnum.CHA_HUA_ZHU);
        historyList.add(gameStepModel);

        ChaHuaZhuStep operationStep = gameStepModel.getOperationStep();

        Map<Integer, List<ChaHuaZhuInfo>> chaHuaZhuInfoMap = operationStep.getChaHuaZhuInfoMap();

        List<ChaHuaZhuDTO> chaHuaZhuDTOList = new ArrayList<>();
        for (Map.Entry<Integer, List<ChaHuaZhuInfo>> entry : chaHuaZhuInfoMap.entrySet()) {
            final Integer posId = entry.getKey();
            XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
            Player player = xueZhanSeat.getPlayer();
            final List<ChaHuaZhuInfo> currentChajiaoInfos = entry.getValue();

            long changeScore = 0;
            for (ChaHuaZhuInfo chaHuaZhuInfo : currentChajiaoInfos) {
                changeScore += chaHuaZhuInfo.getChangeScore();
            }
            long remainingScore = player.getScore();
            ChaHuaZhuDTO chaHuaZhuDTO = new ChaHuaZhuDTO();
            chaHuaZhuDTO.setBeforeScore(remainingScore - changeScore)
                    .setChangeScore(changeScore)
                    .setRemainingScore(remainingScore)
                    .setPosId(posId);
            chaHuaZhuDTOList.add(chaHuaZhuDTO);

        }
        ChaHuaZhuSettlementResponse response = new ChaHuaZhuSettlementResponse(chaHuaZhuDTOList);
        pushToRoomUser(XueZhanPushCommandCode.CHA_HUA_ZHU, response);
    }


    /**
     * 复原未执行的操作（最高优先级的操作）
     *
     * @return
     */
    private boolean restoreAction() {
        List<TempAction> tempActions = mahjongZone.getTempActions();
        if (tempActions.size() > 0) {
            log.info("需要进行还原操作 roomId={} zoneId={} tempActions={}", roomId, gameZone.getZoneId(), tempActions);
            /**
             * 已经有玩家在当前回合操作过
             * 找出临时操作区里，操作级别最高的操作（以目前对麻将的认知而言，一个回合的所有操作中，只有胡牌这个操作等级会可能有多次，碰和杠无论如何只有一次，并且优先级又高于吃牌）
             */
            List<TempAction> needExecuteActionList = new ArrayList<>();
            TempAction needExecuteAction = tempActions.get(0);
            needExecuteActionList.add(needExecuteAction);
            int i = 0;
            for (TempAction tempAction : tempActions) {
                if (i == 0) {
                    i++;
                    continue;
                }
                if (needExecuteAction.getPriority() == tempAction.getPriority()) {
                    needExecuteActionList.add(tempAction);
                }
            }
            mahjongZone.cleanTempAction();

            //H2 不能直接还原，要判断是否有多人胡。如果是多人胡，要把操作类型改为一炮多响
            if (needExecuteActionList.size() > 1) {
                List<Integer> huPosIdList = new ArrayList<>();
                Integer targetCard = null;
                Integer cardSourcePosId = null;
                for (TempAction tempAction : needExecuteActionList) {
                    huPosIdList.add(tempAction.getPosId());
                    final StepAction stepAction = tempAction.getStepAction();
                    targetCard = stepAction.getTargetCard();
                    cardSourcePosId = stepAction.getCardSource();
                }
                multiplePlayerHu(targetCard, cardSourcePosId, huPosIdList);
                return true;
            }

            for (TempAction tempAction : needExecuteActionList) {
                StepAction stepAction = tempAction.getStepAction();
                Integer typeValue = stepAction.getOperationType().value();
                Integer targetCard = stepAction.getTargetCard();
                Long operatorUserId = tempAction.getUserId();

                /**
                 * 得把权限重新赋予给玩家，否则第二次执行就没有权限了，这个时候就体现出 使用TempAction作为权限对象的好处了。
                 *
                 */
                XueZhanSeat xueZhanSeat = posIdSeatMap.get(tempAction.getPosId());
                MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();
                mahjongSeat.addOperation(stepAction);
                if (stepAction.getOperationType().value().equals(XueZhanMahjongOperationEnum.HU.value())) {     //有多个玩家可以胡，但是最终只有一个玩家点了胡
                    hu(targetCard, tempAction.getPosId(), true);

                } else {
                    operation(targetCard, typeValue, operatorUserId, true);
                }

            }
            return true;
        }
        return false;

    }

    public List<Step> getHistoryByGameStatus(SichuanGameStatusEnum statusEnum) {
        List<Step> list = new ArrayList<>();
        for (GameStepModel gameStepModel : historyList) {
            Step operationStep = gameStepModel.getOperationStep();
            if (operationStep.gameStatus().equals(statusEnum)) {
                list.add(operationStep);
            }
        }
        return list;
    }

    private void noticePlayersDealCardsResult() {
        for (GameStepModel gameStepModel : historyList) {
            GameStartStep gameStartStep = (GameStartStep) gameStepModel.getOperationStep();
            int posId = gameStartStep.getPosId();
            XueZhanSeat seat = posIdSeatMap.get(posId);

            XueZhanStartResponse response = new XueZhanStartResponse();
            if (mahjongRule.getRuleConfig().getCanHsz()) {
                mahjongZone.setGameStatus(SichuanGameStatusEnum.EXCHANGE_CARD);
                List<Integer> recommendedCards = SichuanPlayHelper.recommendedChangeCard(gameStartStep.getStandCards(), 3);
                recommendedCards = recommendedCards.subList(0, 3);
                SichuanGameStartStep sichuanGameStartStep = new SichuanGameStartStep();
                sichuanGameStartStep.setGameStartStep(gameStartStep);
                sichuanGameStartStep.setRecommendedCardList(recommendedCards);
                response.setRecommendExchangeList(recommendedCards);

                //替换step为四川麻将类型
                gameStepModel.setOperationStep(sichuanGameStartStep);
                final SichuanMahjongSeat sichuanMahjongSeat = seat.getSichuanMahjongSeat();
                sichuanMahjongSeat.setRecommendExchangeCards(recommendedCards);

                MahjongSeat mahjongSeat = seat.getMahjongSeat();
                mahjongSeat.addOperation(XueZhanMahjongOperationEnum.EXCHANGE_CARD);
                mahjongSeat.addChangce();
                mahjongSeat.addStatus(SeatStatusEnum.EXCHANGE_CARD);

                final SichuanRoomConfig roomConfig = mahjongRule.getRuleConfig();
                final int timeout= roomConfig.getTimeoutTimeByGameStatus(SichuanGameStatusEnum.EXCHANGE_CARD);
                final long currentTimeMillis = System.currentTimeMillis();
                final int gameStartAnimationTime = roomConfig.getGameStartAnimationTime();
                mahjongSeat.setTimeoutTime(currentTimeMillis+timeout+gameStartAnimationTime);

            }else if(mahjongRule.getRuleConfig().getCanDingQue()){
                mahjongZone.setGameStatus(SichuanGameStatusEnum.DING_QUE);
                //....设置定缺推荐....
                MahjongSeat mahjongSeat = seat.getMahjongSeat();
                mahjongSeat.addOperation(XueZhanMahjongOperationEnum.DING_QUE);
                mahjongSeat.addChangce();
                mahjongSeat.addStatus(SeatStatusEnum.DING_QUE);

                noticePlayersDingQue();

                final SichuanRoomConfig roomConfig = mahjongRule.getRuleConfig();
                final int timeout= roomConfig.getTimeoutTimeByGameStatus(SichuanGameStatusEnum.DING_QUE);
                final long currentTimeMillis = System.currentTimeMillis();
                final int gameStartAnimationTime = roomConfig.getGameStartAnimationTime();
                mahjongSeat.setTimeoutTime(currentTimeMillis+timeout+gameStartAnimationTime);
            }else{
                mahjongZone.setGameStatus(SichuanGameStatusEnum.OPERATION_CARD);
                MahjongSeat mahjongSeat = seat.getMahjongSeat();
                Collections.sort(mahjongSeat.getStandCardList());
                mahjongSeat.solution();
                mahjongZone.initCurrentOperator();
                final Integer bankerPosId = mahjongZone.getBankerPosId();
                if(mahjongSeat.getPosId() == bankerPosId){
                    mahjongSeat.addOperation(XueZhanMahjongOperationEnum.OUT_CARD);
                    mahjongSeat.addChangce();
                }

                final SichuanRoomConfig roomConfig = mahjongRule.getRuleConfig();
                final int timeout= roomConfig.getTimeoutTimeByGameStatus(SichuanGameStatusEnum.OUT_CARD);
                final long currentTimeMillis = System.currentTimeMillis();
                final int gameStartAnimationTime = roomConfig.getGameStartAnimationTime();
                mahjongSeat.setTimeoutTime(currentTimeMillis+timeout+gameStartAnimationTime);
            }
            final SichuanRoomConfig roomConfig = mahjongRule.getRuleConfig();
            response.setRoomId(gameStartStep.getRoomId())
                    .setZoneId(gameStartStep.getZoneId())
                    .setStep(gameStartStep.getStep())
                    .setBankerPosId(gameStartStep.getBankerPosId())
                    .setDiceList(gameStartStep.getDiceList())
                    .setPosId(gameStartStep.getPosId())
                    .setStandCardList(gameStartStep.getStandCards())
                    .setCardWallRemaining(mahjongZone.getCardWall().size())
                    .setOutCardTime(roomConfig.getTimeoutTimeByGameStatus(SichuanGameStatusEnum.OUT_CARD))
                    .setOtherOperationCardTime(roomConfig.getTimeoutTimeByGameStatus(SichuanGameStatusEnum.OPERATION_CARD))
                    //发给客户端的游戏状态不应该是记录step时的状态，而是下一个阶段。斗地主那里可能没处理好
                    .setGameStatus(mahjongZone.getGameStatus().status());

            roomManager.pushToUser(PushCommandCode.GAME_START, gameStepModel.getPlayers().getUserId(), response, roomId);
        }
    }

    private void noticePlayersExchangeResult() {
        List<Step> historyByGameStatus = getHistoryByGameStatus(SichuanGameStatusEnum.EXCHANGE_CARD);
        for (Step stepInfo : historyByGameStatus) {
            ExchangeCardStep step = (ExchangeCardStep) stepInfo;
            ExchangeCardResultResponse response = new ExchangeCardResultResponse(step.getExchangeType().type(), step.getGainedCards());
            roomManager.pushToUser(XueZhanPushCommandCode.EXCHANGE_RESULT, posIdSeatMap.get(step.getPosId()).getUserId(), response, roomId);
        }
    }

    private void noticePlayersWhoFinishChangeCard(XueZhanSeat seat) {

        OperationResultResponse response = new OperationResultResponse();
        response.setPosId(seat.getPosId())
                .setOperationType(XueZhanMahjongOperationEnum.EXCHANGE_CARD.value());
        pushToRoomUser(XueZhanPushCommandCode.OPERATION_RESULT_NOTICE, response);
    }

    private void noticePlayersDingQue() {
        for (XueZhanSeat seat : posIdSeatMap.values()) {
            MahjongSeat mahjongSeat = seat.getMahjongSeat();
            mahjongSeat.addOperation(XueZhanMahjongOperationEnum.DING_QUE);
            mahjongSeat.addChangce();
            mahjongSeat.addStatus(SeatStatusEnum.DING_QUE);

            Integer color = SichuanPlayHelper.recommendedDingQue(mahjongSeat.getStandCardList());
            SichuanMahjongSeat sichuanMahjongSeat = seat.getSichuanMahjongSeat();
            sichuanMahjongSeat.setRemommendQueColor(color);

            RecommendDingQueResponse response = new RecommendDingQueResponse(color);
            roomManager.pushToUser(XueZhanPushCommandCode.RECOMMEND_DING_QUE, mahjongSeat.getUserId(), response, roomId);
        }
    }

    private void noticePlayersWhoFinishDingQue(XueZhanSeat seat) {
        OperationResultResponse response = new OperationResultResponse();
        response.setPosId(seat.getPosId())
                .setTargetCard(seat.getSichuanMahjongSeat().getQueColor())
                .setOperationType(XueZhanMahjongOperationEnum.DING_QUE.value());
        pushToRoomUser(XueZhanPushCommandCode.OPERATION_RESULT_NOTICE, response);
    }

    /**
     * @param card 摸的牌。 庄家第一次出牌不用摸牌，所以为null
     */
    private void noticePlayersOperation(Integer card) {
        XueZhanSeat seat = posIdSeatMap.get(mahjongZone.getCurTookCardPlayerPosId());
        MahjongSeat mahjongSeat = seat.getMahjongSeat();
        mahjongSeat.addStatus(SeatStatusEnum.OPERATION_CARD);

        List<StepAction> stepActions = sichuanMahjongZone.whatCanYouDo(card, mahjongRule.getRuleConfig());
        List<OperationDTO> canOperationDTOs = new ArrayList<>();
        OperationDTO outCardOperation = new OperationDTO(XueZhanMahjongOperationEnum.OUT_CARD.value(), null);
        canOperationDTOs.add(outCardOperation);
        mahjongSeat.addOperation(XueZhanMahjongOperationEnum.OUT_CARD);

        if (stepActions.size() > 0) {
            stepActions.stream().forEach(stepAction -> {
                OperationDTO operationDTO = new OperationDTO(stepAction.getOperationType().value(), stepAction.getTargetCard());
                canOperationDTOs.add(operationDTO);
                mahjongSeat.addOperation(stepAction);
            });
            //摸牌的时候 有杠、胡 权限 是没有过这个选项的
            /*StepAction cancel = new StepAction();
            cancel.setTargetCard(card)
                    .setCardSource(seat.getPosId())
                    .setOperationType(XueZhanMahjongOperationEnum.CANCEL);
            mahjongSeat.addOperation(cancel);*/
        }
        final int timeoutTime = getTimeoutTime(mahjongSeat,true);
        OperationNoticeResponse response = new OperationNoticeResponse(seat.getPosId(), canOperationDTOs,timeoutTime);

        roomManager.pushToUser(XueZhanPushCommandCode.OPERATION_NOTICE, seat.getUserId(), response, roomId);
    }

    /**
     * 通知分数发生变动的玩家的积分流水变动
     *
     * @param needNoticeSeats
     */
    private void noticePlayersWaterFlow(List<MahjongSeat> needNoticeSeats) {
        for (MahjongSeat mahjongSeat : needNoticeSeats) {
            final Player player = mahjongSeat.getPlayer();
            int changeScore = (int) (player.getScore() - mahjongSeat.getCarryScore());
            final List<SettlementDetailInfoDTO> waterFlowList = getScoreWaterFlow(mahjongSeat.getPosId());
            WaterFlowResponse response = new WaterFlowResponse(changeScore, waterFlowList);
            roomManager.pushToUser(XueZhanPushCommandCode.SCORE_WATER_FLOW, player.getUserId(), response, roomId);
        }

    }

    private List<SettlementDetailInfoDTO> getScoreWaterFlow(Integer posId) {
        final XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
        final MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();

        List<SettlementDetailInfoDTO> settlementDetailInfoDTOS = new ArrayList<>();

        for (GameStepModel gameStepModel : historyList) {
            final Step operationStep = gameStepModel.getOperationStep();

            if (SichuanGameStatusEnum.SETTLEMENT.status() == operationStep.gameStatus().status()) {
                SettlementStep settlementStep = (SettlementStep) operationStep;
                final StepAction action = settlementStep.getAction();

                final SettlementInfo settlementInfo = settlementStep.getSettlementInfoByPosId(posId);
                if (settlementInfo == null) {
                    continue;
                }
                Integer targetPosId;
                if (settlementStep.posId() == mahjongSeat.getPosId()) {

                    targetPosId = action.getCardSource();
                } else {
                    targetPosId = settlementStep.getPosId();
                }

                SettlementDetailInfoDTO settlementDetailInfoDTO = new SettlementDetailInfoDTO();
                List<Integer> fanIdList = new ArrayList<>();
                settlementDetailInfoDTO.setFanIdList(fanIdList)
                        .setFanNum(settlementInfo.getFanNum())
                        .setFanScore(settlementInfo.getChangeScore())
                        .setTargetPosId(targetPosId);
                settlementDetailInfoDTOS.add(settlementDetailInfoDTO);
                for (FanInfo fanInfo : settlementInfo.getFanInfoList()) {
                    fanIdList.add(fanInfo.getFanType().getId());
                }


            }

            if (XueZhanMahjongOperationEnum.CHA_HUA_ZHU.value().equals(operationStep.actionType())) {
                ChaHuaZhuStep chaHuaZhuStep = (ChaHuaZhuStep) operationStep;
                final List<ChaHuaZhuInfo> chaHuaZhuInfoList = chaHuaZhuStep.getChaHuaZhuInfoListByPosId(posId);
                if (chaHuaZhuInfoList == null) {
                    continue;
                }
                for (ChaHuaZhuInfo chaHuaZhuInfo : chaHuaZhuInfoList) {
                    SettlementDetailInfoDTO settlementDetailInfoDTO = new SettlementDetailInfoDTO();
                    List<Integer> fanIdList = new ArrayList<>();
                    fanIdList.add(SpecificFanTypeEnum.查花猪.getId());
                    settlementDetailInfoDTO.setTargetPosId(chaHuaZhuInfo.getCompensationToPosId())
                            .setFanScore(chaHuaZhuInfo.getChangeScore())
                            .setFanNum(chaHuaZhuInfo.getFanNum())
                            .setFanIdList(fanIdList);

                    settlementDetailInfoDTOS.add(settlementDetailInfoDTO);


                }
            }
            if (XueZhanMahjongOperationEnum.CHA_JIAO.value().equals(operationStep.actionType())) {
                ChaJiaoStep chaJiaoStep = (ChaJiaoStep) operationStep;
                final List<ChaJiaoInfo> chaJiaoInfoList = chaJiaoStep.getChaJiaoInfoListByPosId(posId);
                if (chaJiaoInfoList == null) {
                    continue;
                }
                for (ChaJiaoInfo chaJiaoInfo : chaJiaoInfoList) {
                    SettlementDetailInfoDTO settlementDetailInfoDTO = new SettlementDetailInfoDTO();
                    List<Integer> fanIdList = new ArrayList<>();
                    fanIdList.add(SpecificFanTypeEnum.查叫.getId());
                    settlementDetailInfoDTO.setTargetPosId(chaJiaoInfo.getCompensationToPosId())
                            .setFanScore(chaJiaoInfo.getChangeScore())
                            .setFanNum(chaJiaoInfo.getFanNum())
                            .setFanIdList(fanIdList);

                    settlementDetailInfoDTOS.add(settlementDetailInfoDTO);

                }
            }
            if (XueZhanMahjongOperationEnum.REBATE.value().equals(operationStep.actionType())) {
                RebateStep rebateStep = (RebateStep) operationStep;

                final List<RebateInfo> rebateInfoList = rebateStep.getRebateInfoListByPosId(posId);
                if (rebateInfoList == null) {
                    continue;
                }
                for (RebateInfo rebateInfo : rebateInfoList) {
                    SettlementDetailInfoDTO settlementDetailInfoDTO = new SettlementDetailInfoDTO();
                    List<Integer> fanIdList = new ArrayList<>();
                    fanIdList.add(SpecificFanTypeEnum.退税.getId());
                    settlementDetailInfoDTO.setTargetPosId(rebateInfo.getCompensationToPosId())
                            .setFanScore(rebateInfo.getChangeScore())
                            .setFanNum(rebateInfo.getFanNum())
                            .setFanIdList(fanIdList);

                    settlementDetailInfoDTOS.add(settlementDetailInfoDTO);

                }
            }

        }
        return settlementDetailInfoDTOS;
    }

    public void reconnect(Long userId) {
        final Integer posId = userPosIdMap.get(userId);
        log.info("玩家断线重连：roomId={} zoneId={} userId={} 方位=[{},posId={}]", roomId, gameZone.getZoneId(), userId, getSeatDirection(posId), posId);
        List<SeatInfoDTO> seatInfoDTOList = new ArrayList<>();
        GameZoneInfoDTO gameZoneInfoDTO = new GameZoneInfoDTO();
        ReconnectResponse slefReconnectResponse = new ReconnectResponse(posId, seatInfoDTOList, gameZoneInfoDTO);

        final SichuanRoomConfig roomConfig = mahjongRule.getRuleConfig();
        final ExchangeTypeEnum exchangeType = sichuanMahjongZone.getExchangeType();
        long remainingTime = mahjongZone.getTimeoutTime() - System.currentTimeMillis();
        final Integer curOperatorPosId = mahjongZone.getCurOperatorPosId();
        if(curOperatorPosId != null && curOperatorPosId.equals(posId)){
            final XueZhanSeat xueZhanSeat = posIdSeatMap.get(curOperatorPosId);
            final MahjongSeat currentOperationSeat = xueZhanSeat.getMahjongSeat();
            remainingTime = currentOperationSeat.getTimeoutTime() - System.currentTimeMillis();
        }
        gameZoneInfoDTO
                .setRoomId(roomId)
                .setZoneId(gameZone.getZoneId())
                .setDiceList(Arrays.asList(mahjongZone.getDice()))
                .setExchangeType(exchangeType == null ? null : exchangeType.type())
                .setBankerPosId(mahjongZone.getBankerPosId())
                .setCardWallRemainingSize(mahjongZone.getCardWall().size())
                .setCurrentOperatorPosId(curOperatorPosId)
                .setGameStatus(mahjongZone.getGameStatus().status())
                .setCurrentTookCardPosId(mahjongZone.getCurTookCardPlayerPosId())
                .setOperationRemainingTime(remainingTime)
                .setOutCardTime(roomConfig.getTimeoutTimeByGameStatus(SichuanGameStatusEnum.OUT_CARD))
                .setOtherOperationCardTime(roomConfig.getTimeoutTimeByGameStatus(SichuanGameStatusEnum.OPERATION_CARD));

        for (Map.Entry<Integer, XueZhanSeat> entry : posIdSeatMap.entrySet()) {
            final Integer currentPosId = entry.getKey();
            final XueZhanSeat value = entry.getValue();
            final SichuanMahjongSeat sichuanMahjongSeat = value.getSichuanMahjongSeat();
            final MahjongSeat mahjongSeat = value.getMahjongSeat();
            final Player player = mahjongSeat.getPlayer();
            PlayerDTO playerDTO = new PlayerDTO();
            playerDTO.setUserId(player.getUserId())
                    .setName(player.getNickName())
                    .setScore(player.getScore());

            final List<StepAction> fuLu = mahjongSeat.getFuLu();
            List<ActionDTO> responseFulu = new ArrayList<>();
            for (StepAction stepAction : fuLu) {
                ActionDTO actionDTO = new ActionDTO();
                actionDTO.setOperationType(stepAction.getOperationType().value())
                        .setCardSourcePosId(stepAction.getCardSource())
                        .setTargetCard(stepAction.getTargetCard());
                responseFulu.add(actionDTO);
            }

            SeatInfoDTO seatInfoDTO = new SeatInfoDTO();
            final List<Integer> standCardList = mahjongSeat.getStandCardList();
            if (currentPosId.equals(posId)) {
                seatInfoDTO.setStandCardList(new ArrayList<>(standCardList));
            }
            List<OperationDTO> canOperationList = new ArrayList<>();
            mahjongSeat.getCanOperations().stream().forEach(stepAction -> {
                OperationDTO operationDTO = new OperationDTO(stepAction.getOperationType().value(), stepAction.getTargetCard());
                canOperationList.add(operationDTO);
            });
            seatInfoDTO.setPosId(currentPosId)
                    .setPlayerDTO(playerDTO)
                    .setActionDTOList(responseFulu)
                    .setCanOperationList(canOperationList)
                    .setQueColor(sichuanMahjongSeat.getQueColor())
                    .setStandCardRemaining(standCardList.size())
                    .setRecommendExchangeCards(sichuanMahjongSeat.getRecommendExchangeCards())
                    .setRecommendDingQueColor(sichuanMahjongSeat.getRemommendQueColor())
                    .setHuCard(sichuanMahjongSeat.getHuCard())
                    .setOutCardPool(mahjongSeat.getOutCardPool())
                    .setSeatStatus(mahjongSeat.getSeatStatusList().stream().map(seatStatusEnum -> seatStatusEnum.status()).collect(Collectors.toList()));

            seatInfoDTOList.add(seatInfoDTO);
        }
        roomManager.pushToUser(XueZhanPushCommandCode.RECONNECT_NOTICE_SELF, userId, slefReconnectResponse, roomId);

        ReconnectResponse noticeOtherResponse = new ReconnectResponse(posId);
        pushToRoomUser(XueZhanPushCommandCode.RECONNECT_NOTICE_OTHER, noticeOtherResponse, userId);

    }

    public void pushToRoomUser(int command, Response response, Long... excludeUserIds) {
        IPushManager pushManager = roomManager.getPushManager();
        for (Long userId : userPosIdMap.keySet()) {
            boolean isSend = true;
            for (Long excludeUserId : excludeUserIds) {
                if (userId.equals(excludeUserId)) {
                    isSend = false;
                    break;
                }
            }

            if (isSend) {
                pushManager.pushToUser(command, userId, response, roomId);
            }

        }
    }

    @Override
    public int getTimeoutLimit() {
        return mahjongRule.getRuleConfig().getSerialTimeoutCountLimit();
    }

    public void resetSerialTimeoutCount(Integer posId){
        XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
        final MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();
        mahjongSeat.resetSerialTimeoutCount();

    }

    /**
     * 超时判断与执行方法
     */
    @Override
    public void timeoutExecute() {
        if(mahjongZone == null){
            return;
        }
        if(mahjongZone.checkCurrentGameStatus(SichuanGameStatusEnum.GAME_OVER)){
            return;
        }
        final List<MahjongSeat> needImmediatelyExecuteSeats = mahjongZone.getNeedImmediatelyExecuteSeats();

        for (MahjongSeat mahjongSeat : needImmediatelyExecuteSeats) {
            //麻将的连续超时机制不和斗地主同步，连续超时不进入托管状态
            mahjongSeat.serialTimeoutCountAdd(99999);
            final List<StepAction> canOperations = mahjongSeat.getCanOperations();
                /**
                 *
                 */
            IProducerWithTranslator eventPublisher = DisruptorRegistrar.needEventPublisher(MessageType.SERVICE, roomId);
                final List<Integer> canOperationList = canOperations.stream().map(stepAction -> stepAction.getOperationType().value()).collect(Collectors.toList());
                if(canOperationList.contains(XueZhanMahjongOperationEnum.EXCHANGE_CARD.value())){
                    final int posId = mahjongSeat.getPosId();
                    final XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
                    final SichuanMahjongSeat sichuanMahjongSeat = xueZhanSeat.getSichuanMahjongSeat();
                    ExchangeCardRequest request = new ExchangeCardRequest(sichuanMahjongSeat.getRecommendExchangeCards());
                    request.setChannelUserId(mahjongSeat.getUserId());
                    request.setMessageType(MessageType.TIMEOUT);
                    publishTimeoutEvent(eventPublisher,request,XueZhanCommandCode.EXCHANGE_CARD);
                    continue;
                }
                if(canOperationList.contains(XueZhanMahjongOperationEnum.DING_QUE.value())){
                    final int posId = mahjongSeat.getPosId();
                    final XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
                    final SichuanMahjongSeat sichuanMahjongSeat = xueZhanSeat.getSichuanMahjongSeat();
                    DingQueRequest request = new DingQueRequest(sichuanMahjongSeat.getRemommendQueColor());
                    request.setChannelUserId(mahjongSeat.getUserId());
                    request.setMessageType(MessageType.TIMEOUT);
                    publishTimeoutEvent(eventPublisher,request,XueZhanCommandCode.DING_QUE);
                    continue;
                }
                if(mahjongSeat.isAutoOperation()){

                }else{
                    if(canOperationList.contains(XueZhanMahjongOperationEnum.OUT_CARD.value())){
                        OperationCardRequest request = new OperationCardRequest();

                        /**
                         * 如果玩家摸了牌，就出摸的那张牌。
                         * 否则出最后一张牌（定缺花色的牌放在后面）
                         */
                        final int size = historyList.size();
                        final GameStepModel gameStepModel = historyList.get(size - 1);
                        final Step operationStep = gameStepModel.getOperationStep();
                        final List<Integer> standCardList = mahjongSeat.getStandCardList();
                        if(XueZhanMahjongOperationEnum.TOOK_CARD.value().equals(operationStep.actionType())){
                            request.setCard(standCardList.get(standCardList.size()-1));
                        }else{
                            List<Integer> copyStandCardList = new ArrayList<>(standCardList);
                            final int posId = mahjongSeat.getPosId();
                            final XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
                            final SichuanMahjongSeat sichuanMahjongSeat = xueZhanSeat.getSichuanMahjongSeat();
                            SichuanPlayHelper.sortStandCardList(copyStandCardList,sichuanMahjongSeat.getQueColor());
                            request.setCard(copyStandCardList.get(copyStandCardList.size()-1));
                        }
                        request.setOperationType(XueZhanMahjongOperationEnum.OUT_CARD.value())
                                .setChannelUserId(mahjongSeat.getUserId());
                        request.setMessageType(MessageType.TIMEOUT);

                        publishTimeoutEvent(eventPublisher,request,XueZhanCommandCode.OPERATION_CARD);
                        continue;
                    }
                    if(canOperationList.contains(XueZhanMahjongOperationEnum.CANCEL.value())){
                        StepAction cancelAction = null;
                        for(StepAction stepAction : canOperations){
                            if(XueZhanMahjongOperationEnum.CANCEL.value().equals(stepAction.getOperationType().value())){
                                cancelAction = stepAction;
                            }
                        }

                        OperationCardRequest request = new OperationCardRequest();
                        //cancelAction不可能为null
                        request.setCard(cancelAction.getTargetCard())
                                .setOperationType(XueZhanMahjongOperationEnum.CANCEL.value())
                                .setChannelUserId(mahjongSeat.getUserId());
                        request.setMessageType(MessageType.TIMEOUT);
                        publishTimeoutEvent(eventPublisher,request,XueZhanCommandCode.OPERATION_CARD);
                        continue;
                    }
                }

            }

    }

    private void publishTimeoutEvent(IProducerWithTranslator eventPublisher,Request request,int command){
        Any any = null;
        try {
            any = Any.pack(request);
        } catch (IOException e) {
            log.error("超时任务编码异常 roomId={} zoneId={} request={}",roomId,gameZone.getZoneId(),request);
            e.printStackTrace();
        }
        GameRequestMessage gameRequestMessage = new GameRequestMessage();
        gameRequestMessage.setObject(any);
        GameRequestMessageHead head = new GameRequestMessageHead();
        head.setCmd(command);
        head.setType(MessageType.TIMEOUT.getType());
        head.setRoomId(roomId);
        gameRequestMessage.setHead(head);
        try {
            eventPublisher.publish(gameRequestMessage,null);
        } catch (Exception e) {
            log.error("发布超时任务失败： roomId={} zoneId={}  message={}",roomId,gameZone.getZoneId(),gameRequestMessage);
        }
    }

    @Override
    public XueZhanRoom cloneData() {
        return null;
    }

    private void ruleInit() {
        //H2 拉取房间配置信息,把配置传进去
        final List<Integer> dingQueOption = new ArrayList<>(Arrays.asList(1, 2, 3));

        mahjongRule = new SiChuanMahjongRule();
        SichuanRoomConfig ruleConfig = new SichuanRoomConfig(true, true, dingQueOption, false);
        final List<FanInfo<BaseHuTypeEnum>> baseHuList = new ArrayList<>();
        FanInfo<BaseHuTypeEnum> pingHu = new FanInfo<>(BaseHuTypeEnum.平胡, 1, FanInfo.MULTIPLICATION, PingHuFan.INSTANCE);
        FanInfo<BaseHuTypeEnum> qiDui = new FanInfo<>(BaseHuTypeEnum.七对, 4, FanInfo.MULTIPLICATION, QiDuiFan.INSTANCE);
        baseHuList.add(pingHu);
        baseHuList.add(qiDui);

        final List<FanInfo<HuTypeEnum>> huTypeList = new ArrayList<>();
        FanInfo<HuTypeEnum> ziMo = new FanInfo<>(HuTypeEnum.自摸, 2, FanInfo.MULTIPLICATION, HuTypeFan.INSTANCE);
        FanInfo<HuTypeEnum> dianPaoHu = new FanInfo<>(HuTypeEnum.点炮胡, 1, FanInfo.MULTIPLICATION, HuTypeFan.INSTANCE);
        huTypeList.add(ziMo);
        huTypeList.add(dianPaoHu);

        final List<FanInfo<FormalFanTypeEnum>> formalFanTypeEnumList = new ArrayList<>();
        FanInfo<FormalFanTypeEnum> qingYiSe = new FanInfo<>(FormalFanTypeEnum.清一色, 4, FanInfo.MULTIPLICATION, QingYiSeFan.INSTANCE);
        FanInfo<FormalFanTypeEnum> pengPengHu = new FanInfo<>(FormalFanTypeEnum.大对子, 2, FanInfo.MULTIPLICATION, BigDoubleFan.INSTANCE);
        FanInfo<FormalFanTypeEnum> jinGouDiao = new FanInfo<>(FormalFanTypeEnum.金钩钓, 4, FanInfo.MULTIPLICATION, JinGouDiaoFan.INSTANCE);
        FanInfo<FormalFanTypeEnum> shiBaLuoHan = new FanInfo<>(FormalFanTypeEnum.十八罗汉, 64, FanInfo.MULTIPLICATION, ShiBaLuoHanFan.INSTANCE);

        FanInfo<FormalFanTypeEnum> qingQiDui = new FanInfo<>(FormalFanTypeEnum.清七对, 4, FanInfo.MULTIPLICATION, QingQiDuiFan.INSTANCE);
        FanInfo<FormalFanTypeEnum> longQiDui = new FanInfo<>(FormalFanTypeEnum.龙七对, 2, FanInfo.MULTIPLICATION, LongQiDuiFan.INSTANCE);

        formalFanTypeEnumList.add(qingYiSe);
        formalFanTypeEnumList.add(pengPengHu);
        formalFanTypeEnumList.add(jinGouDiao);
        formalFanTypeEnumList.add(shiBaLuoHan);
        formalFanTypeEnumList.add(qingQiDui);
        formalFanTypeEnumList.add(longQiDui);

        final List<FanInfo<AppendedTypeEnum>> appendedTypeEnumList = new ArrayList<>();
        FanInfo<AppendedTypeEnum> gen = new FanInfo<>(AppendedTypeEnum.根, 2, FanInfo.ADDITION, GenFan.INSTANCE);

        FanInfo<AppendedTypeEnum> tianHu = new FanInfo<>(AppendedTypeEnum.天胡, 256, FanInfo.ADDITION, TianHuFan.INSTANCE);
        FanInfo<AppendedTypeEnum> diHu = new FanInfo<>(AppendedTypeEnum.地胡, 256, FanInfo.ADDITION, DiHuFan.INSTANCE);
        FanInfo<AppendedTypeEnum> haiDiLaoYue = new FanInfo<>(AppendedTypeEnum.海底捞月, 2, FanInfo.ADDITION, HaiDiLaoYueFan.INSTANCE);
        FanInfo<AppendedTypeEnum> haiDiPao = new FanInfo<>(AppendedTypeEnum.海底炮, 2, FanInfo.ADDITION, HaiDiPaoFan.INSTANCE);
        FanInfo<AppendedTypeEnum> qiangGangHu = new FanInfo<>(AppendedTypeEnum.抢杠胡, 2, FanInfo.ADDITION, QiangGangHuFan.INSTANCE);
        FanInfo<AppendedTypeEnum> gangShangKaiHua = new FanInfo<>(AppendedTypeEnum.杠上开花, 2, FanInfo.ADDITION, GangShangKaiHua.INSTANCE);
        FanInfo<AppendedTypeEnum> gangShangPao = new FanInfo<>(AppendedTypeEnum.杠上炮, 2, FanInfo.ADDITION, GangShangPao.INSTANCE);
        appendedTypeEnumList.add(gen);
        appendedTypeEnumList.add(tianHu);
        appendedTypeEnumList.add(diHu);
        appendedTypeEnumList.add(haiDiLaoYue);
        appendedTypeEnumList.add(haiDiPao);
        appendedTypeEnumList.add(qiangGangHu);
        appendedTypeEnumList.add(gangShangKaiHua);
        appendedTypeEnumList.add(gangShangPao);

        final List<FanInfo<CompoundFanTypeEnum>> compoundFanTypeEnumList = new ArrayList<>();
        FanInfo<CompoundFanTypeEnum> qingPeng = new FanInfo<>(CompoundFanTypeEnum.清碰, 8, FanInfo.MULTIPLICATION, QingPengFan.INSTANCE);
        FanInfo<CompoundFanTypeEnum> qingJinGouDiao = new FanInfo<>(CompoundFanTypeEnum.清金钩钓, 16, FanInfo.MULTIPLICATION, QingJinGouDiaoFan.INSTANCE);
        FanInfo<CompoundFanTypeEnum> qingShaiBaLuoHan = new FanInfo<>(CompoundFanTypeEnum.清十八罗汉, 256, FanInfo.MULTIPLICATION, QingShiBaLuoHanFan.INSTANCE);
        compoundFanTypeEnumList.add(qingPeng);
        compoundFanTypeEnumList.add(qingJinGouDiao);
        compoundFanTypeEnumList.add(qingShaiBaLuoHan);

        final List<FanInfo<SpecificFanTypeEnum>> specificFanTypeEnumList = new ArrayList<>();
        FanInfo<SpecificFanTypeEnum> zhiGang = new FanInfo<>(SpecificFanTypeEnum.直杠, 2, null, null);
        FanInfo<SpecificFanTypeEnum> buGang = new FanInfo<>(SpecificFanTypeEnum.补杠, 1, null, null);
        FanInfo<SpecificFanTypeEnum> anGang = new FanInfo<>(SpecificFanTypeEnum.暗杠, 2, null, null);

        FanInfo<SpecificFanTypeEnum> rebate = new FanInfo<>(SpecificFanTypeEnum.退税, null, null, null);
        FanInfo<SpecificFanTypeEnum> chaJiao = new FanInfo<>(SpecificFanTypeEnum.查叫, null, null, null);
        FanInfo<SpecificFanTypeEnum> chaHuaZhu = new FanInfo<>(SpecificFanTypeEnum.查花猪, null, null, null);
        specificFanTypeEnumList.add(zhiGang);
        specificFanTypeEnumList.add(buGang);
        specificFanTypeEnumList.add(anGang);
        specificFanTypeEnumList.add(rebate);
        specificFanTypeEnumList.add(chaJiao);
        specificFanTypeEnumList.add(chaHuaZhu);

        mahjongRule.setRuleConfig(ruleConfig)
                .setBaseHuList(baseHuList)
                .setHuTypeList(huTypeList)
                .setFormalFanTypeEnumList(formalFanTypeEnumList)
                .setAppendedTypeEnumList(appendedTypeEnumList)
                .setCompoundFanTypeEnumList(compoundFanTypeEnumList);
        mahjongRule.setSpecificFanTypeList(specificFanTypeEnumList);
    }

    /**
     * 通过posId获取玩家方位
     *
     * @param posId
     * @return
     */
    public String getSeatDirection(Integer posId) {
        Integer zhuangId = mahjongZone.getBankerPosId();
        if (posId.equals(zhuangId)) {
            return "东";
        } else {
            int seatNum = posIdSeatMap.size();
            if (seatNum == 2) {
                return "西";
            }
            int num = posId - zhuangId;
            int position = num > 0 ? num : seatNum + num;
            switch (position) {
                case 1:
                    return "南";
                case 2:
                    return "西";
                case 3:
                    return "北";
            }
        }
        return null;
    }
}
