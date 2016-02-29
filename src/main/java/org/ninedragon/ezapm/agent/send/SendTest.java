package org.ninedragon.ezapm.agent.send;

import org.ninedragon.ezapm.agent.send.netty.NettyClient;

/**
 * Created by ddakker on 2016-02-19.
 */
public class SendTest {
    public static void main(String[] args) throws InterruptedException {
        //NettyClient.init();
        Thread nettyClientThread = new Thread(NettyClient.initializer);
        nettyClientThread.setDaemon(true);
        nettyClientThread.start();

        NettyClient.send("df", "Asdfasdf");
        Thread.sleep(2000);
        NettyClient.send("df", "Asdfasdf");
        NettyClient.send("df", "Asdfasdf");
        Thread.sleep(1000);
        NettyClient.send("df", "Asdfasdf");

        /*long l = System.currentTimeMillis();
        NettyClient.connectMsg("asdf", "Asdf");
        System.out.println((System.currentTimeMillis()-l));*/
    }
}
