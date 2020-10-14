package com.yude.game.common.contant;

/**
 * @Author: HH
 * @Date: 2020/7/8 15:07
 * @Version: 1.0
 * @Declare:
 */
public abstract class RuleConfig {
    /**
     * 这个值后期会是动态的，相当于房间底注
     */
    protected int baseScoreFactor = 1;


    /**
     * 快速出牌/托管 所需要的玩家连续超时次数
     */
    protected int serialTimeoutCountLimit = 2;

    protected boolean canChi = true;

    protected boolean canPeng = true;

    protected boolean canZhiGang = true;

    protected boolean canBuGang = true;

    protected boolean canAnGang = true;


    public int getBaseScoreFactor() {
        return baseScoreFactor;
    }

    public RuleConfig setBaseScoreFactor(int baseScoreFactor) {
        this.baseScoreFactor = baseScoreFactor;
        return this;
    }

    public int getSerialTimeoutCountLimit() {
        return serialTimeoutCountLimit;
    }

    public RuleConfig setSerialTimeoutCountLimit(int serialTimeoutCountLimit) {
        this.serialTimeoutCountLimit = serialTimeoutCountLimit;
        return this;
    }

    public boolean isCanChi() {
        return canChi;
    }

    public RuleConfig setCanChi(boolean canChi) {
        this.canChi = canChi;
        return this;
    }

    public boolean isCanPeng() {
        return canPeng;
    }

    public RuleConfig setCanPeng(boolean canPeng) {
        this.canPeng = canPeng;
        return this;
    }

    public boolean isCanZhiGang() {
        return canZhiGang;
    }

    public RuleConfig setCanZhiGang(boolean canZhiGang) {
        this.canZhiGang = canZhiGang;
        return this;
    }

    public boolean isCanBuGang() {
        return canBuGang;
    }

    public RuleConfig setCanBuGang(boolean canBuGang) {
        this.canBuGang = canBuGang;
        return this;
    }

    public boolean isCanAnGang() {
        return canAnGang;
    }

    public RuleConfig setCanAnGang(boolean canAnGang) {
        this.canAnGang = canAnGang;
        return this;
    }
}
