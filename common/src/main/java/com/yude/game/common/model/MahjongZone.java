package com.yude.game.common.model;


import com.yude.game.common.constant.Status;
import com.yude.game.common.contant.MahjongStatusCodeEnum;
import com.yude.game.common.contant.OperationEnum;
import com.yude.game.common.mahjong.PlayBoard;
import com.yude.game.common.model.history.GameStartStep;
import com.yude.game.common.model.history.GameStepModel;
import com.yude.game.common.model.history.HuCardStep;
import com.yude.game.common.model.history.OperationCardStep;
import com.yude.game.common.model.sichuan.constant.SeatStatusEnum;
import com.yude.game.exception.BizException;
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
public class MahjongZone extends AbstractGameZoneModel<MahjongSeat, Status> {
    private static final Logger log = LoggerFactory.getLogger(MahjongZone.class);

    private Integer[] dice = new Integer[2];

    /**
     * 庄家
     */
    private Integer bankerPosId;

    /**
     * 牌墙打乱顺序后的所有牌（不会被改变）：用于做记录
     */
    private List<Integer> allCard;

    /**
     * 牌墙
     */
    private List<Integer> cardWall;
    /**
     * 出的牌：不包括被吃碰杠的牌 -- 并没有做这样的处理，现在是包含的
     * 服务器不应该直接信任客户端传上来的目标牌，而是应该使用 cardPool里的最后一张牌？  或者从hitory里面取最后一个操作，那里有更完整的最后一次操作的信息。验证的时候用cardPool。响应的时候用hisory ???
     * 额，其实可以不用这个和history来判断，因为玩家的可操作权限集合里面，保存了可以进行的操作的具体信息
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

    private Map<Integer,Integer> cardRemainingMap;

    /**
     * 即使把玩家的操作级别按从大到小牌型，还是免不了遍历的过程，因为可能有多个的操作级别是一样的（多人胡 ）
     */
    private List<TempAction> tempActions;


    /**
     * 本质上来说，是帮助当前有操作权限但是并未的玩家进行操作。虽然麻将可能在一个回合中有多个玩家操作，但是他们应该共享一个超时间
     */
    private volatile long timeoutTime;

    public MahjongZone(MahjongSeat[] playerSeats, int round, int inning) {
        super(playerSeats, round, inning);
        gameHistory = new ArrayList<>();
    }

    @Override
    public void init() {
        cardWall = new ArrayList<>();
        cardPool = new ArrayList<>();
        tempActions = new ArrayList<>();
        allCard = new ArrayList<>();
        cardRemainingMap = new HashMap<>();
    }

    @Override
    public void clean() {

    }

    public void deal(MahjongCard[] mahjongCards, Status gameStatus, Long roomId, List<GameStepModel> historyList) {
        this.gameStatus = gameStatus;
        rollingDice();

        Map<Integer, List<Integer>> dealCardGroup = MahjongProp.getDealCardGroup(mahjongCards, bankerPosId, allCard);
        //对allCard进行分组，用于标识 某张牌的剩余张数
        //cardRemainingMap = allCard.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        for(Integer card : allCard){
            cardRemainingMap.compute(card,(k,num) -> {
                if(num == null){
                    num = 0;
                }
                return ++num;
            });
        }

        cardWall.addAll(dealCardGroup.get(4));
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

    public boolean checkCurrentGameStatus(Status gameStatusEnum) {
        return gameStatusEnum.equals(gameStatus);
    }

