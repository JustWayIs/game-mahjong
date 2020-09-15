package com.yude.game.common.model.sichuan;

import com.yude.game.common.contant.MahjongStatusCodeEnum;
import com.yude.game.common.contant.OperationEnum;
import com.yude.game.common.mahjong.PlayerHand;
import com.yude.game.common.mahjong.Solution;
import com.yude.game.common.mahjong.Tile;
import com.yude.game.common.model.*;
import com.yude.game.common.model.fan.Rule;
import com.yude.game.common.model.fan.*;
import com.yude.game.common.model.fan.param.AppendedFanParam;
import com.yude.game.common.model.fan.param.CompoundFanParam;
import com.yude.game.common.model.fan.param.FormalFanParam;
import com.yude.game.common.model.history.*;
import com.yude.game.common.model.sichuan.constant.ExchangeTypeEnum;
import com.yude.game.common.model.sichuan.constant.SeatStatusEnum;
import com.yude.game.common.model.sichuan.constant.SichuanGameStatusEnum;
import com.yude.game.common.model.sichuan.history.*;
import com.yude.game.common.model.sichuan.history.info.ChaHuaZhuInfo;
import com.yude.game.common.model.sichuan.history.info.ChaJiaoInfo;
import com.yude.game.common.model.sichuan.history.info.RebateInfo;
import com.yude.game.exception.BizException;
import com.yude.game.exception.SystemException;
import com.yude.protocol.common.constant.StatusCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: HH
 * @Date: 2020/8/4 14:55
 * @Version: 1.0
 * @Declare:
 */
public class SichuanMahjongZone extends AbstractGameZoneModel<SichuanMahjongSeat, SichuanGameStatusEnum> {
    private static final Logger log = LoggerFactory.getLogger(SichuanMahjongZone.class);

    private ExchangeTypeEnum exchangeType;
    private MahjongZone mahjongZone;


    public SichuanMahjongZone(SichuanMahjongSeat[] playerSeats, int round, int inning) {
        super(playerSeats, round, inning);
    }

    @Override
    public void init() {

    }

    @Override
    public void clean() {

    }

    public void updateGameStatus() {

    }

    public boolean dingQue(Integer color, SichuanMahjongSeat seat, List<GameStepModel> historyList) {
        seat.setQueColor(color);

        DingQueStep step = new DingQueStep();
        List<Integer> standCardList = new ArrayList<>(seat.getMahjongSeat().getStandCardList());
        step.setStep(mahjongZone.getStepCount())
                .setPosId(seat.getMahjongSeat().getPosId())
                .setColor(color)
                .setGameStatus(mahjongZone.getGameStatus())
                .setHandCards(standCardList)
                .setHandCardConvertList(MahjongProp.cardConvertName(standCardList));
        GameStepModel<DingQueStep> gameStepModel = new GameStepModel<>(zoneId, seat.getPlayer(), step);
        historyList.add(gameStepModel);
        mahjongZone.stepAdd();

        boolean isFinishDingQue = true;
        for (SichuanMahjongSeat sichuanMahjongSeat : playerSeats) {
            if (sichuanMahjongSeat.getQueColor() == null) {
                isFinishDingQue = false;
            }
        }
        if (isFinishDingQue) {

            for (SichuanMahjongSeat sichuanMahjongSeat : playerSeats) {
                //庄家进行理牌
                //XueZhanSeat xueZhanSeat = playerSeats[mahjongZone.getBankerPosId()];
                /**
                 * 由于庄家出完牌，其他玩家得通过理牌的数据来判断是否能 碰杠胡，所以也得给其他玩家理牌
                 */
                MahjongSeat mahjongSeat = sichuanMahjongSeat.getMahjongSeat();
                PlayerHand playerHand = mahjongSeat.getPlayerHand();
                playerHand.bannedSuit = sichuanMahjongSeat.getQueColor();
                mahjongSeat.solution();
            }


            mahjongZone.setGameStatus(SichuanGameStatusEnum.OPERATION_CARD);
            //初始化当前操作人为庄家
            mahjongZone.initCurrentOperator();

        }

        return isFinishDingQue;
    }


