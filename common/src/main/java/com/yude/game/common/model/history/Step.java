package com.yude.game.common.model.history;

import com.yude.game.common.constant.Status;

/**
 * @Author: HH
 * @Date: 2020/8/3 16:47
 * @Version: 1.0
 * @Declare:
 */
public interface Step {
    Status stepType();

    int posId();

}
