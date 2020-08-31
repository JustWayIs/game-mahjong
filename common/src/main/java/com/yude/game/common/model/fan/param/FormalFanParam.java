package com.yude.game.common.model.fan.param;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/17 16:28
 * @Version: 1.0
 * @Declare:
 */
public class FormalFanParam implements HuFanParam {
    /**
     * 设想中，吃碰杠 只用开头的一张牌来标识。 碰3万 -> 13    吃345万 -> 13  刻子3万 -> 13
     */

    //副露信息
    private List<Integer> chiList;
    private List<Integer> pengList;
    private List<Integer> zhiGangList;
    private List<Integer> buGangList;
    private List<Integer> anGangList;

    //立牌信息
    private List<Integer> wanList;
    private List<Integer> tongList;
    private List<Integer> tiaoList;

    private List<Integer> keziList;
    private List<Integer> shunziList;
    private List<Integer> duiziList;

    //不太可能传进来的参数刚好可以把上面的属性组装完，可能需要其他的参数来转换
    /*public static FormalFanTypeEnum build(....){
        FormalFanParam param = new FormalFanParam();
        ...
        ...
        return param;
    }*/


    public List<Integer> getChiList() {
        return chiList;
    }

    public List<Integer> getPengList() {
        return pengList;
    }

    public List<Integer> getZhiGangList() {
        return zhiGangList;
    }

    public List<Integer> getBuGangList() {
        return buGangList;
    }

    public List<Integer> getAnGangList() {
        return anGangList;
    }

    public List<Integer> getWanList() {
        return wanList;
    }

    public List<Integer> getTongList() {
        return tongList;
    }

    public List<Integer> getTiaoList() {
        return tiaoList;
    }

    public List<Integer> getKeziList() {
        return keziList;
    }

    public List<Integer> getShunziList() {
        return shunziList;
    }

    public List<Integer> getDuiziList() {
        return duiziList;
    }
}
