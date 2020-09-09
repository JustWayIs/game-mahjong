package com.yude.game.common.model.fan.param;

import com.yude.game.common.mahjong.Meld;
import com.yude.game.common.mahjong.Solution;

/**
 * @Author: HH
 * @Date: 2020/8/17 16:55
 * @Version: 1.0
 * @Declare:
 */
public class AppendedFanParam implements HuFanParam {
    //因为已经走过了胡牌方式的判断，所以，这里是能够确定是否是自摸
    private boolean ziMo;
    //立牌数量
    private int standCardCount;
    //牌墙剩余牌的数量
    private int cardWallRemainingCount;

    //杠牌数量 : 似乎没有用到
    private int gangCount;

    private boolean qiangGang;

    private int mocardNum;
    private boolean banker;

    //根的数量
    private int genCount;

    private boolean beforeOperationIsGang;

    public boolean isZiMo() {
        return ziMo;
    }

    public AppendedFanParam setZiMo(boolean ziMo) {
        this.ziMo = ziMo;
        return this;
    }

    public int getStandCardCount() {
        return standCardCount;
    }

    public AppendedFanParam setStandCardCount(int standCardCount) {
        this.standCardCount = standCardCount;
        return this;
    }

    public int getCardWallRemainingCount() {
        return cardWallRemainingCount;
    }

    public AppendedFanParam setCardWallRemainingCount(int cardWallRemainingCount) {
        this.cardWallRemainingCount = cardWallRemainingCount;
        return this;
    }

    public int getGangCount() {
        return gangCount;
    }

    public AppendedFanParam setGangCount(int gangCount) {
        this.gangCount = gangCount;
        return this;
    }

    public boolean isQiangGang() {
        return qiangGang;
    }

    public AppendedFanParam setQiangGang(boolean qiangGang) {
        this.qiangGang = qiangGang;
        return this;
    }

    public int getMocardNum() {
        return mocardNum;
    }

    public AppendedFanParam setMocardNum(int mocardNum) {
        this.mocardNum = mocardNum;
        return this;
    }

    public boolean isBanker() {
        return banker;
    }

    public AppendedFanParam setBanker(boolean banker) {
        this.banker = banker;
        return this;
    }

    public int getGenCount() {
        return genCount;
    }

    public AppendedFanParam setGenCount(int genCount) {
        this.genCount = genCount;
        return this;
    }

    public AppendedFanParam setGenCountBySolution(Solution solution){
        int genNum = 0;
        for(Meld meld : solution.melds){
            if(Meld.TYPE_KONG == meld.type){
                genNum++;
            }
        }
        this.gangCount = genNum;
        return this;
    }

    public boolean isBeforeOperationIsGang() {
        return beforeOperationIsGang;
    }

    public AppendedFanParam setBeforeOperationIsGang(boolean beforeOperationIsGang) {
        this.beforeOperationIsGang = beforeOperationIsGang;
        return this;
    }
}
