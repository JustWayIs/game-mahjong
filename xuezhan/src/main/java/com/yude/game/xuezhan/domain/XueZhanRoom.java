package com.yude.game.xuezhan.domain;


import com.yude.game.common.contant.MahjongStatusCodeEnum;
import com.yude.game.common.contant.PushCommandCode;
import com.yude.game.common.mahjong.Solution;
import com.yude.game.common.manager.IPushManager;
import com.yude.game.common.manager.IRoomManager;
import com.yude.game.common.model.*;
import com.yude.game.common.model.fan.Rule;
import com.yude.game.common.model.fan.*;
import com.yude.game.common.model.fan.judge.HuTypeFan;
import com.yude.game.common.model.fan.judge.appended.*;
import com.yude.game.common.model.fan.judge.base.PingHuFan;
import com.yude.game.common.model.fan.judge.base.QiDuiFan;
import com.yude.game.common.model.fan.judge.compound.QingJinGouDiaoFan;
import com.yude.game.common.model.fan.judge.compound.QingPengFan;
import com.yude.game.common.model.fan.judge.compound.QingShiBaLuoHanFan;
import com.yude.game.common.model.fan.judge.formal.*;
import com.yude.game.common.model.fan.param.AppendedFanParam;
import com.yude.game.common.model.fan.param.CompoundFanParam;
import com.yude.game.common.model.fan.param.FormalFanParam;
import com.yude.game.common.model.history.*;
import com.yude.game.common.model.sichuan.*;
import com.yude.game.common.model.sichuan.constant.SeatStatusEnum;
import com.yude.game.common.model.sichuan.constant.SichuanGameStatusEnum;
import com.yude.game.common.model.sichuan.history.ExchangeCardStep;
import com.yude.game.common.model.sichuan.history.SichuanGameStartStep;
import com.yude.game.common.timeout.MahjongTimeoutTaskPool;
import com.yude.game.exception.BizException;
import com.yude.game.exception.SystemException;
import com.yude.game.xuezhan.application.response.*;
import com.yude.game.xuezhan.application.response.dto.OperationDTO;
import com.yude.game.xuezhan.application.response.dto.SettlementInfoDTO;
import com.yude.game.xuezhan.constant.XueZhanMahjongOperationEnum;
import com.yude.game.xuezhan.constant.XueZhanPushCommandCode;
import com.yude.game.xuezhan.domain.action.XueZhanAction;
import com.yude.protocol.common.constant.StatusCodeEnum;
import com.yude.protocol.common.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @Author: HH
 * @Date: 2020/8/1 15:29
 * @Version: 1.0
 * @Declare:
 */
public class XueZhanRoom extends AbstractRoomModel<XueZhanZone, XueZhanSeat, MahjongTimeoutTaskPool> implements XueZhanAction {

    private static final Logger log = LoggerFactory.getLogger(XueZhanRoom.class);
    private SichuanMahjongZone sichuanMahjongZone;
    private MahjongZone mahjongZone;
    private Rule<SichuanRoomConfig> rule;

    /**
     * 从XueZhanZone挪到外层
     */
    private List<GameStepModel> historyList;

    @Override
    public void init(IRoomManager roomManager, Long roomId, List<Player> playerList, int roundLimit, int inningLimit) {
        ruleInit();
        historyList = new ArrayList<>();
        super.init(roomManager, roomId, playerList, roundLimit, inningLimit);

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

        mahjongZone.setLastOperationTime(System.currentTimeMillis());
        mahjongZone.setGameStatus(SichuanGameStatusEnum.EXCHANGE_CARD);

        noticePlayersDealCardsResult();
        //noticePlayersChangeCard();
    }