    public boolean exchangeCard(List<Integer> cards, SichuanMahjongSeat seat, List<GameStepModel> historyList) {
        seat.setDiscardCards(cards);
        List<Integer> standCardList = seat.getMahjongSeat().getStandCardList();
        List<Integer> copyStandCardList = new ArrayList<>(standCardList);
        for (Integer card : cards) {
            boolean remove = copyStandCardList.remove(card);
            if (!remove) {
                //怎么记录roomId
                log.warn("不能交换不存在的牌 立牌：{}  要给出去的牌：{}", standCardList, copyStandCardList);
                throw new BizException(MahjongStatusCodeEnum.EXCHANGE_ERROR);
            }
        }
        standCardList.clear();
        standCardList.addAll(copyStandCardList);
        //buildDiscardStep(seat,historyList,cards);

        boolean isFinishExchange = true;
        for (SichuanMahjongSeat sichuanMahjongSeat : playerSeats) {
            if (!sichuanMahjongSeat.alredyDiscardCard()) {
                isFinishExchange = false;
            }
        }
        ExchangeCardStep exchangeCardStep = new ExchangeCardStep();
        MahjongSeat mahjongSeat = seat.getMahjongSeat();
        List<Integer> stepStandList = new ArrayList<>(mahjongSeat.getStandCardList());
        exchangeCardStep.setStep(mahjongZone.getStepCount())
                .setPosId(mahjongSeat.getPosId())
                .setDiscardCards(cards)
                .setGameStatus(mahjongZone.getGameStatus())
                .setStandCards(stepStandList);
        GameStepModel<ExchangeCardStep> gameStepModel = new GameStepModel<>(zoneId, mahjongSeat.getPlayer(), exchangeCardStep);
        historyList.add(gameStepModel);

        mahjongZone.stepAdd();
        return isFinishExchange;
    }

    public void executeExchange(List<Step> exechangeHistory) {
        Random random = new Random();
        ExchangeTypeEnum[] values = ExchangeTypeEnum.values();
        int i = random.nextInt(values.length);
        exchangeType = values[i];
        for (SichuanMahjongSeat sichuanMahjongSeat : playerSeats) {
            MahjongSeat curMahjongSeat = sichuanMahjongSeat.getMahjongSeat();
            int posId = curMahjongSeat.getPosId();
            switch (exchangeType) {
                case CLOCKWISE:

                    posId--;
                    if (posId < 0) {
                        posId = 3;
                    }
                    List<Integer> discardCards = playerSeats[posId].getDiscardCards();
                    sichuanMahjongSeat.setGainedCards(new ArrayList<>(discardCards));
                    sichuanMahjongSeat.getMahjongSeat().getStandCardList().addAll(discardCards);
                    break;
                case ANTICLOCKWISE:
                    posId = (posId + 1) % 4;
                    discardCards = playerSeats[posId].getDiscardCards();
                    sichuanMahjongSeat.setGainedCards(new ArrayList<>(discardCards));
                    sichuanMahjongSeat.getMahjongSeat().getStandCardList().addAll(discardCards);
                    break;
                case FACE_TO_FACE:
                    posId = (posId + 2) % 4;
                    discardCards = playerSeats[posId].getDiscardCards();
                    sichuanMahjongSeat.setGainedCards(new ArrayList<>(discardCards));
                    sichuanMahjongSeat.getMahjongSeat().getStandCardList().addAll(discardCards);
                    break;
                default:
            }
        }

        /**
         * 补充ExchangeStep中未赋值的属性
         */
        for (Step step : exechangeHistory) {
            ExchangeCardStep curExchangeCardStep = (ExchangeCardStep) step;
            //换牌后重新排序
            SichuanMahjongSeat playerSeat = playerSeats[curExchangeCardStep.getPosId()];
            Collections.sort(playerSeat.getMahjongSeat().getStandCardList());

            List<Integer> standCardList = playerSeats[curExchangeCardStep.getPosId()].getMahjongSeat().getStandCardList();
            curExchangeCardStep.setStandCards(new ArrayList<>(standCardList))
                    .setExchangeType(exchangeType)
                    .setGainedCards(playerSeat.getGainedCards())
                    .setStandCardConvertList(MahjongProp.cardConvertName(standCardList))
                    .setGameStatus(mahjongZone.getGameStatus());

        }
        mahjongZone.setGameStatus(SichuanGameStatusEnum.DING_QUE);
    }

