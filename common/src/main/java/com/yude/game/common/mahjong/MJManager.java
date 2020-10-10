package com.yude.game.common.mahjong;

import com.google.common.collect.ImmutableList;
import com.yude.game.common.model.CardEnum;
import com.yude.game.common.model.fan.BaseHuTypeEnum;

import java.util.*;

/**
 * Created by someone on 2020/8/5 21:49.
 */
public enum MJManager {
    // ===========================================================
    // Enums
    // ===========================================================
    INSTANCE;

    // ===========================================================
    // Constants
    // ===========================================================
    private static final ImmutableList<Tile> ORIGIN_TILES = ImmutableList.of(
            Tile.w1, Tile.w2, Tile.w3, Tile.w4, Tile.w5, Tile.w6, Tile.w7, Tile.w8, Tile.w9,
            Tile.w1, Tile.w2, Tile.w3, Tile.w4, Tile.w5, Tile.w6, Tile.w7, Tile.w8, Tile.w9,
            Tile.w1, Tile.w2, Tile.w3, Tile.w4, Tile.w5, Tile.w6, Tile.w7, Tile.w8, Tile.w9,
            Tile.w1, Tile.w2, Tile.w3, Tile.w4, Tile.w5, Tile.w6, Tile.w7, Tile.w8, Tile.w9,
            Tile.t1, Tile.t2, Tile.t3, Tile.t4, Tile.t5, Tile.t6, Tile.t7, Tile.t8, Tile.t9,
            Tile.t1, Tile.t2, Tile.t3, Tile.t4, Tile.t5, Tile.t6, Tile.t7, Tile.t8, Tile.t9,
            Tile.t1, Tile.t2, Tile.t3, Tile.t4, Tile.t5, Tile.t6, Tile.t7, Tile.t8, Tile.t9,
            Tile.t1, Tile.t2, Tile.t3, Tile.t4, Tile.t5, Tile.t6, Tile.t7, Tile.t8, Tile.t9,
            Tile.b1, Tile.b2, Tile.b3, Tile.b4, Tile.b5, Tile.b6, Tile.b7, Tile.b8, Tile.b9,
            Tile.b1, Tile.b2, Tile.b3, Tile.b4, Tile.b5, Tile.b6, Tile.b7, Tile.b8, Tile.b9,
            Tile.b1, Tile.b2, Tile.b3, Tile.b4, Tile.b5, Tile.b6, Tile.b7, Tile.b8, Tile.b9,
            Tile.b1, Tile.b2, Tile.b3, Tile.b4, Tile.b5, Tile.b6, Tile.b7, Tile.b8, Tile.b9,
            Tile.E, Tile.S, Tile.W, Tile.N, Tile.Z, Tile.F, Tile.B,
            Tile.E, Tile.S, Tile.W, Tile.N, Tile.Z, Tile.F, Tile.B,
            Tile.E, Tile.S, Tile.W, Tile.N, Tile.Z, Tile.F, Tile.B,
            Tile.E, Tile.S, Tile.W, Tile.N, Tile.Z, Tile.F, Tile.B
    );

    // ===========================================================
    // Fields
    // ===========================================================


    // ===========================================================
    // Constructors
    // ===========================================================


    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    // ===========================================================
    // Methods
    // ===========================================================
    public PlayBoard create(int pBanker, List<Integer> cardWall) {
        //List<Tile> tiles = Lists.newArrayList(ORIGIN_TILES);
        //Collections.shuffle(tiles);

        List<Tile> tiles = new ArrayList<>();
        for (Integer card : cardWall) {
            final Tile tile = Tile.getTileByID(card);
            tiles.add(tile);
        }
        return new PlayBoard(pBanker, tiles);
    }


    /*public PlayBoard create() {
        return create(0);
    }*/

