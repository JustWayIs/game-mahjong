package com.yude.game.common.model.sichuan;

import com.yude.game.common.contant.OperationEnum;
import com.yude.game.common.contant.RuleConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    /**
     * key:operationType  value:番数
     */
    private Map<Integer,Integer> gangFanMap;

    public SichuanRoomConfig(Boolean canHsz, Boolean canDingQue,List<Integer> dingQueOption) {
        this.canChi = false;
        this.canHsz = canHsz;
        this.canDingQue = canDingQue;
        this.dingQueOption = dingQueOption;
        gangFanMap = new HashMap<>();
        gangFanMap.put(OperationEnum.BU_GANG.value(),1);
        gangFanMap.put(OperationEnum.ZHI_GANG.value(),2);
        gangFanMap.put(OperationEnum.AN_GANG.value(),2);

    }

    public Integer getGangFan(Integer operationType){
        Integer fanNum = gangFanMap.get(operationType);
        return fanNum;
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

    public List<Integer> getDingQueOption() {
        return dingQueOption;
    }

    public SichuanRoomConfig setDingQueOption(List<Integer> dingQueOption) {
        this.dingQueOption = dingQueOption;
        return this;
    }



    @Override
    public String toString() {
        return "SichuanRoomConfig{" +
                "canHsz=" + canHsz +
                ", canDingQue=" + canDingQue +
                ", dingQueOption=" + dingQueOption +
                ", baseScoreFactor=" + baseScoreFactor +
                ", SERIAL_TIMEOUT_OUNT=" + SERIAL_TIMEOUT_OUNT +
                ", canChi=" + canChi +
                ", canPeng=" + canPeng +
                ", canZhiGang=" + canZhiGang +
                ", canBuGang=" + canBuGang +
                ", canAnGang=" + canAnGang +
                "} ";
    }
}