    public GameStepModel<OperationCardStep> outCard(Integer card, Integer posId) {
        MahjongSeat mahjongSeat = playerSeats[posId];
        final boolean haveCard = mahjongSeat.removeCardFromStandCards(card);
        if(!haveCard){
            log.error("不能出立牌中不存在的牌： userId={} posId={} card={}",mahjongSeat.getUserId(),posId,card);
            throw new BizException(MahjongStatusCodeEnum.CARD_NOT_EXISTS_IN_STAND);
        }
        OperationCardStep step = new OperationCardStep();
        StepAction outCardStepAction = new StepAction();
        outCardStepAction.setTargetCard(card)
                .setOperationType(OperationEnum.OUT_CARD)
                .setCardSource(posId);


        List<Integer> standCardList = new ArrayList<>(mahjongSeat.getStandCardList());
        step.setPosId(posId)
                .setStep(stepCount)
                .setAction(outCardStepAction)
                .setGameStatus(gameStatus)
                .setRemainingCardSize(standCardList.size())
                .setStandCardList(standCardList)
                .setStandCardConvertList(MahjongProp.cardConvertName(standCardList));
        mahjongSeat.addStep(step);
        GameStepModel<OperationCardStep> stepModel = new GameStepModel<>(zoneId, mahjongSeat.getPlayer(), step);

        mahjongSeat.clearOperation();
        //出牌完成后，该玩家就变成了前一个摸牌玩家 -- 实际在后面都没有对这两个值做过修改
        beforeTookCardPlayerPosId = mahjongSeat.getPosId();
        beforeOperatorPosId = mahjongSeat.getPosId();
        cardPool.add(card);
        Integer remaining = cardRemainingMap.get(card);
        cardRemainingMap.put(card,--remaining);
        stepCount++;
        return stepModel;
    }

    /**
     * @param card
     * @param posId
     * @return
     */
    public GameStepModel<HuCardStep> hu(Integer card, Integer posId,boolean isZiMo) {
        /**
         * 把胡的牌 加入到立牌中
         */
        MahjongSeat playerSeat = playerSeats[posId];
        List<Integer> standCardList = playerSeat.getStandCardList();

        //playerSeat.solution();
        playerSeat.addStatus(SeatStatusEnum.ALREADY_HU);
        GameStepModel<OperationCardStep> operation = operation(card, posId, OperationEnum.HU);
        OperationCardStep operationStep = operation.getOperationStep();

        Integer remaining = cardRemainingMap.get(card);
        if(isZiMo){
            cardRemainingMap.put(card,--remaining);
        }else{
            //移除放炮玩家的出牌池里 放炮的那张牌
            final StepAction action = operationStep.getAction();
            final Integer cardSource = action.getCardSource();
            final MahjongSeat outCardMahjongSeat = playerSeats[cardSource];
            outCardMahjongSeat.removeLastOutCard();
        }


        HuCardStep huCardStep = new HuCardStep();
        huCardStep.setAction(operationStep.getAction())
                .setGameStatus(operationStep.getGameStatus())
                .setStandCardConvertList(operationStep.getStandCardConvertList())
                .setStandCardList(operationStep.getStandCardList())
                .setRemainingCardSize(operationStep.getRemainingCardSize())
                .setStep(operationStep.getStep())
                .setPosId(operationStep.getPosId());
        //最终应不应该把胡的牌从手牌中拿出来呢？
        if(!isZiMo){
            standCardList.add(card);
        }
        Collections.sort(standCardList);
        //要删掉胡牌的 OperationCardStep 改成用 HuCardStep
        List<OperationCardStep> operationHistory = playerSeat.getOperationHistory();
        operationHistory.remove(operationStep);
        playerSeat.addStep(huCardStep);
        GameStepModel<HuCardStep> huStepModel =  new GameStepModel<>(zoneId,operation.getPlayers(),huCardStep);
        return huStepModel;
    }


