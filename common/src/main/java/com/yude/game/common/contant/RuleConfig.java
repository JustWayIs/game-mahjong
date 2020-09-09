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
    protected Integer baseScoreFactor = 1;


    /**
     * 快速出牌/托管 所需要的玩家连续超时次数
     */
    protected Integer SERIAL_TIMEOUT_OUNT = 2;

    protected Boolean canChi = true;

    protected Boolean canPeng = true;

    protected Boolean canZhiGang = true;

    protected Boolean canBuGang = true;

    protected Boolean canAnGang = true;


    public Integer getBaseScoreFactor() {
        return baseScoreFactor;
    }

    public RuleConfig setBaseScoreFactor(Integer baseScoreFactor) {
        this.baseScoreFactor = baseScoreFactor;
        return this;
    }

    public Integer getSERIAL_TIMEOUT_OUNT() {
        return SERIAL_TIMEOUT_OUNT;
    }

    public RuleConfig setSERIAL_TIMEOUT_OUNT(Integer SERIAL_TIMEOUT_OUNT) {
        this.SERIAL_TIMEOUT_OUNT = SERIAL_TIMEOUT_OUNT;
        return this;
    }

    public Boolean getCanChi() {
        return canChi;
    }

    public RuleConfig setCanChi(Boolean canChi) {
        this.canChi = canChi;
        return this;
    }

    public Boolean getCanPeng() {
        return canPeng;
    }

    public RuleConfig setCanPeng(Boolean canPeng) {
        this.canPeng = canPeng;
        return this;
    }

    public Boolean getCanZhiGang() {
        return canZhiGang;
    }

    public RuleConfig setCanZhiGang(Boolean canZhiGang) {
        this.canZhiGang = canZhiGang;
        return this;
    }

    public Boolean getCanBuGang() {
        return canBuGang;
    }

    public RuleConfig setCanBuGang(Boolean canBuGang) {
        this.canBuGang = canBuGang;
        return this;
    }

    public Boolean getCanAnGang() {
        return canAnGang;
    }

    public RuleConfig setCanAnGang(Boolean canAnGang) {
        this.canAnGang = canAnGang;
        return this;
    }


}
