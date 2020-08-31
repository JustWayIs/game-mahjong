package com.yude.game.common.model;


import com.yude.game.common.mahjong.MJManager;
import com.yude.game.common.mahjong.PlayerHand;
import com.yude.game.common.mahjong.Tile;
import com.yude.game.common.model.history.Step;

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
    private List<MahjongOperation> canOperations;
    /**
     * 出的牌、吃碰杠胡的牌、小结算
     */
    private List<Step> operationHistory;

    /**
     * 摸牌数量:不包含初始发牌
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


    public MahjongSeat(Player player, int posId) {
        super(player, posId);
        canOperations = new ArrayList<>();
        operationHistory = new ArrayList<>();
        changce = new AtomicInteger(0);
    }

    @Override
    public void init() {
    }

    @Override
    public void clean() {

    }

    public void addOperation(MahjongOperation operation){
        canOperations.add(operation);
    }

    public void clearOperation(){
        canOperations.clear();
    }

    public boolean canOperation(MahjongOperation operation) {
        return canOperations.contains(operation);
    }

    public void addChangce(){
        changce.incrementAndGet();
    }

    public boolean winChangce(){
        return changce.compareAndSet(1,0);
    }

    public void setStandCardList(List<Integer> standCardList) {
        this.standCardList = standCardList;
    }

    public List<Integer> getStandCardList() {
        return standCardList;
    }

    public void addTile(Tile tile){
        playerHand.tiles.add(tile);
    }

    public PlayerHand getPlayerHand() {
        return playerHand;
    }

    public void solution(){
        playerHand.solutions = MJManager.INSTANCE.solutions(playerHand.tiles, true, playerHand.bannedSuit);
    }

}
