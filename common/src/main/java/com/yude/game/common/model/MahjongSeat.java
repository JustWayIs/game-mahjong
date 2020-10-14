package com.yude.game.common.model;


import com.google.common.collect.ImmutableList;
import com.yude.game.common.contant.OperationEnum;
import com.yude.game.common.mahjong.MJManager;
import com.yude.game.common.mahjong.Meld;
import com.yude.game.common.mahjong.PlayerHand;
import com.yude.game.common.mahjong.Tile;
import com.yude.game.common.model.history.OperationCardStep;
import com.yude.game.common.model.sichuan.constant.SeatStatusEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: HH
 * @Date: 2020/8/3 19:29
 * @Version: 1.0
 * @Declare:
 */
public class MahjongSeat extends AbstractSeatModel implements Cloneable{

    private long carryScore;

    /**
     * 可操作权限，每次操作完要清空，直到轮到自己
     */
    private List<StepAction> canOperations;
    /**
     * 所有操作记录，需要做到OperationCardStep的引用指向 gameZone里面的histroy的OperrationCardStep
     */
    private List<OperationCardStep> operationHistory;

    /**
     * 摸牌数量:不包含初始发牌 --> 因为把摸牌也视为一个操作，添加进operations里面了，所以这个属性可以不用
     * 但是如果考虑到空间换时间，倒是可以用
     */
    private int tookCardCount;

    private boolean qiangGang;

    /**
     * 托管
     */
    private boolean auto;

    /**
     * 立牌
     */
    private List<Integer> standCardList;

    /**
     * 玩家的牌的信息
     */
    private PlayerHand playerHand;

    /**
     * 操作机会，用于应对重复操作
     */
    private AtomicInteger changce;

    private List<SeatStatusEnum> seatStatusList;

    /**
     * 出过的牌：不能包含被吃碰杠的牌
     */
    private List<Integer> outCardPool;

    private long timeoutTime;


    public MahjongSeat(Player player, int posId) {
        super(player, posId);
        canOperations = new ArrayList<>();
        operationHistory = new ArrayList<>();
        changce = new AtomicInteger(0);
        seatStatusList = new ArrayList<>();
        playerHand = new PlayerHand();
        carryScore = player.getScore();
        outCardPool = new ArrayList<>();
    }

    @Override
    public void init() {
    }

    @Override
    public void clean() {

    }

    public void addOperations(List<StepAction> canOperations){
        this.canOperations.addAll(canOperations);
    }

    /**
     * 牌来源是自己 并且 不需要记录牌值
     * @param operation
     */
    public void addOperation(MahjongOperation operation) {
        StepAction stepAction = new StepAction();
        stepAction.setOperationType(operation)
                .setCardSource(posId);
        canOperations.add(stepAction);
    }

    /**
     * 需要记录牌的来源的动作
     * @param operation
     */
    public void addOperation(StepAction operation) {
        canOperations.add(operation);
    }

    public void clearOperation() {
        canOperations.clear();
    }

    /**
     * 自摸胡牌的时候，点过不能把出牌权限也去掉
     * @param excludeOperation
     */
    public void clearOperation(Integer excludeOperation){
        Iterator<StepAction> iterator = canOperations.iterator();
        while (iterator.hasNext()){
            StepAction next = iterator.next();
            Integer type = next.getOperationType().value();
            if(!type.equals(excludeOperation)){
                iterator.remove();
            }
        }
    }

    public boolean canOperation() {
        return canOperations.size() > 0;
    }

    public boolean canOperation(MahjongOperation operation) {
        for(StepAction stepAction : canOperations){
            if(operation.value().equals(stepAction.getOperationType().value())){
                return true;
            }
        }
        return false;
    }


    /**
     *  获取指定的权限
     * @param type 不用MahjongOperation是因为 不确定用的是MahjongOperation 还是地方麻将的 Operation.但是他们的值是一样的
     * @param card ： 因为可能有多个暗杠操作权限，需要用具体牌来区分
     * @return
     */
    public StepAction getDesignateOperationCardSource(Integer type,Integer card){
        for(StepAction stepAction : canOperations){
            if(stepAction.getOperationType().value().equals(type) && stepAction.getTargetCard().equals(card)){
                return stepAction;
            }
        }
        return null;
    }

