package com.yude.game.common.model;


import com.yude.game.common.constant.Status;
import com.yude.game.common.mahjong.PlayBoard;
import com.yude.game.common.model.history.GameStartStep;
import com.yude.game.common.model.history.GameStepModel;

import java.util.*;

/**
 * @Author: HH
 * @Date: 2020/8/3 19:28
 * @Version: 1.0
 * @Declare:
 */
public class MahjongZone<T extends MahjongSeat> extends AbstractGameZoneModel<MahjongSeat, Status> {


    private Integer[] dice = new Integer[2];

    /**
     * 庄家
     */
    private Integer bankerPosId;

    /**
     * 牌墙
     */
    private List<Integer> cardWall;
    /**
     * 出的牌：不包括被吃碰杠的牌
     */
    private List<Integer> cardPool;

    /**
     * 操作玩家->当前可以操作的玩家  和 摸牌玩家->当前抓了牌的玩家：
     *  下一个摸牌玩家由当前操作玩家决定
     *  玩家进行某个操作后，如果没有其他玩家可以进行操作：吃碰杠胡。 那么当前操作玩家 和 当前摸牌玩家就应该重置为 下一个摸牌玩家
     *
     *  当前操作玩家如果不是摸牌玩家，那么其他玩家是不能知道是谁可以操作的
     */
    private Integer curOperatorPosId;
    private Integer beforeOperatorPosId;
    private Integer curObtainCardPlayerPosId;
    private Integer beforeObtainCardPlayerPosId;

    private PlayBoard playBoard;


    private List<GameStepModel> gameHistory;

    public MahjongZone(T[] playerSeats, int round, int inning) {
        super(playerSeats, round, inning);
        gameHistory = new ArrayList<>();
    }

    @Override
    public void init() {
        cardWall = new ArrayList<>();
        cardPool = new ArrayList<>();
    }

    @Override
    public void clean() {

    }

    public void deal(MahjongCard[] mahjongCards,Status gameStatus,Long roomId,List<GameStepModel> historyList) {
        this.gameStatus = gameStatus;
        rollingDice();

        Map<Integer, List<Integer>> dealCardGroup = MahjongProp.getDealCardGroup(mahjongCards, bankerPosId,cardWall);

        for (MahjongSeat mahjongSeat : playerSeats) {
            int posId = mahjongSeat.getPosId();
            mahjongSeat.setStandCardList(dealCardGroup.get(posId));
            GameStartStep step = new GameStartStep();
            List<Integer> standCardList = mahjongSeat.getStandCardList();

            step.setRoomId(roomId)
                    .setZoneId(zoneId)
                    .setStep(stepCount)
                    .setBankerPosId(bankerPosId)
                    .setDiceList(Arrays.asList(dice))
                    .setPosId(posId)
                    .setStandCards(new ArrayList<>(standCardList))
                    .setStandCardCovertList(MahjongProp.cardConvertName(standCardList))
                    .setGameStatus(this.gameStatus);
            GameStepModel<GameStartStep> gameStepModel = new GameStepModel<>(zoneId,mahjongSeat.getPlayer(),step);
            historyList.add(gameStepModel);

            Collections.sort(standCardList);
        }
        stepAdd();
    }

    private void rollingDice() {
        Random random = new Random();
        this.bankerPosId = random.nextInt(playerSeats.length);

        for (int i = 0; i < dice.length; ++i) {
            dice[i] = random.nextInt(6) + 1;
        }
    }

    public PlayBoard getPlayBoard() {
        return playBoard;
    }

    public MahjongZone<T> setPlayBoard(PlayBoard playBoard) {
        this.playBoard = playBoard;
        return this;
    }

    public Integer[] getDice() {
        return dice;
    }

    public Integer getBankerPosId() {
        return bankerPosId;
    }

    public List<Integer> getCardWall() {
        return cardWall;
    }

    public List<Integer> getCardPool() {
        return cardPool;
    }

    public Integer getCurOperatorPosId() {
        return curOperatorPosId;
    }

    public Integer getBeforeOperatorPosId() {
        return beforeOperatorPosId;
    }


    public Integer getCurObtainCardPlayerPosId() {
        return curObtainCardPlayerPosId;
    }

    public MahjongZone<T> setCurObtainCardPlayerPosId(Integer curObtainCardPlayerPosId) {
        this.curObtainCardPlayerPosId = curObtainCardPlayerPosId;
        return this;
    }

    public Integer getBeforeObtainCardPlayerPosId() {
        return beforeObtainCardPlayerPosId;
    }

    public MahjongZone<T> setBeforeObtainCardPlayerPosId(Integer beforeObtainCardPlayerPosId) {
        this.beforeObtainCardPlayerPosId = beforeObtainCardPlayerPosId;
        return this;
    }

    public MahjongZone<T> setCurOperatorPosId(Integer curOperatorPosId) {
        this.curOperatorPosId = curOperatorPosId;
        return this;
    }

    /**
     *
     * @return
     */
    public Integer getNextObtainCardPosId(){
        /**
         * 游戏正式开始:只有游戏进入出牌流程前 当前操作人 和 摸牌人为null
         */
        if(curObtainCardPlayerPosId == null && curOperatorPosId == null){
            curObtainCardPlayerPosId = bankerPosId;
            curOperatorPosId = bankerPosId;
        }

        Integer nextPosId = (curOperatorPosId + 1) % playerSeats.length;
        return nextPosId;
    }

    public void undateObtaionCardPosId(){

    }



    public void canChi(MahjongSeat mahjongSeat,Integer card){}

    public void canPeng(MahjongSeat mahjongSeat,Integer card){

    }

    public void canZhiGang(MahjongSeat mahjongSeat,Integer card){}

    public void canBuGang(MahjongSeat mahjongSeat,Integer card){}

    public void canAnGang(MahjongSeat mahjongSeat,Integer card){
        if(card == null){
            /**
             * 开局检测庄家
             */

        }
    }

    public void canHu(MahjongSeat mahjongSeat,Integer card){
        if(card == null){
            /**
             * 开局检测庄家
             */
        }
    }
}
