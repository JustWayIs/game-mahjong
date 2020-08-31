package com.yude.game.xuezhan;

import cn.hutool.core.util.ClassUtil;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.yude.game.common.RoomManager;
import com.yude.game.common.timeout.MahjongTimeoutTaskPool;
import com.yude.game.communication.tcp.server.CommonTCPServer;
import com.yude.game.xuezhan.domain.XueZhanRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: HH
 * @Date: 2020/8/1 15:28
 * @Version: 1.0
 * @Declare:
 */
@Component
@ComponentScan({"com.yude"})
//@PropertySource("classpath:config/core.properties")
public class XueZhanApplication {

    private static final Logger log = LoggerFactory.getLogger(XueZhanApplication.class);


    public static void main(String[] args) {
        try {
            log.info("----------血战到底游戏服启动中-----------");

            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(XueZhanApplication.class);
            CommonTCPServer server = context.getBean(CommonTCPServer.class);
            Thread thread = new Thread(server);
            thread.setName("Thread-TCP-Server");
            thread.start();

            RoomManager roomManager = context.getBean(RoomManager.class);
            roomManager.initRoomType(XueZhanRoom.class,4);

            jprotobufClassInit();
            MahjongTimeoutTaskPool.getInstance().init();
        } catch (Exception e) {
            log.error("血战到底游戏服启动失败",e);
            System.exit(1);
        }
    }

    public static void jprotobufClassInit(){
        Set<Class<?>> classes = new HashSet<>();
        classes.addAll(ClassUtil.scanPackageByAnnotation("com.yude", ProtobufClass.class));
        for (Class c : classes) {
            ProtobufProxy.create(c);
        }
    }
}