    public GameStepModel<OperationCardStep> cancel(Integer card, Integer posId) {
        MahjongSeat playerSeat = playerSeats[posId];
        OperationCardStep step = new OperationCardStep();
        StepAction stepAction = new StepAction();
        step.setAction(stepAction);

        StepAction action = playerSeat.getDesignateOperationCardSource(OperationEnum.CANCEL.value(), card);
        Integer cardSource = action.getCardSource();
        if (cardSource == null) {
            log.error("严重错误：服务器存储的玩家可操作权限信息，和实际操作信息不一致");
            throw new SystemException("没有匹配的可操作信息");
        }
        stepAction.setTargetCard(card)
                .setOperationType(OperationEnum.CANCEL)
                .setCardSource(cardSource);

        List<Integer> standCardList = playerSeat.getStandCardList();
        step.setPosId(posId)
                .setStep(stepCount)
                .setStandCardList(standCardList)
                .setRemainingCardSize(standCardList.size())
                .setStandCardConvertList(MahjongProp.cardConvertName(standCardList))
                .setGameStatus(gameStatus)
                .setAction(stepAction);

        GameStepModel<OperationCardStep> gameStepModel = new GameStepModel<>(zoneId, playerSeat.getPlayer(), step);
        stepCount++;
        playerSeat.clearOperation();

        if (needJoinTempActionZone(OperationEnum.CANCEL)) {
            TempAction tempAction = new TempAction(posId, playerSeat.getUserId(), stepAction);
            tempActions.add(tempAction);
            Collections.sort(tempActions);
        }
        return gameStepModel;
    }


    public GameStepModel<OperationCardStep> buGang(Integer card, Integer posId) {
        GameStepModel<OperationCardStep> stepModel = null;
        return stepModel;
    }

    public GameStepModel<OperationCardStep> zhiGang(Integer card, Integer posId) {
        GameStepModel<OperationCardStep> stepModel = null;
        return stepModel;
    }

    public GameStepModel<OperationCardStep> anGang(Integer card, Integer posId) {
        MahjongSeat playerSeat = playerSeats[posId];
        OperationCardStep step = new OperationCardStep();
        StepAction stepAction = new StepAction();
        step.setAction(stepAction);

        StepAction action = playerSeat.getDesignateOperationCardSource(OperationEnum.CANCEL.value(), card);
        Integer cardSource = action.getCardSource();
        if (cardSource == null) {
            log.error("严重错误：服务器存储的 玩家吃 操作信息，和实际操作信息不一致");
            throw new SystemException("没有匹配的可操作信息");
        }
        stepAction.setTargetCard(card)
                .setOperationType(OperationEnum.AN_GANG)
                .setCardSource(cardSource)
                .setCombinationRsult(new ArrayList<>(Arrays.asList(card, card, card, card)));

        List<Integer> standCardList = playerSeat.getStandCardList();
        step.setPosId(posId)
                .setStep(stepCount)
                .setStandCardList(standCardList)
                .setRemainingCardSize(standCardList.size())
                .setStandCardConvertList(MahjongProp.cardConvertName(standCardList))
                .setGameStatus(gameStatus)
                .setAction(stepAction);

        GameStepModel<OperationCardStep> gameStepModel = new GameStepModel<>(zoneId, playerSeat.getPlayer(), step);
        stepCount++;
        playerSeat.clearOperation();

        if (needJoinTempActionZone(OperationEnum.AN_GANG)) {
            TempAction tempAction = new TempAction(posId, playerSeat.getUserId(), stepAction);
            tempActions.add(tempAction);
            Collections.sort(tempActions);
        }
        return gameStepModel;
    }