    /**
     * 抓牌的人能做什么
     *
     * @param card
     * @return
     */
    public List<StepAction> whatCanYouDo(final Integer card,SichuanRoomConfig roomConfig) {
        Integer curObtainCardPosId = mahjongZone.getCurTookCardPlayerPosId();
        SichuanMahjongSeat playerSeat = playerSeats[curObtainCardPosId];
        MahjongSeat mahjongSeat = playerSeat.getMahjongSeat();

        List<StepAction> stepActions = new ArrayList<>();
        PlayerHand playerHand = mahjongSeat.getPlayerHand();
        if(mahjongZone.cardWallHasCard() || !roomConfig.isLastCardProhibitGang()){
            playerHand.canAnGang(stepActions, card);
            for (StepAction stepAction : stepActions) {
                stepAction.setCardSource(mahjongSeat.getPosId())
                        .setOperationType(OperationEnum.AN_GANG);
            }
            boolean canBuGang = playerHand.canBuGang(card);
            if (canBuGang) {
                StepAction stepAction = new StepAction();
                stepAction.setTargetCard(card)
                        .setCardSource(mahjongSeat.getPosId())
                        .setOperationType(OperationEnum.BU_GANG);
                stepActions.add(stepAction);
            }
        }

        List<Solution> solutions = playerHand.canHu(card, true);
        if (solutions.size() > 0) {
            StepAction stepAction = new StepAction();
            stepAction.setTargetCard(card)
                    .setCardSource(mahjongSeat.getPosId())
                    .setOperationType(OperationEnum.HU);
            stepActions.add(stepAction);
        }
        /*canAnGang(mahjongSeat, stepActions);
        canBuGang(mahjongSeat,card);
        canHu(mahjongSeat, card, true);*/


        return stepActions;
    }

    /**
     * @param outCardSet
     * @param card
     * @param roomConfig
     * @return
     */
    public List<MahjongSeat> otherPalyerCanDo(SichuanMahjongSeat outCardSet, Integer card, SichuanRoomConfig roomConfig) {
        List<MahjongSeat> canOperationSeats = new ArrayList<>();
        for (SichuanMahjongSeat seat : playerSeats) {
            if (outCardSet.equals(seat)) {
                continue;
            }

            //能否胡多次 -- 血流不知道可以不可以直杠
            boolean canHus = roomConfig.isCanHus();
            if (seat.isAlreadyHu() && !canHus) {
                continue;
            }

            MahjongSeat mahjongSeat = seat.getMahjongSeat();
            PlayerHand playerHand = mahjongSeat.getPlayerHand();
            boolean canPeng = playerHand.canPeng(card);
            if (canPeng) {
                StepAction stepAction = new StepAction();
                stepAction.setTargetCard(card)
                        .setCardSource(outCardSet.getPosId())
                        .setOperationType(OperationEnum.PENG);
                mahjongSeat.addOperation(stepAction);
               /* StepAction stepAction = new StepAction();
                stepAction.setCardSource(outCardSet.getPosId())
                        .setTargetCard(card)
                        .setOperationType(XueZhanMahjongOperationEnum.PENG)
                        .setCombinationRsult(Arrays.asList(card,card,card));
                stepActions.add(stepAction);*/
            }

            boolean canZhiGang = false;
            if(mahjongZone.cardWallHasCard() || !roomConfig.isLastCardProhibitGang()){
                canZhiGang = playerHand.canZhiGang(card);
                if (canZhiGang) {
                    StepAction stepAction = new StepAction();
                    stepAction.setTargetCard(card)
                            .setCardSource(outCardSet.getPosId())
                            .setOperationType(OperationEnum.ZHI_GANG);
                    mahjongSeat.addOperation(stepAction);
                /*StepAction stepAction = new StepAction();
                stepAction.setCardSource(outCardSet.getPosId())
                            .setTargetCard(card)
                            .setOperationType(XueZhanMahjongOperationEnum.ZHI_GANG)
                            .setCombinationRsult(Arrays.asList(card,card,card,card));*/
                }
            }

            List<Solution> solutions = playerHand.canHu(card, false);
            if (solutions.size() > 0) {
                StepAction stepAction = new StepAction();
                stepAction.setTargetCard(card)
                        .setCardSource(outCardSet.getPosId())
                        .setOperationType(OperationEnum.HU);
                mahjongSeat.addOperation(stepAction);
            }

            if (canPeng || canZhiGang || solutions.size() > 0) {
                StepAction stepAction = new StepAction();
                stepAction.setTargetCard(card)
                        .setCardSource(outCardSet.getPosId())
                        .setOperationType(OperationEnum.CANCEL);
                mahjongSeat.addOperation(stepAction);

                canOperationSeats.add(mahjongSeat);
            }
        }

        return canOperationSeats;
    }