    public void operation(Integer card, Integer operationType, Long userId, boolean isRestore) {
        Integer operationPosId = userPosIdMap.get(userId);
        XueZhanMahjongOperationEnum operationEnum = XueZhanMahjongOperationEnum.matchByValue(operationType);
        if(isRestore){
            log.info("多操作的最终执行： roomId={} zoneId={} card={} operation={} 方位=[{},posId={}]",roomId,gameZone.getZoneId(),card,operationEnum,getSeatDirection(operationPosId),operationPosId);
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
        log.info("出牌：roomId={} zoneId={} 方位=[{},posId={}] card={}", roomId, gameZone.getZoneId(), getSeatDirection(posId), posId, card);
        XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
        if (!xueZhanSeat.canOperation(XueZhanMahjongOperationEnum.OUT_CARD)) {
            log.warn("玩家没有出牌权限：roomId={} zoneId=={} userId={}  方位=[{},posId={}] card={}", roomId, gameZone.getZoneId(), xueZhanSeat.getUserId(), getSeatDirection(posId), posId, card);
            throw new BizException(MahjongStatusCodeEnum.NO_OUT_CARD_PERMISSION);
        }

        XueZhanSeat seat = posIdSeatMap.get(posId);
        GameStepModel<OperationCardStep> gameStepModel = mahjongZone.outCard(card, posId);
        OperationCardStep step = gameStepModel.getOperationStep();
        StepAction action = step.getAction();

        OperationResultResponse response = new OperationResultResponse();
        response.setPosId(step.getPosId())
                .setTargetCard(action.getTargetCard())
                .setOperationType(action.getOperationType().value());
        pushToRoomUser(XueZhanPushCommandCode.OPERATION_RESULT_NOTICE, response);


        /**
         * 判断有没有玩家可以针对这次出牌进行操作
         */
        List<MahjongSeat> canOperationSeats =
                sichuanMahjongZone.otherPalyerCanDo(seat.getSichuanMahjongSeat(), card);
        if (canOperationSeats.size() > 0) {
            for (MahjongSeat canOperationSeat : canOperationSeats) {
                List<OperationDTO> operationList = new ArrayList<>();
                List<StepAction> canOperations = canOperationSeat.getCanOperations();
                canOperations.stream().forEach(operation -> {
                    OperationDTO operationDTO = new OperationDTO(operation.getOperationType().value(), card);
                    operationList.add(operationDTO);
                });

                OperationNoticeResponse noticeResponse = new OperationNoticeResponse(canOperationSeat.getPosId(), operationList);
                roomManager.pushToUser(XueZhanPushCommandCode.OPERATION_NOTICE, canOperationSeat.getUserId(), noticeResponse, roomId);
            }
        } else {
            Integer needTookCardPosId = sichuanMahjongZone.refreshTookPlayer();
            nextPalyerTookCard(needTookCardPosId);
        }

    }

    public void nextPalyerTookCard(Integer needTookCardPosId) {
        /**
         * 没有玩家可以操作，下一个玩家摸牌
         */
        if (sichuanMahjongZone.gameover()) {
            settlement();
            return;
        }

        GameStepModel<OperationCardStep> tookCardStep = sichuanMahjongZone.tookCardStep(needTookCardPosId);
        historyList.add(tookCardStep);
        TookCardNoticeResponse tookCardNoticeResponse = new TookCardNoticeResponse(needTookCardPosId);
        tookCardNoticeResponse.setPosId(needTookCardPosId);

        XueZhanSeat needTookCardSeat = posIdSeatMap.get(needTookCardPosId);
        Long userId = needTookCardSeat.getUserId();
        pushToRoomUser(XueZhanPushCommandCode.TOOK_CARD_NOTICE, tookCardNoticeResponse, userId);

        Integer tookCard = tookCardStep.getOperationStep().getAction().getTargetCard();
        //因为在pushToRoomUser方法中，需要推送的数据已经被序列化了，所以这里修改相同的response不会影响之前的推送
        tookCardNoticeResponse.setCard(tookCard);
        needTookCardSeat.getMahjongSeat().addOperation(XueZhanMahjongOperationEnum.OUT_CARD);
        roomManager.pushToUser(XueZhanPushCommandCode.TOOK_CARD_NOTICE, userId, tookCardNoticeResponse, roomId);

        //sichuanMahjongZone.whatCanYouDo(tookCard);
        noticePlayersOperation(tookCard);
    }

    @Override
    public void hu(Integer card, Integer posId, boolean isRestore) {
        log.info("请求胡操作： roomId={} zoneId={} 方位=[{},posId={}] type={} card={} posId={}", roomId, gameZone.getZoneId(), getSeatDirection(posId), posId, posId);
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
            GameStepModel<OperationCardStep> stepModel = mahjongZone.hu(card, posId);
            historyList.add(stepModel);
        }


        boolean needWait = mahjongZone.existsCanOperation();
        if (needWait) {
            return;
        }
        /**
         * 还原临时区的最高优先级操作
         * 或者下一个玩家摸牌
         */
        if (!restoreAction()) {
            //如果不是还原出来的操作，这里的清理其实是第二次执行了，但是多清理一次也没有影响
            huSeat.clearOperation();
            OperationResultResponse operationResultResponse = new OperationResultResponse();
            operationResultResponse
                    .setPosId(huSeat.getPosId())
                    .setTargetCard(card)
                    .setOperationType(action.getOperationType().value())
                    .setCardSourcePosId(action.getCardSource())
                    .setCombinationCards(huSeat.getStandCardList());

            pushToRoomUser(XueZhanPushCommandCode.OPERATION_RESULT_NOTICE, operationResultResponse);

            List<FanInfo> fanInfos = checkFan(xueZhanSeat, action.getTargetCard(), XueZhanMahjongOperationEnum.HU.value());
            huSettlement(xueZhanSeat.getMahjongSeat(),action.getOperationType().value(),action,fanInfos);
            mahjongZone.stepAdd();

            Integer needTookCardPosId = sichuanMahjongZone.refreshTookPlayer();
            nextPalyerTookCard(needTookCardPosId);
        }
    }