    /**
     * 貌似这一个方法可以用来代替 吃、碰、补杠、直杠、暗杠
     *
     * @param card
     * @param posId
     * @param operationType
     * @return
     */
    public GameStepModel<OperationCardStep> operation(Integer card, Integer posId, MahjongOperation operationType) {
        MahjongSeat playerSeat = playerSeats[posId];
        List<Integer> cardCombination = null;
        OperationEnum value = OperationEnum.values()[operationType.value()];
        List<Integer> standCardListSort = playerSeat.getStandCardList();

        StepAction action = playerSeat.getDesignateOperationCardSource(operationType.value(), card);
        Integer cardSource = action.getCardSource();

        if (cardSource == null) {
            log.error("严重错误：操作 operation ={} ，和实际操作信息不一致", operationType);
            throw new SystemException("没有匹配的可操作信息");
        }

        //把被吃碰杠的牌，从出牌玩家的出牌池中移除
        MahjongSeat outCardSeat = playerSeats[cardSource];

        Integer remaining = cardRemainingMap.get(card);
        switch (value) {
            case PENG:
                remaining -= 2;
                cardRemainingMap.put(card,remaining);
                cardCombination = new ArrayList<>(Arrays.asList(card, card, card));
                playerSeat.removeCard(card);
                playerSeat.removeCard(card);
                //Collections.sort(standCardListSort);
                playerSeat.solution();

                outCardSeat.removeLastOutCard();
                break;
            case ZHI_GANG:
                remaining -= 3;
                cardRemainingMap.put(card,remaining);
                cardCombination = new ArrayList<>(Arrays.asList(card, card, card, card));
                playerSeat.removeCard(card);
                playerSeat.removeCard(card);
                playerSeat.removeCard(card);
                //Collections.sort(standCardListSort);

                outCardSeat.removeLastOutCard();
                break;
            case BU_GANG:
                remaining -= 1;
                cardRemainingMap.put(card,remaining);
                cardCombination = new ArrayList<>(Arrays.asList(card, card, card, card));
                playerSeat.removeCard(card);
                Collections.sort(standCardListSort);
                //补杠把碰的step标记为无效，以免通过玩家位置获取副露信息的时候，还把碰作为副露
                final OperationCardStep desinateStep = playerSeat.getDesinateStep(OperationEnum.PENG.value(), card);
                desinateStep.setEffective(false);
                break;
            case AN_GANG:
                remaining -= 4;
                cardRemainingMap.put(card,remaining);
                cardCombination = new ArrayList<>(Arrays.asList(card, card, card, card));
                playerSeat.removeCard(card);
                playerSeat.removeCard(card);
                playerSeat.removeCard(card);
                playerSeat.removeCard(card);
                //Collections.sort(standCardListSort);
                playerSeat.solution();
                break;
            case CHI:
                Integer beforeCardRemaning = cardRemainingMap.get(card + 1);
                cardRemainingMap.put(card + 1,--beforeCardRemaning);
                Integer afterCardRemaining = cardRemainingMap.get(card - 1);
                cardRemainingMap.put(card - 1,--afterCardRemaining);

                cardCombination = new ArrayList<>(Arrays.asList(card - 1, card, card + 1));
                playerSeat.removeCard(card - 1);
                playerSeat.removeCard(card + 1);
                //Collections.sort(standCardListSort);
                playerSeat.solution();

                outCardSeat.removeLastOutCard();
                break;
            case HU:
                //自摸才减
                //cardRemainingMap.put(card,--remaining);

                cardCombination = new ArrayList<>();
                cardCombination.addAll(playerSeat.getStandCardList());
                //应该不需要把副露拼回手牌
                /*List<StepAction> fuLuOperations = playerSeat.getFuLuOperationsByType();
                for(StepAction stepAction : fuLuOperations){
                    cardCombination.addAll(stepAction.getCombinationRsult());
                }
                Collections.sort(cardCombination);*/
                break;
            default:
                ;
        }


        OperationCardStep step = new OperationCardStep();
        StepAction stepAction = new StepAction();
        step.setAction(stepAction);


        List<Integer> standCardList = new ArrayList<>(playerSeat.getStandCardList());
        stepAction.setTargetCard(card)
                .setOperationType(operationType)
                .setCardSource(cardSource)
                .setCombinationRsult(cardCombination);
        step.setPosId(posId)
                .setStep(stepCount)
                .setStandCardList(standCardList)
                .setRemainingCardSize(standCardList.size())
                .setStandCardConvertList(MahjongProp.cardConvertName(standCardList))
                .setGameStatus(gameStatus)
                .setAction(stepAction);

        GameStepModel<OperationCardStep> gameStepModel = new GameStepModel<>(zoneId, playerSeat.getPlayer(), step);
        playerSeat.addStep(step);

        stepCount++;
        playerSeat.clearOperation();

        if (needJoinTempActionZone(operationType)) {
            TempAction tempAction = new TempAction(posId, playerSeat.getUserId(), stepAction);
            tempActions.add(tempAction);
            Collections.sort(tempActions);
        }
        return gameStepModel;
    }

