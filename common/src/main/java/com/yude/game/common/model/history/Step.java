package com.yude.game.common.model.history;

import com.yude.game.common.constant.Status;

/**
 * @Author: HH
 * @Date: 2020/8/3 16:47
 * @Version: 1.0
 * @Declare:
 */
public interface Step {
    /**
     * 游戏状态:在遍历时，用于确定Step类型
     * @return
     */
    Status gameStatus();

    /**
     * 操作类型:其意义在于有些具体游戏的操作，用的是同一个gameStatus.需要其他的属性来确认（StepAction的type）
     * @return 不是所有step都有...
     */
    Integer actionType();

    int posId();

}
