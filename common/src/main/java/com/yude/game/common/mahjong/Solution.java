package com.yude.game.common.mahjong;

import com.yude.game.common.model.fan.BaseHuTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by someone on 2020/8/7 10:35.
 */
public class Solution {
    // ===========================================================
    // Constants
    // ===========================================================


    // ===========================================================
    // Fields
    // ===========================================================
    public List<Meld> melds         = new ArrayList<>(); // 面子
    public List<Meld> melding       = new ArrayList<>(); // 搭子
    public List<Tile> tiles         = new ArrayList<>(); // 散张
    public List<Tile> canWin        = new ArrayList<>(); // 听张
    public List<Tile> canChow       = new ArrayList<>(); // 可吃张
    public List<Tile> canPong       = new ArrayList<>(); // 可碰张
    public List<Tile> canKong       = new ArrayList<>(); // 可杠张
    public List<Tile> improve       = new ArrayList<>(); // 改良牌
    public boolean    isWin         = false; // 是否已经胡牌
    public boolean    isReadyHand   = false; // 是否已经听牌
    public int        meldCount     = 0; // 面子数量
    public int        sequenceCount = 0; // 顺子数量
    public int        tripletCount  = 0; // 刻子数量
    public int        pairCount     = 0; // 对子数量
    public int        kongCount     = 0; // 杠数量
    public int        meldingCount  = 0; // 搭子数量

    private BaseHuTypeEnum baseHuType;

    // ===========================================================
    // Constructors
    // ===========================================================


    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    public Solution setBaseHuType(BaseHuTypeEnum baseHuType) {
        this.baseHuType = baseHuType;
        return this;
    }

    public BaseHuTypeEnum getBaseHuType() {
        return baseHuType;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        if (this.melds.size() > 0) {
            s.append("面子/将眼(").append(this.meldCount).append("/").append(this.pairCount).append(")");
            for (Meld meld : this.melds) {
                s.append("[ ");
                s.append(meld.tiles.stream().map(t -> t.name).collect(Collectors.joining(" ")));
                s.append(" ] ");
            }
        }//.append("/").append(this.meldingCount)
        if (this.melding.size() > 0) {
            s.append("搭子(").append(this.meldingCount).append(")");
            for (Meld meld : this.melding) {
                s.append("[ ");
                s.append(meld.tiles.stream().map(t -> t.name).collect(Collectors.joining(" ")));
                s.append(" ] ");
            }
        }
        s.append("散张(").append(this.tiles.size()).append(")[ ");
        s.append(this.tiles.stream().map(t -> t.name).collect(Collectors.joining(" ")));
        s.append(" ]").append(System.lineSeparator());
        if(this.isWin) {
            s.append("    胡牌").append(System.lineSeparator());
        }
        if(this.canWin.size() > 0) {
            s.append("    可听(").append(this.canWin.size()).append(")[ ");
            s.append(this.canWin.stream().map(t -> t.name).collect(Collectors.joining(" ")));
            s.append(" ] ").append(System.lineSeparator());
        }
        if(this.canChow.size() > 0) {
            s.append("    可吃(").append(this.canChow.size()).append(")[ ");
            s.append(this.canChow.stream().map(t -> t.name).collect(Collectors.joining(" ")));
            s.append(" ] ").append(System.lineSeparator());
        }
        if(this.canPong.size() > 0) {
            s.append("    可碰(").append(this.canPong.size()).append(")[ ");
            s.append(this.canPong.stream().map(t -> t.name).collect(Collectors.joining(" ")));
            s.append(" ] ").append(System.lineSeparator());
        }
        if(this.canKong.size() > 0) {
            s.append("    可杠(").append(this.canKong.size()).append(")[ ");
            s.append(this.canKong.stream().map(t -> t.name).collect(Collectors.joining(" ")));
            s.append(" ] ").append(System.lineSeparator());
        }
        if(this.improve.size() > 0) {
            s.append("    改良牌(").append(this.improve.size()).append(")[ ");
            s.append(this.improve.stream().map(t -> t.name).collect(Collectors.joining(" ")));
            s.append(" ]").append(System.lineSeparator());
        }
        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Solution solution = (Solution) o;
        return toString().equals(solution.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    // ===========================================================
    // Methods
    // ===========================================================


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
