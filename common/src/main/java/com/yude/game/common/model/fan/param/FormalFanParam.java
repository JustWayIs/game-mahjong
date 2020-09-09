package com.yude.game.common.model.fan.param;

import com.yude.game.common.contant.OperationEnum;
import com.yude.game.common.mahjong.Meld;
import com.yude.game.common.mahjong.Solution;
import com.yude.game.common.model.CardEnum;
import com.yude.game.common.model.StepAction;
import com.yude.game.common.model.fan.BaseHuTypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/17 16:28
 * @Version: 1.0
 * @Declare:
 */
public class FormalFanParam implements HuFanParam {
    /**
     * 设想中，吃碰杠 只用开头的一张牌来标识。 碰 三万 -> 13    吃 四万 -> 13  刻子 三万 -> 13
     */
    private BaseHuTypeEnum baseHuType;

    //副露信息
    private List<Integer> chiList = new ArrayList<>();
    private List<Integer> pengList = new ArrayList<>();
    private List<Integer> zhiGangList = new ArrayList<>();
    private List<Integer> buGangList = new ArrayList<>();
    private List<Integer> anGangList = new ArrayList<>();

    //立牌信息
    private List<Integer> wanList = new ArrayList<>();
    private List<Integer> tongList = new ArrayList<>();
    private List<Integer> tiaoList = new ArrayList<>();

    private List<Integer> keziList = new ArrayList<>();
    private List<Integer> shunziList = new ArrayList<>();
    private List<Integer> duiziList = new ArrayList<>();
    private List<Integer> genList = new ArrayList<>();

    //不太可能传进来的参数刚好可以把上面的属性组装完，可能需要其他的参数来转换
    public static FormalFanParam build(List<Integer> standList, List<StepAction> fuluList){
        FormalFanParam param = new FormalFanParam();

        return param;
    }

    public static FormalFanParam build(List<Integer> standList,List<StepAction> fuluList,Solution solution,BaseHuTypeEnum baseHuType){
        FormalFanParam param = new FormalFanParam();
        param.baseHuType = baseHuType;

        for(Integer card : standList){
            CardEnum.万.equals(CardEnum.judgeCardColor(card));
            switch (CardEnum.judgeCardColor(card)){
                case 万:param.wanList.add(card);break;
                case 条:param.tiaoList.add(card);break;
                case 筒:param.tongList.add(card);break;
                default:
            }
        }

        for(StepAction stepAction : fuluList){
            Integer value = stepAction.getOperationType().value();
            OperationEnum operationEnum = OperationEnum.matchByValue(value);
            switch (operationEnum){
                case CHI:param.chiList.add(stepAction.getTargetCard()-1);break;
                case PENG:param.pengList.add(stepAction.getTargetCard());break;
                case ZHI_GANG:param.zhiGangList.add(stepAction.getTargetCard());break;
                case BU_GANG:param.buGangList.add(stepAction.getTargetCard());break;
                case AN_GANG:param.anGangList.add(stepAction.getTargetCard());break;
            }
        }

        List<Meld> melds = solution.melds;
        for(Meld meld : melds){
            switch (meld.type){
                case Meld.TYPE_PAIR:param.duiziList.add(meld.tiles.get(0).id);break;
                case Meld.TYPE_TRIPLET:param.keziList.add(meld.tiles.get(0).id);break;
                case Meld.TYPE_SEQUENCE:param.shunziList.add(meld.tiles.get(0).id);break;
                case Meld.TYPE_KONG:param.genList.add(meld.tiles.get(0).id);break;
                default:
            }
        }
        return param;
    }


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

    public BaseHuTypeEnum getBaseHuType() {
        return baseHuType;
    }

    public List<Integer> getGenList() {
        return genList;
    }
}