    /**
     * @param seat
     * @param card
     * @param operationType 用于标识是否是一炮多响
     */
    public List<FanInfo> checkFan(SichuanMahjongSeat seat, Integer card, Integer operationType, Integer cardSourcePosId, Rule<SichuanRoomConfig> rule) {
        MahjongSeat huPlayerSeat = seat.getMahjongSeat();
        huPlayerSeat.solution();
        boolean cardFromSelf = huPlayerSeat.getPosId() == cardSourcePosId;


        List<Integer> standCardList = huPlayerSeat.getStandCardList();
        List<Solution> solutions = huPlayerSeat.getPlayerHand().canHu(card, cardFromSelf);
        //血战（无赖子）可能没有要找最大番型的需要
        //可能会出现多个solution能胡，但是番型完全一致的情况（没有被优化掉），比如面子 334455 的理牌
        /*for(Solution solution : solutions){

        }*/
        if (solutions.size() == 0) {
            throw new SystemException(StatusCodeEnum.FAIL);
        }

        Solution solution = solutions.get(0);
        BaseHuTypeEnum certaintyBaseHuType = solution.getBaseHuType();
        certaintyBaseHuType = certaintyBaseHuType == null ? BaseHuTypeEnum.七对 : certaintyBaseHuType;
        HuTypeEnum huType = cardFromSelf ? HuTypeEnum.自摸 : HuTypeEnum.点炮胡;
        List<FanInfo> certaintyFanList = new ArrayList<>();
        for (FanInfo<BaseHuTypeEnum> fanInfo : rule.getBaseHuList()) {
            if (certaintyBaseHuType.equals(fanInfo.getFanType())) {
                certaintyFanList.add(fanInfo);
            }
        }

        for (FanInfo<HuTypeEnum> fanInfo : rule.getHuTypeList()) {
            if (huType.equals(fanInfo.getFanType())) {
                certaintyFanList.add(fanInfo);
            }
        }

        List<FanInfo<FormalFanTypeEnum>> allFormalFanList = rule.getFormalFanTypeEnumList();
        //这里传的是原立牌，涉及到修改立牌的番型判断，应该先copy一次
        FormalFanParam formalFanParam = FormalFanParam.build(standCardList, huPlayerSeat.getFuLu(), solution, certaintyBaseHuType);

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


        SichuanMahjongSeat cardRourceXueZhanSeat = playerSeats[cardSourcePosId];
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
        HuCardStep huStep = (HuCardStep) huPlayerSeat.getOperationHistoryByTypeAndCard(OperationEnum.HU.value(), card);
        huStep.setFanInfoList(certaintyFanList);
        return certaintyFanList;
    }

    public Integer refreshTookPlayer() {
        SichuanMahjongSeat playerSeat;
        Integer curTookCardPlayerPosId;
        do {
            mahjongZone.refreshObtaionCardPosId();
            curTookCardPlayerPosId = mahjongZone.getCurTookCardPlayerPosId();
            playerSeat = playerSeats[curTookCardPlayerPosId];

        } while (playerSeat.isAlreadyHu());

        return curTookCardPlayerPosId;
    }

    public GameStepModel<OperationCardStep> tookCardStep(Integer posId) {
        Integer card = mahjongZone.TookCardFromCardWall();
        SichuanMahjongSeat seat = playerSeats[posId];
        MahjongSeat mahjongSeat = seat.getMahjongSeat();
        mahjongSeat.appendCard(card);

        List<Integer> standCardList = mahjongSeat.getStandCardList();
        OperationCardStep step = new OperationCardStep();
        StepAction stepAction = new StepAction();
        stepAction.setTargetCard(card)
                .setOperationType(OperationEnum.TOOK_CARD);

        step.setStep(mahjongZone.getStepCount())
                .setPosId(posId)
                .setAction(stepAction)
                .setGameStatus(mahjongZone.getGameStatus())
                .setRemainingCardSize(standCardList.size())
                .setStandCardList(standCardList)
                .setStandCardConvertList(MahjongProp.cardConvertName(standCardList));

        GameStepModel<OperationCardStep> gameStepModel = new GameStepModel(zoneId, seat.getPlayer(), step);

        return gameStepModel;
    }

