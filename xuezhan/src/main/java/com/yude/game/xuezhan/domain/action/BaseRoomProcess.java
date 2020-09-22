package com.yude.game.xuezhan.domain.action;


/**
 * @Author: HH
 * @Date: 2020/7/6 15:02
 * @Version: 1.0
 * @Declare:
 */
public interface BaseRoomProcess {
    /**
     * 由于房间可能被重复使用，所以每打完一局，需要清除上一局的游戏数据【但是某些记录可能会需要保存下来】
     */
    void clean();

    void destroy();
}