    public String encode(List<Tile> tiles) {
        StringBuilder encoded = new StringBuilder();
        String        suffix  = "";
        for (Tile tile : tiles) {
            if (encoded.length() > 0 && !suffix.equals(tile.suffix)) {
                encoded.append(suffix);
            }
            encoded.append(tile.value);
            suffix = tile.suffix;
        }
        encoded.append(suffix);
        return encoded.toString();
    }

    public String encode(PlayerHand pPlayerHand) {
        StringBuilder encoded = new StringBuilder();
        for (Meld meld : pPlayerHand.melds) {
            encoded.append("[");
            switch (meld.type) {
                case Meld.TYPE_PAIR: {
                    Tile tile = meld.tiles.get(0);
                    encoded.append(tile.value).append(tile.value).append(tile.suffix);
                    break;
                }
                case Meld.TYPE_SEQUENCE: {
                    encoded.append(meld.tiles.get(0).value);
                    encoded.append(meld.tiles.get(1).value);
                    encoded.append(meld.tiles.get(2).value);
                    encoded.append(meld.tiles.get(2).suffix);
                    // 标记顺子成面入张游标
                    encoded.append(",").append(meld.index);
                    break;
                }
                case Meld.TYPE_TRIPLET: {
                    Tile tile = meld.tiles.get(0);
                    encoded.append(tile.value);
                    encoded.append(tile.value);
                    encoded.append(tile.value);
                    encoded.append(tile.suffix);
                    // 标记刻子成面玩家来源
                    encoded.append(",").append(meld.player);
                    break;
                }
                case Meld.TYPE_KONG: {
                    Tile tile = meld.tiles.get(0);
                    encoded.append(tile.value);
                    encoded.append(tile.value);
                    encoded.append(tile.value);
                    encoded.append(tile.value);
                    encoded.append(tile.suffix);
                    // 标记刻子成面玩家来源
                    encoded.append(",").append(meld.player + (meld.isPlusKong ? 4 : 0));
                    break;
                }
            }
            encoded.append("]");
        }
        encoded.append(encode(pPlayerHand.tiles));
        return encoded.toString();
    }

    public List<Solution> solutions(List<Tile> pTiles) {
        pTiles.sort(Comparator.comparingInt(t -> t.id));
        return solutions(new ArrayList<>(), pTiles, true, 0);
    }

    public List<Solution> solutions(List<Tile> pTiles, int pBannedSuit) {
        pTiles.sort(Comparator.comparingInt(t -> t.id));
        return solutions(new ArrayList<>(), pTiles, true, pBannedSuit);
    }

    public List<Solution> solutions(List<Tile> pTiles, boolean pSorted, int pBannedSuit) {
        return solutions(new ArrayList<>(), pTiles, pSorted, pBannedSuit);
    }

    public List<Solution> solutions(List<Meld> pMelds, List<Tile> pTiles, int pBannedSuit) {
        pTiles.sort(Comparator.comparingInt(t -> t.id));
        return solutions(pMelds, pTiles, true, pBannedSuit);
    }

    public List<Solution> solutions(List<Meld> pMelds, List<Tile> pTiles, boolean pSorted, int pBannedSuit) {
        List<Solution> solutions = new ArrayList<>();
        if (pTiles != null) {
            if (!pSorted) {
                pTiles.sort(Comparator.comparingInt(t -> t.id));
            }
            TileBranch branch = null;
            for (Tile tile : pTiles) {
                if (branch == null) {
                    branch = new TileBranch(tile, null, 1, Meld.TYPE_SINGLE_TILE);
                } else {
                    attachTileBranch(branch, tile, pBannedSuit);
                }
            }
            Map<String, Solution> solutionMap = new HashMap<>();
            parseSolutions(solutionMap, branch, pMelds, pBannedSuit);
            solutions = new ArrayList<>(solutionMap.values());
            solutions.sort((a, b) -> a.isWin || b.isWin ?
                    ((b.isWin ? 1 : 0) - (a.isWin ? 1 : 0)) : ((a.isReadyHand || b.isReadyHand) ?
                    (b.canWin.size() - a.canWin.size()) : (a.meldCount != b.meldCount) ? // 对子多的排前
                    (b.meldCount - a.meldCount) : (a.pairCount != b.pairCount ? // 面子多的排前
                    (b.pairCount - a.pairCount) : (b.meldingCount != a.meldingCount ? // 搭子的多的排前
                    (b.meldingCount - a.meldingCount) : (a.tiles.size() - b.tiles.size()))))); // 散牌少的排前
        }
        return solutions;
    }

