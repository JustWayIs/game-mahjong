package com.yude.game.xuezhan.domain;


import com.yude.game.common.mahjong.PlayerHand;
import com.yude.game.common.mahjong.Solution;
import com.yude.game.common.model.*;
import com.yude.game.common.model.history.GameStepModel;
import com.yude.game.common.model.history.OperationCardStep;
import com.yude.game.common.model.history.Step;
import com.yude.game.common.model.sichuan.SichuanMahjongCard;
import com.yude.game.common.model.sichuan.SichuanMahjongZone;
import com.yude.game.common.model.sichuan.constant.SeatStatusEnum;
import com.yude.game.common.model.sichuan.constant.SichuanGameStatusEnum;
import com.yude.game.xuezhan.constant.XueZhanMahjongOperationEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * @Author: HH
 * @Date: 2020/8/1 15:29
 * @Version: 1.0
 * @Declare:
 */
public class XueZhanZone extends AbstractGameZoneModel<XueZhanSeat, SichuanGameStatusEnum> {
    private static final Logger log = LoggerFactory.getLogger(XueZhanZone.class);

    private SichuanMahjongZone sichuanMahjongZone;
    private MahjongZone mahjongZone;

    /**
     *  因为换三张操作 、 定缺操作这种地方麻将玩法，导致，historyList不能放在MahjongZone里面
     *  也就导致多了一层对GameZone的调用。核心问题是，多一重掉用对性能真的有影响么，像现在的Controller省略了Service一样，又对性能提高有多少贡献呢。
     *  如果想要少一层调用，倒是可以把historyList放在RoomModel里面，这里的核心问题是类的职能划分
     *  从代码复用的层面来说，这里面的方法确实应该放在 MahjongZone 和 地方麻将Zone里面
     */

    private List<GameStepModel> historyList;

    public XueZhanZone(XueZhanSeat[] playerSeats, int round, int inning,MahjongZone mahjongZone,SichuanMahjongZone sichuanMahjongZone) {
        super(playerSeats, round, inning);
        /*MahjongSeat[] mahjongSeats = new MahjongSeat[playerSeats.length];
        SichuanMahjongSeat[] sichuanMahjongSeats = new SichuanMahjongSeat[playerSeats.length];
        int i = 0;
        for (XueZhanSeat xueZhanSeat : playerSeats) {
            mahjongSeats[i] = xueZhanSeat.getMahjongSeat();
            sichuanMahjongSeats[i] = xueZhanSeat.getSichuanMahjongSeat();
            sichuanMahjongSeats[i].setMahjongSeat(mahjongSeats[i]);
            ++i;
        }
        mahjongZone = new MahjongZone(mahjongSeats, round, inning);
        sichuanMahjongZone = new SichuanMahjongZone(sichuanMahjongSeats, round, inning);
        sichuanMahjongZone.setMahjongZone(mahjongZone);*/

        this.mahjongZone = mahjongZone;
        this.sichuanMahjongZone = sichuanMahjongZone;
    }

    @Override
    public void init() {
        mahjongZone.init();
        sichuanMahjongZone.init();
        historyList = new ArrayList<>();
    }

    @Override
    public void clean() {

    }

    public void deal(Long roomId) {
        mahjongZone.deal(SichuanMahjongCard.values(), SichuanGameStatusEnum.DEAL_CARD, roomId, historyList);
        //0号玩家在该游戏没有任何操作的情况下重连了
        lastOperationTime = System.currentTimeMillis();
        gameStatus = SichuanGameStatusEnum.EXCHANGE_CARD;

    }