    /**
     * 血战的判断方式
     * @return
     */
    public boolean gameover() {
        if (!mahjongZone.cardWallHasCard()) {
            return true;
        }

        int alreadyHuCount = 0;
        for (SichuanMahjongSeat seat : playerSeats) {
            MahjongSeat mahjongSeat = seat.getMahjongSeat();
            boolean alreadyHu = mahjongSeat.existsStatus(SeatStatusEnum.ALREADY_HU);
            if (alreadyHu) {
                alreadyHuCount++;
            }
        }
        if (alreadyHuCount >= playerSeats.length - 1) {
            return true;
        }
        return false;
    }

    public GameStepModel<RebateStep> rebate(List<SettlementStep> gangSettlementHistory, SichuanRoomConfig ruleConfig,MahjongOperation operation) {
        RebateStep rebatStep = new RebateStep();
        Map<Integer, List<RebateInfo>> seatRebateMap = new HashMap<>();
        rebatStep.setSeatRebateMap(seatRebateMap)
                .setOperation(operation);

        GameStepModel<RebateStep> rebateStepGameStepModel = new GameStepModel<>(mahjongZone.getZoneId(), null, rebatStep);

        for (SichuanMahjongSeat sichuanMahjongSeat : playerSeats) {
            MahjongSeat mahjongSeat = sichuanMahjongSeat.getMahjongSeat();
            if (mahjongSeat.existsStatus(SeatStatusEnum.ALREADY_HU)) {
                continue;
            }
            PlayerHand playerHand = mahjongSeat.getPlayerHand();
            if (playerHand.isTing()) {
                continue;
            }

            for (SettlementStep settlementStep : gangSettlementHistory) {
                /**
                 * 找出当前遍历的需要退税玩家的 杠分结算记录
                 */
                if (settlementStep.getPosId() != mahjongSeat.getPosId()) {
                    continue;
                }
                /**
                 * 在一次杠牌结算Step中，Map中，所有玩家都 - changeSocore，再封装成rebateInfo
                 */
                for (Map.Entry<Integer, SettlementInfo> entry : settlementStep.getSeatSettlementInfoMap().entrySet()) {
                    StepAction action = settlementStep.getAction();
                    MahjongOperation operationType = action.getOperationType();
                    SettlementInfo settlementInfo = entry.getValue();

                    //退分 或者 拿回杠分（因为用的现有的SettlementStep 对于同一个StepAction 赋给了操作涉及的玩家，在杠牌玩家那里是 加分， 在被杠玩家那里是减分。本身就有多条记录）
                    Integer posId = settlementInfo.getPosId();
                    SichuanMahjongSeat playerSeat = playerSeats[posId];
                    Player player = playerSeat.getPlayer();
                    long beforeScore = player.getScore();
                    int changeScore = (int) -settlementInfo.getChangeScore();
                    player.scoreSettle(changeScore);
                    long remainingScore = player.getScore();

                    RebateInfo rebateInfo = new RebateInfo();
                    rebateInfo.setPosId(settlementInfo.getPosId())
                            .setBeforeScore(beforeScore)
                            .setChangeScore(changeScore)
                            .setRemainingScore(remainingScore)
                            .setRebateActions(action)
                            .setFanNum(ruleConfig.getGangFan(operationType.value()));


                    List<RebateInfo> rebateInfos = seatRebateMap.get(mahjongSeat.getPosId());
                    if (rebateInfos == null) {
                        rebateInfos = new ArrayList<>();
                        seatRebateMap.put(mahjongSeat.getPosId(), rebateInfos);
                    }
                    rebateInfos.add(rebateInfo);

                }

            }
        }
        return rebateStepGameStepModel;
    }