    private void parseSolutions(Map<String, Solution> pSolutions, TileBranch pBranch, List<Meld> pMelds, int pBannedSuit) {
        if (pBranch != null) {
            if (pBranch.meld.size() > 0) {
                pBranch.meld.forEach(meld -> {
                    parseSolutions(pSolutions, meld, pMelds, pBannedSuit);
                });
            }
            if (pBranch.next != null) {
                parseSolutions(pSolutions, pBranch.next, pMelds, pBannedSuit);
            }
            if (pBranch.meld.size() == 0 && pBranch.next == null) {
                // 回溯牌路
                Solution solution = new Solution();
                for (Meld meld : pMelds) {
                    meld.isStable = true;
                    // 已成型的面子，通过吃杠碰得到
                    solution.melds.add(meld);
                    solution.meldCount++;
                    switch (meld.type) {
                        case Meld.TYPE_SEQUENCE:
                            solution.sequenceCount++;
                            break;
                        case Meld.TYPE_TRIPLET:
                            solution.tripletCount++;
                            break;
                        case Meld.TYPE_KONG:
                            solution.kongCount++;
                            break;
                    }
                }
                TileBranch current = pBranch;
                do {
                    switch (current.type) {
                        case Meld.TYPE_SINGLE_TILE:    // 单张
                            solution.tiles.add(current.tile);
                            current = current.prev;
                            break;
                        case Meld.TYPE_READY_SEQUENCE: {// 搭子
                            Meld meld = new Meld();
                            meld.tiles = ImmutableList.of(current.prev.tile, current.tile);
                            meld.type = Meld.TYPE_READY_SEQUENCE;
                            meld.from = Meld.FROM_WALL;
                            meld.player = Meld.PLAYER_SELF;
                            meld.index = -1; // 吃杠碰的张游标
                            solution.melding.add(meld);
                            solution.meldingCount++;
                            current = current.prev.prev;
                            break;
                        }
                        case Meld.TYPE_PAIR: {          // 对子/将眼
                            Meld meld = new Meld();
                            meld.tiles = ImmutableList.of(current.tile, current.tile);
                            meld.type = Meld.TYPE_PAIR;
                            meld.from = Meld.FROM_WALL;
                            meld.player = Meld.PLAYER_SELF;
                            meld.index = -1; // 吃杠碰的张游标
                            solution.melds.add(meld);
                            solution.pairCount++;
                            current = current.prev.prev;
                            break;
                        }
                        case Meld.TYPE_SEQUENCE: {      // 顺子
                            Meld meld = new Meld();
                            meld.tiles = ImmutableList.of(current.prev.prev.tile, current.prev.tile, current.tile);
                            meld.type = Meld.TYPE_SEQUENCE;
                            meld.from = Meld.FROM_WALL;
                            meld.player = Meld.PLAYER_SELF;
                            meld.index = -1; // 吃杠碰的张游标
                            solution.melds.add(meld);
                            solution.sequenceCount++;
                            solution.meldCount++;
                            current = current.prev.prev.prev;
                            break;
                        }
                        case Meld.TYPE_TRIPLET: {       // 刻子
                            Meld meld = new Meld();
                            meld.tiles = ImmutableList.of(current.tile, current.tile, current.tile);
                            meld.type = Meld.TYPE_TRIPLET;
                            meld.from = Meld.FROM_WALL;
                            meld.player = Meld.PLAYER_SELF;
                            meld.index = -1; // 吃杠碰的张游标
                            solution.melds.add(meld);
                            solution.tripletCount++;
                            solution.meldCount++;
                            current = current.prev.prev.prev;
                            break;
                        }
                        case Meld.TYPE_KONG: {           // 杠
                            Meld meld = new Meld();
                            meld.tiles = ImmutableList.of(current.tile, current.tile, current.tile, current.tile);
                            meld.type = Meld.TYPE_KONG;
                            meld.from = Meld.FROM_WALL;
                            meld.player = Meld.PLAYER_SELF;
                            meld.index = -1; // 吃杠碰的张游标
                            solution.melds.add(meld);
                            solution.kongCount++;
                            solution.meldCount++;
                            current = current.prev.prev.prev.prev;
                            break;
                        }
                    }
                } while (current != null);

                if ((solution.meldCount == 4 && solution.pairCount == 1)) {
                    solution.isWin = true;
                    solution.setBaseHuType(BaseHuTypeEnum.平胡);
                }else if(solution.pairCount == 7){
                    solution.isWin = true;
                    solution.setBaseHuType(BaseHuTypeEnum.七对);
                } else if (solution.meldCount == 4) {
                    // 四面子，听单张，可杠上花
                    for (Tile tile : solution.tiles) {
                        if (tile.suit != pBannedSuit) {
                            solution.canWin.add(tile);
                        }
                    }
                    solution.setBaseHuType(BaseHuTypeEnum.平胡);
                    for (Meld meld : solution.melds) {
                        if (meld.type == Meld.TYPE_TRIPLET) {
                            solution.canKong.add(meld.tiles.get(0));
                        }
                    }
                    solution.isReadyHand = solution.canWin.size() > 0;
                } else if(solution.pairCount == 6){
                    // 四面子，听单张，可杠上花
                    for (Tile tile : solution.tiles) {
                        if (tile.suit != pBannedSuit) {
                            solution.canWin.add(tile);
                        }
                    }
                    solution.setBaseHuType(BaseHuTypeEnum.七对);
                    for (Meld meld : solution.melds) {
                        if (meld.type == Meld.TYPE_TRIPLET) {
                            solution.canKong.add(meld.tiles.get(0));
                        }
                    }
                    solution.isReadyHand = solution.canWin.size() > 0;
                }else if (solution.meldCount == 3 && solution.pairCount == 1 && solution.meldingCount == 1) {
                    // 听搭子张
                    for (Meld meld : solution.melding) {
                        if (meld.type == Meld.TYPE_READY_SEQUENCE) {
                            Tile a     = meld.tiles.get(0);
                            Tile b     = meld.tiles.get(1);
                            int  delta = Math.abs(a.id - b.id);
                            if (delta == 1) {
                                // 两面搭子
                                if (a.id % 10 != 1) {
                                    Tile t = Tile.getTileByID(a.id - 1);
                                    if (!solution.canWin.contains(t)) {
                                        solution.canWin.add(t);
                                        solution.setBaseHuType(BaseHuTypeEnum.平胡);
                                    }
                                }
                                if (b.id % 10 != 9) {
                                    Tile t = Tile.getTileByID(b.id + 1);
                                    if (!solution.canWin.contains(t)) {
                                        solution.canWin.add(t);
                                        solution.setBaseHuType(BaseHuTypeEnum.平胡);
                                    }
                                }
                            } else {
                                // 嵌张
                                Tile t = Tile.getTileByID((a.id + b.id) / 2);
                                if (!solution.canWin.contains(t)) {
                                    solution.canWin.add(t);
                                    solution.setBaseHuType(BaseHuTypeEnum.平胡);
                                }
                            }
                        }
                    }
                    for (Meld meld : solution.melds) {
                        if (meld.type == Meld.TYPE_TRIPLET) {
                            solution.canKong.add(meld.tiles.get(0));
                        }
                    }
                    solution.isReadyHand = true;
                } else if (solution.meldCount == 3 && solution.pairCount == 2) {
                    // 两对倒
                    for (Meld meld : solution.melds) {
                        if (meld.type == Meld.TYPE_PAIR) {
                            if (!solution.canWin.contains(meld.tiles.get(0))) {
                                solution.canWin.add(meld.tiles.get(0));
                                solution.setBaseHuType(BaseHuTypeEnum.平胡);
                            } else {
                                // 暗杠被拆，放弃此方案，因为同3面子、1杠的方案代替
                                return;
                            }
                        } else if (meld.type == Meld.TYPE_TRIPLET) {
                            solution.canKong.add(meld.tiles.get(0));
                        }
                    }
                    solution.isReadyHand = true;
                } else {
                    for (Meld meld : solution.melds) {
                        switch (meld.type) {
                            case Meld.TYPE_TRIPLET: {
                                if (!solution.canKong.contains(meld.tiles.get(0))) { // 可以杠
                                    solution.canKong.add(meld.tiles.get(0));
                                }
                                if (!solution.canPong.contains(meld.tiles.get(0))) { // 可以碰
                                    solution.canPong.add(meld.tiles.get(0));
                                }
                                break;
                            }
                            case Meld.TYPE_PAIR: {
                                if (!solution.canPong.contains(meld.tiles.get(0))) { // 可以碰
                                    solution.canPong.add(meld.tiles.get(0));
                                } else {
                                    // 暗杠被拆, 去除可碰范围
                                    solution.canPong.remove(meld.tiles.get(0));
                                }
                                break;
                            }
                        }
                    }
                    for (Meld meld : solution.melding) {
                        Tile a     = meld.tiles.get(0);
                        Tile b     = meld.tiles.get(1);
                        int  delta = Math.abs(a.id - b.id);
                        if (delta == 1) {
                            // 两面搭子
                            if (a.id % 10 != 1) {
                                Tile t = Tile.getTileByID(a.id - 1);
                                if (!solution.canChow.contains(t)) {
                                    solution.canChow.add(t);
                                }
                            }
                            if (b.id % 10 != 9) {
                                Tile t = Tile.getTileByID(b.id + 1);
                                if (!solution.canChow.contains(t)) {
                                    solution.canChow.add(t);
                                }
                            }
                        } else {
                            // 嵌张
                            Tile t = Tile.getTileByID((a.id + b.id) / 2);
                            if (!solution.canChow.contains(t)) {
                                solution.canChow.add(t);
                            }
                        }
                    }
                    for (Tile tile : solution.tiles) {
                        // 如果吃杠碰听包含散张，放弃此方案
                        if (solution.canWin.contains(tile) || solution.canChow.contains(tile)
                                || solution.canPong.contains(tile) || solution.canKong.contains(tile)) {
                            return;
                        }
                        // 改良张
                        if (tile.suit != pBannedSuit) {
                            switch (tile.suit) {
                                case Tile.SUIT_DOTS:
                                case Tile.SUIT_BAMBOO:
                                case Tile.SUIT_CHARACTERS:
                                    if (!solution.improve.contains(tile)) {
                                        solution.improve.add(tile);
                                    }
                                    // 相邻张
                                    int index = tile.id % 10;
                                    boolean hasLeft = index != 1;
                                    boolean hasRight = index != 9;
                                    if (hasLeft) {
                                        Tile left = Tile.getTileByID(tile.id - 1);
                                        if (!solution.improve.contains(left)) {
                                            solution.improve.add(left);
                                        }
                                    }
                                    if (hasRight) {
                                        Tile right = Tile.getTileByID(tile.id + 1);
                                        if (!solution.improve.contains(right)) {
                                            solution.improve.add(right);
                                        }
                                    }
                                    break;
                                case Tile.SUIT_WINDS:
                                case Tile.SUIT_DRAGONS:
                                    if (!solution.improve.contains(tile)) {
                                        solution.improve.add(tile);
                                    }
                                    break;
                            }
                        }
                    }
                }
                solution.melds.sort(Comparator.comparingInt(meld -> -meld.tiles.size()));
                //if (!pSolutions.contains(solution)) {
                pSolutions.put(solution.toString(), solution);
                //}
            }
        }
    }

