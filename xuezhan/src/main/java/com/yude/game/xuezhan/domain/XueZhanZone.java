package com.yude.game.xuezhan.domain;


import com.yude.game.common.mahjong.PlayerHand;
import com.yude.game.common.mahjong.Tile;
import com.yude.game.common.model.AbstractGameZoneModel;
import com.yude.game.common.model.MahjongSeat;
import com.yude.game.common.model.MahjongZone;
import com.yude.game.common.model.history.GameStepModel;
import com.yude.game.common.model.history.Step;
import com.yude.game.common.model.sichuan.SichuanGameStatusEnum;
import com.yude.game.common.model.sichuan.SichuanMahjongCard;
import com.yude.game.common.model.sichuan.SichuanMahjongSeat;
import com.yude.game.common.model.sichuan.SichuanMahjongZone;
import com.yude.game.common.model.sichuan.history.DingQueStep;
import com.yude.game.common.model.sichuan.history.ExchangeCardStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    private List<GameStepModel> historyList;

    public XueZhanZone(XueZhanSeat[] playerSeats, int round, int inning) {
        super(playerSeats, round, inning);
        MahjongSeat[] mahjongSeats = new MahjongSeat[playerSeats.length];
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
        sichuanMahjongZone.setMahjongZone(mahjongZone);
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

    public boolean dingQue(Integer color, XueZhanSeat operationSeat) {
        SichuanMahjongSeat operationSeatSichuanMahjongSeat = operationSeat.getSichuanMahjongSeat();
        final boolean isFinishDingQue = sichuanMahjongZone.dingQue(color, operationSeatSichuanMahjongSeat);

        DingQueStep step = new DingQueStep();
        step.setStep(mahjongZone.getStepCount())
                .setPosId(operationSeat.getPosId())
                .setColor(color)
                .setGameStatus(gameStatus)
                .setHandCards(operationSeat.getMahjongSeat().getStandCardList());
        GameStepModel<DingQueStep> gameStepModel = new GameStepModel<>(zoneId,operationSeat.getPlayer(),step);
        historyList.add(gameStepModel);
        mahjongZone.stepAdd();

        if(isFinishDingQue){
            /**
             * 定缺完成后，进行理牌
             * posId -> standCardList
             */
            Map<Integer, List<Integer>> cardGroup = new HashMap<>();
            /**
             * posId -> dingQueColor
             */
            Map<Integer, Integer> posIdQueColorMap = new HashMap<>();
            for (XueZhanSeat xueZhanSeat : playerSeats) {
                MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();
                cardGroup.compute(mahjongSeat.getPosId(), (k, v) -> {
                    if (v == null) {
                        v = new ArrayList<>();
                    }
                    v.addAll(mahjongSeat.getStandCardList());
                    return v;
                });

                SichuanMahjongSeat sichuanMahjongSeat = xueZhanSeat.getSichuanMahjongSeat();
                posIdQueColorMap.put(mahjongSeat.getPosId(), sichuanMahjongSeat.getQueColor());

                List<Integer> standCardList = mahjongSeat.getStandCardList();
                for(Integer card : standCardList){
                    Tile tile = Tile.getTileByID(card);
                    mahjongSeat.addTile(tile);
                }
                PlayerHand playerHand = mahjongSeat.getPlayerHand();
                playerHand.bannedSuit = sichuanMahjongSeat.getQueColor();
                mahjongSeat.solution();

            }
            /*final PlayBoard playBoard = MJManager.INSTANCE.create(mahjongZone.getBankerPosId(), mahjongZone.getCardWall());
            playBoard.deal(cardGroup, posIdQueColorMap);
            mahjongZone.setPlayBoard(playBoard);*/
            gameStatus = SichuanGameStatusEnum.OPERATION_CARD;
            //H2 设置当前操作人

        }

        return isFinishDingQue;

    }


    public boolean exchangeCard(List<Integer> cards, XueZhanSeat xueZhanSeat) {
        SichuanMahjongSeat sichuanMahjongSeat = xueZhanSeat.getSichuanMahjongSeat();
        boolean isFinishExchange = sichuanMahjongZone.exchangeCard(cards, sichuanMahjongSeat);
        if (!isFinishExchange) {
            ExchangeCardStep exchangeCardStep = new ExchangeCardStep();
            MahjongSeat mahjongSeat = xueZhanSeat.getMahjongSeat();
            exchangeCardStep.setStep(mahjongZone.getStepCount())
                    .setPosId(xueZhanSeat.getPosId())
                    .setDiscardCards(cards)
                    .setGameStatus(gameStatus)
                    .setStandCards(mahjongSeat.getStandCardList());
            GameStepModel<ExchangeCardStep> gameStepModel = new GameStepModel<>(zoneId, xueZhanSeat.getPlayer(), exchangeCardStep);
            historyList.add(gameStepModel);
        } else {
            List<Step> historyByGameStatus = getHistoryByGameStatus(SichuanGameStatusEnum.EXCHANGE_CARD);
            for (Step step : historyByGameStatus) {
                ExchangeCardStep exchangeCardStep = (ExchangeCardStep) step;
                exchangeCardStep.setStandCards(playerSeats[exchangeCardStep.getPosId()].getMahjongSeat().getStandCardList())
                        .setExchangeType(sichuanMahjongZone.getExchangeType());
            }
            gameStatus = SichuanGameStatusEnum.DING_QUE;
        }

        mahjongZone.stepAdd();
        return isFinishExchange;

    }

    public List<Integer> whatCanYouDo(){
        Integer nextObtainCardPosId = mahjongZone.getNextObtainCardPosId();
        XueZhanSeat playerSeat = playerSeats[nextObtainCardPosId];
        MahjongSeat mahjongSeat = playerSeat.getMahjongSeat();
        canAnGang(mahjongSeat);
        canHu(mahjongSeat);
        List<Integer> list = new ArrayList<>();

        return list;
    }

    public void canPeng(MahjongSeat mahjongSeat,Integer card){

    }

    public void canZhiGang(MahjongSeat mahjongSeat,Integer card){};

    public void canBuGang(MahjongSeat mahjongSeat,Integer card){};

    public void canAnGang(MahjongSeat mahjongSeat){

            // 判断手上有暗杠，但是没有开杠的牌
            int cardNum = 0;
            int tempCard = 0;
            for (Integer c : mahjongSeat.getStandCardList()) {
                if (tempCard != c) {
                    cardNum = 0;
                }
                tempCard = c;
                cardNum++;
                if (cardNum >= 4) {

                }
            }

    }

    public void canHu(MahjongSeat mahjongSeat){};

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

    public Integer getCurObtainCardPlayerPosId() {
        return mahjongZone.getCurObtainCardPlayerPosId();
    }

    public Integer getBeforeObtainCardPlayerPosId() {
        return mahjongZone.getBeforeObtainCardPlayerPosId();
    }

    public Integer getNextObtainCardPosId(){
        return mahjongZone.getNextObtainCardPosId();
    }

    public boolean checkCurrentGameStatus(SichuanGameStatusEnum gameStatusEnum){
        return gameStatusEnum.equals(gameStatus);
    }
}