    public GameStepModel<ChaJiaoStep> chaJiao(Rule<SichuanRoomConfig> rule,MahjongOperation operation) {
        List<SichuanMahjongSeat> lossScoreSeats = new ArrayList<>();
        List<SichuanMahjongSeat> winScoreSeats = new ArrayList<>();

        for (SichuanMahjongSeat sichuanMahjongSeat : playerSeats) {
            MahjongSeat mahjongSeat = sichuanMahjongSeat.getMahjongSeat();
            PlayerHand playerHand = mahjongSeat.getPlayerHand();
            if (!sichuanMahjongSeat.isAlreadyHu() && !sichuanMahjongSeat.isHuaZhu() && playerHand.isTing()) {
                lossScoreSeats.add(sichuanMahjongSeat);
            } else if (!sichuanMahjongSeat.isAlreadyHu() && playerHand.isTing()) {
                winScoreSeats.add(sichuanMahjongSeat);
            }
        }

        ChaJiaoStep chaJiaoStep = new ChaJiaoStep();
        Map<Integer, List<ChaJiaoInfo>> chaJiaoInfoMap = new HashMap<>();
        chaJiaoStep.setChaJiaoInfoMap(chaJiaoInfoMap)
                .setOperation(operation);
        for (SichuanMahjongSeat lossSiChuanSeat : lossScoreSeats) {
            MahjongSeat lossSeat = lossSiChuanSeat.getMahjongSeat();
            Player loserPlayer = lossSeat.getPlayer();
            final int posId = lossSeat.getPosId();
            for (SichuanMahjongSeat winSichuanSeat : winScoreSeats) {
                MahjongSeat winScoreSeat = winSichuanSeat.getMahjongSeat();
                final int winPosId = winScoreSeat.getPosId();
                Player winScorePlayer = winScoreSeat.getPlayer();
                PlayerHand playerHand = winScoreSeat.getPlayerHand();

                /**
                 * 找出听牌玩家可胡的最大番
                 */
                int resultFanScore = 0;
                int resultFanNum = 0;
                List<FanInfo> resultFanInfos = null;
                for (Solution solution : playerHand.solutions) {
                    List<Tile> canWin = solution.canWin;
                    if (canWin.size() > 0) {
                        for (Tile tile : canWin) {
                            List<FanInfo> fanInfos = checkFan(winSichuanSeat, tile.id, OperationEnum.HU.value(), posId, rule);
                            final int sumFan = calculateFanNumByFanInfo(fanInfos);
                            SichuanRoomConfig ruleConfig = rule.getRuleConfig();
                            int fanScore = ruleConfig.getBaseScoreFactor() * sumFan;
                            if (fanScore > resultFanScore) {
                                resultFanScore = fanScore;
                                resultFanInfos = fanInfos;
                                resultFanNum = sumFan;
                            }
                        }

                    }
                }
                List<ChaJiaoInfo> loserChaJiaoInfos = chaJiaoInfoMap.get(posId);
                if (loserChaJiaoInfos == null) {
                    loserChaJiaoInfos = new ArrayList<>();
                    chaJiaoInfoMap.put(posId, loserChaJiaoInfos);
                }
                List<ChaJiaoInfo> winChaJiaoInfos = chaJiaoInfoMap.get(winPosId);
                if (winChaJiaoInfos == null) {
                    winChaJiaoInfos = new ArrayList<>();
                    chaJiaoInfoMap.put(winPosId, winChaJiaoInfos);
                }
                //被查叫玩家
                long beforeScore = loserPlayer.getScore();
                loserPlayer.scoreSettle(-resultFanScore);
                ChaJiaoInfo chaJiaoInfo = new ChaJiaoInfo();
                chaJiaoInfo.setBeforeScore(beforeScore)
                        .setChangeScore(-resultFanScore)
                        .setRemainingScore(loserPlayer.getScore())
                        .setFanNum(resultFanNum)
                        .setFanInfoList(resultFanInfos)
                        .setPosId(posId)
                        .setCompensationToPosId(winPosId);
                loserChaJiaoInfos.add(chaJiaoInfo);

                //查叫玩家
                beforeScore = winScorePlayer.getScore();
                loserPlayer.scoreSettle(resultFanScore);
                ChaJiaoInfo winChaJiaoInfo = new ChaJiaoInfo();
                winChaJiaoInfo.setBeforeScore(beforeScore)
                        .setChangeScore(resultFanScore)
                        .setRemainingScore(winScorePlayer.getScore())
                        .setFanNum(resultFanNum)
                        .setFanInfoList(resultFanInfos)
                        .setPosId(winPosId)
                        .setCompensationToPosId(posId);
                winChaJiaoInfos.add(winChaJiaoInfo);
            }
        }
        GameStepModel<ChaJiaoStep> chaJiaoStepGameStepModel = new GameStepModel<>(mahjongZone.getZoneId(), null, chaJiaoStep);
        return chaJiaoStepGameStepModel;
    }

