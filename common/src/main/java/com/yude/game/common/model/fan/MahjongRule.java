package com.yude.game.common.model.fan;

import com.yude.game.common.contant.RuleConfig;
import com.yude.game.common.model.Rule;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/18 10:46
 * @Version: 1.0
 * @Declare:
 */
public class MahjongRule<T extends RuleConfig> implements Rule {
    protected List<FanInfo<BaseHuTypeEnum>> baseHuList;
    protected List<FanInfo<HuTypeEnum>> huTypeList;
    protected List<FanInfo<FormalFanTypeEnum>> formalFanTypeEnumList;
    protected List<FanInfo<AppendedTypeEnum>> appendedTypeEnumList;
    protected List<FanInfo<CompoundFanTypeEnum>> compoundFanTypeEnumList;



    protected T ruleConfig;

    public List<FanInfo<BaseHuTypeEnum>> getBaseHuList() {
        return baseHuList;
    }

    public MahjongRule<T> setBaseHuList(List<FanInfo<BaseHuTypeEnum>> baseHuList) {
        this.baseHuList = baseHuList;
        return this;
    }

    public List<FanInfo<HuTypeEnum>> getHuTypeList() {
        return huTypeList;
    }

    public MahjongRule<T> setHuTypeList(List<FanInfo<HuTypeEnum>> huTypeList) {
        this.huTypeList = huTypeList;
        return this;
    }

    public List<FanInfo<FormalFanTypeEnum>> getFormalFanTypeEnumList() {
        return formalFanTypeEnumList;
    }

    public MahjongRule<T> setFormalFanTypeEnumList(List<FanInfo<FormalFanTypeEnum>> formalFanTypeEnumList) {
        this.formalFanTypeEnumList = formalFanTypeEnumList;
        return this;
    }

    public List<FanInfo<AppendedTypeEnum>> getAppendedTypeEnumList() {
        return appendedTypeEnumList;
    }

    public MahjongRule<T> setAppendedTypeEnumList(List<FanInfo<AppendedTypeEnum>> appendedTypeEnumList) {
        this.appendedTypeEnumList = appendedTypeEnumList;
        return this;
    }

    public List<FanInfo<CompoundFanTypeEnum>> getCompoundFanTypeEnumList() {
        return compoundFanTypeEnumList;
    }

    public MahjongRule<T> setCompoundFanTypeEnumList(List<FanInfo<CompoundFanTypeEnum>> compoundFanTypeEnumList) {
        this.compoundFanTypeEnumList = compoundFanTypeEnumList;
        return this;
    }

    public T getRuleConfig() {
        return ruleConfig;
    }

    public MahjongRule<T> setRuleConfig(T ruleConfig) {
        this.ruleConfig = ruleConfig;
        return this;
    }

    @Override
    public String toString() {
        return "MahjongRule{" +
                "baseHuList=" + baseHuList +
                ", huTypeList=" + huTypeList +
                ", formalFanTypeEnumList=" + formalFanTypeEnumList +
                ", appendedTypeEnumList=" + appendedTypeEnumList +
                ", compoundFanTypeEnumList=" + compoundFanTypeEnumList +
                ", ruleConfig=" + ruleConfig +
                '}';
    }
}
