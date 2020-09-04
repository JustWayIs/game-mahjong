package com.yude.game.xuezhan.domain;


import com.yude.game.common.contant.MahjongStatusCodeEnum;
import com.yude.game.common.contant.PushCommandCode;
import com.yude.game.common.manager.IPushManager;
import com.yude.game.common.manager.IRoomManager;
import com.yude.game.common.model.*;
import com.yude.game.common.model.fan.Rule;
import com.yude.game.common.model.fan.*;
import com.yude.game.common.model.history.GameStartStep;
import com.yude.game.common.model.history.GameStepModel;
import com.yude.game.common.model.history.OperationCardStep;
import com.yude.game.common.model.history.Step;
import com.yude.game.common.model.sichuan.SichuanGameStatusEnum;
import com.yude.game.common.model.sichuan.SichuanMahjongRoom;
import com.yude.game.common.model.sichuan.SichuanPlayHelper;
import com.yude.game.common.model.sichuan.SichuanRoomConfig;
import com.yude.game.common.model.sichuan.history.ExchangeCardStep;
import com.yude.game.common.model.sichuan.history.SichuanGameStartStep;
import com.yude.game.common.timeout.MahjongTimeoutTaskPool;
import com.yude.game.exception.BizException;
import com.yude.game.xuezhan.application.response.*;
import com.yude.game.xuezhan.application.response.dto.OperationDTO;
import com.yude.game.xuezhan.constant.XueZhanMahjongOperationEnum;
import com.yude.game.xuezhan.constant.XueZhanPushCommandCode;
import com.yude.game.xuezhan.domain.action.XueZhanAction;
import com.yude.game.xuezhan.domain.status.SeatStatusEnum;
import com.yude.protocol.common.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @Author: HH
 * @Date: 2020/8/1 15:29
 * @Version: 1.0
 * @Declare:
 */
public class XueZhanRoom extends AbstractRoomModel<XueZhanZone, XueZhanSeat, MahjongTimeoutTaskPool> implements XueZhanAction {

    private static final Logger log = LoggerFactory.getLogger(XueZhanRoom.class);
    private SichuanMahjongRoom sichuanMahjongRoom;
    private MahjongRoom mahjongRoom;
    private Rule<SichuanRoomConfig> rule;

    /**
     *  因为换三张操作 、 定缺操作这种地方麻将玩法，导致，historyList不能放在MahjongZone里面
     *  也就导致多了一层对GameZone的调用。核心问题是，多一重掉用对性能真的有影响么，像现在的Controller省略了Service一样，又对性能提高有多少贡献呢。
     *  如果想要少一层调用，倒是可以把historyList放在RoomModel里面，这里的核心问题是类的职能划分
     *  从代码复用的层面来说，这里面的方法确实应该放在 MahjongZone 和 地方麻将Zone里面
     */

    private List<GameStepModel> historyList;

    @Override
    public void init(IRoomManager roomManager, Long roomId, List<Player> playerList, int roundLimit, int inningLimit) {
        ruleInit();
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
        XueZhanSeat[] seatModels = new XueZhanSeat[posIdSeatMap.size()];
        for (int i = 0; i < seatModels.length; i++) {
            seatModels[i] = posIdSeatMap.get(i);
        }

        return new XueZhanZone(seatModels, gameRound, gameInning);
    }

    @Override
    public void startGame() {
        gameZone = getPracticalGameZoneModel();
        log.debug("血战到底 游戏开始 roomId={}  gameId={}", roomId, gameZone.getZoneId());
        gameZone.init();
        gameZone.deal(roomId);
        noticePlayersDealCardsResult();
        //noticePlayersChangeCard();
    }


