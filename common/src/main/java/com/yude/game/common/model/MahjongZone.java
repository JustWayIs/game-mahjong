package com.yude.game.common.model;


import com.yude.game.common.constant.Status;
import com.yude.game.common.contant.OperationEnum;
import com.yude.game.common.mahjong.PlayBoard;
import com.yude.game.common.model.history.GameStartStep;
import com.yude.game.common.model.history.GameStepModel;
import com.yude.game.common.model.history.OperationCardStep;
import com.yude.game.exception.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @Author: HH
 * @Date: 2020/8/3 19:28
 * @Version: 1.0
 * @Declare:
 */
public class MahjongZone<T extends MahjongSeat> extends AbstractGameZoneModel<MahjongSeat, Status> {
    private static final Logger log = LoggerFactory.getLogger(MahjongZone.class);

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
     * 下一个摸牌玩家由当前操作玩家决定
     * 玩家进行某个操作后，如果没有其他玩家可以进行操作：吃碰杠胡。 那么当前操作玩家 和 当前摸牌玩家就应该重置为 下一个摸牌玩家
     * <p>
     * 当前操作玩家如果不是摸牌玩家，那么其他玩家是不能知道是谁可以操作的
     */
    private Integer curOperatorPosId;
    private Integer beforeOperatorPosId;
    private Integer curTookCardPlayerPosId;
    private Integer beforeTookCardPlayerPosId;

    private PlayBoard playBoard;


    private List<GameStepModel> gameHistory;

    /**
     * 即使把玩家的操作级别按从大到小牌型，还是免不了遍历的过程，因为可能有多个的操作级别是一样的（多人胡 ）
     */
    private List<TempAction> tempActions;

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

