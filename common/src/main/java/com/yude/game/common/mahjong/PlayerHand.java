package com.yude.game.common.mahjong;

import com.yude.game.common.model.CardEnum;
import com.yude.game.common.model.StepAction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by someone on 2020/8/6 10:52.
 */
public class PlayerHand implements Cloneable {
    // ===========================================================
    // Constants
    // ===========================================================


    // ===========================================================
    // Fields
    // ===========================================================
    public List<Meld> melds; // 副露
    public List<Tile> tiles; // 立牌
    public List<Tile> discards; // 打掉的牌
    public List<Solution> solutions;
    public int bannedSuit;

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

    public boolean canOperation(Integer card) {
        final CardEnum cardEnum = CardEnum.judgeCardColor(card);
        int color = cardEnum.getColor();
        if (color == bannedSuit) {
            return false;
        }
        return true;
    }

    // ===========================================================
    // Methods
    // ===========================================================
    public boolean canChi(Integer card) {
        for (Solution solution : solutions) {
            List<Tile> canChow = solution.canChow;
            for (Tile tile : canChow) {
                if (tile.id == card) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canPeng(Integer card) {
        if (!canOperation(card)) {
            return false;
        }
        int num = 0;
        for (Tile tile : tiles) {
            if (tile.id == card) {
                num++;
                if (num == 2) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canZhiGang(Integer card) {
        if (!canOperation(card)) {
            return false;
        }
        int num = 0;
        for (Tile tile : tiles) {
            if (tile.id == card) {
                num++;
            }
        }
        if (num == 3) {
            return true;
        }
        return false;
    }

    /**
     * 可能有多个暗杠，所以把stepActions传进来
     *
     * @param stepActions
     */
    public void canAnGang(List<StepAction> stepActions, Integer tookCard) {

        // 判断手上有暗杠，但是没有开杠的牌
        int cardNum = 0;
        int tempCard = 0;
        for (Tile card : tiles) {
            if (tempCard != card.id || CardEnum.judgeCardColor(tempCard).getColor() == bannedSuit) {
                cardNum = 0;
            }
            tempCard = card.id;
            cardNum++;
            //摸上来的牌并没有立即更新到PalyerHand对象的tiles里。
            if (cardNum == 4 || (cardNum == 3 && tookCard != null && tempCard == tookCard)) {
                StepAction stepAction = new StepAction();
                stepAction.setTargetCard(card.id);
                //.setOperationType(XueZhanMahjongOperationEnum.AN_GANG);
                stepActions.add(stepAction);
            }
        }


    }

    /**
     * 由于这里的补杠指定了card。所以该方法是不允许过手碰的
     * @param card
     * @return
     */
    public boolean canBuGang(Integer card) {
        if (card == null) {
            return false;
        }
        if (!canOperation(card)) {
            return false;
        }
        for (Meld meld : melds) {
            if (meld.type == Meld.TYPE_TRIPLET && meld.tiles.get(0).id == card) {
                return true;
            }
        }
        return false;
    }

    /**
     * 由于需求变动，可以过手杠，那么就可能出现多张可以补杠的牌
     * @param stepActions
     * @return
     */
    public void canBuGang(List<StepAction> stepActions,Integer tookCard) {
        for (Meld meld : melds) {
            if (meld.type == Meld.TYPE_TRIPLET) {
                final int pengCard = meld.tiles.get(0).id;
                if(pengCard == tookCard){
                    StepAction stepAction = new StepAction();
                    stepAction.setTargetCard(tookCard);
                    stepActions.add(stepAction);
                    continue;
                }
                for(Tile card : tiles){
                    if(pengCard == card.id){
                        StepAction stepAction = new StepAction();
                        stepAction.setTargetCard(card.id);
                        stepActions.add(stepAction);
                    }
                }

            }
        }

    }


    public boolean canHu() {
        for (Solution solution : solutions) {
            if (solution.isWin) {
                return true;
            }
        }
        return false;
    }

    /**
     * 通知胡牌的时候不需要告诉客户端胡什么
     * 但是客户端请求胡的时候需要知道胡什么，并且找出胡牌的最大番，因为solutions里面可能不止有一种立牌方式可以胡牌
     * 摸牌后，没有立马solution 【也没有必要立马solution，判断胡牌的话，像点炮胡一样，直接判断听牌列表就行了，出牌的时候再判断需不需要solution】
     *
     * @return
     */
    public List<Solution> getHuCardSolution() {
        List<Solution> canHuSolutions = new ArrayList<>();

        for (Solution solution : solutions) {
            if (solution.isWin) {
                canHuSolutions.add(solution);
            }

        }
        /*else {
            //庄家第一次判断胡没胡牌是没有摸牌的

            //H2 玩家摸牌后判断胡不胡牌，这个时候因为牌已经加入到手牌里了，所以直接判断能不能胡就行了，问题在于有没有solution。如果加入手牌后，没有立马solution 【也没有必要立马solution，像点炮胡一样，直接判断听牌列表就行了，出牌的时候再solution】，就要走上面的逻辑
            for (Solution solution : solutions) {
                if (solution.isWin) {
                    canHuSolutions.add(solution);
                }
            }
        }*/
        return canHuSolutions;
    }

    public boolean canTingTargetCard(Integer card) {
        if (card != null) {
            for (Solution solution : solutions) {
                List<Tile> canWin = solution.canWin;
                for (Tile tile : canWin) {
                    if (tile.id == card) {
                        return true;
                    }

                }
            }
        }
        return false;
    }


    /**
     * 已经胡牌的玩家 貌似一定会有一种理牌 是可以听的
     *
     * @return
     */
    public boolean isTing() {
        for (Solution solution : solutions) {
            if (solution.canWin.size() > 0) {
                return true;
            }
        }
        return false;
    }

    public Set<Integer> getTingCards() {
        Set<Integer> set = new HashSet<>();
        for (Solution solution : solutions) {
            if (solution.canWin.size() > 0) {
                for (Tile tile : solution.canWin) {
                    set.add(tile.id);
                }
            }
        }
        return set;
    }

    @Override
    public PlayerHand clone() throws CloneNotSupportedException {
        PlayerHand playerHand = (PlayerHand) super.clone();
        playerHand.tiles = new ArrayList<>(playerHand.tiles);
        playerHand.melds = new ArrayList<>(playerHand.melds);
        playerHand.discards = new ArrayList<>(playerHand.discards);
        playerHand.solutions = new ArrayList<>(playerHand.solutions);
        return playerHand;
    }


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
