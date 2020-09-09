package com.yude.game.common.model.sichuan;

import com.yude.game.common.contant.MahjongStatusCodeEnum;
import com.yude.game.common.contant.OperationEnum;
import com.yude.game.common.mahjong.PlayerHand;
import com.yude.game.common.mahjong.Solution;
import com.yude.game.common.model.*;
import com.yude.game.common.model.history.GameStepModel;
import com.yude.game.common.model.history.OperationCardStep;
import com.yude.game.common.model.history.Step;
import com.yude.game.common.model.sichuan.constant.ExchangeTypeEnum;
import com.yude.game.common.model.sichuan.constant.SeatStatusEnum;
import com.yude.game.common.model.sichuan.constant.SichuanGameStatusEnum;
import com.yude.game.common.model.sichuan.history.DingQueStep;
import com.yude.game.common.model.sichuan.history.ExchangeCardStep;
import com.yude.game.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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

    public boolean dingQue(Integer color, SichuanMahjongSeat seat,List<GameStepModel> historyList) {
        seat.setQueColor(color);

        DingQueStep step = new DingQueStep();
        step.setStep(mahjongZone.getStepCount())
                .setPosId(seat.getMahjongSeat().getPosId())
                .setColor(color)
                .setGameStatus(mahjongZone.getGameStatus())
                .setHandCards(seat.getMahjongSeat().getStandCardList());
        GameStepModel<DingQueStep> gameStepModel = new GameStepModel<>(zoneId, seat.getPlayer(), step);
        historyList.add(gameStepModel);
        mahjongZone.stepAdd();

        boolean isFinishDingQue = true;
        for(SichuanMahjongSeat  sichuanMahjongSeat : playerSeats){
            if(sichuanMahjongSeat.getQueColor() == null){
                isFinishDingQue =  false;
            }
        }
        if (isFinishDingQue) {

            for(SichuanMahjongSeat sichuanMahjongSeat: playerSeats){
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



            gameStatus = SichuanGameStatusEnum.OPERATION_CARD;
            //初始化当前操作人为庄家
            mahjongZone. initCurrentOperator();

        }

        return isFinishDingQue;
    }


    public boolean exchangeCard(List<Integer> cards, SichuanMahjongSeat seat,List<GameStepModel> historyList) {
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
        exchangeCardStep.setStep(mahjongZone.getStepCount())
                .setPosId(mahjongSeat.getPosId())
                .setDiscardCards(cards)
                .setGameStatus(mahjongZone.getGameStatus())
                .setStandCards(mahjongSeat.getStandCardList());
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
            curExchangeCardStep.setStandCards(standCardList)
                    .setExchangeType(exchangeType)
                    .setGainedCards(playerSeat.getGainedCards())
                    .setStandCardConvertList(MahjongProp.cardConvertName(standCardList))
                    .setGameStatus(mahjongZone.getGameStatus());

        }
        mahjongZone.setGameStatus(SichuanGameStatusEnum.DING_QUE);
    }

    /**
     * 抓牌的人能做什么
     * @param card
     * @return
     */
    public List<StepAction> whatCanYouDo(Integer card) {
        Integer curObtainCardPosId = mahjongZone.getCurTookCardPlayerPosId();
        SichuanMahjongSeat playerSeat = playerSeats[curObtainCardPosId];
        MahjongSeat mahjongSeat = playerSeat.getMahjongSeat();

        List<StepAction> stepActions = new ArrayList<>();
        PlayerHand playerHand = mahjongSeat.getPlayerHand();
        playerHand.canAnGang(stepActions);
        for(StepAction stepAction : stepActions){
            stepAction.setCardSource(mahjongSeat.getPosId())
                    .setOperationType(OperationEnum.AN_GANG);
        }
        boolean canBuGang = playerHand.canBuGang(card);
        if(canBuGang){
            StepAction stepAction = new StepAction();
            stepAction.setTargetCard(card)
                    .setCardSource(mahjongSeat.getPosId())
                    .setOperationType(OperationEnum.BU_GANG);
            stepActions.add(stepAction);
        }
        List<Solution> solutions = playerHand.canHu(card, true);
        if(solutions.size() > 0){
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
     * 对于某个玩家出的牌，其他玩家能做什么
     * @param outCardSet
     * @param card
     * @return
     */
    public List<MahjongSeat> otherPalyerCanDo(SichuanMahjongSeat outCardSet, Integer card) {
        List<MahjongSeat> canOperationSeats = new ArrayList<>();
        for(SichuanMahjongSeat seat : playerSeats){
            if(outCardSet.equals(seat)){
                continue;
            }
            MahjongSeat mahjongSeat = seat.getMahjongSeat();
            PlayerHand playerHand = mahjongSeat.getPlayerHand();
            boolean canPeng = playerHand.canPeng(card);
            if(canPeng){
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
            boolean canZhiGang = playerHand.canZhiGang(card);
            if(canZhiGang){
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
            List<Solution> solutions = playerHand.canHu(card, false);
            if(solutions.size() > 0){
                StepAction stepAction = new StepAction();
                stepAction.setTargetCard(card)
                        .setCardSource(outCardSet.getPosId())
                        .setOperationType(OperationEnum.HU);
                mahjongSeat.addOperation(stepAction);
            }

            if(canPeng || canZhiGang || solutions.size() > 0){
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

    public Integer refreshTookPlayer(){
        mahjongZone.refreshObtaionCardPosId();
        return mahjongZone.getCurTookCardPlayerPosId();
    }

    public GameStepModel<OperationCardStep> tookCardStep(Integer posId){
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
                .setGameStatus(gameStatus)
                .setRemainingCardSize(standCardList.size())
                .setStandCardList(standCardList)
                .setStandCardConvertList(MahjongProp.cardConvertName(standCardList));

        GameStepModel<OperationCardStep> gameStepModel = new GameStepModel(zoneId,seat.getPlayer(),step);

        return gameStepModel;
    }

    public boolean gameover(){
        List cardWall = mahjongZone.getCardWall();
        if(cardWall.size() == 0){
            return true;
        }

        int alreadyHuCount = 0;
        for(SichuanMahjongSeat seat : playerSeats){
            MahjongSeat mahjongSeat = seat.getMahjongSeat();
            boolean alreadyHu = mahjongSeat.existsStatus(SeatStatusEnum.ALREADY_HU);
            if(alreadyHu){
                alreadyHuCount++;
            }
        }
        if(alreadyHuCount >= playerSeats.length - 1){
            return true;
        }
        return false;
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