    public GameStepModel<OperationCardStep> chi(Integer card, Integer posId) {
        MahjongSeat playerSeat = playerSeats[posId];
        OperationCardStep step = new OperationCardStep();
        StepAction stepAction = new StepAction();
        step.setAction(stepAction);

        StepAction action = playerSeat.getDesignateOperationCardSource(OperationEnum.CANCEL.value(), card);
        Integer cardSource = action.getCardSource();
        if (cardSource == null) {
            log.error("严重错误：服务器存储的 玩家吃 操作信息，和实际操作信息不一致");
            throw new SystemException("没有匹配的可操作信息");
        }
        stepAction.setTargetCard(card)
                .setOperationType(OperationEnum.CHI)
                .setCardSource(cardSource)
                .setCombinationRsult(new ArrayList<>(Arrays.asList(card - 1, card, card + 1)));

        List<Integer> standCardList = playerSeat.getStandCardList();
        step.setPosId(posId)
                .setStep(stepCount)
                .setStandCardList(standCardList)
                .setRemainingCardSize(standCardList.size())
                .setStandCardConvertList(MahjongProp.cardConvertName(standCardList))
                .setGameStatus(gameStatus)
                .setAction(stepAction);

        GameStepModel<OperationCardStep> gameStepModel = new GameStepModel<>(zoneId, playerSeat.getPlayer(), step);
        stepCount++;
        playerSeat.clearOperation();

        if (needJoinTempActionZone(OperationEnum.CHI)) {
            TempAction tempAction = new TempAction(posId, playerSeat.getUserId(), stepAction);
            tempActions.add(tempAction);
            Collections.sort(tempActions);
        }
        return gameStepModel;
    }

    public GameStepModel<OperationCardStep> peng(Integer card, Integer posId) {
        MahjongSeat playerSeat = playerSeats[posId];
        OperationCardStep step = new OperationCardStep();
        StepAction stepAction = new StepAction();
        step.setAction(stepAction);

        StepAction action = playerSeat.getDesignateOperationCardSource(OperationEnum.CANCEL.value(), card);
        Integer cardSource = action.getCardSource();
        if (cardSource == null) {
            log.error("严重错误：服务器存储的 玩家碰 操作信息，和实际操作信息不一致");
            throw new SystemException("没有匹配的可操作信息");
        }
        stepAction.setTargetCard(card)
                .setOperationType(OperationEnum.PENG)
                .setCardSource(cardSource)
                .setCombinationRsult(new ArrayList<>(Arrays.asList(card, card, card)));

        List<Integer> standCardList = playerSeat.getStandCardList();
        step.setPosId(posId)
                .setStep(stepCount)
                .setStandCardList(standCardList)
                .setRemainingCardSize(standCardList.size())
                .setStandCardConvertList(MahjongProp.cardConvertName(standCardList))
                .setGameStatus(gameStatus)
                .setAction(stepAction);

        GameStepModel<OperationCardStep> gameStepModel = new GameStepModel<>(zoneId, playerSeat.getPlayer(), step);
        stepCount++;
        playerSeat.clearOperation();

        if (needJoinTempActionZone(OperationEnum.PENG)) {
            TempAction tempAction = new TempAction(posId, playerSeat.getUserId(), stepAction);
            tempActions.add(tempAction);
            Collections.sort(tempActions);
        }
        return gameStepModel;
    }


    /**
     * 如果还有玩家可以操作，或者已经有玩家操作过（当前回合）
     *
     * @return
     */
    public boolean needJoinTempActionZone(MahjongOperation operation) {
        /**
         * 是否已经有玩家先操作了，在该回合
         */
        if (tempActions.size() > 0) {
            return true;
        }

        if (existsCanOperation(operation)) {
            return true;
        }
        return false;
    }

