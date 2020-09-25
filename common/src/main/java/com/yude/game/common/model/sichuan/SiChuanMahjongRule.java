package com.yude.game.common.model.sichuan;

import com.yude.game.common.model.fan.FanInfo;
import com.yude.game.common.model.fan.MahjongRule;
import com.yude.game.common.model.fan.SpecificFanTypeEnum;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/25 15:50
 * @Version: 1.0
 * @Declare:
 */
public class SiChuanMahjongRule extends MahjongRule<SichuanRoomConfig> {
    private List<FanInfo<SpecificFanTypeEnum>> specificFanTypeList;

    public List<FanInfo<SpecificFanTypeEnum>> getSpecificFanTypeList() {
        return specificFanTypeList;
    }

    public SiChuanMahjongRule setSpecificFanTypeList(List<FanInfo<SpecificFanTypeEnum>> specificFanTypeList) {
        this.specificFanTypeList = specificFanTypeList;
        return this;
    }

    @Override
    public String toString() {
        return "SiChuanMahjongRule{" +
                "specificFanTypeList=" + specificFanTypeList +
                "} " + super.toString();
    }
}