    private void attachTileBranch(TileBranch pBranch, Tile pTile, int pBannedSuit) {
        // 牌型分支
        // meld 路径为有效面子或搭子
        // next 路径为全不搭，为保证所有路径为全牌面，保证每张牌都会像末端张 next 挂载
        if (pBranch != null) {
            // 定缺张只能挂载在next
            if (pBranch.tile.suit == pTile.suit && pTile.suit != pBannedSuit) {
                // meld 为空集，判断是否需要进行挂载
                if (pBranch.meld.size() == 0) {
                    if (pBranch.length == 1) {
                        // 当前张暂无关联
                        if (pBranch.tile.type == Tile.TYPE_SIMPLES) {
                            // 数牌
                            int delta    = Math.abs(pBranch.tile.id - pTile.id);
                            int meldType = Meld.TYPE_READY_SEQUENCE;
                            switch (delta) {
                                case 0: // 对子
                                    meldType = Meld.TYPE_PAIR;
                                case 1: // 两面
                                case 2: // 嵌张
                                    pBranch.meld.add(new TileBranch(pTile, pBranch, 2, meldType));
                                    break;
                            }
                        } else if (pBranch.tile.type == Tile.TYPE_HONORS) {
                            // 字牌
                            if (pBranch.tile.id == pTile.id) {
                                pBranch.meld.add(new TileBranch(pTile, pBranch, 2, Meld.TYPE_PAIR));
                            }
                        }
                    } else if (pBranch.length == 2) {
                        // 当前张已关联前张
                        if (pBranch.type == Meld.TYPE_READY_SEQUENCE) {
                            // 嵌张/两面
                            // 外部保证 Tile 已经排序，那么在嵌张的情况下不可能成面子
                            if (pTile.id - pBranch.tile.id == 1 && pBranch.tile.id - pBranch.prev.tile.id == 1) {
                                TileBranch meld = new TileBranch(pTile, pBranch, 3, Meld.TYPE_SEQUENCE);
                                pBranch.meld.add(meld);
                                // 如果当前节点（pBranch.tile）存在next，需要移动此next到挂载节点的next，以确保不会丢失牌张
                                // 当前节点应该不再继续挂载next内容
                                if (pBranch.next != null) {
                                    pBranch.next.prev = meld;
                                    meld.next = pBranch.next;
                                    pBranch.next = null;
                                    pBranch.enableNext = false;
                                }
                            }
                        } else if (pBranch.type == Meld.TYPE_PAIR && pBranch.tile.id == pTile.id) {
                            // 对子
                            pBranch.meld.add(new TileBranch(pTile, pBranch, 3, Meld.TYPE_TRIPLET));
                        }
                    } else if (pBranch.length == 3 && pBranch.type == Meld.TYPE_TRIPLET && pBranch.tile.id == pTile.id) {
                        pBranch.meld.add(new TileBranch(pTile, pBranch, 4, Meld.TYPE_KONG));
                    }
                } else if (pBranch.meld.size() == 1) {
                    // meld 包含1个牌张
                    TileBranch exists = pBranch.meld.get(0);
                    // 因为牌张已经排序，仅当当前面子长度为1时，才有可能挂载2个meld
                    // 同时已经挂载的meld应当是组成对子才有可能挂载多个，主要为了解决排序中多个同张后接搭子的情况
                    if (exists.tile != pTile && pBranch.length == 1 && exists.type == Meld.TYPE_PAIR) {
                        // 可以同时挂载
                        int delta    = Math.abs(pBranch.tile.id - pTile.id);
                        int meldType = Meld.TYPE_READY_SEQUENCE;
                        switch (delta) {
                            case 1: // 两面
                            case 2: // 嵌张
                                TileBranch meld = new TileBranch(pTile, pBranch, 2, meldType);
                                pBranch.meld.add(meld);
                                // 同时挂载时，增加已有挂载张到当前张的next位置
                                meld.next = clone(exists);
                                break;
                        }
                    }
                    // 不可以同时挂载，继续向下挂载
                    attachTileBranch(exists, pTile, pBannedSuit);
                } else {
                    // 其他：meld 包含2个牌张，继续向下挂载
                    pBranch.meld.forEach(meld -> {
                        attachTileBranch(meld, pTile, pBannedSuit);
                    });
                }
            } else {
                // 为保证所有路径为全牌面，非同类张向meld挂载，会挂载至meld节点的next上
                pBranch.meld.forEach(meld -> {
                    attachTileBranch(meld, pTile, pBannedSuit);
                });
            }
            // 为保证所有路径为全牌面，保证每张牌都会像末端张 next 挂载
            if (pBranch.next == null) {
                // 顺子中间张不需要挂载next
                if (pBranch.enableNext) {
                    // 无下一非搭子张
                    pBranch.next = new TileBranch(pTile, pBranch, 1, Meld.TYPE_SINGLE_TILE);
                }
            } else {
                attachTileBranch(pBranch.next, pTile, pBannedSuit);
            }
        }
    }