    /**
     * @param seat
     * @param card
     * @param operationType 用于标识是否是一炮多响
     */
    public List<FanInfo> checkFan(XueZhanSeat seat, Integer card, Integer operationType) {
        MahjongSeat huPlayerSeat = seat.getMahjongSeat();
        huPlayerSeat.solution();

        StepAction huAction = huPlayerSeat.getDesignateOperationCardSource(operationType, card);
        Integer cardSourcePosId = huAction.getCardSource();
        boolean cardFromSelf = cardSourcePosId.equals(seat.getPosId());

        List<Integer> standCardList = huPlayerSeat.getStandCardList();
        List<Solution> solutions = huPlayerSeat.getPlayerHand().canHu(card, cardFromSelf);
        //血战（无赖子）可能没有要找最大番型的需要
        //可能会出现多个solution能胡，但是番型完全一致的情况（没有被优化掉），比如面子 334455 的理牌
        /*for(Solution solution : solutions){

        }*/
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
        for(FanInfo<AppendedTypeEnum> fanInfo : rule.getAppendedTypeEnumList()){
            boolean b = fanInfo.judgeFan(appendedFanParam);
            if(b){
                certaintyFanList.add(fanInfo);
            }
        }
        //这里要求一炮多响，不能立马修改step的Action类型，要走完这里，要不然checkFan方法没法区分是普通胡牌，还是一炮多响
        HuCardStep huStep = (HuCardStep) huPlayerSeat.getOperationHistoryByTypeAndCard(XueZhanMahjongOperationEnum.HU.value(), card);
        huStep.setFanInfoList(certaintyFanList);
        return certaintyFanList;
    }

    public void multiplePlayerHu(Integer card, List<Integer> posId) {

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
        boolean needWait = mahjongZone.existsCanOperation();
        if (needWait) {
            return;
        }
        /**
         * 还原临时区的最高优先级操作
         * 或者下一个玩家摸牌
         */
        if (!restoreAction()) {
            OperationResultResponse operationResultResponse = new OperationResultResponse();
            operationResultResponse
                    .setPosId(posId)
                    .setOperationType(XueZhanMahjongOperationEnum.CANCEL.value())
                    .setTargetCard(card);
            roomManager.pushToUser(XueZhanPushCommandCode.OPERATION_RESULT_NOTICE, xueZhanSeat.getUserId(), operationResultResponse, roomId);

            Integer needTookCardPosId = sichuanMahjongZone.refreshTookPlayer();
            nextPalyerTookCard(needTookCardPosId);
        }
    }

