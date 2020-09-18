package com.yude.game.common.model.sichuan;

import com.yude.game.common.model.CardEnum;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: HH
 * @Date: 2020/8/26 17:19
 * @Version: 1.0
 * @Declare:
 */
public class SichuanPlayHelper {

    public static void main(String[] args) {
        List<Integer> cards = new ArrayList<>(Arrays.asList(12,15,15,15,16,18,18,19,19,19,21,23,28,29));
        List<Integer> result = null;
        Integer color = null;
        long beforeTime = System.currentTimeMillis();
        for(int i = 0 ; i < 1 ; ++i){
             result = recommendedChangeCard(cards, 3);
             color = recommendedDingQue(cards);
        }
        System.out.println("百万次耗时："+(System.currentTimeMillis()-beforeTime)+"毫秒");

        System.out.println("换三张："+result);
        System.out.println("定缺："+color);
    }


    public static Integer recommendedDingQue(List<Integer> standCardList){
        Map<CardEnum,Boolean> colorGroup = new HashMap<>();
        colorGroup.put(CardEnum.万,false);
        colorGroup.put(CardEnum.条,false);
        colorGroup.put(CardEnum.筒,false);
        for(Integer card : standCardList){
            CardEnum cardEnum = CardEnum.judgeCardColor(card);
            colorGroup.put(cardEnum,true);
        }
        for(Map.Entry<CardEnum,Boolean> entry : colorGroup.entrySet()){
            if(!entry.getValue()){
                return entry.getKey().getColor();
            }
        }

        List<Integer> selectCard = recommendedChangeCard(standCardList, 0);
        CardEnum cardEnum = CardEnum.judgeCardColor(selectCard.get(0));
        return cardEnum.getColor();
    }

    public static List<Integer> recommendedChangeCard(List<Integer> standCardList, int changeCount){
        List<Integer> copyStandCardList = new ArrayList<>(standCardList);
        Collections.sort(copyStandCardList);
        System.out.println("排序后："+copyStandCardList);

        final Map<CardEnum, List<Integer>> colorCardGroup = copyStandCardList.stream().collect(Collectors.groupingBy(card -> CardEnum.judgeCardColor(card), Collectors.toList()));

        int beforeCardCount =  15;
        CardEnum select = null;
        /**
         * 选出数量大于等于换牌数量，并且是3个花色中，牌数量最少的数值
         */
        for(Map.Entry<CardEnum,List<Integer>> entry : colorCardGroup.entrySet()){
            CardEnum cardEnum = entry.getKey();
            List<Integer> cardList = entry.getValue();
            int cardCount = cardList.size();
            if(cardCount < beforeCardCount && cardCount >= changeCount){
                select = cardEnum;
                beforeCardCount = cardCount;
            }
        }

        /**
         * 牌数量相同的花色还要对比牌的类型： 选单牌数量较少的
         */
        Map<CardEnum,List<Integer>> allSelectMap = new HashMap<>();
        for(Map.Entry<CardEnum,List<Integer>> entry : colorCardGroup.entrySet()){
            CardEnum cardEnum = entry.getKey();
            List<Integer> cardList = entry.getValue();
            int cardCount = cardList.size();
            if(cardCount == beforeCardCount){
                allSelectMap.put(cardEnum,cardList);

            }
        }

        /**
         * 定缺
         */
        if(allSelectMap.size() == 1 && changeCount == 0){
            List<Integer> selectColorList = allSelectMap.get(select);
            return selectColorList;
        }
        final List<Integer> sorting = sorting(allSelectMap);

        return sorting;
    }

    /**
     *
     * @param allSelectMap
     * @return key:单牌数量    value:根据优先级排好序的牌组
     */
    private static List<Integer> sorting(Map<CardEnum,List<Integer>> allSelectMap){
        int beforeSingleCardNum = 0;
        Map<Integer,List<Integer>> result = new HashMap<>();
        for(Map.Entry<CardEnum,List<Integer>> entry : allSelectMap.entrySet()){
            List<Integer> cardList = entry.getValue();

            List<Integer> singleCardList = new ArrayList<>();
            List<Integer> doubleCardList = new ArrayList<>();
            List<Integer> shunziList = new ArrayList<>();
            List<Integer> keziList = new ArrayList<>();
            List<Integer> genList = new ArrayList<>();

            /**
             * 根据优先级层数 循环多次，按优先级从高到底分别找出牌组进行分类，并且从原牌组中清除已经找出的高优先级组合，在剩余牌中找次优先级的牌组
             */
            selectRepetitionGroup(cardList,genList,4);
            selectRepetitionGroup(cardList,keziList,3);
            selectSerialGroup(cardList,shunziList);
            selectRepetitionGroup(cardList,doubleCardList,2);
            //cardList剩下的全是单牌

            Integer singleCardNum = cardList.size();
            /**
             * 按照优先级从低到高，再把牌重新放进一个集合中
             */
            resetRepetition(cardList,doubleCardList,2);
            resetSerial(cardList,shunziList);
            resetRepetition(cardList,keziList,3);
            resetRepetition(cardList,genList,4);


            result.put(singleCardNum,cardList);

            /**
             * 在牌数量一致的花色中，找出单牌最多的花色
             */
            if(singleCardNum > beforeSingleCardNum){
                beforeSingleCardNum = singleCardNum;
            }
        }
        final List<Integer> resultCardList = result.get(beforeSingleCardNum);
        return  resultCardList;
    }

    private static void selectRepetitionGroup(List<Integer> cardList,List<Integer> genList,int repetitionNum){
        int sameCount = 1;
        Integer beforeCard = 0;
        for(Integer card : cardList){
            if(beforeCard == 0){
                beforeCard = card;
                continue;
            }
            if(beforeCard.equals(card)){
                sameCount++;
                if(sameCount == repetitionNum){
                    genList.add(card);
                    sameCount = 1;
                    beforeCard = 0;
                    continue;
                }
            }else{
                sameCount = 1;
            }
            beforeCard = card;
        }
        for(Integer card : genList){
            for(int i = 0 ; i < repetitionNum ; ++i){
                cardList.remove(card);
            }
        }
    }

    /**
     * 不可以有万条筒之外的花色
     * @param cardList
     * @param shunziList
     */
    private static void selectSerialGroup(List<Integer> cardList,List<Integer> shunziList){
        List<Integer> distinctCards = cardList.stream().distinct().collect(Collectors.toList());
        int serialCount = 1;
        Integer beforeCard = 0;
        for(Integer card : distinctCards){
            if(beforeCard == 0){
                beforeCard = card;
                continue;
            }
            if(card - beforeCard == 1){
                serialCount++;
                if(serialCount == 3){
                    shunziList.add(card);
                    serialCount = 1;
                    beforeCard = 0 ;
                    continue;
                }
            }else{
                serialCount = 1;
            }
            beforeCard = card;
        }

        for(Integer card : shunziList){
            cardList.remove(card);
            Integer cardInfo = card - 1;
            cardList.remove(cardInfo);
            cardInfo = card -2;
            cardList.remove(cardInfo);
        }
    }

    private static void resetRepetition(List<Integer> cardList,List<Integer> genList,int repetitionNum){
        for(Integer card : genList){
            for(int i = 0 ; i < repetitionNum ; ++i){
                cardList.add(card);
            }
        }
    }

    private static void resetSerial(List<Integer> cardList,List<Integer> shunziList){

        for(Integer card : shunziList){
            cardList.add(card - 2);
            cardList.add(card - 1);
            cardList.add(card);


        }
    }
}
