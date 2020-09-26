package com.yude.game.common;

import com.yude.game.common.application.response.MatchFinishResponse;
import com.yude.game.common.application.response.dto.BaseSeatInfo;
import com.yude.game.common.application.response.dto.PlayerDTO;
import com.yude.game.common.constant.PlayerStatusEnum;
import com.yude.game.common.constant.Status;
import com.yude.game.common.contant.MahjongStatusCodeEnum;
import com.yude.game.common.contant.PushCommandCode;
import com.yude.game.common.manager.IPushManager;
import com.yude.game.common.manager.IRoomManager;
import com.yude.game.common.model.AbstractRoomModel;
import com.yude.game.common.model.Player;
import com.yude.game.common.util.AtomicSeatDown;
import com.yude.game.common.util.TempSeatPool;
import com.yude.game.exception.BizException;
import com.yude.game.exception.SystemException;
import com.yude.protocol.common.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

/**
 * @Author: HH
 * @Date: 2020/6/17 20:28
 * @Version: 1.0
 * @Declare: 没有匹配服务器，所以直接在游戏服做匹配
 */

@Service
public class RoomManager<T extends
        AbstractRoomModel> implements IRoomManager {
    private static final Logger log = LoggerFactory.getLogger(RoomManager.class);

    private ExecutorService matchThreadPool;

    private ExecutorService createRoomThreadPool;

    /**
     * roomId -> Room
     */
    private Map<Long, T> roomMap = new ConcurrentHashMap<>();

    /**
     * userId -> roomId
     */
    private Map<Long, Long> userRoomMap = new ConcurrentHashMap<>();

    private Class<T> roomType;

    @Autowired
    private IPushManager pushManager;

    /**
     * uerId
     */
    //private ConcurrentLinkedQueue<Long> matchQueue = new ConcurrentLinkedQueue<>();
    private BlockingQueue<Long> matchQueue = new LinkedBlockingQueue<>(10000);

    private static final short matchQueueSize = 10000;


    /**
     * userId -> Player 假装是 获取玩家信息服务
     */
    public static final ConcurrentHashMap<Long, Player> playerStatusMap = new ConcurrentHashMap<>();


    private static final int ROUND_LIMIT = 1;

    private static final int INNING_LIMIT = 1;

    protected static final AtomicLong roomIdGenerator = new AtomicLong(System.currentTimeMillis());

    private TempSeatPool tempSeatPool;


    private AtomicBoolean atomicBoolean = new AtomicBoolean(false);
    /**
     * 超时机制
     * @return
     */
    ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(1);
    private void timeoutExecute(){
        scheduled.scheduleWithFixedDelay(() ->{
            try {
                for(Map.Entry<Long, T> entry : roomMap.entrySet()){
                    final T roomModel = entry.getValue();
                    roomModel.timeoutExecute();
                }

            } catch (Exception e) {
                log.error("超时任务执行异常： ",e);
            }
        },1000*60,300,TimeUnit.MILLISECONDS);
    }


    protected long getNewRoomId() {
        long roomId = RoomManager.roomIdGenerator.getAndIncrement();
        return roomId;
    }

    //临时充当远程用户服务
    public static Player pullPlayer(Long userId) {
        Player playerInfo = RoomManager.playerStatusMap.computeIfAbsent(userId, key -> {
            Player player = RoomManager.playerStatusMap.get(userId);
            if (player != null) {
                return player;
            }
            player = new Player(userId, userId + "", "", 100, PlayerStatusEnum.FREE_TIME);
            return player;
        });
        return playerInfo;
    }

    /**
     *
     * @param userId 或者说用户唯一标识
     */
    @Override
    public void match(Long userId) {
        //由于队列中存的只是单个userId,这里又是耗时不定的IO操作，可以把这个拉取服务的过程异步化，异步结果再存进playerStatusMap。匹配成功，才会从map中取Player
        Player player = RoomManager.pullPlayer(userId);
        if (player == null) {
            log.error("拉取玩家信息失败： userId={}", userId);
            throw new BizException(MahjongStatusCodeEnum.FAIL);
        }

        Status playerStatus = player.getStatus();
        if (playerStatus.status() > PlayerStatusEnum.FREE_TIME.status()) {
            log.error("该玩家当前状态不能进行匹配： {}", playerStatus.status());
            throw new BizException(MahjongStatusCodeEnum.MATCH_EXISTS);
        }

        //要通知管理服务器修改玩家状态
        player.setStatus(PlayerStatusEnum.MATCHING);
        /*AtomicSeatDown atomicSeatDown = AtomicSeatDown.getInstance();
        boolean isSuccess;
        try {
            isSuccess = atomicSeatDown.trySeatDown(userId);
            if (isSuccess) {
                log.info("尝试入座成功：{}", userId);
                executeRoomInitAndGameStart();
                return;
            }
        } catch (Exception e) {
            log.error("尝试入座异常：{}", userId, e);
            throw new SystemException("尝试入座异常");
        }*/

        /*boolean offer = matchQueue.offer(userId);
        if (!offer) {
            log.warn("加入匹配队列失败： userId={}", userId);
            throw new BizException(StatusCodeEnum.MATCH_FAIL);
        }*/
        try {
            matchQueue.put(userId);
        } catch (InterruptedException e) {
            log.error("加入匹配队列失败： userId={} ",userId,e);
            throw new SystemException(MahjongStatusCodeEnum.MATCH_FAIL);
        }
        //这里不一定准，可能处于并发状态
        log.debug("匹配过的人数：{}", matchCount.incrementAndGet());
    }
    public static final AtomicInteger matchCount = new AtomicInteger(0);

    long cur;
    List<Long> costTime = new ArrayList<>();

    /**
     * 作为public其实会产生误导，实际上并不能直接调用这个方法
     * 创建房间并：开始游戏-》发牌
     */
    private void createRoom(AtomicSeatDown atomicSeatDown) {
        //cur = System.currentTimeMillis();
        log.info("开始创建房间：users:{}", atomicSeatDown.toString());
        AtomicLongArray seatZone = atomicSeatDown.getZone();
        //tempSeatPool.resetAtomicSeatDown(atomicSeatDown);
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < seatZone.length(); ++i) {
            long userId = seatZone.get(i);
            playerList.add(playerStatusMap.get(userId));
            //创建具体房间实例：1.实现ApplicationContextAware,房间子类注册为spring bean。 2.直接反射实例化。
        }
        T gameRoom;
        try {
            gameRoom = roomType.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            log.error("反射获取房间实例失败：", e);
            throw new SystemException("反射创建房间异常：", e);
        }
        long newRoomId = getNewRoomId();
        roomMap.put(newRoomId, gameRoom);

        List<BaseSeatInfo> seatInfoDTOS = new ArrayList<>();

        playerList.stream().forEach(player -> {
            //由于这里的posId没有传进Room的初始化方法，所以里面又进行了一次类似的操作来获得posId。问题在于Player需不需要添加一个posId。如果需要的话，就可以传进去
            int posId = seatInfoDTOS.size();
            long userId = player.getUserId();
            String nickName = player.getNickName();
            String headUrl = player.getHeadUrl();
            long score = player.getScore();

            PlayerDTO playerDTO = new PlayerDTO();
            playerDTO.setUserId(userId)
                    .setHeadUrl(headUrl)
                    .setName(nickName)
                    .setScore(score);
            BaseSeatInfo seatInfoDTO = new BaseSeatInfo(posId,playerDTO);
            seatInfoDTOS.add(seatInfoDTO);
            //这里用putIfAbsent()是不是会更好：如果关闭房间后，应该手动清理到玩家与房间的映射关系
            //所以如果在存进map的时候，已经有值了的话，是一种异常现象，应该标识出来
            userRoomMap.put(player.getUserId(), newRoomId);
        });

        MatchFinishResponse matchFinishResponse = new MatchFinishResponse(newRoomId, seatInfoDTOS);
        for (Player player : playerList) {
            pushManager.pushToUser(PushCommandCode.MATH_FINISH, player.getUserId(), matchFinishResponse, newRoomId);
        }

        gameRoom.init(this, newRoomId, playerList, ROUND_LIMIT, INNING_LIMIT);
        //costTime.add(System.currentTimeMillis() - cur);
        //log.info("创建房间总数：{}   耗时：：：：：：：{}",costTime.size(),costTime);
    }

    @Override
    public void destroyRoom(Long roomId) {
        //怎么保证这两个操作的原子性：不需要
        for (Map.Entry<Long, Long> entry : userRoomMap.entrySet()) {
            Long userId = entry.getKey();
            Long roomIdInfo = entry.getValue();
            if (roomIdInfo.equals(roomId)) {
                userRoomMap.remove(userId);
            }
        }
        roomMap.remove(roomId);


    }

    @Override
    public AbstractRoomModel getRoomByRoomId(Long roomId) {
        return roomMap.get(roomId);
    }

    @Override
    public Long getRoomIdByUserId(Long userId) {
        return userRoomMap.get(userId);
    }

    @Override
    public AbstractRoomModel getRoomByUserId(Long userId) {
        Long roomId = getRoomIdByUserId(userId);
        if(roomId == null){
            return null;
        }
        AbstractRoomModel roomModel = getRoomByRoomId(roomId);
        return roomModel;
    }


    @Override
    public IPushManager getPushManager() {
        return pushManager;
    }

    @Override
    public void pushToUser(Integer command, Long userId, Response response, Long roomId) {
        pushManager.pushToUser(command,userId,response,roomId);
    }

    public void initRoomType(Class<T> roomType,int playerNum) {
        this.roomType = roomType;
        tempSeatPool = TempSeatPool.matchPlayerInstance(playerNum);
    }


    @Override
    public void changePlayerStatus(Player player, Status status) {
        player.setStatus(status);
        playerStatusMap.put(player.getUserId(), player);
    }

    @Override
    public void restartMath(ExecutorService matchThreadPool) {
        if (matchThreadPool == null) {
            createMatchThreadPool();
        }
        executeMatchThread(matchThreadPool);
    }


    @Override
    @PostConstruct
    public void init() {
        createMatchThreadPool();
        createRoomThreadPool();
        executeMatchThread(matchThreadPool);
        timeoutExecute();
    }

    public ExecutorService createMatchThreadPool() {
        matchThreadPool = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new SynchronousQueue<>(), (task) -> {
            Thread thread = new Thread(task);
            thread.setName("Thread-match");
            thread.setDaemon(true);
            return thread;
        });
        return matchThreadPool;
    }


    public ExecutorService createRoomThreadPool() {
        createRoomThreadPool = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue(100000), (task) -> {
            final Thread thread = new Thread(task);
            thread.setName("Thread-room-create");
            thread.setDaemon(true);
            return thread;
        });
        return createRoomThreadPool;
    }

    private void executeRoomInitAndGameStart(AtomicSeatDown atomicSeatDown) {
        createRoomThreadPool.execute(new RoomCreateAndGameStartTask(atomicSeatDown));
    }

    public void executeMatchThread(ExecutorService matchThreadPool) {
        matchThreadPool.execute(() -> {
            log.debug("---------------匹配线程启动------------------");


            //允许关闭这个线程,前提：提供重启机制
            while (!Thread.currentThread().isInterrupted()) {
                Long userId = null;
                /*if (matchQueue.size() == 0) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        log.error("匹配线程休眠异常：", e);
                    }
                }
                userId = matchQueue.poll();
                //匹配队为空的时候，由于是非阻塞队列，这里会为null
                if (userId == null) {
                    continue;
                }
                */

                /**
                 * 不能直接在这里用阻塞队列，因为有尝试入座机制，虽然队列里面没有玩家，但是实际上一局有玩家已经入座了（临时座位）
                 * 在尝试入座成功后，调用了房间创建任务，现在可以用阻塞队列了
                 */

                try {
                    userId = matchQueue.take();
                } catch (InterruptedException e) {
                    log.error("从队列中获取用户ID失败 ： userId={}",e);
                }
                //避免上面出现异常userId没有值
                if (userId == null) {
                    continue;
                }
                boolean online = pushManager.isOnline(userId);
                if (!online) {
                    log.info("匹配玩家已经离线 不再对该玩家进行匹配： userId={}", userId);
                    //H2 应该从Map中清除重新拉取才对，因为这个时候玩家信息可能发生了改变，但是由于没有玩家信息来源，所以把这个playerStatusMap当做来源
                    Player player = playerStatusMap.get(userId);
                    player.setStatus(PlayerStatusEnum.FREE_TIME);
                    continue;
                }
                AtomicSeatDown atomicSeatDown = null;
                if (userId != null) {
                    try {
                        boolean isSuccess;
                        do {
                            atomicSeatDown = tempSeatPool.getAtomicSeatDownInstanceToMatch();
                            isSuccess = atomicSeatDown.seatDown(userId);
                        } while (!isSuccess);
                    } catch (Exception e) {
                        log.error("入座失败：{}", userId, e);
                        Player player = playerStatusMap.get(userId);
                        player.setStatus(PlayerStatusEnum.FREE_TIME);
                    }
                }
                if (atomicSeatDown.canStartGame()) {
                    tempSeatPool.removeTempSeat(atomicSeatDown);
                    executeRoomInitAndGameStart(atomicSeatDown);
                }
            }
            log.info("---------------------匹配线程退出了---------------------------");
        });

    }

    public static final AtomicInteger roomCount = new AtomicInteger(0);
    class RoomCreateAndGameStartTask implements Runnable {
        private AtomicSeatDown atomicSeatDown;

        public RoomCreateAndGameStartTask(AtomicSeatDown atomicSeatDown) {
            this.atomicSeatDown = atomicSeatDown;
        }

        @Override
        public void run() {
            log.debug("---------------房间创建任务启动------------------");

            try {
                createRoom(atomicSeatDown);
                log.debug("创建第 {} 个房间",roomCount.incrementAndGet());
            } catch (Exception e) {
                log.error("创建房间失败：", e);

                AtomicLongArray zone = atomicSeatDown.getZone();
                for (int i = 0; i < zone.length(); i++) {
                    Player player = playerStatusMap.get(zone.get(i));
                    player.setStatus(PlayerStatusEnum.FREE_TIME);
                    //playerMatchStatus.remove(zone.get(i));
                }

            }
            atomicSeatDown = null;
        }
    }

}
