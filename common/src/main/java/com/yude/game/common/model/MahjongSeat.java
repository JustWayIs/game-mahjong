package com.yude.game.common.model;


import com.google.common.collect.ImmutableList;
import com.yude.game.common.contant.OperationEnum;
import com.yude.game.common.mahjong.MJManager;
import com.yude.game.common.mahjong.Meld;
import com.yude.game.common.mahjong.PlayerHand;
import com.yude.game.common.mahjong.Tile;
import com.yude.game.common.model.history.OperationCardStep;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: HH
 * @Date: 2020/8/3 19:29
 * @Version: 1.0
 * @Declare:
 */
public class MahjongSeat extends AbstractSeatModel {


    /**
     * 可操作权限，每次操作完要清空，直到轮到自己
     */
    private List<StepAction> canOperations;
    /**
     * 所有操作记录，需要做到OperationCardStep的引用指向 gameZone里面的histroy的OperrationCardStep
     */
    private List<OperationCardStep> operationhistory;

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

    private List<Integer> statusList;


    public MahjongSeat(Player player, int posId) {
        super(player, posId);
        canOperations = new ArrayList<>();
        operationhistory = new ArrayList<>();
        changce = new AtomicInteger(0);
        statusList = new ArrayList<>();
        playerHand = new PlayerHand();
    }

    @Override
    public void init() {
    }

    @Override
    public void clean() {

    }

    /**
     * 牌来源是自己
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
     *
     * @param type
     * @param card ： 因为可能有多个暗杠操作权限，需要用具体牌来区分
     * @return
     */
    public Integer getDesignateOperationCardSource(Integer type,Integer card){
        for(StepAction stepAction : canOperations){
            if(stepAction.getOperationType().value().equals(type) && stepAction.getTargetCard().equals(card)){
                return stepAction.getCardSource();
            }
        }
        return null;
    }

    public void addChangce() {
        changce.incrementAndGet();
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

    public void removeCardFromStandCards(Integer card) {
        standCardList.remove(card);

        //如果出的不是摸上来的牌，就重新理牌
        //这里找出该玩家最后一个操作的意义不仅仅在于找摸的牌，因为吃、碰操作是没有摸牌的。如果采用一个成员变量来存储上一次摸的牌，除非在吃碰的时候手动把 上一次摸的牌设为null，否则判断会有问题
        int size = operationhistory.size();
        if (size > 0) {
            OperationCardStep step = operationhistory.get(size);
            StepAction action = step.getAction();
            if (OperationEnum.TOOK_CARD.value().equals(action.getOperationType().value()) && action.getTargetCard().equals(card)) {
                solution();
            }
        } else {
            solution();
        }

    }

    public void addStep(OperationCardStep step) {
        operationhistory.add(step);
    }

    public void appendCard(Integer card) {
        standCardList.add(card);
        tookCardCount++;
        //这里没有立马排序，因为出牌之后还需要(如果出的牌不是摸的牌)排一次序
    }


    public void addStatus(Integer status) {
        statusList.add(status);
    }

    public boolean existsStatus(Integer status) {
        return statusList.contains(status);
    }

    public void removeStatus(Integer status) {
        standCardList.remove(status);
    }

    public void solution() {

        for (Integer card : standCardList) {
            Tile tile = Tile.getTileByID(card);
            playerHand.tiles.add(tile);
        }
        List<Meld> melds = new ArrayList<>();
        for (OperationCardStep step : operationhistory) {
            if (!step.getAction().getOperationType().canProductFulu()) {
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
            melds.add(meld);
        }
        playerHand.solutions = MJManager.INSTANCE.solutions(melds, playerHand.tiles, true, playerHand.bannedSuit);
    }

}
