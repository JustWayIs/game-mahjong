package com.yude.game.common.mahjong;

import com.google.common.collect.ImmutableList;
import com.yude.game.common.contant.OperationEnum;

/**
 * Created by someone on 2020/8/6 10:57.
 */
public class Meld {
    // ===========================================================
    // Constants
    // ===========================================================
    public static final int FROM_WALL = 0; // 摸入
    public static final int FROM_DISCARD = 1; // 吃入

    public static final int TYPE_SINGLE_TILE = 0; // 单张
    public static final int TYPE_READY_SEQUENCE = 1; // 搭子
    public static final int TYPE_PAIR = 2; // 对子/将眼
    public static final int TYPE_SEQUENCE = 3; // 顺子
    public static final int TYPE_TRIPLET = 4; // 刻子
    public static final int TYPE_KONG = 5; // 杠

    public static final int PLAYER_SELF = 0; // 自己
    public static final int PLAYER_UP = 1; // 上家
    public static final int PLAYER_OPPOSITE = 2; // 对家
    public static final int PLAYER_DOWN = 3; // 下家

    // ===========================================================
    // Fields
    // ===========================================================
    public ImmutableList<Tile> tiles; // 面子的牌张
    public int type;  // 面子类型
    public int from;  // 面子成型方式，摸入或别家打的张
    public int player;// 面子入张来源，自己、上下对家
    public int index; // 吃杠碰的张游标
    public boolean isPlusKong; // 是否加杠，加杠时player依旧表示杠前刻子入张来源，但由原来的基础变为 4，5，6，7
    public boolean isStable;

    // ===========================================================
    // Constructors
    // ===========================================================


    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================

    public ImmutableList<Tile> getTiles() {
        return tiles;
    }

    public Meld setTiles(ImmutableList<Tile> tiles) {
        this.tiles = tiles;
        return this;
    }

    public int getType() {
        return type;
    }

    public Meld operationConvertType(int operationValue) {
        int typeParam;
        if(operationValue == OperationEnum
                .CHI.value()){
            typeParam = TYPE_SEQUENCE;

        }else if(operationValue == OperationEnum.PENG.value()){
            typeParam = TYPE_TRIPLET;
        }else{
            typeParam = TYPE_KONG;
        }
        this.type = typeParam;
        return this;
    }

    public int getFrom() {
        return from;
    }

    public Meld convertFrom(int operatorPosId,int resourcePosId) {
        this.from = operatorPosId == resourcePosId ? FROM_WALL : FROM_DISCARD;
        return this;
    }

    public int getPlayer() {
        return player;
    }

    public Meld setPlayer(int operatorPosId,int resourcePosId) {
        this.player = player;
        return this;
    }

    public Meld converPlayer(int operatorPosId,int resourcePosId){
        int target;
        if(operatorPosId == resourcePosId){
            target = PLAYER_SELF;
        }else if((operatorPosId + 1 ) % 4 == resourcePosId){
            target = PLAYER_DOWN;
        }else if((operatorPosId - 1 ) == resourcePosId || (operatorPosId + 4 - 1) == resourcePosId){
            target = PLAYER_UP;
        }else {
            target = PLAYER_OPPOSITE;
        }
        this.player = target;
        return this;
    }

    public int getIndex() {
        return index;
    }

    public Meld setIndex(int index) {
        this.index = index;
        return this;
    }

    public boolean isPlusKong() {
        return isPlusKong;
    }

    public Meld setPlusKong(boolean plusKong) {
        isPlusKong = plusKong;
        return this;
    }

    public boolean isStable() {
        return isStable;
    }

    public Meld setStable(boolean stable) {
        isStable = stable;
        return this;
    }


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    // ===========================================================
    // Methods
    // ===========================================================


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