    /*public boolean dingQue(Integer color, XueZhanSeat operationSeat) {
        SichuanMahjongSeat operationSeatSichuanMahjongSeat = operationSeat.getSichuanMahjongSeat();
        final boolean isFinishDingQue = sichuanMahjongZone.dingQue(color, operationSeatSichuanMahjongSeat);

        DingQueStep step = new DingQueStep();
        step.setStep(mahjongZone.getStepCount())
                .setPosId(operationSeat.getPosId())
                .setColor(color)
                .setGameStatus(gameStatus)
                .setHandCards(operationSeat.getMahjongSeat().getStandCardList());
        GameStepModel<DingQueStep> gameStepModel = new GameStepModel<>(zoneId, operationSeat.getPlayer(), step);
        historyList.add(gameStepModel);
        mahjongZone.stepAdd();

        if (isFinishDingQue) {

            for(XueZhanSeat xueZhanSeat : playerSeats){
                //庄家进行理牌
                //XueZhanSeat xueZhanSeat = playerSeats[mahjongZone.getBankerPosId()];
                *//**
                 * 由于庄家出完牌，其他玩家得通过理牌的数据来判断是否能 碰杠胡，所以也得给其他玩家理牌
                 *//*
                MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();
                SichuanMahjongSeat sichuanMahjongSeat = xueZhanSeat.getSichuanMahjongSeat();
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


    public boolean exchangeCard(List<Integer> cards, XueZhanSeat xueZhanSeat) {
        SichuanMahjongSeat sichuanMahjongSeat = xueZhanSeat.getSichuanMahjongSeat();
        boolean isFinishExchange = sichuanMahjongZone.exchangeCard(cards, sichuanMahjongSeat);

        ExchangeCardStep exchangeCardStep = new ExchangeCardStep();
        MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();
        exchangeCardStep.setStep(mahjongZone.getStepCount())
                .setPosId(xueZhanSeat.getPosId())
                .setDiscardCards(cards)
                .setGameStatus(gameStatus)
                .setStandCards(mahjongSeat.getStandCardList());
        GameStepModel<ExchangeCardStep> gameStepModel = new GameStepModel<>(zoneId, xueZhanSeat.getPlayer(), exchangeCardStep);
        historyList.add(gameStepModel);
        if (isFinishExchange) {
            List<Step> historyByGameStatus = getHistoryByGameStatus(SichuanGameStatusEnum.EXCHANGE_CARD);
            for (Step step : historyByGameStatus) {
                ExchangeCardStep curExchangeCardStep = (ExchangeCardStep) step;
                //换牌后重新排序
                XueZhanSeat playerSeat = playerSeats[curExchangeCardStep.getPosId()];
                Collections.sort(playerSeat.getMahjongSeat().getStandCardList());

                List<Integer> standCardList = playerSeats[curExchangeCardStep.getPosId()].getMahjongSeat().getStandCardList();
                curExchangeCardStep.setStandCards(standCardList)
                        .setExchangeType(sichuanMahjongZone.getExchangeType())
                        .setGainedCards(playerSeat.getSichuanMahjongSeat().getGainedCards())
                        .setStandCardConvertList(MahjongProp.cardConvertName(standCardList));

            }
            mahjongZone.setGameStatus(SichuanGameStatusEnum.DING_QUE);
            gameStatus = SichuanGameStatusEnum.DING_QUE;
        }

        mahjongZone.stepAdd();
        return isFinishExchange;

    }*/


    public  GameStepModel<OperationCardStep> outCard(Integer card, Integer posId) {
        GameStepModel<OperationCardStep> gameStepModel = mahjongZone.outCard(card, posId);
        historyList.add(gameStepModel);
        return gameStepModel;
    }


    public GameStepModel<OperationCardStep> hu(Integer card, Integer posId) {
        GameStepModel<OperationCardStep> gameStepModel = null;
        return gameStepModel;
    }


    public GameStepModel<OperationCardStep> cancel(Integer card,Integer posId) {
        GameStepModel<OperationCardStep> cancel = mahjongZone.cancel(card,posId);
        if(cancel != null){
            historyList.add(cancel);
        }
        return cancel;
    }


    public GameStepModel<OperationCardStep> gang(Integer card, Integer type, Integer posId) {
        GameStepModel<OperationCardStep> gameStepModel = null;
        return  null;
    }

    public GameStepModel<OperationCardStep> peng(Integer card, Integer posId) {
        GameStepModel<OperationCardStep> gameStepModel = null;
        return null;
    }

