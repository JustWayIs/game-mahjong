package com.yude.game.xuezhan.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/3 10:38
 * @Version: 1.0
 * @Declare:
 */
public class MahjongProp {

    private int[] WAN = {1,2,3,4,5,6,7,8,9};
    private int[] TONG = {11,12,13,14,15,16,17,18,19};
    private int[] TIAO = {21,22,23,24,25,26,27,28,29};

    public static final int COLOR_WAN = 0;
    public static final int COLOR_TONG = 1;
    public static final int COLOR_TIAO = 2;

    public static final int THRESHOLD = 10;

    public static List<String> cardConvertName(int... card){
        List<String> list = new ArrayList<>();
        Arrays.stream(card).forEachOrdered(value -> {
            MahjongColor color = getMahjongColorByIndex(value / THRESHOLD);
            int realValue = value % THRESHOLD;
            list.add(realValue+color.toString());
        });
        return list;
    }

    public static void main(String[] args) {
        List<String> strings = cardConvertName(1, 12, 27);
        System.out.println(strings);
    }

    enum MahjongColor{
        /**
         *
         */
        万,
        筒,
        条;
    }

    public static MahjongColor getMahjongColorByIndex(int index){
        return MahjongColor.values()[index];
    }
}