    @Override
    public void outCard(Integer card, Integer posId) {
        log.info("出牌：roomId={} zoneId={} 方位=[{},posId={}] card={}",roomId,gameZone.getZoneId(),getSeatDirection(posId),posId,card);
        XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
        if(!xueZhanSeat.canOperation(XueZhanMahjongOperationEnum.OUT_CARD)){
            log.warn("玩家没有出牌权限：roomId={} zoneId=={} userId={}  方位=[{},posId={}] card={}",roomId,gameZone.getZoneId(),xueZhanSeat.getUserId(),getSeatDirection(posId),posId,card);     throw new BizException(MahjongStatusCodeEnum.NO_OUT_CARD_PERMISSION);
        }

        XueZhanSeat seat = posIdSeatMap.get(posId);
        GameStepModel<OperationCardStep> gameStepModel = gameZone.outCard(card, posId);
        OperationCardStep step = gameStepModel.getOperationStep();
        StepAction action = step.getAction();

        OperationResultResponse response = new OperationResultResponse();
        response.setPosId(step.getPosId())
                .setTargetCard(action.getTargetCard())
                .setOperationType(action.getOperationType().value());
        pushToRoomUser(XueZhanPushCommandCode.OPERATION_RESULT_NOTICE,response);


        /**
         * 判断有没有玩家可以针对这次出牌进行操作
         */
        List<MahjongSeat> canOperationSeats =
                gameZone.otherPalyerCanDo(seat, card);
        if(canOperationSeats.size() > 0){
            for(MahjongSeat canOperationSeat : canOperationSeats){
                List<OperationDTO> operationList = new ArrayList<>();
                List<StepAction> canOperations = canOperationSeat.getCanOperations();
                canOperations.stream().forEach(operation -> {
                    OperationDTO operationDTO = new OperationDTO(operation.getOperationType().value(),card);
                    operationList.add(operationDTO);
                });
                if(operationList.size() > 0){
                    OperationDTO cancelOperationDTO = new OperationDTO(XueZhanMahjongOperationEnum.CANCEL.value(),card);
                    operationList.add(cancelOperationDTO);
                }

                OperationNoticeResponse noticeResponse = new OperationNoticeResponse(canOperationSeat.getPosId(),operationList);
                roomManager.pushToUser(XueZhanPushCommandCode.OPERATION_NOTICE,canOperationSeat.getUserId(),noticeResponse,roomId);
            }
        }else{
            /**
             * 没有玩家可以操作，下一个玩家摸牌
             */
            if(gameZone.gameover()){
                settlement();
                return;
            }
            Integer needTookCardPosId = gameZone.refreshTookPlayer();
            OperationCardStep tookCardStep = gameZone.tookCardStep(needTookCardPosId);
            TookCardNoticeResponse tookCardNoticeResponse = new TookCardNoticeResponse(needTookCardPosId);
            tookCardNoticeResponse.setPosId(needTookCardPosId);

            XueZhanSeat needTookCardSeat = posIdSeatMap.get(needTookCardPosId);
            Long userId = needTookCardSeat.getUserId();
            pushToRoomUser(XueZhanPushCommandCode.TOOK_CARD_NOTICE,tookCardNoticeResponse,userId);

            Integer tookCard = tookCardStep.getAction().getTargetCard();
            //因为在pushToRoomUser方法中，需要推送的数据已经被序列化了，所以这里修改相同的response不会影响之前的推送
            tookCardNoticeResponse.setCard(tookCard);
            xueZhanSeat.getMahjongSeat().addOperation(XueZhanMahjongOperationEnum.OUT_CARD);
            roomManager.pushToUser(XueZhanPushCommandCode.TOOK_CARD_NOTICE,userId,tookCardNoticeResponse,roomId);

            gameZone.whatCanYouDo(tookCard);
        }

    }

    @Override
    public void hu(Integer card, Integer posId) {

    }