    /**
     * 抓牌的人能做什么
     * @param card
     * @return
     */
    public List<StepAction> whatCanYouDo(Integer card) {
        Integer curObtainCardPosId = mahjongZone.getCurTookCardPlayerPosId();
        XueZhanSeat playerSeat = playerSeats[curObtainCardPosId];
        MahjongSeat mahjongSeat = playerSeat.getMahjongSeat();

        List<StepAction> stepActions = new ArrayList<>();
        PlayerHand playerHand = mahjongSeat.getPlayerHand();
        playerHand.canAnGang(stepActions);
        for(StepAction stepAction : stepActions){
            stepAction.setCardSource(mahjongSeat.getPosId())
                    .setOperationType(XueZhanMahjongOperationEnum.AN_GANG);
        }
        boolean canBuGang = playerHand.canBuGang(card);
        if(canBuGang){
            StepAction stepAction = new StepAction();
            stepAction.setTargetCard(card)
                    .setCardSource(mahjongSeat.getPosId())
                    .setOperationType(XueZhanMahjongOperationEnum.BU_GANG);
            stepActions.add(stepAction);
        }
        List<Solution> solutions = playerHand.canHu(card, true);
        if(solutions.size() > 0){
            StepAction stepAction = new StepAction();
            stepAction.setTargetCard(card)
                    .setCardSource(mahjongSeat.getPosId())
                    .setOperationType(XueZhanMahjongOperationEnum.HU);
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
    public List<MahjongSeat> otherPalyerCanDo(XueZhanSeat outCardSet, Integer card) {
        List<MahjongSeat> canOperationSeats = new ArrayList<>();
        for(XueZhanSeat xueZhanSeat : playerSeats){
            if(outCardSet.equals(xueZhanSeat)){
                continue;
            }
            MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();
            PlayerHand playerHand = mahjongSeat.getPlayerHand();
            boolean canPeng = playerHand.canPeng(card);
            if(canPeng){
                StepAction stepAction = new StepAction();
                stepAction.setTargetCard(card)
                        .setCardSource(outCardSet.getPosId())
                        .setOperationType(XueZhanMahjongOperationEnum.PENG);
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
                        .setOperationType(XueZhanMahjongOperationEnum.ZHI_GANG);
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
                        .setOperationType(XueZhanMahjongOperationEnum.HU);
                mahjongSeat.addOperation(stepAction);
            }

            if(canPeng || canZhiGang || solutions.size() > 0){
                canOperationSeats.add(mahjongSeat);
            }
        }

        return canOperationSeats;
    }

    public Integer refreshTookPlayer(){
        mahjongZone.refreshObtaionCardPosId();
        return mahjongZone.getCurTookCardPlayerPosId();
    }

    public OperationCardStep tookCardStep(Integer posId){
        Integer card = mahjongZone.TookCardFromCardWall();
        XueZhanSeat xueZhanSeat = playerSeats[posId];
        MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();
        mahjongSeat.appendCard(card);

        List<Integer> standCardList = new ArrayList<>(mahjongSeat.getStandCardList());
        OperationCardStep step = new OperationCardStep();
        StepAction stepAction = new StepAction();
        stepAction.setTargetCard(card)
                .setOperationType(XueZhanMahjongOperationEnum.TOOK_CARD);

        step.setStep(mahjongZone.getStepCount())
                .setPosId(posId)
                .setAction(stepAction)
                .setGameStatus(gameStatus)
                .setRemainingCardSize(standCardList.size())
                .setStandCardList(standCardList)
                .setStandCardConvertList(MahjongProp.cardConvertName(standCardList));

        GameStepModel<OperationCardStep> gameStepModel = new GameStepModel(zoneId,xueZhanSeat.getPlayer(),step);
        historyList.add(gameStepModel);
        return step;
    }

    public boolean gameover(){
        List cardWall = mahjongZone.getCardWall();
        if(cardWall.size() == 0){
            return true;
        }

        int alreadyHuCount = 0;
        for(XueZhanSeat xueZhanSeat : playerSeats){
            MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();
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

    /*public boolean canPeng(MahjongSeat mahjongSeat, Integer card) {
        PlayerHand playerHand = mahjongSeat.getPlayerHand();
        List<Solution> solutions = playerHand.solutions;
        for(Solution solution : solutions){
            List<Tile> canChow = solution.canChow;
            for(Tile tile : canChow){
                if(tile.id == card){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canZhiGang(MahjongSeat mahjongSeat, Integer card) {
        return false;
    }

    public void canAnGang(MahjongSeat mahjongSeat,List<StepAction> stepActions) {
        // 判断手上有暗杠，但是没有开杠的牌
        int cardNum = 0;
        int tempCard = 0;
        for (Integer card : mahjongSeat.getStandCardList()) {
            if (tempCard != card) {
                cardNum = 0;
            }
            tempCard = card;
            cardNum++;
            if (cardNum >= 4) {
                StepAction stepAction = new StepAction();
                stepAction.setTargetCard(card)
                        .setOperationType(XueZhanMahjongOperationEnum.AN_GANG);
                stepActions.add(stepAction);
            }
        }

    }

    public boolean canBuGang(MahjongSeat mahjongSeat, Integer card) {
        if(card == null){
            return false;
        }
        mahjongSeat.getPlayerHand()
        return false;
    }

    public void canHu(MahjongSeat mahjongSeat, Integer card, boolean cardFromSelf) {
        PlayerHand playerHand = mahjongSeat.getPlayerHand();
        List<Solution> solutions = playerHand.solutions;
        if (card != null) {
            for(Solution solution : solutions){
                List<Tile> canWin = solution.canWin;
                for(Tile tile : canWin){
                    if(tile.id == card){
                        //番型判断
                    }
                }
            }
        } else {

            for(Solution solution : solutions){
                if(solution.isWin){
                    //番型判断
                }
            }
        }
    }*/


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


    public List<GameStepModel> getHistoryList() {
        return historyList;
    }

    public Integer[] getDice() {
        return mahjongZone.getDice();
    }

    public Integer getBankerPosId() {
        return mahjongZone.getBankerPosId();
    }

    public List<Integer> getCardWall() {
        return mahjongZone.getCardWall();
    }

    public List<Integer> getCardPool() {
        return mahjongZone.getCardPool();
    }

    public Integer getCurOperatorPosId() {
        return mahjongZone.getCurOperatorPosId();
    }

    public Integer getBeforeOperatorPosId() {
        return mahjongZone.getBeforeOperatorPosId();
    }

    public Integer getCurTookCardPlayerPosId() {
        return mahjongZone.getCurTookCardPlayerPosId();
    }

    public Integer getBeforeTookCardPlayerPosId() {
        return mahjongZone.getBeforeTookCardPlayerPosId();
    }

    /**
     * 不修改原值
     * @return
     */
    public Integer getNextTookCardPosId() {
        return mahjongZone.getNextObtainCardPosId();
    }

    public boolean checkCurrentGameStatus(SichuanGameStatusEnum gameStatusEnum) {
        return gameStatusEnum.equals(gameStatus);
    }
}
