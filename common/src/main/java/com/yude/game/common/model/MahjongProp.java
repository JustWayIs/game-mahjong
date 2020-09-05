package com.yude.game.common.model;

import cn.hutool.core.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.*;

/**
 * @Author: HH
 * @Date: 2020/8/3 10:38
 * @Version: 1.0
 * @Declare:
 */
public enum MahjongProp {
    /**
     *
     */
    INSTANCE;


    /**
     * 序牌
     */
    private static final int[] WAN = {11, 12, 13, 14, 15, 16, 17, 18, 19};
    private static final int[] TIAO = {21, 22, 23, 24, 25, 26, 27, 28, 29};
    private static final int[] TONG = {31, 32, 33, 34, 35, 33, 37, 38, 39};

    /**
     * 字牌
     */
    private static final int[] FENG = {41,42,43,44};
    private static final String[] FENG_STR = {"东风","南风","西风","北风"};

    private static final int[] JIAN = {51,52,53};
    private static final String[] JIAN_STR = {"红中","发财","白板"};

    /**
     * 花牌
     */
    private static final int[] HUA = {61,62,63,64,65,66,67,68};
    private static final String[] HUA_STR = {"春","夏","秋","冬","梅","兰","竹","菊"};


    public static final int COLOR_WAN = 0;
    public static final int COLOR_TONG = 1;
    public static final int COLOR_TIAO = 2;

    public static final int THRESHOLD = 10;
    public static final int XU_LENGTH = 9;

    //H2 用于测试时在本地配牌
    public static final boolean OPEN_CONFIGURATION_CARD = false;

    public static List<String> cardConvertName(Integer... card) {
        List<String> list = new ArrayList<>();
        Arrays.stream(card).forEachOrdered(value -> {
            int color = value / THRESHOLD;
            int realValue = value % THRESHOLD;
            String colorStr = null;
            switch (color){
                case 1 : colorStr = realValue+"万";break;
                case 2 : colorStr = realValue+"条";break;
                case 3 : colorStr = realValue+"筒";break;
                case 4 : colorStr = FENG_STR[realValue];break;
                case 5 : colorStr = JIAN_STR[realValue];break;
                case 6 : colorStr = HUA_STR[realValue];break;
                default:
            }
            list.add(colorStr);
        });
        return list;
    }

    public static List<String> cardConvertName(List<Integer> cardList) {
        List<String> list = new ArrayList<>();
        cardList.stream().forEachOrdered(value -> {
            int color = value / THRESHOLD;
            int realValue = value % THRESHOLD;
            String colorStr = null;
            switch (color){
                case 1 : colorStr = realValue+"万";break;
                case 2 : colorStr = realValue+"筒";break;
                case 3 : colorStr = realValue+"条";break;
                case 4 : colorStr = FENG_STR[realValue];break;
                case 5 : colorStr = JIAN_STR[realValue];break;
                case 6 : colorStr = HUA_STR[realValue];break;
                default:
            }
            list.add(colorStr);
        });
        return list;
    }

    public static List<Integer> getAllCard(MahjongCard[] mahjongs) {
        List<Integer> cards = new ArrayList<>();
        for(MahjongCard mahjongCardInfo : mahjongs){
            for(int i = 0 ; i < mahjongCardInfo.getCardCount() ; ++i){
                cards.addAll(mahjongCardInfo.getCards());
            }
        }
        return cards;
    }

    public static Map<Integer, List<Integer>> getCardConfiguration() {
        String path;
        if (isWindows()) {
            path = "E://log/";
        } else {
            path = "/data/game/config/";
        }
        return parseTestCardConfigXml(path);
    }