    /**
     *
     * 如果存在更高优先级，就需要等待
     * @return
     */
    public boolean existsCanOperation(MahjongOperation mahjongOperation) {
        for (MahjongSeat mahjongSeat : playerSeats) {
            if (mahjongSeat.canOperation()) {
                for(StepAction stepAction : mahjongSeat.getCanOperations()){
                    final MahjongOperation operationType = stepAction.getOperationType();
                    if(operationType.priority() >= mahjongOperation.priority()){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 每次多操作结束都要清理
     */
    public void cleanTempAction() {
        tempActions.clear();
    }

    public boolean cardWallHasCard(){
        return cardWall.size() > 0;
    }

    public List<TempAction> getTempActions() {
        return tempActions;
    }


    public void setGameStatus(Status status) {
        gameStatus = status;
    }

    public PlayBoard getPlayBoard() {
        return playBoard;
    }

    public MahjongZone setPlayBoard(PlayBoard playBoard) {
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

    public MahjongZone setCurOperatorPosId(Integer curOperatorPosId) {
        this.curOperatorPosId = curOperatorPosId;
        return this;
    }

    public Integer getBeforeOperatorPosId() {
        return beforeOperatorPosId;
    }

    public MahjongZone setBeforeOperatorPosId(Integer beforeOperatorPosId) {
        this.beforeOperatorPosId = beforeOperatorPosId;
        return this;
    }

    public Integer getCurTookCardPlayerPosId() {
        return curTookCardPlayerPosId;
    }

    public MahjongZone setCurTookCardPlayerPosId(Integer curTookCardPlayerPosId) {
        this.curTookCardPlayerPosId = curTookCardPlayerPosId;
        return this;
    }

    public Integer getBeforeTookCardPlayerPosId() {
        return beforeTookCardPlayerPosId;
    }

    public MahjongZone setBeforeTookCardPlayerPosId(Integer beforeTookCardPlayerPosId) {
        this.beforeTookCardPlayerPosId = beforeTookCardPlayerPosId;
        return this;
    }

    public long getTimeoutTime() {
        return timeoutTime;
    }

    public MahjongZone setTimeoutTime(long timeoutTime) {
        this.timeoutTime = timeoutTime;
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
        return getNexPosId(curOperatorPosId);
    }

    public int getNexPosId(int posId) {
        Integer nextPosId = (posId + 1) % playerSeats.length;
        return nextPosId;
    }

    /**
     * 出牌后
     * 通常下一个摸牌玩家是当前操作玩家的下一个，除了杠牌外
     */
    public void refreshObtaionCardPosId() {
        curTookCardPlayerPosId = getNexPosId(curOperatorPosId);
        curOperatorPosId = curTookCardPlayerPosId;
    }

    public void refreshCurrentPosId(int posId) {
        curTookCardPlayerPosId = posId;
        curOperatorPosId = posId;
    }

    public void pengAfterRefresh(int pengSeatPosId) {
        int nexPosId = getNexPosId(pengSeatPosId);
        curOperatorPosId = pengSeatPosId;
    }

    public Integer TookCardFromCardWall() {
        Integer nextCard = cardWall.remove(0);
        return nextCard;
    }

    public void setLastOperationTime(long time) {
        this.lastOperationTime = time;
    }


    public Integer getCardRemainingNum(Integer card){
        return cardRemainingMap.get(card);
    }

    /**
     * 获取操作已经超时的位置 和 托管的位置
     * @return
     */
    public List<MahjongSeat> getNeedImmediatelyExecuteSeats(){
        List<MahjongSeat> mahjongSeatList = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        boolean isTimeOut = false;
        /*if(timeoutTime <= currentTime){
            isTimeOut = true;
        }*/
        for(MahjongSeat seat : playerSeats){
            if(seat.canOperation() && (seat.isAutoOperation()  || seat.getTimeoutTime() < currentTime)){
                mahjongSeatList.add(seat);
            }
        }
        return mahjongSeatList;
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
