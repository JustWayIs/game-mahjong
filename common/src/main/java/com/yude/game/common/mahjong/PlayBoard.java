package com.yude.game.common.mahjong;

import com.google.common.collect.ImmutableList;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by someone on 2020/8/5 21:41.
 */
public class PlayBoard {
    // ===========================================================
    // Constants
    // ===========================================================


    // ===========================================================
    // Fields
    // ===========================================================
    public final int                       banker;
    public final ImmutableList<Tile> tiles; // 初始洗牌
    public final String                    tilesEncoded; // 初始洗牌编码
    public       int                       drawIndex; // 发牌游标，从0开始
    public final ImmutableList<PlayerHand> playerHands;

    // ===========================================================
    // Constructors
    // ===========================================================
    public PlayBoard(int pBanker, List<Tile> pShuffledTiles) {
        this.banker = pBanker % 4; // 玩家编号{0,1,2,3}
        this.tiles = ImmutableList.copyOf(pShuffledTiles);
        this.tilesEncoded = MJManager.INSTANCE.encode(this.tiles);
        // 初始游标位置，3家13张牌，庄家14张牌
        this.drawIndex = 13 * 4 + 1;
        this.playerHands = ImmutableList.of(new PlayerHand(), new PlayerHand(), new PlayerHand(), new PlayerHand());
        // 发牌

    }


    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("洗牌: ").append(System.lineSeparator());
        for(int i = 0; i < this.tiles.size(); i++){
            if(i % 17 == 0){
                s.append(i > 0 ? System.lineSeparator() : "").append("　　");
            }
            s.append(this.tiles.get(i).name).append(this.drawIndex == i ? "< " : " ");
            if(i == this.tiles.size() - 1){
                s.append(System.lineSeparator());
            }
        }
        s.append("玩家: ").append(System.lineSeparator());
        for(int i = 0; i < this.playerHands.size(); i++){
            PlayerHand hand = this.playerHands.get(i);
            s.append(this.banker == i ? "庄　" : "　　").append(i).append(" ");
            s.append(hand.toString()).append(" ");
            s.append(MJManager.INSTANCE.encode(hand)).append(System.lineSeparator());
        }
        return s.toString();
    }

    // ===========================================================
    // Methods
    // ===========================================================
    public void deal( Map<Integer, List<Integer>> cardGroup,Map<Integer,Integer> queColorMap){
        for(int i = 0 ; i < 4 ; ++i){
            List<Integer> cardList = cardGroup.get(i);
            for(Integer card : cardList){
                Tile tile = Tile.getTileByID(card);
                this.playerHands.get(i).tiles.add(tile);
                this.playerHands.get(i).bannedSuit = queColorMap.get(i);
            }

        }
        /*// 非跳牌阶段
        for (int i = 0; i < 48; i++) {
            // 摸牌顺序: (i / 4) % 4
            // 当前摸牌玩家: (banker + 摸牌顺序) % 4
            this.playerHands.get((this.banker + (i / 4) % 4) % 4).tiles.add(tiles.get(i));
        }
        // 跳牌阶段
        for (int i = 48; i < 53; i++) {
            // 摸牌顺序: i % 4
            // 当前摸牌玩家: (banker + 摸牌顺序) % 4
            this.playerHands.get((this.banker + i % 4) % 4).tiles.add(tiles.get(i));
        }*/
        //Random random = new Random();
        // 排序
        for(PlayerHand hand : this.playerHands){
            hand.tiles.sort(Comparator.comparingInt(t -> t.id));
            //这个属性用于定缺后的理牌，把定缺花色的所有牌算作散牌
            //hand.bannedSuit = random.nextInt(4) + 1;
            //hand.bannedSuit = MathUtils.random(1,3);
            //所以如果是血战血流，就不能在发牌阶段理牌，应该在定缺后。这里就耦合了
            hand.solutions = MJManager.INSTANCE.solutions(hand.tiles, true, hand.bannedSuit);
        }
    }


    public static void main(String[] args) {
        for (int i = 0; i < 48; i++) {
            System.out.println("index: "+i+" "+(i/4)%4);
        }
        for (int i = 48; i < 53; i++) {
            System.out.println("index: "+i+" "+(i%4));
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
