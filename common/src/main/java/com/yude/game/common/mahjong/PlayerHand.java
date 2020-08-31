package com.yude.game.common.mahjong;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by someone on 2020/8/6 10:52.
 */
public class PlayerHand {
    // ===========================================================
    // Constants
    // ===========================================================


    // ===========================================================
    // Fields
    // ===========================================================
    public List<Meld>     melds; // 副露
    public List<Tile>     tiles; // 立牌
    public List<Tile>     discards; // 打掉的牌
    public List<Solution> solutions;
    public int            bannedSuit;

    // ===========================================================
    // Constructors
    // ===========================================================
    public PlayerHand() {
        this.melds = new ArrayList<>();
        this.tiles = new ArrayList<>();
        this.discards = new ArrayList<>();
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
        s.append("定缺[ ").append(this.bannedSuit == Tile.SUIT_DOTS ? "饼" : this.bannedSuit == Tile.SUIT_BAMBOO ? "条" : this.bannedSuit == Tile.SUIT_CHARACTERS ? "万" : "无").append(" ] ");
        if (this.melds.size() > 0) {
            s.append("副露[ ");
            for (Meld meld : this.melds) {
                s.append("[ ");
                s.append(IntStream.range(0, meld.tiles.size()).mapToObj(index -> {
                    Tile tile = meld.tiles.get(index);
                    return index == meld.index ? "{" + tile.name + "}" : tile.name;
                }).collect(Collectors.joining(" ")));
                s.append(" ] ");
            }
            s.append(" ] ");
        }
        s.append("立牌[ ");
        s.append(this.tiles.stream().map(t -> t.name).collect(Collectors.joining(" ")));
        s.append(" ] ");
        s.append("理牌方案[").append(this.solutions.size()).append("]");
        return s.toString();
    }


    // ===========================================================
    // Methods
    // ===========================================================


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
