package com.yude.game.common.model.sichuan;

import com.yude.game.common.model.AbstractSeatModel;
import com.yude.game.common.model.MahjongSeat;
import com.yude.game.common.model.Player;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/4 14:55
 * @Version: 1.0
 * @Declare:
 */
public class SichuanMahjongSeat extends AbstractSeatModel {
    private MahjongSeat mahjongSeat;

    /**
     * 换三张：要交换的牌
     */
    private List<Integer> discardCards;

    /**
     * 换三张：得到的牌
     */
    private List<Integer> gainedCards;

    /**
     * 定缺的花色
     */
    private Integer queColor;

    public SichuanMahjongSeat(Player player, int posId) {
        super(player, posId);
    }

    @Override
    public void init() {

    }

    @Override
    public void clean() {

    }

    public void setMahjongSeat(MahjongSeat seat){
        this.mahjongSeat = seat;
    }

    public boolean alredyDiscardCard(){
       return discardCards != null;
    }

    public List<Integer> getDiscardCards() {
        return discardCards;
    }

    public void setDiscardCards(List<Integer> discardCards) {
        this.discardCards = discardCards;
    }

    public List<Integer> getGainedCards() {
        return gainedCards;
    }

    public void setGainedCards(List<Integer> gainedCards) {
        this.gainedCards = gainedCards;
    }

    public Integer getQueColor() {
        return queColor;
    }

    public void setQueColor(Integer queColor) {
        this.queColor = queColor;
    }

    public MahjongSeat getMahjongSeat() {
        return mahjongSeat;
    }
}
