package com.yude.game.common.model.sichuan;

import com.yude.game.common.model.AbstractSeatModel;
import com.yude.game.common.model.CardEnum;
import com.yude.game.common.model.MahjongSeat;
import com.yude.game.common.model.Player;
import com.yude.game.common.model.sichuan.constant.SeatStatusEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/4 14:55
 * @Version: 1.0
 * @Declare:
 */
public class SichuanMahjongSeat extends AbstractSeatModel{
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

    private boolean alreadyHu;

    /**
     * 用于血战
     */
    private Integer huCard;

    /**
     * 用于血流
     */
    private List<Integer> huCards;

    public SichuanMahjongSeat(Player player, int posId) {
        super(player, posId);
    }

    @Override
    public void init() {
        huCards = new ArrayList<>();
    }

    @Override
    public void clean() {

    }

    public boolean isHuaZhu(){
        List<Integer> standCardList = mahjongSeat.getStandCardList();
        CardEnum queColorEnum = CardEnum.judgeCardColorByDingQueColor(this.queColor);
        for(Integer card : standCardList){
            CardEnum cardEnum = CardEnum.judgeCardColor(card);
            if(queColorEnum.equals(cardEnum)){
                return true;
            }
        }
        return false;
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

    public boolean isAlreadyHu() {
        return mahjongSeat.existsStatus(SeatStatusEnum.ALREADY_HU);
    }

    public List<Integer> getHuCards() {
        return huCards;
    }

    public void addHuCardToHuCards(Integer card){
        huCards.add(card);
    }

    public Integer getHuCard() {
        return huCard;
    }

    public SichuanMahjongSeat setHuCard(Integer huCard) {
        this.huCard = huCard;
        return this;
    }

}