    private TileBranch clone(TileBranch pBranch) {
        if (pBranch != null) {
            TileBranch clone = new TileBranch(pBranch.tile, null, 1, Meld.TYPE_SINGLE_TILE);
            if (pBranch.meld.size() > 0) {
                pBranch.meld.forEach(meld -> {
                    cloneAttacheMeld(clone, meld);
                });
            }
            if (pBranch.next != null) {
                cloneAttacheNext(clone, pBranch.next);
            }
            return clone;
        }
        return null;
    }

    private void cloneAttacheMeld(TileBranch pClone, TileBranch pMeld) {
        if (pClone != null && pMeld != null) {
            int delta    = Math.abs(pClone.tile.id - pMeld.tile.id);
            int meldType = Meld.TYPE_READY_SEQUENCE;
            switch (delta) {
                case 0: // 对子
                    if (pClone.length == 1) {
                        meldType = Meld.TYPE_PAIR;
                    } else if (pClone.length == 2) {
                        meldType = Meld.TYPE_TRIPLET;
                    } else if (pClone.length == 3) {
                        meldType = Meld.TYPE_KONG;
                    }//
                case 1: // 两面
                    if (pClone.length == 2) {
                        meldType = Meld.TYPE_SEQUENCE;
                    }
            }
            TileBranch meld = new TileBranch(pMeld.tile, pClone, pClone.length + 1, meldType);
            pClone.meld.add(meld);
            if (pMeld.meld.size() > 0) {
                pMeld.meld.forEach(m -> {
                    cloneAttacheMeld(meld, m);
                });
            }
            if (pMeld.next != null) {
                cloneAttacheNext(meld, pMeld.next);
            }
        }
    }

