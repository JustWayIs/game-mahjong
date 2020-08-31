package com.yude.game.xuezhan.domain;


import com.yude.game.common.contant.PushCommandCode;
import com.yude.game.common.manager.IPushManager;
import com.yude.game.common.manager.IRoomManager;
import com.yude.game.common.model.AbstractRoomModel;
import com.yude.game.common.model.MahjongRoom;
import com.yude.game.common.model.MahjongSeat;
import com.yude.game.common.model.Player;
import com.yude.game.common.model.fan.*;
import com.yude.game.common.model.history.GameStartStep;
import com.yude.game.common.model.history.GameStepModel;
import com.yude.game.common.model.history.Step;
import com.yude.game.common.model.sichuan.SichuanGameStatusEnum;
import com.yude.game.common.model.sichuan.SichuanMahjongRoom;
import com.yude.game.common.model.sichuan.SichuanPlayHelper;
import com.yude.game.common.model.sichuan.SichuanRoomConfig;
import com.yude.game.common.model.sichuan.history.ExchangeCardStep;
import com.yude.game.common.model.sichuan.history.SichuanGameStartStep;
import com.yude.game.common.timeout.MahjongTimeoutTaskPool;
import com.yude.game.xuezhan.application.response.ExchangeCardResultResponse;
import com.yude.game.xuezhan.application.response.RecommendDingQueResponse;
import com.yude.game.xuezhan.application.response.XueZhanStartResponse;
import com.yude.game.xuezhan.constant.XueZhanMahjongOperationEnum;
import com.yude.game.xuezhan.constant.XueZhanPushCommandCode;
import com.yude.game.xuezhan.domain.action.XueZhanAction;
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
        for(int i = 0 ; i < seatModels.length ; i++){
            seatModels[i] = posIdSeatMap.get(i);
        }

        return new XueZhanZone(seatModels, gameRound, gameInning);
    }

    @Override
    public void startGame() {
        gameZone = getPracticalGameZoneModel();
        log.debug("血战到底 游戏开始 roomId={}  gameId={}",roomId,gameZone.getZoneId());
        gameZone.init();
        gameZone.deal(roomId);
        noticePlayersDealCardsResult();
        //noticePlayersChangeCard();
    }



    @Override
    public void outCard(Integer card, Integer posId) {

    }

    @Override
    public void hu(Integer card, Integer posId) {

    }

    @Override
    public void cancel() {

    }

    @Override
    public void gang(Integer card, Integer type, Integer posId) {

    }

    @Override
    public void peng(Integer card, Integer posId) {

    }

    @Override
    public void dingQue(Integer color, Integer posId) {
        XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
        if(!rule.getRuleConfig().getCanDingQue()){
            log.error("当前游戏不支持定缺: roomId={} zoneId={} posId={}",roomId,gameZone.getZoneId(),posId);
            return;
        }
        if(!gameZone.checkCurrentGameStatus(SichuanGameStatusEnum.DING_QUE)){
            log.error("当前游戏阶段不是定缺：roomId={} zoneId={} userId={} 方位：[{},posId={}]",roomId,gameZone.getZoneId(),xueZhanSeat.getUserId(),getSeatDirection(posId),posId);
            return;
        }

        MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();
        if(!mahjongSeat.canOperation(XueZhanMahjongOperationEnum.DING_QUE)){
            log.warn("该玩家没有进行定缺的权限： roomId={} zoneId={} 方位：[{},posId={}] 定缺的花色：{}",roomId,gameZone.getZoneId(),getSeatDirection(posId),posId,color);
            return;
        }
        final boolean isFinishDingQue = gameZone.dingQue(color, posIdSeatMap.get(posId));
        mahjongSeat.clearOperation();
        if(isFinishDingQue){

            noticePlayersOperation();

        }

    }

    @Override
    public void exchangeCard(List<Integer> cards, Integer posId) {
        XueZhanSeat xueZhanSeat = posIdSeatMap.get(posId);
        if(!rule.getRuleConfig().getCanHsz()){
            log.error("当前游戏不支持换三张： roomId={} zoneId={} userId={} 方位:[{},posId={}]",roomId, gameZone.getZoneId(),xueZhanSeat.getUserId(),getSeatDirection(posId),posId);
            return;
        }
        if(!gameZone.checkCurrentGameStatus(SichuanGameStatusEnum.EXCHANGE_CARD)){
            log.error("当前游戏阶段不是换三张：roomId={} zoneId={} userId={} 方位：[{},posId={}]",roomId,gameZone.getZoneId(),xueZhanSeat.getUserId(),getSeatDirection(posId),posId);
            return;
        }

        MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();
        if(!mahjongSeat.canOperation(XueZhanMahjongOperationEnum.EXCHANGE_CARD)){
            log.warn("该玩家没有进行换牌的权限： roomId={} zoneId={} 方位：[{},posId={}] 要交换的牌：{}",roomId,gameZone.getZoneId(),getSeatDirection(posId),posId,cards);
            return;
        }
        boolean isFinishExchange = gameZone.exchangeCard(cards, xueZhanSeat);
        mahjongSeat.clearOperation();
        if(isFinishExchange){
            noticePlayersExchangeResult();
            if(rule.getRuleConfig().getCanDingQue()){
                noticePlayersDingQue();
            }
        }
    }

    private void noticePlayersDealCardsResult(){
        List<GameStepModel> history = gameZone.getHistoryList();

        for(GameStepModel gameStepModel : history){
            GameStartStep gameStartStep = (GameStartStep) gameStepModel.getOperationStep();

            XueZhanStartResponse response = new XueZhanStartResponse();
            if(rule.getRuleConfig().getCanHsz()){
                List<Integer> recommendedCards = SichuanPlayHelper.recommendedChangeCard(gameStartStep.getStandCards(), 3);
                recommendedCards = recommendedCards.subList(0,3);
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

            response.setRoomId(gameStartStep.getRoomId())
                    .setZoneId(gameStartStep.getZoneId())
                    .setStep(gameStartStep.getStep())
                    .setBankerPosId(gameStartStep.getBankerPosId())
                    .setDiceList(gameStartStep.getDiceList())
                    .setPosId(gameStartStep.getPosId())
                    .setStandCardList(gameStartStep.getStandCards())
                    //发给客户端的游戏状态不应该是记录step时的状态，而是下一个阶段。斗地主那里可能没处理好
                    .setGameStatus(gameZone.getGameStatus().status());

            roomManager.pushToUser(PushCommandCode.GAME_START,gameStepModel.getPlayers().getUserId(),response,roomId);
        }
    }

    private void noticePlayersExchangeResult(){
        List<Step> historyByGameStatus = gameZone.getHistoryByGameStatus(SichuanGameStatusEnum.EXCHANGE_CARD);
        for(Step stepInfo : historyByGameStatus){
            ExchangeCardStep step = (ExchangeCardStep) stepInfo;
            ExchangeCardResultResponse response = new ExchangeCardResultResponse(step.getExchangeType().type(),step.getGainedCards());
            roomManager.pushToUser(XueZhanPushCommandCode.EXCHANGE_RESULT,posIdSeatMap.get(step.getPosId()).getUserId(),response,roomId);
        }
    }

    private void noticePlayersChangeCard(){
        if(!rule.getRuleConfig().getCanHsz()){
            log.error("当前游戏不支持换三张： roomId={} zoneId={}",roomId, gameZone.getZoneId());
            return;
        }
        List<Integer> canOperations = new ArrayList<>();
        canOperations.add(XueZhanMahjongOperationEnum.EXCHANGE_CARD.value());

        for(XueZhanSeat seat : posIdSeatMap.values()){
            MahjongSeat mahjongSeat = seat.getMahjongSeat();
            mahjongSeat.addOperation(XueZhanMahjongOperationEnum.EXCHANGE_CARD);
            mahjongSeat.addChangce();

            /*换三张的话直接发推荐牌好了，前面游戏开始下发的数据里，游戏状态已经改为换三张了
            OperationPermissionsResponse response = new OperationPermissionsResponse(mahjongSeat.getPosId(),canOperations);
            roomManager.pushToUser(PushCommandCode.CAN_OPERATION,mahjongSeat.getUserId(),response,roomId);*/
        }
    }

    private void noticePlayersDingQue(){
        for(XueZhanSeat seat : posIdSeatMap.values()){
            MahjongSeat mahjongSeat = seat.getMahjongSeat();
            mahjongSeat.addOperation(XueZhanMahjongOperationEnum.DING_QUE);
            mahjongSeat.addChangce();
            Integer color = SichuanPlayHelper.recommendedDingQue(mahjongSeat.getStandCardList());
            RecommendDingQueResponse response = new RecommendDingQueResponse(color);
            roomManager.pushToUser(XueZhanPushCommandCode.RECOMMEND_DING_QUE,mahjongSeat.getUserId(),response,roomId);
        }
    }

    private void noticePlayersOperation(){
       //List<Integer> curOperatorCanDoList = gameZone.whatCanIdo();
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

    private void ruleInit(){
        //H2 拉取房间配置信息,把配置传进去
        final List<Integer> dingQueOption = new ArrayList<>(Arrays.asList(1,2,3));

        rule = new Rule<>();
        SichuanRoomConfig ruleConfig = new SichuanRoomConfig(true, true,dingQueOption);
        final List<FanInfo<BaseHuTypeEnum>> baseHuList = new ArrayList<>();
        FanInfo<BaseHuTypeEnum> pingHu = new FanInfo<>(BaseHuTypeEnum.平胡,1,0);
        FanInfo<BaseHuTypeEnum> qiDui = new FanInfo<>(BaseHuTypeEnum.七对,4,0);
        baseHuList.add(pingHu);
        baseHuList.add(qiDui);

        final List<FanInfo<HuTypeEnum>> huTypeList = new ArrayList<>();
        FanInfo<HuTypeEnum> ziMo = new FanInfo<>(HuTypeEnum.自摸,2,0);
        FanInfo<HuTypeEnum> dianPaoHu = new FanInfo<>(HuTypeEnum.点炮胡,1,0);
        huTypeList.add(ziMo);
        huTypeList.add(dianPaoHu);

        final List<FanInfo<FormalFanTypeEnum>> formalFanTypeEnumList = new ArrayList<>();
        FanInfo<FormalFanTypeEnum> qingYiSe = new FanInfo<>(FormalFanTypeEnum.清一色,4,0);
        FanInfo<FormalFanTypeEnum> pengPengHu = new FanInfo<>(FormalFanTypeEnum.大对子,2,0);
        FanInfo<FormalFanTypeEnum> jinGouDiao = new FanInfo<>(FormalFanTypeEnum.金钩钓,4,0);
        FanInfo<FormalFanTypeEnum> shiBaLuoHan = new FanInfo<>(FormalFanTypeEnum.十八罗汉,64,0);

        FanInfo<FormalFanTypeEnum> qingQiDui = new FanInfo<>(FormalFanTypeEnum.清七对,4,0);
        FanInfo<FormalFanTypeEnum> longQiDui = new FanInfo<>(FormalFanTypeEnum.龙七对,2,0);

        formalFanTypeEnumList.add(qingYiSe);
        formalFanTypeEnumList.add(pengPengHu);
        formalFanTypeEnumList.add(jinGouDiao);
        formalFanTypeEnumList.add(shiBaLuoHan);
        formalFanTypeEnumList.add(qingQiDui);
        formalFanTypeEnumList.add(longQiDui);

        final List<FanInfo<AppendedTypeEnum>> appendedTypeEnumList = new ArrayList<>();
        FanInfo<AppendedTypeEnum> gen = new FanInfo<>(AppendedTypeEnum.根,2,1);

        FanInfo<AppendedTypeEnum> tianHu = new FanInfo<>(AppendedTypeEnum.天胡,256,1);
        FanInfo<AppendedTypeEnum> diHu = new FanInfo<>(AppendedTypeEnum.地胡,256,1);
        FanInfo<AppendedTypeEnum> haiDiLaoYue = new FanInfo<>(AppendedTypeEnum.海底捞月,2,1);
        FanInfo<AppendedTypeEnum> haiDiPao = new FanInfo<>(AppendedTypeEnum.海底炮,2,1);
        FanInfo<AppendedTypeEnum> qiangGangHu = new FanInfo<>(AppendedTypeEnum.抢杠胡,2,1);
        FanInfo<AppendedTypeEnum> gangShangKaiHua = new FanInfo<>(AppendedTypeEnum.杠上开花,2,1);
        FanInfo<AppendedTypeEnum> gangShangPao = new FanInfo<>(AppendedTypeEnum.杠上炮,2,1);
        appendedTypeEnumList.add(gen);
        appendedTypeEnumList.add(tianHu);
        appendedTypeEnumList.add(diHu);
        appendedTypeEnumList.add(haiDiLaoYue);
        appendedTypeEnumList.add(haiDiPao);
        appendedTypeEnumList.add(qiangGangHu);
        appendedTypeEnumList.add(gangShangKaiHua);
        appendedTypeEnumList.add(gangShangPao);

        final List<FanInfo<CompoundFanTypeEnum>> compoundFanTypeEnumList = new ArrayList<>();
        FanInfo<CompoundFanTypeEnum> qingPeng = new FanInfo<>(CompoundFanTypeEnum.清碰,8,0);
        FanInfo<CompoundFanTypeEnum> qingJinGouDiao = new FanInfo<>(CompoundFanTypeEnum.清金钩钓,16,0);
        FanInfo<CompoundFanTypeEnum> qingShaiBaLuoHan = new FanInfo<>(CompoundFanTypeEnum.清十八罗汉,256,0);
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
