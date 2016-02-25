package org.ninedragon.ezapm.send.netty;

import org.ninedragon.extlib.io.netty.bootstrap.Bootstrap;
import org.ninedragon.extlib.io.netty.buffer.ByteBuf;
import org.ninedragon.extlib.io.netty.buffer.Unpooled;
import org.ninedragon.extlib.io.netty.channel.*;
import org.ninedragon.extlib.io.netty.channel.nio.NioEventLoopGroup;
import org.ninedragon.extlib.io.netty.channel.socket.SocketChannel;
import org.ninedragon.extlib.io.netty.channel.socket.nio.NioSocketChannel;
import org.ninedragon.ezapm.send.netty.handler.ChannelHandler;

/**
 * Created by ddakker on 2016-02-19.
 */
public class NettyClient {
    public static ChannelHandlerContext ctx = null;
    public static Channel channel = null;

    static {
        System.out.println("1");
        start();
        System.out.println("2");
        check();
        System.out.println("3");
    }

    public static void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Thread.currentThread(): " + Thread.currentThread());
                String host = "127.0.0.1";
                int port = 9999;

                EventLoopGroup workerGroup = new NioEventLoopGroup();
                try {
                    Bootstrap b = new Bootstrap(); // (1)
                    b.group(workerGroup); // (2)
                    b.channel(NioSocketChannel.class); // (3)
                    b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
                    b.handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ChannelHandler());
                        }
                    });

                    // Start the client.
                    ChannelFuture f = b.connect(host, port).sync(); // (5)
                    //ChannelFuture f = b.connect(host, port); // (5)


                    // Wait until the connection is closed.
                    f.channel().closeFuture().sync();

                    //channel = b.connect(host, port).sync().channel();
                    //System.out.println("channel.isOpen(): " + channel.isOpen());


                } catch (Exception e) {
                    System.err.println("접속 실패 class: " + e.getClass() + ", message: " + e.getMessage());
                } finally {
                    System.out.println("FFFFF workerGroup: " + workerGroup);

                    try {
                        workerGroup.shutdownGracefully();
                    } catch (Exception e) {
                        System.err.println("e: " + e);
                    }

                }
            }
        }, "스레드명" + System.currentTimeMillis()).start();


        System.out.println("connect end");

        /*try {Thread.sleep(5000);}catch(Exception e){}

        String msgStr = "접속 완료 후";
        ByteBuf message;

        //message = Unpooled.buffer(EchoClient.MESSAGE_SIZE);
        message = Unpooled.buffer();
        // 예제로 사용할 바이트 배열을 만듭니다.
        byte[] str = (msgStr + "\n").getBytes();
        // 예제 바이트 배열을 메시지에 씁니다.
        message.writeBytes(str);

        channel.writeAndFlush(message);*/
    }

    public static void check() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {Thread.sleep(2000);}catch(Exception e){}

                    if (ctx == null || ctx.isRemoved()) {
                        System.out.println("재 접속");
                        try {
                            start();
                        } catch (Exception e) {
                            System.err.println("재 접속 실패: " + e.getClass() + ", message: " + e.getMessage());
                        }
                    }
                }
            }
        }).start();
    }

    public static void send(String message) {
        System.out.println("send ctx: " + ctx);
        if (ctx != null) {
            System.out.println("send ctx.isRemoved(): " + ctx.isRemoved());
        }
        if (ctx == null || ctx.isRemoved()) {
            System.err.println("누락: " + message);
        } else {
            System.out.println("send");


            ByteBuf messageBuf;

            //message = Unpooled.buffer(EchoClient.MESSAGE_SIZE);
            messageBuf = Unpooled.buffer();
            // 예제로 사용할 바이트 배열을 만듭니다.
            byte[] str = (message + "\n").getBytes();
            // 예제 바이트 배열을 메시지에 씁니다.
            messageBuf.writeBytes(str);

            // 메시지를 쓴 후 플러쉬합니다.
            try {
                System.out.println("ctx.isRemoved(): " + ctx.isRemoved());
                ctx.writeAndFlush(messageBuf);
            System.out.println("발송: " + message);
            } catch (Exception e) {
                System.err.println("발송 실패: " + e.getClass() + ", message: " + e.getMessage());
            }
        }
    }

    public static void init() {
        System.out.println("init");
    }
}