    @Override
    public void cancel(Integer card,Integer posId) {
        log.info("请求过 roomId={} 方位=[{},posId={}]",roomId,getSeatDirection(posId),posId);
        XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
        boolean canCancel = xueZhanSeat.canOperation(XueZhanMahjongOperationEnum.CANCEL);
        if(!canCancel){
            log.warn("玩家没有 过 的操作权限：roomId={} userId={} 方位=[{},posId={}]",roomId,xueZhanSeat.getUserId(),getSeatDirection(posId),posId);
        }
        //过操作不用通知玩家，要不要响应给本人呢
        //接下来要判断其他用户可不可以操作，可能会涉及到还原操作、通知下一个玩家摸牌
        GameStepModel<OperationCardStep> cancelStep = gameZone.cancel(card,posId);

    }

    @Override
    public void gang(Integer card, Integer type, Integer posId) {
        log.info("请求杠操作： roomId={} 方位=[{},posId={}] type={} card={} posId={}",roomId,getSeatDirection(posId),posId,type,posId);
    }

    @Override
    public void peng(Integer card, Integer posId) {
        log.info("请求碰操作： roomId={} 方位=[{},posId={}] card={} posId={}",roomId,getSeatDirection(posId),posId,posId);
        gameZone.peng(card,posId);
    }

    @Override
    public void exchangeCard(List<Integer> cards, Integer posId) {
        XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
        if (!rule.getRuleConfig().getCanHsz()) {
            log.error("当前游戏不支持换三张： roomId={} zoneId={} userId={} 方位=[{},posId={}]", roomId, gameZone.getZoneId(), xueZhanSeat.getUserId(), getSeatDirection(posId), posId);
            return;
        }
        if (!gameZone.checkCurrentGameStatus(SichuanGameStatusEnum.EXCHANGE_CARD)) {
            log.error("当前游戏阶段不是换三张：roomId={} zoneId={} userId={} 方位=[{},posId={}]", roomId, gameZone.getZoneId(), xueZhanSeat.getUserId(), getSeatDirection(posId), posId);
            return;
        }

        MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();
        if (!mahjongSeat.canOperation(XueZhanMahjongOperationEnum.EXCHANGE_CARD)) {
            log.warn("该玩家没有进行换牌的权限： roomId={} zoneId={} 方位=[{},posId={}] 要交换的牌：{}", roomId, gameZone.getZoneId(), getSeatDirection(posId), posId, cards);
            return;
        }
        boolean isFinishExchange = gameZone.exchangeCard(cards, xueZhanSeat);
        mahjongSeat.removeStatus(SeatStatusEnum.EXCHANGE_CARD.status());
        mahjongSeat.clearOperation();
        noticePlayersWhoFinishChangeCard(xueZhanSeat);
        if (isFinishExchange) {
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
        if (!gameZone.checkCurrentGameStatus(SichuanGameStatusEnum.DING_QUE)) {
            log.error("当前游戏阶段不是定缺：roomId={} zoneId={} userId={} 方位=[{},posId={}]", roomId, gameZone.getZoneId(), xueZhanSeat.getUserId(), getSeatDirection(posId), posId);
            return;
        }

        MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();
        if (!mahjongSeat.canOperation(XueZhanMahjongOperationEnum.DING_QUE)) {
            log.warn("该玩家没有进行定缺的权限： roomId={} zoneId={} 方位=[{},posId={}] 定缺的花色：{}", roomId, gameZone.getZoneId(), getSeatDirection(posId), posId, color);
            return;
        }
        final boolean isFinishDingQue = gameZone.dingQue(color, posIdSeatMap.get(posId));
        mahjongSeat.clearOperation();
        mahjongSeat.removeStatus(SeatStatusEnum.DING_QUE.status());
        noticePlayersWhoFinishDingQue(xueZhanSeat);
        if (isFinishDingQue) {
            FormalProcessNoticeResponse response = new FormalProcessNoticeResponse(gameZone.getBankerPosId(),10);
            pushToRoomUser(XueZhanPushCommandCode.FORMAL_PROCESS_NOTICE,response);
            noticePlayersOperation();
        }

    }

    private void settlement(){

    }

    private void noticePlayersDealCardsResult() {
        List<GameStepModel> history = gameZone.getHistoryList();

        for (GameStepModel gameStepModel : history) {
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
            mahjongSeat.addStatus(SeatStatusEnum.EXCHANGE_CARD.status());

            response.setRoomId(gameStartStep.getRoomId())
                    .setZoneId(gameStartStep.getZoneId())
                    .setStep(gameStartStep.getStep())
                    .setBankerPosId(gameStartStep.getBankerPosId())
                    .setDiceList(gameStartStep.getDiceList())
                    .setPosId(gameStartStep.getPosId())
                    .setStandCardList(gameStartStep.getStandCards())
                    //发给客户端的游戏状态不应该是记录step时的状态，而是下一个阶段。斗地主那里可能没处理好
                    .setGameStatus(gameZone.getGameStatus().status());

            roomManager.pushToUser(PushCommandCode.GAME_START, gameStepModel.getPlayers().getUserId(), response, roomId);
        }
    }

    private void noticePlayersExchangeResult() {
        List<Step> historyByGameStatus = gameZone.getHistoryByGameStatus(SichuanGameStatusEnum.EXCHANGE_CARD);
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
            mahjongSeat.addStatus(SeatStatusEnum.DING_QUE.status());

            Integer color = SichuanPlayHelper.recommendedDingQue(mahjongSeat.getStandCardList());
            RecommendDingQueResponse response = new RecommendDingQueResponse(color);
            roomManager.pushToUser(XueZhanPushCommandCode.RECOMMEND_DING_QUE, mahjongSeat.getUserId(), response, roomId);
        }
    }