    public GameStepModel<ChaHuaZhuStep> chaHuazhu(Rule<SichuanRoomConfig> rule,MahjongOperation operation) {
        SichuanRoomConfig roomConfig = rule.getRuleConfig();
        List<SichuanMahjongSeat> huaZhuSeats = new ArrayList<>();
        List<SichuanMahjongSeat> winSichuanSeats = new ArrayList<>();
        final int huaZhuBaseFan = roomConfig.getHuaZhuBaseFan();
        final int baseScoreFactor = roomConfig.getBaseScoreFactor();
        final int huaZhuBaseScore = huaZhuBaseFan * baseScoreFactor;

        for (SichuanMahjongSeat sichuanMahjongSeat : playerSeats) {
            if (sichuanMahjongSeat.isHuaZhu()) {
                huaZhuSeats.add(sichuanMahjongSeat);
            } else {
                winSichuanSeats.add(sichuanMahjongSeat);
            }
        }

        ChaHuaZhuStep step = new ChaHuaZhuStep();
        Map<Integer, List<ChaHuaZhuInfo>> chaHuaZhuInfoMap = new HashMap<>();
        step.setChaHuaZhuInfoMap(chaHuaZhuInfoMap)
                .setOperation(operation);
        for (SichuanMahjongSeat seat : huaZhuSeats) {
            final MahjongSeat huZhuSeat = seat.getMahjongSeat();
            Player loserPlayer = huZhuSeat.getPlayer();
            final int huaZhuPosId = huZhuSeat.getPosId();

            List<ChaHuaZhuInfo> huaZhuInfos = chaHuaZhuInfoMap.get(huaZhuPosId);
            if (huaZhuInfos == null) {
                huaZhuInfos = new ArrayList<>();
                chaHuaZhuInfoMap.put(huaZhuPosId, huaZhuInfos);
            }

            for (SichuanMahjongSeat winSichuanSeat : winSichuanSeats) {
                final MahjongSeat winSeat = winSichuanSeat.getMahjongSeat();
                final Player winPlayer = winSeat.getPlayer();
                final PlayerHand playerHand = winSeat.getPlayerHand();
                final int winSeatPosId = winSeat.getPosId();
                List<ChaHuaZhuInfo> winChaHuaZhuInfoList = chaHuaZhuInfoMap.get(winSeatPosId);
                if (winChaHuaZhuInfoList == null) {
                    winChaHuaZhuInfoList = new ArrayList<>();
                    chaHuaZhuInfoMap.put(winSeatPosId, winChaHuaZhuInfoList);

                }
                ChaHuaZhuInfo winInfo = new ChaHuaZhuInfo();
                ChaHuaZhuInfo loserInfo = new ChaHuaZhuInfo();
                //赢分玩家分为类：1.未胡牌的听牌玩家 2.已胡牌的玩家、未胡牌且未听牌的玩家
                if (!winSichuanSeat.isAlreadyHu() && playerHand.isTing()) {
                    /**
                     * 找出听牌玩家可胡的最大番
                     */
                    int resultFanScore = 0;
                    int resultFanNum = 0;
                    List<FanInfo> resultFanInfos = null;
                    for (Solution solution : playerHand.solutions) {
                        List<Tile> canWin = solution.canWin;
                        if (canWin.size() > 0) {
                            for (Tile tile : canWin) {
                                List<FanInfo> fanInfos = checkFan(winSichuanSeat, tile.id, OperationEnum.HU.value(), winSeatPosId, rule);
                                final int sumFan = calculateFanNumByFanInfo(fanInfos);
                                SichuanRoomConfig ruleConfig = rule.getRuleConfig();
                                int fanScore = ruleConfig.getBaseScoreFactor() * sumFan;
                                if (fanScore > resultFanScore) {
                                    resultFanScore = fanScore;
                                    resultFanInfos = fanInfos;
                                    resultFanNum = sumFan;
                                }
                            }

                        }

                    }
                    int resultChangeScore = huaZhuBaseScore + resultFanScore;
                    int resultSettleFanNum = huaZhuBaseFan + resultFanNum;

                    final long winnerBeforeScore = winPlayer.getScore();
                    winPlayer.scoreSettle(resultChangeScore);
                    final long winnerRemainingScore = winPlayer.getScore();
                    winInfo.setBeforeScore(winnerBeforeScore)
                            .setChangeScore(resultChangeScore)
                            .setRemainingScore(winnerRemainingScore)
                            .setCompensationToPosId(huaZhuPosId)
                            .setFanNum(resultSettleFanNum)
                            .setPosId(winSeatPosId);
                    winChaHuaZhuInfoList.add(winInfo);

                    final long loserBaseScore = loserPlayer.getScore();
                    loserPlayer.scoreSettle(-resultChangeScore);
                    final long loserRemainingScore = loserPlayer.getScore();
                    winInfo.setBeforeScore(loserBaseScore)
                            .setChangeScore(-resultChangeScore)
                            .setRemainingScore(loserRemainingScore)
                            .setCompensationToPosId(winSeatPosId)
                            .setFanNum(resultSettleFanNum)
                            .setPosId(huaZhuPosId);
                    winChaHuaZhuInfoList.add(loserInfo);

                }else{
                    final long winnerBeforeScore = winPlayer.getScore();
                    winPlayer.scoreSettle(huaZhuBaseScore);
                    final long winnerRemainingScore = winPlayer.getScore();
                    winInfo.setBeforeScore(winnerBeforeScore)
                            .setChangeScore(huaZhuBaseScore)
                            .setRemainingScore(winnerRemainingScore)
                            .setCompensationToPosId(huaZhuPosId)
                            .setFanNum(huaZhuBaseFan)
                            .setPosId(winSeatPosId);
                    winChaHuaZhuInfoList.add(winInfo);

                    final long loserBaseScore = loserPlayer.getScore();
                    loserPlayer.scoreSettle(-huaZhuBaseScore);
                    final long loserRemainingScore = loserPlayer.getScore();
                    winInfo.setBeforeScore(loserBaseScore)
                            .setChangeScore(-huaZhuBaseScore)
                            .setRemainingScore(loserRemainingScore)
                            .setCompensationToPosId(winSeatPosId)
                            .setFanNum(huaZhuBaseFan)
                            .setPosId(huaZhuPosId);
                    winChaHuaZhuInfoList.add(loserInfo);

                }
            }
        }
        GameStepModel<ChaHuaZhuStep> gameStepModel = new GameStepModel<>(mahjongZone.getZoneId(),null,step);
        return gameStepModel;
    }

