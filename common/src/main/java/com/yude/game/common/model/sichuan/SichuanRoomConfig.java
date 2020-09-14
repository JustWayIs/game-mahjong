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

    private int huaZhuBaseFan;

    /**
     * 能不能胡多次
     */
    private boolean canHus;

    /**
     * 当前血战文档说明 牌墙没牌时的 游戏的最后一次操作 不能杠
     */
    private boolean lastCardProhibitGang;

    /**
     * key:operationType  value:番数
     */
    private Map<Integer,Integer> gangFanMap;

    public SichuanRoomConfig(Boolean canHsz, Boolean canDingQue,List<Integer> dingQueOption,boolean canHus) {
        this.canChi = false;
        this.canHsz = canHsz;
        this.canDingQue = canDingQue;
        this.dingQueOption = dingQueOption;
        gangFanMap = new HashMap<>();
        gangFanMap.put(OperationEnum.BU_GANG.value(),1);
        gangFanMap.put(OperationEnum.ZHI_GANG.value(),2);
        gangFanMap.put(OperationEnum.AN_GANG.value(),2);
        huaZhuBaseFan = 16;
        this.canHus = canHus;
        this.lastCardProhibitGang = true;

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

    public int getHuaZhuBaseFan() {
        return huaZhuBaseFan;
    }

    public SichuanRoomConfig setHuaZhuBaseFan(int huaZhuBaseFan) {
        this.huaZhuBaseFan = huaZhuBaseFan;
        return this;
    }

    public Map<Integer, Integer> getGangFanMap() {
        return gangFanMap;
    }

    public SichuanRoomConfig setGangFanMap(Map<Integer, Integer> gangFanMap) {
        this.gangFanMap = gangFanMap;
        return this;
    }

    public boolean isCanHus() {
        return canHus;
    }

    public SichuanRoomConfig setCanHus(boolean canHus) {
        this.canHus = canHus;
        return this;
    }

    public boolean isLastCardProhibitGang() {
        return lastCardProhibitGang;
    }

    public SichuanRoomConfig setLastCardProhibitGang(boolean lastCardProhibitGang) {
        this.lastCardProhibitGang = lastCardProhibitGang;
        return this;
    }

    @Override
    public String toString() {
        return "SichuanRoomConfig{" +
                "canHsz=" + canHsz +
                ", canDingQue=" + canDingQue +
                ", dingQueOption=" + dingQueOption +
                ", huaZhuBaseFan=" + huaZhuBaseFan +
                ", canHus=" + canHus +
                ", lastCardProhibitGang=" + lastCardProhibitGang +
                ", gangFanMap=" + gangFanMap +
                ", baseScoreFactor=" + baseScoreFactor +
                ", SERIAL_TIMEOUT_OUNT=" + SERIAL_TIMEOUT_OUNT +
                ", canChi=" + canChi +
                ", canPeng=" + canPeng +
                ", canZhiGang=" + canZhiGang +
                ", canBuGang=" + canBuGang +
                ", canAnGang=" + canAnGang +
                '}';
    }
}