    private void noticePlayersWhoFinishDingQue(XueZhanSeat seat){
        OperationResultResponse response = new OperationResultResponse();
        response.setPosId(seat.getPosId())
                .setTargetCard(seat.getSichuanMahjongSeat().getQueColor())
                .setOperationType(XueZhanMahjongOperationEnum.DING_QUE.value());
        pushToRoomUser(XueZhanPushCommandCode.OPERATION_RESULT_NOTICE, response);
    }

    private void noticePlayersOperation() {
        XueZhanSeat seat = posIdSeatMap.get(gameZone.getCurTookCardPlayerPosId());
        MahjongSeat mahjongSeat = seat.getMahjongSeat();
        mahjongSeat.addStatus(SeatStatusEnum.OPERATION_CARD.status());

        List<StepAction> stepActions = gameZone.whatCanYouDo(null);
        List<OperationDTO> canOperations = new ArrayList<>();
        OperationDTO outCardOperation = new OperationDTO(XueZhanMahjongOperationEnum.OUT_CARD.value(),null);
        canOperations.add(outCardOperation);
        mahjongSeat.addOperation(XueZhanMahjongOperationEnum.OUT_CARD);

        stepActions.stream().forEach(stepAction -> {
            OperationDTO operationDTO = new OperationDTO(stepAction.getOperationType().value(),stepAction.getTargetCard());
            canOperations.add(operationDTO);
            mahjongSeat.addOperation(stepAction.getOperationType());
        });
        OperationNoticeResponse response = new OperationNoticeResponse(gameZone.getBankerPosId(),canOperations);

        roomManager.pushToUser(XueZhanPushCommandCode.OPERATION_NOTICE,seat.getUserId(),response,roomId);
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
        FanInfo<BaseHuTypeEnum> pingHu = new FanInfo<>(BaseHuTypeEnum.平胡, 1, 0);
        FanInfo<BaseHuTypeEnum> qiDui = new FanInfo<>(BaseHuTypeEnum.七对, 4, 0);
        baseHuList.add(pingHu);
        baseHuList.add(qiDui);

        final List<FanInfo<HuTypeEnum>> huTypeList = new ArrayList<>();
        FanInfo<HuTypeEnum> ziMo = new FanInfo<>(HuTypeEnum.自摸, 2, 0);
        FanInfo<HuTypeEnum> dianPaoHu = new FanInfo<>(HuTypeEnum.点炮胡, 1, 0);
        huTypeList.add(ziMo);
        huTypeList.add(dianPaoHu);

        final List<FanInfo<FormalFanTypeEnum>> formalFanTypeEnumList = new ArrayList<>();
        FanInfo<FormalFanTypeEnum> qingYiSe = new FanInfo<>(FormalFanTypeEnum.清一色, 4, 0);
        FanInfo<FormalFanTypeEnum> pengPengHu = new FanInfo<>(FormalFanTypeEnum.大对子, 2, 0);
        FanInfo<FormalFanTypeEnum> jinGouDiao = new FanInfo<>(FormalFanTypeEnum.金钩钓, 4, 0);
        FanInfo<FormalFanTypeEnum> shiBaLuoHan = new FanInfo<>(FormalFanTypeEnum.十八罗汉, 64, 0);

        FanInfo<FormalFanTypeEnum> qingQiDui = new FanInfo<>(FormalFanTypeEnum.清七对, 4, 0);
        FanInfo<FormalFanTypeEnum> longQiDui = new FanInfo<>(FormalFanTypeEnum.龙七对, 2, 0);

        formalFanTypeEnumList.add(qingYiSe);
        formalFanTypeEnumList.add(pengPengHu);
        formalFanTypeEnumList.add(jinGouDiao);
        formalFanTypeEnumList.add(shiBaLuoHan);
        formalFanTypeEnumList.add(qingQiDui);
        formalFanTypeEnumList.add(longQiDui);

        final List<FanInfo<AppendedTypeEnum>> appendedTypeEnumList = new ArrayList<>();
        FanInfo<AppendedTypeEnum> gen = new FanInfo<>(AppendedTypeEnum.根, 2, 1);

        FanInfo<AppendedTypeEnum> tianHu = new FanInfo<>(AppendedTypeEnum.天胡, 256, 1);
        FanInfo<AppendedTypeEnum> diHu = new FanInfo<>(AppendedTypeEnum.地胡, 256, 1);
        FanInfo<AppendedTypeEnum> haiDiLaoYue = new FanInfo<>(AppendedTypeEnum.海底捞月, 2, 1);
        FanInfo<AppendedTypeEnum> haiDiPao = new FanInfo<>(AppendedTypeEnum.海底炮, 2, 1);
        FanInfo<AppendedTypeEnum> qiangGangHu = new FanInfo<>(AppendedTypeEnum.抢杠胡, 2, 1);
        FanInfo<AppendedTypeEnum> gangShangKaiHua = new FanInfo<>(AppendedTypeEnum.杠上开花, 2, 1);
        FanInfo<AppendedTypeEnum> gangShangPao = new FanInfo<>(AppendedTypeEnum.杠上炮, 2, 1);
        appendedTypeEnumList.add(gen);
        appendedTypeEnumList.add(tianHu);
        appendedTypeEnumList.add(diHu);
        appendedTypeEnumList.add(haiDiLaoYue);
        appendedTypeEnumList.add(haiDiPao);
        appendedTypeEnumList.add(qiangGangHu);
        appendedTypeEnumList.add(gangShangKaiHua);
        appendedTypeEnumList.add(gangShangPao);

        final List<FanInfo<CompoundFanTypeEnum>> compoundFanTypeEnumList = new ArrayList<>();
        FanInfo<CompoundFanTypeEnum> qingPeng = new FanInfo<>(CompoundFanTypeEnum.清碰, 8, 0);
        FanInfo<CompoundFanTypeEnum> qingJinGouDiao = new FanInfo<>(CompoundFanTypeEnum.清金钩钓, 16, 0);
        FanInfo<CompoundFanTypeEnum> qingShaiBaLuoHan = new FanInfo<>(CompoundFanTypeEnum.清十八罗汉, 256, 0);
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
        Integer zhuangId = gameZone.getBankerPosId();
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
