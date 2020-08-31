package com.yude.game.common.mahjong;

import com.yude.game.common.model.CardEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by someone on 2020/8/5 21:09.
 */
public enum Tile {
    // ===========================================================
    // Enums
    // ===========================================================
    b1(11, "一万", "1", "b", Tile.TYPE_SIMPLES, CardEnum.万.getColor()),
    b2(12, "二万", "2", "b", Tile.TYPE_SIMPLES,  CardEnum.万.getColor()),
    b3(13, "三万", "3", "b", Tile.TYPE_SIMPLES,  CardEnum.万.getColor()),
    b4(14, "四万", "4", "b", Tile.TYPE_SIMPLES,  CardEnum.万.getColor()),
    b5(15, "五万", "5", "b", Tile.TYPE_SIMPLES,  CardEnum.万.getColor()),
    b6(16, "六万", "6", "b", Tile.TYPE_SIMPLES,  CardEnum.万.getColor()),
    b7(17, "七万", "7", "b", Tile.TYPE_SIMPLES,  CardEnum.万.getColor()),
    b8(18, "八万", "8", "b", Tile.TYPE_SIMPLES,  CardEnum.万.getColor()),
    b9(19, "九万", "9", "b", Tile.TYPE_SIMPLES,  CardEnum.万.getColor()),
    t1(21, "一条", "1", "t", Tile.TYPE_SIMPLES, CardEnum.条.getColor()),
    t2(22, "二条", "2", "t", Tile.TYPE_SIMPLES, CardEnum.条.getColor()),
    t3(23, "三条", "3", "t", Tile.TYPE_SIMPLES, CardEnum.条.getColor()),
    t4(24, "四条", "4", "t", Tile.TYPE_SIMPLES, CardEnum.条.getColor()),
    t5(25, "五条", "5", "t", Tile.TYPE_SIMPLES, CardEnum.条.getColor()),
    t6(26, "六条", "6", "t", Tile.TYPE_SIMPLES, CardEnum.条.getColor()),
    t7(27, "七条", "7", "t", Tile.TYPE_SIMPLES, CardEnum.条.getColor()),
    t8(28, "八条", "8", "t", Tile.TYPE_SIMPLES, CardEnum.条.getColor()),
    t9(29, "九条", "9", "t", Tile.TYPE_SIMPLES, CardEnum.条.getColor()),
    w1(31, "一筒", "1", "w", Tile.TYPE_SIMPLES, CardEnum.筒.getColor()),
    w2(32, "二筒", "2", "w", Tile.TYPE_SIMPLES, CardEnum.筒.getColor()),
    w3(33, "三筒", "3", "w", Tile.TYPE_SIMPLES, CardEnum.筒.getColor()),
    w4(34, "四筒", "4", "w", Tile.TYPE_SIMPLES, CardEnum.筒.getColor()),
    w5(35, "五筒", "5", "w", Tile.TYPE_SIMPLES, CardEnum.筒.getColor()),
    w6(36, "六筒", "6", "w", Tile.TYPE_SIMPLES, CardEnum.筒.getColor()),
    w7(37, "七筒", "7", "w", Tile.TYPE_SIMPLES, CardEnum.筒.getColor()),
    w8(38, "八筒", "8", "w", Tile.TYPE_SIMPLES, CardEnum.筒.getColor()),
    w9(39, "九筒", "9", "w", Tile.TYPE_SIMPLES, CardEnum.筒.getColor()),
    E(41, "東", "E", "", Tile.TYPE_HONORS, Tile.SUIT_WINDS),
    S(42, "南", "S", "", Tile.TYPE_HONORS, Tile.SUIT_WINDS),
    W(43, "西", "W", "", Tile.TYPE_HONORS, Tile.SUIT_WINDS),
    N(44, "北", "N", "", Tile.TYPE_HONORS, Tile.SUIT_WINDS),
    Z(51, "中", "Z", "", Tile.TYPE_HONORS, Tile.SUIT_DRAGONS),
    F(52, "發", "F", "", Tile.TYPE_HONORS, Tile.SUIT_DRAGONS),
    B(53, "白", "B", "", Tile.TYPE_HONORS, Tile.SUIT_DRAGONS);

    // ===========================================================
    // Constants
    // ===========================================================
    public static final int TYPE_SIMPLES = 1; // 数牌
    public static final int TYPE_HONORS  = 2; // 字牌
    public static final int TYPE_BONUS   = 3; // 花牌

    public static final int SUIT_CHARACTERS = 1; // 万
    public static final int SUIT_BAMBOO     = 2; // 条
    public static final int SUIT_DOTS       = 3; // 饼
    public static final int SUIT_WINDS      = 4; // 风
    public static final int SUIT_DRAGONS    = 5; // 箭
    public static final int SUIT_FLOWERS    = 6; // 花
    public static final int SUIT_SEASONS    = 7; // 季

    // ===========================================================
    // Fields
    // ===========================================================

    final public         int                id;
    final public         String             name;
    final public         String             value;
    final public         String             suffix;
    final public         int                type;
    final public         int                suit;

    // ===========================================================
    // Constructors
    // ===========================================================
    Tile(int pID, String pName, String pValue, String pSuffix, int pType, int pSuit) {
        this.id = pID;
        this.name = pName;
        this.value = pValue;
        this.suffix = pSuffix;
        this.type = pType;
        this.suit = pSuit;
        TileStatic.TILES.put(id, this);
    }

    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    // ===========================================================
    // Methods
    // ===========================================================
    public static Tile getTileByID(int pTileID) {
        if(TileStatic.TILES.containsKey(pTileID)){
            return TileStatic.TILES.get(pTileID);
        }
        return null;
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    private static final class TileStatic{
        final private static Map<Integer, Tile> TILES = new HashMap<>();
    }
}