    private void cloneAttacheNext(TileBranch pClone, TileBranch pMeld) {
        if (pClone != null && pMeld != null) {
            pClone.next = new TileBranch(pMeld.tile, pClone, 1, Meld.TYPE_SINGLE_TILE);
            if (pMeld.meld.size() > 0) {
                pMeld.meld.forEach(meld -> {
                    cloneAttacheMeld(pClone.next, meld);
                });
            }
            if (pMeld.next != null) {
                cloneAttacheNext(pClone.next, pMeld.next);
            }
        }
    }

    public static void main(String[] args) {
//        int times = 10000;
//        long start = System.nanoTime();
//        int banker = 0;
//        for(int i = 0; i < times; i++) {
//            PlayBoard board = MJManager.INSTANCE.create(banker);
//            banker = (banker + 1) % 4;
//        }
//        long end = System.nanoTime();
//        System.out.println("create " + times + " boards cost: " + (end - start)/1000000D + "ms, average: " + (end - start)/1000000D/times + "ms");

        /*long start = System.nanoTime();
        PlayBoard board = MJManager.INSTANCE.create(0);
        System.out.println(board.toString());
        for(Solution solution : board.playerHands.get(0).solutions){
            System.out.println(solution.toString());
        }
        long end = System.nanoTime();
        System.out.println("solutions: " + board.playerHands.get(0).solutions.size() + ", cost: " + (end - start)/1000000D + "ms");*/

        long start = System.nanoTime();
//        // 九宝莲灯
        List<Tile> tiles = Arrays.asList(Tile.t1, Tile.t1, Tile.t1, Tile.t2, Tile.t2, Tile.t3, Tile.t4, Tile.t5, Tile.t6, Tile.t7, Tile.t8, Tile.t9, Tile.t9, Tile.t9);

        /**
         * 七小对
         */
        tiles = Arrays.asList(Tile.t1, Tile.t1, Tile.b2, Tile.b2, Tile.b5, Tile.b5, Tile.t3, Tile.t3, Tile.w8, Tile.w8, Tile.t7, Tile.t7, Tile.t9);

        //tiles = Arrays.asList(Tile.t2,Tile.t3,Tile.t4,Tile.b1,Tile.b2,Tile.b3,Tile.b5,Tile.b6,Tile.b7,Tile.b8,Tile.b8,Tile.b8,Tile.b9,Tile.b9);

        //tiles = Arrays.asList(Tile.w4,Tile.w4,Tile.b6,Tile.b7);

        //tiles = Arrays.asList(Tile.t1,Tile.t9,Tile.b1,Tile.b9,)

        tiles = Arrays.asList(Tile.w6, Tile.w6, Tile.w7, Tile.w7, Tile.w7, Tile.b5, Tile.b5, Tile.b6, Tile.b6, Tile.b7,Tile.b7);
        List<Meld> pMelds = new ArrayList<>();
        Meld       meld   = new Meld();
        meld.tiles =  ImmutableList.copyOf(Arrays.asList(Tile.w2,Tile.w2,Tile.w2));
        meld.type = 4;
        meld.from = 1;
        meld.player = 1;
        meld.isPlusKong = false;
        meld.isStable = true;
        pMelds.add(meld);
//        // 四饼 四饼 四饼 七饼 一条 六条 九条 九条 三万 六万 東 中 發
//        //List<Tile> tiles = Arrays.asList(Tile.b4, Tile.b4, Tile.b4, Tile.b7, Tile.t1, Tile.t6, Tile.t9, Tile.t9, Tile.w3, Tile.w6, Tile.E, Tile.Z, Tile.F);
        List<Solution> solutions     = MJManager.INSTANCE.solutions(pMelds,tiles, CardEnum.条.getColor());
        List<Solution> tingSolutions = new ArrayList<>();
        for (Solution solution : solutions) {
            if (solution.isWin) {
                System.out.println(solution.toString());

            }
            if (solution.canWin.size() > 0) {
                tingSolutions.add(solution);
            }
        }
        long end = System.nanoTime();
        System.out.println("solutions: " + solutions.size() + ", cost: " + (end - start) / 1000000D + "ms");
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    private static final class TileBranch {
        Tile tile;
        transient TileBranch prev; // 上一张
        List<TileBranch> meld;   // 下张搭子
        TileBranch       next;   // 不搭或不想搭的张
        int              length; // 搭子长度[2,3,4]
        int              type;   // 搭子、面子类型
        boolean          enableNext;

        TileBranch(Tile pTile, TileBranch pPrev, int pLength, int pType) {
            this.tile = pTile;
            this.prev = pPrev;
            this.meld = new ArrayList<>();
            this.next = null;
            this.length = pLength;
            this.type = pType;
            this.enableNext = true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TileBranch that = (TileBranch) o;
            return tile == that.tile;
        }

        @Override
        public int hashCode() {
            return Objects.hash(tile);
        }
    }
}
