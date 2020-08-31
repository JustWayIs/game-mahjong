import com.yude.game.common.model.MahjongProp;
import com.yude.game.common.model.sichuan.SichuanMahjongCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: HH
 * @Date: 2020/8/22 14:39
 * @Version: 1.0
 * @Declare:
 */
public class DealCardTest {
    public static void main(String[] args) {
        List<Integer> cardWall = new ArrayList<>();
        Map<Integer, List<Integer>> dealCardGroup = MahjongProp.getDealCardGroup(SichuanMahjongCard.values(), 2,cardWall);
        System.out.println();
    }
}
