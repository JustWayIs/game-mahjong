package com.yude.game.common.model.sichuan;

import com.yude.game.common.contant.RuleConfig;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/18 11:00
 * @Version: 1.0
 * @Declare:
 */
public class SichuanRoomConfig extends RuleConfig {

    private Boolean canHsz = true;

    private Boolean canDingQue = true;
    private List<Integer> dingQueOption;

    public SichuanRoomConfig(Boolean canHsz, Boolean canDingQue,List<Integer> dingQueOption) {
        this.canChi = false;
        this.canHsz = canHsz;
        this.canDingQue = canDingQue;
        this.dingQueOption = dingQueOption;

    }

    public Boolean getCanHsz() {
        return canHsz;
    }

    public SichuanRoomConfig setCanHsz(Boolean canHsz) {
        this.canHsz = canHsz;
        return this;
    }

    public Boolean getCanDingQue() {
        return canDingQue;
    }

    public SichuanRoomConfig setCanDingQue(Boolean canDingQue) {
        this.canDingQue = canDingQue;
        return this;
    }
}