    private List<SichuanMahjongSeat> huaZhuSeats() {
        List<SichuanMahjongSeat> seats = new ArrayList<>();
        for (SichuanMahjongSeat sichuanMahjongSeat : playerSeats) {
            if (sichuanMahjongSeat.isHuaZhu()) {
                seats.add(sichuanMahjongSeat);
            }
        }
        return seats;
    }

    public int calculateFanNumByFanInfo(List<FanInfo> fanInfos) {
        int sumFan = 1;
        List<FanInfo> additionFan = new ArrayList<>();
        for (FanInfo fanInfo : fanInfos) {
            if (FanInfo.MULTIPLICATION == fanInfo.getCalculationType()) {
                sumFan *= fanInfo.getFanNum();
            }
        }
        for (FanInfo fanInfo : additionFan) {
            sumFan += fanInfo.getFanNum();
        }

        return sumFan;
    }


    public List<MahjongSeat> findNotHuSeat() {
        List<MahjongSeat> list = new ArrayList<>();
        for (SichuanMahjongSeat sichuanMahjongSeat : playerSeats) {
            MahjongSeat mahjongSeat = sichuanMahjongSeat.getMahjongSeat();
            //胡牌的玩家已经增加了 已经胡牌 的状态
            /*if(mahjongSeat.getPosId() == posId){
                continue;
            }*/
            if (mahjongSeat.existsStatus(SeatStatusEnum.ALREADY_HU)) {
                continue;
            }
            list.add(mahjongSeat);
        }
        return list;
    }

    public MahjongZone getMahjongZone() {
        return mahjongZone;
    }

    public void setMahjongZone(MahjongZone mahjongZone) {
        this.mahjongZone = mahjongZone;
    }

    public ExchangeTypeEnum getExchangeType() {
        return exchangeType;
    }
}
