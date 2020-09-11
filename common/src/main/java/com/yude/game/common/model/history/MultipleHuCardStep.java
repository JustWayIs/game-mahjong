package com.yude.game.common.model.history;

import com.yude.game.common.constant.Status;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/9/7 18:00
 * @Version: 1.0
 * @Declare:  用不到这个，如果是一炮多响，修改StepAction的类型为一炮多响就行了
 */
public class MultipleHuCardStep implements Step {
    List<HuCardStep> list;

    @Override
    public Status gameStatus() {
        return null;
    }

    @Override
    public Integer actionType() {
        return null;
    }

    @Override
    public int posId() {
        return 0;
    }
}
