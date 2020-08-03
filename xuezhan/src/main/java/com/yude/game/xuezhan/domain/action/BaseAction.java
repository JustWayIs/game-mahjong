package com.yude.game.xuezhan.domain.action;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/3 11:12
 * @Version: 1.0
 * @Declare:
 */
public interface BaseAction {
    void outCard(Integer card,int posId);

    void hu(Integer card,int posId);

    void chi(Integer card,int posId);

    void peng(Integer card,int posId);

    void gang(Integer card,Integer type,int posId);

    void cancel();
}
