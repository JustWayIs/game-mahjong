package com.yude.game.xuezhan.application.response.dto;

import com.yude.game.common.application.response.dto.BaseSeatInfo;
import com.yude.game.common.application.response.dto.PlayerDTO;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/27 10:23
 * @Version: 1.0
 * @Declare:
 */
public class SichuanSeatInfoDTO extends BaseSeatInfo {
    /**
     * 不包含被碰、杠、胡的牌
     */
    private List<Integer> outCardPool;
    /**
     * 不能看到别人的
     */
    private List<Integer> canOperations;
    private List<Integer> recommendExchangeCards;
    private Integer recommendDingQueColor;
    private PlayerDTO playerDTO;

}
