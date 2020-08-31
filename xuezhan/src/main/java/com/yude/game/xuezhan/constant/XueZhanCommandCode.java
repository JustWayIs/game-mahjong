package com.yude.game.xuezhan.constant;

import com.yude.game.common.contant.MahjongCommandCode;

/**
 * @Author: HH
 * @Date: 2020/8/20 15:08
 * @Version: 1.0
 * @Declare:
 */
public interface XueZhanCommandCode extends MahjongCommandCode {
    /**
     * 换三张
     */
    int EXCHANGE_CARD = 0x1003;

    /**
     * 定缺
     */
    int DING_QUE = 0x1004;

    int OUT_CARD = 0x1005;

    int PENG = 0x1006;

    int ZHI_GANG = 0x1007;

    int BU_GANG = 0x1008;

    int AN_GANG = 0x1009;

    int HU = 0x100A;

    int YI_PAO_DUO_XIANG = 0x100B;

    int RECONNECTION = 0x100F;

}