    /**
     * 能产生副露的操作
     *
     * @param card
     * @param posId
     * @param operationType
     */
    public void operation(Integer card, Integer posId, MahjongOperation operationType, boolean isRestore) {
        log.info("请求操作：  roomId={} zoneId={} 方位=[{},posId={}] type={} card={}", roomId, gameZone.getZoneId(), getSeatDirection(posId),posId,operationType, card);
        XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
        boolean canCancel = xueZhanSeat.canOperation(operationType);
        if (!canCancel) {
            log.warn("玩家没有该的操作权限：operation={} roomId={} userId={} 方位=[{},posId={}]", operationType, roomId, xueZhanSeat.getUserId(), getSeatDirection(posId), posId);
            throw new BizException(MahjongStatusCodeEnum.matchNotOperationErrorByOperation(operationType.value()));
        }
        MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();
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
    private void nextProcess(XueZhanSeat xueZhanSeat, Integer card, StepAction action) {
        boolean needWait = mahjongZone.existsCanOperation();
        if (needWait) {
            return;
        }
        /**
         * 还原临时区的最高优先级操作
         * 或者下一个玩家摸牌
         */
        if (!restoreAction()) {
            log.info("不需要还原操作： roomId={} zoneId={} 方位=[{},posId={}] action={}",roomId,gameZone.getZoneId(),getSeatDirection(xueZhanSeat.getPosId()),xueZhanSeat.getPosId(),action);
            MahjongSeat operationSeat = xueZhanSeat.getMahjongSeat();
            //historyList.add(stepModel);
            OperationResultResponse operationResultResponse = new OperationResultResponse();
            operationResultResponse
                    .setPosId(operationSeat.getPosId())
                    .setTargetCard(card)
                    .setOperationType(action.getOperationType().value())
                    .setCardSourcePosId(action.getCardSource());

            pushToRoomUser(XueZhanPushCommandCode.OPERATION_RESULT_NOTICE, operationResultResponse);
            mahjongZone.cleanTempAction();

            //H2 如果是杠操作 还要触发小结算。 既然要在这里做具体类型判断，那么其实胡牌也可以直接用operation方法
            Integer type = action.getOperationType().value();
            if(XueZhanMahjongOperationEnum.PENG.value().equals(type)){
                //碰完要出牌
                operationSeat.addOperation(XueZhanMahjongOperationEnum.OUT_CARD);
                OperationDTO operationDTO = new OperationDTO();
                operationDTO.setOpreation(XueZhanMahjongOperationEnum.OUT_CARD.value());
                List<OperationDTO> operationList = new ArrayList<>();
                operationList.add(operationDTO);
                OperationNoticeResponse noticeResponse = new OperationNoticeResponse(operationSeat.getPosId(), operationList);
                roomManager.pushToUser(XueZhanPushCommandCode.OPERATION_NOTICE, operationSeat.getUserId(), noticeResponse, roomId);
                return;
            } else if(XueZhanMahjongOperationEnum.AN_GANG.value().equals(type) || XueZhanMahjongOperationEnum.BU_GANG.value().equals(type)){
                otherGangSettlement(operationSeat,type,action);

            }else if(XueZhanMahjongOperationEnum.ZHI_GANG.value().equals(type)){
                zhiGangSettlement(operationSeat,type,action);
            }else if(XueZhanMahjongOperationEnum.HU.equals(type)){
                //在这里实现的话  hu()方法还有存在必要么

            }
            mahjongZone.stepAdd();
            //杠完要摸牌
            nextPalyerTookCard(operationSeat.getPosId());
        }
    }
    private void huSettlement(MahjongSeat operationSeat,Integer type,final StepAction action,final List<FanInfo> fanInfos){
        List<MahjongSeat> neededutionSeats;
        //自摸
        Integer cardSourcePosId = action.getCardSource();
        int huPosId = operationSeat.getPosId();
        if(huPosId == action.getCardSource()){
            neededutionSeats = sichuanMahjongZone.findNotHuSeat();
        }else{
            neededutionSeats = new ArrayList<>();
            XueZhanSeat xueZhanSeat = posIdSeatMap.get(cardSourcePosId);
            neededutionSeats.add(xueZhanSeat.getMahjongSeat());
        }

        int sumFan = 1;
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


        SettlementStep settlementStep = new SettlementStep();
        Map<Integer,SettlementInfo> seatSettlementInfoMap = new HashMap<>();
        settlementStep.setPosId(huPosId)
                .setStep(mahjongZone.getStepCount())
                .setSeatSettlementInfoMap(seatSettlementInfoMap)
                .setGameStatus(SichuanGameStatusEnum.SETTLEMENT)
                //这里的action来自可操作权限
                .setAction(action);

        List<SettlementInfoDTO> settlementInfoDTOList = new ArrayList<>();
        for(MahjongSeat curSeat : neededutionSeats){
            Integer curPosId = curSeat.getPosId();

            Player player = curSeat.getPlayer();
            long beforeScore = player.getScore();
            player.scoreSettle(huScore);
            long remainintScore = player.getScore();


            SettlementInfo settlementInfo = new SettlementInfo();
            settlementInfo.setPosId(curPosId)
                    .setBeforeScore(beforeScore)
                    .setChangeScore((long)-huScore)
                    .setRemaningScore(remainintScore)
                    .setFanInfoList(fanInfos);
            seatSettlementInfoMap.put(curPosId,settlementInfo);

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
                .setChangeScore((long)winScore)
                .setRemaningScore(remainintScore)
                .setFanInfoList(fanInfos);

        seatSettlementInfoMap.put(huPosId,settlementInfo);
        GameStepModel<SettlementStep> huStepModel = new GameStepModel<>(gameZone.getZoneId(),operationSeat.getPlayer(),settlementStep);
        historyList.add(huStepModel);

        SettlementResponse settlementResponse = new SettlementResponse();
        settlementResponse.setSettlementInfoDTOS(settlementInfoDTOList)
                .setTargetCard(action.getTargetCard())
                .setOperationType(action.getOperationType().value())
                .setOperationPosId(huPosId)
                .setCardSourcePosId(action.getTargetCard());

        pushToRoomUser(XueZhanPushCommandCode.SETTLEMENT_NOTICE,settlementResponse);

        /*if(action.getCardSource().equals(operationSeat.getPosId())){
            ziMoHuSettlement(operationSeat,type,action,fanInfos);
        }else{
            dianPaoHuSettlement(operationSeat,type,action,fanInfos);
        }*/
    }

    private void ziMoHuSettlement(MahjongSeat operationSeat,Integer type,StepAction action,List<FanInfo> fanInfos){
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

    private void dianPaoHuSettlement(MahjongSeat operationSeat,Integer type,StepAction action,List<FanInfo> fanInfos){

    }

    private void zhiGangSettlement(MahjongSeat operationSeat,Integer type,StepAction action){
        SichuanRoomConfig ruleConfig = rule.getRuleConfig();
        Integer gangFan = ruleConfig.getGangFan(type);
        Integer baseScoreFactor = ruleConfig.getBaseScoreFactor();
        int changeScore = gangFan * baseScoreFactor;

        SettlementStep settlementStep = new SettlementStep();
        settlementStep.setStep(mahjongZone.getStepCount())
                .setPosId(operationSeat.getPosId())
                .setGameStatus(SichuanGameStatusEnum.SETTLEMENT)
                .setAction(action);

        Map<Integer,SettlementInfo> seatSettlementInfoMap = new HashMap<>();
        List<SettlementInfoDTO> settlementInfoDTOS = new ArrayList<>();
        for(Map.Entry<Integer,XueZhanSeat> entry : posIdSeatMap.entrySet()){
            Integer xueZhanPosId = entry.getKey();
            XueZhanSeat seat = entry.getValue();
            MahjongSeat currentSeat = seat.getMahjongSeat();
            if(currentSeat.existsStatus(SeatStatusEnum.ALREADY_HU)){
                continue;
            }
            int resultChangeScore = 0;
            if(!xueZhanPosId.equals(operationSeat.getPosId())){
                resultChangeScore = -changeScore;
            }

            List<StepAction> fuLu = currentSeat.getFuLu();

            Player player = currentSeat.getPlayer();
            long beforeScore = player.getScore();
            player.scoreSettle(-changeScore);
            long remainingScore = player.getScore();

            ArrayList<Integer> standCardList = new ArrayList<>(currentSeat.getStandCardList());
            SettlementInfo settlementInfo = new SettlementInfo();
            settlementInfo.setPosId(xueZhanPosId)
                    .setBeforeScore(beforeScore)
                    .setChangeScore((long)resultChangeScore)
                    .setRemaningScore(remainingScore)
                    .setStandCards(standCardList)
                    .setStandCardsConvertList(MahjongProp.cardConvertName(standCardList))
                    .setFuluList(new ArrayList<>(fuLu));

            seatSettlementInfoMap.put(currentSeat.getPosId(),settlementInfo);
            SettlementInfoDTO settlementInfoDTO = new SettlementInfoDTO();
            settlementInfoDTO.setPosId(xueZhanPosId)
                    .setBeforeScore(beforeScore)
                    .setChangeScore((long)resultChangeScore)
                    .setRemaningScore(remainingScore);
            settlementInfoDTOS.add(settlementInfoDTO);
        }
        settlementStep.setSeatSettlementInfoMap(seatSettlementInfoMap);
        GameStepModel<SettlementStep> gameStepModel = new GameStepModel<>(gameZone.getZoneId(),operationSeat.getPlayer(),settlementStep);
        historyList.add(gameStepModel);

        SettlementResponse settlementResponse = new SettlementResponse();
        settlementResponse.setCardSourcePosId(action.getCardSource())
                .setOperationPosId(operationSeat.getPosId())
                .setOperationType(action.getOperationType().value())
                .setTargetCard(action.getTargetCard())
                .setSettlementInfoDTOS(settlementInfoDTOS);
        pushToRoomUser(XueZhanPushCommandCode.SETTLEMENT_NOTICE,settlementResponse);
    }

    private void otherGangSettlement(MahjongSeat operationSeat,Integer type,StepAction action){
        SichuanRoomConfig ruleConfig = rule.getRuleConfig();
        Integer gangFan = ruleConfig.getGangFan(type);
        Integer baseScoreFactor = ruleConfig.getBaseScoreFactor();
        int changeScore = gangFan * baseScoreFactor;
        int sumScore = 0;

        SettlementStep settlementStep = new SettlementStep();
        settlementStep.setStep(mahjongZone.getStepCount())
                .setPosId(operationSeat.getPosId())
                .setGameStatus(SichuanGameStatusEnum.SETTLEMENT)
                .setAction(action);

        Map<Integer,SettlementInfo> seatSettlementInfoMap = new HashMap<>();
        List<SettlementInfoDTO> settlementInfoDTOS = new ArrayList<>();
        for(Map.Entry<Integer,XueZhanSeat> entry : posIdSeatMap.entrySet()){
            Integer xueZhanPosId = entry.getKey();
            if(xueZhanPosId.equals(operationSeat.getPosId())){
                continue;
            }
            XueZhanSeat seat = entry.getValue();
            MahjongSeat currentSeat = seat.getMahjongSeat();
            if(currentSeat.existsStatus(SeatStatusEnum.ALREADY_HU)){
                continue;
            }
            sumScore += changeScore;
            List<StepAction> fuLu = currentSeat.getFuLu();

            Player player = currentSeat.getPlayer();
            long beforeScore = player.getScore();
            //H2 接收长整型更加合适
            player.scoreSettle(-changeScore);
            long remainingScore = player.getScore();

            ArrayList<Integer> standCardList = new ArrayList<>(currentSeat.getStandCardList());
            SettlementInfo settlementInfo = new SettlementInfo();
            settlementInfo.setPosId(xueZhanPosId)
                    .setBeforeScore(beforeScore)
                    .setChangeScore((long)-changeScore)
                    .setRemaningScore(remainingScore)
                    .setStandCards(standCardList)
                    .setStandCardsConvertList(MahjongProp.cardConvertName(standCardList))
                    .setFuluList(new ArrayList<>(fuLu));

            seatSettlementInfoMap.put(currentSeat.getPosId(),settlementInfo);

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

        SettlementInfo settlementInfo = new SettlementInfo();
        settlementInfo.setPosId(operationSeat.getPosId())
                .setBeforeScore(beforeScore)
                .setChangeScore((long)-sumScore)
                .setRemaningScore(remainingScore)
                .setStandCards(standCardList)
                .setStandCardsConvertList(MahjongProp.cardConvertName(standCardList))
                .setFuluList(new ArrayList<>(fuLu));

        seatSettlementInfoMap.put(operationSeat.getPosId(),settlementInfo);
        settlementStep.setSeatSettlementInfoMap(seatSettlementInfoMap);
        GameStepModel<SettlementStep> gameStepModel = new GameStepModel<>(gameZone.getZoneId(),operationSeat.getPlayer(),settlementStep);
        historyList.add(gameStepModel);

        SettlementInfoDTO settlementInfoDTO = new SettlementInfoDTO();
        settlementInfoDTO.setPosId(operationSeat.getPosId())
                .setBeforeScore(beforeScore)
                .setChangeScore(-sumScore)
                .setRemaningScore(remainingScore);
        settlementInfoDTOS.add(settlementInfoDTO);

        SettlementResponse settlementResponse = new SettlementResponse();
        settlementResponse.setCardSourcePosId(action.getCardSource())
                .setOperationPosId(operationSeat.getPosId())
                .setOperationType(action.getOperationType().value())
                .setTargetCard(action.getTargetCard())
                .setSettlementInfoDTOS(settlementInfoDTOS);
        pushToRoomUser(XueZhanPushCommandCode.SETTLEMENT_NOTICE,settlementResponse);
    }
    private void checkGangFan(Integer operationType){
        SichuanRoomConfig ruleConfig = rule.getRuleConfig();
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
        GameStepModel<OperationCardStep> stepModel = mahjongZone.peng(card, posId);
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
        XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
        if (!rule.getRuleConfig().getCanHsz()) {
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
            List<Step> exchangeHistory = getHistoryByGameStatus(SichuanGameStatusEnum.EXCHANGE_CARD);
            sichuanMahjongZone.executeExchange(exchangeHistory);

            noticePlayersExchangeResult();
            if (rule.getRuleConfig().getCanDingQue()) {
                noticePlayersDingQue();
            }
        }
    }

    @Override
    public void dingQue(Integer color, Integer posId) {
        XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
        if (!rule.getRuleConfig().getCanDingQue()) {
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
            FormalProcessNoticeResponse response = new FormalProcessNoticeResponse(mahjongZone.getBankerPosId(), 10);
            pushToRoomUser(XueZhanPushCommandCode.FORMAL_PROCESS_NOTICE, response);

            //定缺完成后，没有摸牌，庄家直接出牌
            noticePlayersOperation(null);
        }

    }

    private void settlement() {

    }

    /**
     * 复原未执行的操作（最高优先级的操作）
     *
     * @param
     */
    private boolean restoreAction() {
        List<TempAction> tempActions = mahjongZone.getTempActions();
        if (tempActions.size() > 0) {
            log.info("需要进行还原操作 roomId={} zoneId={} tempActions={}",roomId, gameZone.getZoneId(),tempActions);
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

            //H2 不能直接还原，要判断是否有多人胡。如果是多人胡，要把操作类型改为一炮多响
            if (needExecuteActionList.size() > 1) {
                List<Integer> huPosIdList = new ArrayList<>();
                Integer targetCard = null;
                for (TempAction tempAction : needExecuteActionList) {
                    huPosIdList.add(tempAction.getPosId());
                    targetCard = tempAction.getStepAction().getTargetCard();
                }
                multiplePlayerHu(targetCard, huPosIdList);
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
            if (operationStep.stepType().equals(statusEnum)) {
                list.add(operationStep);
            }
        }
        return list;
    }

    private void noticePlayersDealCardsResult() {
        for (GameStepModel gameStepModel : historyList) {
            GameStartStep gameStartStep = (GameStartStep) gameStepModel.getOperationStep();

            XueZhanStartResponse response = new XueZhanStartResponse();
            if (rule.getRuleConfig().getCanHsz()) {
                List<Integer> recommendedCards = SichuanPlayHelper.recommendedChangeCard(gameStartStep.getStandCards(), 3);
                recommendedCards = recommendedCards.subList(0, 3);
                SichuanGameStartStep sichuanGameStartStep = new SichuanGameStartStep();
                sichuanGameStartStep.setGameStartStep(gameStartStep);
                sichuanGameStartStep.setRecommendedCardList(recommendedCards);
                response.setRecommendExchangeList(recommendedCards);
                //替换step为四川麻将类型
                gameStepModel.setOperationStep(sichuanGameStartStep);
            }
            int posId = gameStartStep.getPosId();
            XueZhanSeat seat = posIdSeatMap.get(posId);
            MahjongSeat mahjongSeat = seat.getMahjongSeat();
            mahjongSeat.addOperation(XueZhanMahjongOperationEnum.EXCHANGE_CARD);
            mahjongSeat.addChangce();
            mahjongSeat.addStatus(SeatStatusEnum.EXCHANGE_CARD);

            response.setRoomId(gameStartStep.getRoomId())
                    .setZoneId(gameStartStep.getZoneId())
                    .setStep(gameStartStep.getStep())
                    .setBankerPosId(gameStartStep.getBankerPosId())
                    .setDiceList(gameStartStep.getDiceList())
                    .setPosId(gameStartStep.getPosId())
                    .setStandCardList(gameStartStep.getStandCards())
                    .setCardWallRemaining(mahjongZone.getCardWall().size())
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

        List<StepAction> stepActions = sichuanMahjongZone.whatCanYouDo(card);
        List<OperationDTO> canOperations = new ArrayList<>();
        OperationDTO outCardOperation = new OperationDTO(XueZhanMahjongOperationEnum.OUT_CARD.value(), null);
        canOperations.add(outCardOperation);
        mahjongSeat.addOperation(XueZhanMahjongOperationEnum.OUT_CARD);

        if (stepActions.size() > 0) {
            stepActions.stream().forEach(stepAction -> {
                OperationDTO operationDTO = new OperationDTO(stepAction.getOperationType().value(), stepAction.getTargetCard());
                canOperations.add(operationDTO);
                mahjongSeat.addOperation(stepAction.getOperationType());
            });
            StepAction cancel = new StepAction();
            cancel.setTargetCard(card)
                    .setCardSource(seat.getPosId())
                    .setOperationType(XueZhanMahjongOperationEnum.CANCEL);
            mahjongSeat.addOperation(cancel);
        }

        OperationNoticeResponse response = new OperationNoticeResponse(seat.getPosId(), canOperations);

        roomManager.pushToUser(XueZhanPushCommandCode.OPERATION_NOTICE, seat.getUserId(), response, roomId);
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
        return rule.getRuleConfig().getSERIAL_TIMEOUT_OUNT();
    }

    @Override
    public XueZhanRoom cloneData() {
        return null;
    }

    private void ruleInit() {
        //H2 拉取房间配置信息,把配置传进去
        final List<Integer> dingQueOption = new ArrayList<>(Arrays.asList(1, 2, 3));

        rule = new Rule<>();
        SichuanRoomConfig ruleConfig = new SichuanRoomConfig(true, true, dingQueOption);
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

        rule.setRuleConfig(ruleConfig)
                .setBaseHuList(baseHuList)
                .setHuTypeList(huTypeList)
                .setFormalFanTypeEnumList(formalFanTypeEnumList)
                .setAppendedTypeEnumList(appendedTypeEnumList)
                .setCompoundFanTypeEnumList(compoundFanTypeEnumList);
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
