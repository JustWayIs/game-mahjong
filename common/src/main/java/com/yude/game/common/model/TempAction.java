package com.yude.game.common.model;

import com.yude.game.common.model.history.GameStepModel;
import com.yude.game.common.model.history.OperationCardStep;

/**
 * @Author: HH
 * @Date: 2020/9/4 11:01
 * @Version: 1.0
 * @Declare: 用于游戏出现多操作时，把玩家的动作保存起来。当多操作结束后，找出优先级最高的操作执行。因为保存的是整个GameStepModel，所以在多操作结束后，也可以直接取出来存进history里面（虽然最终执行的只有一个操作，但是其他玩家的操作过程也应该被记录下来）.
 */
public class TempAction implements Comparable{
    private int priority;
    private GameStepModel<OperationCardStep> stepModel;

    public TempAction() {
    }

    public TempAction(GameStepModel<OperationCardStep> stepModel) {
        this.stepModel = stepModel;
        this.priority = stepModel.getOperationStep().getAction().getOperationType().priority();
    }

    public TempAction(int priority, GameStepModel<OperationCardStep> stepModel) {
        this.priority = priority;
        this.stepModel = stepModel;
    }

    public int getPriority() {
        return priority;
    }

    public TempAction setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public GameStepModel<OperationCardStep> getStepModel() {
        return stepModel;
    }

    public TempAction setStepModel(GameStepModel<OperationCardStep> stepModel) {
        this.stepModel = stepModel;
        return this;
    }

    /**
     * 从大到小
     * @param o
     * @return
     */
    @Override
    public int compareTo(Object o) {
        TempAction other = (TempAction) o;
        if(this.priority < other.priority){
            return 1;
        }else if(this.priority == other.priority){
            return 0;
        }
        return -1;

    }
}