    public List<StepAction> getFuLu(){
        List<Integer> fuluList = OperationEnum.getFuluList();
        List<StepAction> list = getDesinateTypeAction(fuluList);
        return list;
    }


    /**
     *
     * @param desinateTypeList 操作枚举的值的集合
     * @return
     */
    public List<StepAction> getDesinateTypeAction(List<Integer> desinateTypeList){
        List<StepAction> list = new ArrayList<>();
        for(OperationCardStep operationCardStep : operationHistory){
            if (operationCardStep.isEffective()) {
                StepAction stepAction = operationCardStep.getAction();
                Integer value = stepAction.getOperationType().value();
                if(desinateTypeList.contains(value)){
                    list.add(stepAction);
                }
            }
        }
        return list;
    }

    public OperationCardStep getDesinateStep(Integer type,Integer card){
        for(OperationCardStep operationCardStep : operationHistory){
            StepAction stepAction = operationCardStep.getAction();
            Integer value = stepAction.getOperationType().value();
            if(type.equals(value) && stepAction.getTargetCard().equals(card)){
                return operationCardStep;
            }
        }
        return null;
    }

    public List<OperationCardStep> getDesinateStep(List<Integer> desinateTypeList){
        List<OperationCardStep> list = new ArrayList<>();
        for(OperationCardStep operationCardStep : operationHistory){
            StepAction stepAction = operationCardStep.getAction();
            Integer value = stepAction.getOperationType().value();
            if(desinateTypeList.contains(value)){
                list.add(operationCardStep);
            }
        }
        return list;
    }

    public void addChangce() {
        changce.incrementAndGet();
    }

    public long getCarryScore() {
        return carryScore;
    }


    public boolean winChangce() {
        return changce.compareAndSet(1, 0);
    }

    public void setStandCardList(List<Integer> standCardList) {
        this.standCardList = standCardList;
    }

    public List<Integer> getStandCardList() {
        return standCardList;
    }

    public PlayerHand getPlayerHand() {
        return playerHand;
    }

    public List<StepAction> getCanOperations() {
        return canOperations;
    }

    public int getTookCardCount() {
        return tookCardCount;
    }

    public List<OperationCardStep> getOperationHistory() {
        return operationHistory;
    }

    public OperationCardStep getOperationHistoryByTypeAndCard(Integer type,Integer card){
        for(OperationCardStep step : operationHistory){
            StepAction action = step.getAction();
            Integer value = action.getOperationType().value();
            if(value.equals(type) && action.getTargetCard().equals(card)){
                return step;
            }
        }
        return null;
    }

    /**
     * 判断是否是杠上操作：杠上开花、杠上炮
     * 和判断抢杠操作一样，由于是依赖于step来判断，所以只在当前情景能够做出有效判断，如果结算之后再调用，就永远得不到正确结果
     * @return
     */
    public boolean judgeIsGangShangOperation(){
        //因为杠之后的操作是摸牌，而摸牌也被做为一个step记录起来，所以找是不是杠上操作，应该找该玩家的前前操作
        int index = operationHistory.size() - 2;
        if(index > 0){
            OperationCardStep step = operationHistory.get(index);
            StepAction action = step.getAction();
            Integer type = action.getOperationType().value();
            if(OperationEnum.ZHI_GANG.value().equals(type) || OperationEnum.BU_GANG.value().equals(type) || OperationEnum.AN_GANG.value().equals(type)){
                return true;
            }
        }
        return false;
    }

    public boolean judgeIsQiangGang(){
        int index = operationHistory.size() - 1;
        if(index > 0){
            OperationCardStep step = operationHistory.get(index);
            StepAction action = step.getAction();
            Integer type = action.getOperationType().value();
            if(OperationEnum.BU_GANG.value().equals(type)){
                qiangGang = true;
                return true;
            }
        }
        return false;
    }

    public void removeCard(Integer card){
        standCardList.remove(card);
    }

    public boolean removeCardFromStandCards(Integer card) {
        final boolean remove = standCardList.remove(card);
        if(!remove){
            return remove;
        }
        outCard(card);

        //如果出的不是摸上来的牌，就重新理牌
        //这里找出该玩家最后一个操作的意义不仅仅在于找摸的牌，因为吃、碰操作是没有摸牌的。如果采用一个成员变量来存储上一次摸的牌，除非在吃碰的时候手动把 上一次摸的牌设为null，否则判断会有问题
        int size = operationHistory.size();
        if (size > 0) {
            OperationCardStep step = operationHistory.get(size - 1);
            StepAction action = step.getAction();
            if (OperationEnum.TOOK_CARD.value().equals(action.getOperationType().value()) && !action.getTargetCard().equals(card)) {
                solution();
                return remove;
            }
        }
        Collections.sort(standCardList);
        solution();
        return remove;
    }