    public static Map<Integer, List<Integer>> parseTestCardConfigXml(String path) {
       /* File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }*/
        String fileName = "testCardConfig.xml";
        File file = new File(path + fileName);
        Document document = XmlUtil.readXML(file);

        //根节点
        Element rootElement = document.getDocumentElement();
        String type = rootElement.getAttribute("type");
        String open = rootElement.getAttribute("open");
        Boolean isOpen = Boolean.valueOf(open);
        if (!isOpen) {
            return null;
        }

        List<Element> cardsList = XmlUtil.getElements(rootElement, "cards");
        Element target = null;
        for (Element element : cardsList) {
            if (element.hasAttribute("type") && element.getAttribute("type").equals(type)) {
                target = element;
                break;
            }
        }
        List<Element> cardList = XmlUtil.getElements(target, "card");

        Map<Integer, List<Integer>> cardGroup = new HashMap();
        List<Integer> group_1 = new ArrayList<>();
        List<Integer> group_2 = new ArrayList<>();
        List<Integer> group_3 = new ArrayList<>();
        List<Integer> group_4 = new ArrayList<>();
        List<Integer> cardWall = new ArrayList<>();
        cardGroup.put(0, group_1);
        cardGroup.put(1, group_2);
        cardGroup.put(2, group_3);
        cardGroup.put(3, group_4);
        cardGroup.put(4, cardWall);

        for (int i = 0; i < cardList.size(); ++i) {
            Element element = cardList.get(i);
            final Integer posId = i;
            Arrays.stream(element.getAttribute("values").split(",")).forEach(card -> cardGroup.get(posId).add(Integer.valueOf(card.trim())));
        }

        /*List<Integer> list = new ArrayList<>();
        cardList.stream().map(element -> element.getAttribute("values")
                .split(","))
                .flatMap(cards -> Arrays.stream(cards))
                .forEach(card -> list.add(Integer.valueOf(card.trim())));*/

        //XmlUtil.getRootElement(document);
        System.out.println();
        return cardGroup;
    }

    private static boolean isWindows() {
        String systemStr = System.getProperty("os.name");
        if (systemStr != null && systemStr.startsWith("Windows")) {
            return true;
        }
        return false;
    }

    public static Map<Integer, List<Integer>> getDealCardGroup(MahjongCard[] mahjongCards,Integer bankerPosId,List<Integer> cardWall) {
        if (OPEN_CONFIGURATION_CARD) {
            //用于本地配牌
            Map<Integer, List<Integer>> cardConfiguration = getCardConfiguration();
            if (cardConfiguration != null) {
                return cardConfiguration;
            }
        }
        List<Integer> allCardList = getAllCard(mahjongCards);
        Collections.shuffle(allCardList);

        Map<Integer, List<Integer>> cardGroup = new HashMap();
        List<Integer> group_1 = new ArrayList<>();
        List<Integer> group_2 = new ArrayList<>();
        List<Integer> group_3 = new ArrayList<>();
        List<Integer> group_4 = new ArrayList<>();
        List<Integer> cardWallRamaining = new ArrayList<>();
        cardGroup.put(0, group_1);
        cardGroup.put(1, group_2);
        cardGroup.put(2, group_3);
        cardGroup.put(3, group_4);
        cardGroup.put(4, cardWallRamaining);
        int j = 0;
        int i = 0;
        for(; i < 48 ;i+=4){
            cardGroup.get((j + bankerPosId) % 4).addAll(allCardList.subList(i,i+4));
            j++;
        }
        for(; i <= 52 ; ++i){
            cardGroup.get((i + bankerPosId) % 4).add(allCardList.get(i));
        }
        cardWallRamaining.addAll(allCardList.subList(53, allCardList.size()));
        /*int length = 14;
        List<Integer> group_1 = new ArrayList<>(allCardList.subList(0, length));
        cardGroup.put(bankerPosId, group_1);
        for (int i = 0; i < 4; ++i) {
            if(bankerPosId == i){
                continue;
            }
            List<Integer> group_2 = new ArrayList<>(allCardList.subList(length, length + 13));
            length += 13;
            cardGroup.put(i,group_2);
        }
        List<Integer> cardWall = new ArrayList<>(allCardList.subList(length, allCardList.size()));
        cardGroup.put(4, cardWall);

        final PlayBoard playBoard = MJManager.INSTANCE.create(bankerPosId, cardWall, cardGroup);*/

        cardWall.addAll(allCardList);
        cardGroup.put(4, cardWallRamaining);
        return cardGroup;
    }

    public static void main(String[] args) {
        /*List<String> strings = cardConvertName(1, 12, 27);
        System.out.println(strings);

        boolean windows = isWindows();
        Map<Integer, List<Integer>> dealCardGroup = getDealCardGroup();
        System.out.println();*/

        //getDealCardGroup(0);
    }



    /*public static MahjongCard getMahjongColorByIndex(int index,MahjongCard mahjongCard) {
        return mahjongCard.values()[index];
    }*/
}
