package com.yude.game.common.model;

/**
 * @Author: HH
 * @Date: 2020/8/24 17:31
 * @Version: 1.0
 * @Declare:
 */
public interface MahjongOperation {

    Integer value();

    /**
     * 该操作会不会产生副露
     * @return
     */
    boolean canProductFulu();

    int priority();
}