    public void addStep(OperationCardStep step) {
        operationHistory.add(step);
    }

    public void appendCard(Integer card) {
        standCardList.add(card);
        tookCardCount++;
        //这里没有立马排序，因为出牌之后还需要(如果出的牌不是摸的牌)排一次序
    }


    public void addStatus(SeatStatusEnum status) {
        seatStatusList.add(status);
    }

    public boolean existsStatus(SeatStatusEnum status) {
        return seatStatusList.contains(status);
    }

    public void removeStatus(Integer status) {
        standCardList.remove(status);
    }

    public void outCard(Integer card){
        outCardPool.add(card);
    }

    /**
     * 出的牌被吃碰杠胡后调用
     */
    public void removeLastOutCard(){
        outCardPool.remove(outCardPool.size()-1);
    }

    public List<Integer> getOutCardPool() {
        return outCardPool;
    }

    public List<SeatStatusEnum> getSeatStatusList() {
        return seatStatusList;
    }

    public int getSerialTimeoutCount(){
        return  serialTimeoutCount;
    }

    public long getTimeoutTime() {
        return timeoutTime;
    }

    public void setTimeoutTime(long timeoutTime) {
        this.timeoutTime = timeoutTime;
    }

    public void resetSerialTimeoutCount(){
        serialTimeoutCount = 0;
    }


    @Override
    public MahjongSeat clone() throws CloneNotSupportedException {
        final MahjongSeat cloneMahjongSeat = (MahjongSeat) super.clone();
        cloneMahjongSeat.playerHand = cloneMahjongSeat.playerHand.clone();
        cloneMahjongSeat.standCardList = new ArrayList<>(cloneMahjongSeat.getStandCardList());
        return cloneMahjongSeat;
    }

    public void solution() {
        playerHand.tiles.clear();
        playerHand.melds.clear();
        for (Integer card : standCardList) {
            Tile tile = Tile.getTileByID(card);
            playerHand.tiles.add(tile);
        }

        for (OperationCardStep step : operationHistory) {
            if (!step.getAction().getOperationType().canProductFulu()) {
                continue;
            }
            if(!step.isEffective()){
                continue;
            }
            Meld meld = new Meld();
            StepAction action = step.getAction();
            List<Integer> combinationRsult = action.getCombinationRsult();
            List<Tile> tiles = Tile.cardConvertTileList(combinationRsult);
            ImmutableList<Tile> fuluIiles = ImmutableList.copyOf(tiles);

            //这里不能用枚举，因为StepAction里面的MahjongOperation并不是固定的
            int operationType = action.getOperationType().value();
            meld.setTiles(fuluIiles)
                    .operationConvertType(operationType)
                    .converPlayer(step.getPosId(), action.getCardSource())
                    .convertFrom(step.getPosId(), action.getCardSource())
                    .setPlusKong(OperationEnum.BU_GANG.value() == operationType)
                    .setStable(OperationEnum.PENG.value() == operationType);
            playerHand.melds.add(meld);
        }
        playerHand.solutions = MJManager.INSTANCE.solutions(playerHand.melds, playerHand.tiles, true, playerHand.bannedSuit);
    }

    @Override
    public String toString() {
        return "MahjongSeat{" +
                "carryScore=" + carryScore +
                ", canOperations=" + canOperations +
                ", operationHistory=" + operationHistory +
                ", tookCardCount=" + tookCardCount +
                ", qiangGang=" + qiangGang +
                ", auto=" + auto +
                ", standCardList=" + standCardList +
                ", playerHand=" + playerHand +
                ", changce=" + changce +
                ", seatStatusList=" + seatStatusList +
                ", outCardPool=" + outCardPool +
                ", player=" + player +
                ", posId=" + posId +
                ", isAutoOperation=" + isAutoOperation +
                ", serialTimeoutCount=" + serialTimeoutCount +
                "} ";
    }
}