    public void deal(MahjongCard[] mahjongCards, Status gameStatus, Long roomId, List<GameStepModel> historyList) {
        this.gameStatus = gameStatus;
        rollingDice();

        Map<Integer, List<Integer>> dealCardGroup = MahjongProp.getDealCardGroup(mahjongCards, bankerPosId, cardWall);

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
            GameStepModel<GameStartStep> gameStepModel = new GameStepModel<>(zoneId, mahjongSeat.getPlayer(), step);
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

    public GameStepModel<OperationCardStep> outCard(Integer card, Integer posId) {
        MahjongSeat mahjongSeat = playerSeats[posId];
        mahjongSeat.removeCardFromStandCards(card);
        OperationCardStep step = new OperationCardStep();
        StepAction outCardStepAction = new StepAction();
        outCardStepAction.setTargetCard(card)
                .setOperationType(OperationEnum.OUT_CARD)
                .setCardSource(posId);


        List<Integer> standCardList = mahjongSeat.getStandCardList();
        step.setPosId(posId)
                .setStep(stepCount)
                .setAction(outCardStepAction)
                .setGameStatus(gameStatus)
                .setRemainingCardSize(standCardList.size())
                .setStandCardList(standCardList)
                .setStandCardConvertList(MahjongProp.cardConvertName(standCardList));
        mahjongSeat.addStep(step);
        GameStepModel<OperationCardStep> stepModel = new GameStepModel<>(zoneId,mahjongSeat.getPlayer(),step);

        mahjongSeat.clearOperation();
        //出牌完成后，该玩家就变成了前一个摸牌玩家
        beforeTookCardPlayerPosId = mahjongSeat.getPosId();
        beforeOperatorPosId = mahjongSeat.getPosId();
        cardPool.add(card);
        stepCount++;
        return stepModel;
    }

    public GameStepModel<OperationCardStep> hu(Integer card, Integer posId) {
        GameStepModel<OperationCardStep> stepModel = null;
        return stepModel;
    }


    public GameStepModel<OperationCardStep> cancel(Integer card,Integer posId) {
        MahjongSeat playerSeat = playerSeats[posId];
        GameStepModel<OperationCardStep> stepModel = null;
        OperationCardStep step = new OperationCardStep();
        StepAction stepAction = new StepAction();
        step.setAction(stepAction);

        Integer cardSource = playerSeat.getDesignateOperationCardSource(OperationEnum.CANCEL.value(), card);
        if(cardSource == null){
            log.error("严重错误：服务器存储的玩家可操作权限信息，和实际操作信息不一致");
            throw new SystemException("没有匹配的可操作信息");
        }
        stepAction.setTargetCard(card)
                .setOperationType(OperationEnum.CANCEL)
                .setCardSource(cardSource);
        step.setPosId(posId)
                .setStep(stepCount)
                .setStandCardList(playerSeat.getStandCardList())
                .setRemainingCardSize(playerSeat.getStandCardList().size())
                .setStandCardConvertList(MahjongProp.cardConvertName(playerSeat.getStandCardList()))
                .setGameStatus(gameStatus)
                .setAction(stepAction);

        GameStepModel<OperationCardStep> gameStepModel = new GameStepModel<>(zoneId,playerSeat.getPlayer(),step);
        if(needJoinTempActionZone(posId)){
            TempAction tempAction = new TempAction(gameStepModel);
            tempActions.add(tempAction);
            return null;
        }
        return stepModel;
    }


    public GameStepModel<OperationCardStep> gang(Integer card, Integer type, Integer posId) {
        GameStepModel<OperationCardStep> stepModel = null;
        return stepModel;
    }

    public GameStepModel<OperationCardStep> chi(Integer card,Integer posId){
        GameStepModel<OperationCardStep> stepModel = null;
        return stepModel;
    }

    public GameStepModel<OperationCardStep> peng(Integer card, Integer posId) {
        GameStepModel<OperationCardStep> stepModel = null;
        return stepModel;
    }

    private GameStepModel<OperationCardStep> zhiGang(){
        GameStepModel<OperationCardStep> stepModel = null;
        return stepModel;
    }

    private GameStepModel<OperationCardStep> anGang(){
        GameStepModel<OperationCardStep> stepModel = null;
        return stepModel;
    }

    private GameStepModel<OperationCardStep> buGang(){
        GameStepModel<OperationCardStep> stepModel = null;
        return stepModel;
    }

    /**
     * 如果还有玩家可以操作，或者已经有玩家操作过（当前回合）
     * @param posId 操作玩家自己
     * @return
     */
    public boolean needJoinTempActionZone(Integer posId){
        if(tempActions.size() > 0){
            return true;
        }

        if(existsCanOperation(posId)){
            return true;
        }
        return false;
    }

    /**
     * 现在操作了的玩家
     * @param posId
     * @return
     */
    public boolean existsCanOperation(Integer posId){
        for(MahjongSeat mahjongSeat : playerSeats){
            if(mahjongSeat.getPosId() == posId){
                continue;
            }
            if(mahjongSeat.canOperation()){
                return true;
            }
        }
        return false;
    }

    public void setGameStatus(Status status){
        gameStatus = status;
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

    public MahjongZone<T> setCurOperatorPosId(Integer curOperatorPosId) {
        this.curOperatorPosId = curOperatorPosId;
        return this;
    }

    public Integer getBeforeOperatorPosId() {
        return beforeOperatorPosId;
    }

    public MahjongZone<T> setBeforeOperatorPosId(Integer beforeOperatorPosId) {
        this.beforeOperatorPosId = beforeOperatorPosId;
        return this;
    }

    public Integer getCurTookCardPlayerPosId() {
        return curTookCardPlayerPosId;
    }

    public MahjongZone<T> setCurTookCardPlayerPosId(Integer curTookCardPlayerPosId) {
        this.curTookCardPlayerPosId = curTookCardPlayerPosId;
        return this;
    }

    public Integer getBeforeTookCardPlayerPosId() {
        return beforeTookCardPlayerPosId;
    }

    public MahjongZone<T> setBeforeTookCardPlayerPosId(Integer beforeTookCardPlayerPosId) {
        this.beforeTookCardPlayerPosId = beforeTookCardPlayerPosId;
        return this;
    }

    public void initCurrentOperator() {
        /**
         * 游戏正式开始:只有游戏进入出牌流程前 当前操作人 和 摸牌人为null
         */
        if (curTookCardPlayerPosId == null && curOperatorPosId == null) {
            curTookCardPlayerPosId = bankerPosId;
            curOperatorPosId = bankerPosId;
        }
    }

    /**
     * @return
     */
    public Integer getNextObtainCardPosId() {
        Integer nextPosId = (curOperatorPosId + 1) % playerSeats.length;
        return nextPosId;
    }

    /**
     * 通常下一个摸牌玩家是当前操作玩家的下一个，除了杠牌外
     */
    public void refreshObtaionCardPosId() {
        curTookCardPlayerPosId = (curOperatorPosId + 1) % playerSeats.length;
        curOperatorPosId = curTookCardPlayerPosId;
    }

    public Integer TookCardFromCardWall(){
        Integer nextCard = cardWall.remove(0);
        return nextCard;
    }


    public void canChi(MahjongSeat mahjongSeat, Integer card) {
    }

    public void canPeng(MahjongSeat mahjongSeat, Integer card) {

    }

    public void canZhiGang(MahjongSeat mahjongSeat, Integer card) {
    }

    public void canBuGang(MahjongSeat mahjongSeat, Integer card) {
    }

    public void canAnGang(MahjongSeat mahjongSeat, Integer card) {
        if (card == null) {
            /**
             * 开局检测庄家
             */

        }
    }

    public void canHu(MahjongSeat mahjongSeat, Integer card) {
        if (card == null) {
            /**
             * 开局检测庄家
             */
        }
    }
}
