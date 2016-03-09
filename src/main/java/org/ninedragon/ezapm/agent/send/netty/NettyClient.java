package org.ninedragon.ezapm.agent.send.netty;

import org.ninedragon.extlib.io.netty.bootstrap.Bootstrap;
import org.ninedragon.extlib.io.netty.buffer.ByteBuf;
import org.ninedragon.extlib.io.netty.buffer.Unpooled;
import org.ninedragon.extlib.io.netty.channel.*;
import org.ninedragon.extlib.io.netty.channel.nio.NioEventLoopGroup;
import org.ninedragon.extlib.io.netty.channel.socket.SocketChannel;
import org.ninedragon.extlib.io.netty.channel.socket.nio.NioSocketChannel;
import org.ninedragon.ezapm.agent.AgentMain;
import org.ninedragon.ezapm.agent.Conf;
import org.ninedragon.ezapm.agent.send.netty.handler.NettyClientChannelHandler;

import javax.management.*;
import javax.management.openmbean.CompositeData;
import java.lang.management.ManagementFactory;

/**
 * Created by ddakker on 2016-02-19.
 */
public class NettyClient {
    public static Channel c = null;

    static {
        //startAndCheck();
    }

    public static Thread nettyClientThread = null;

    public static void startAndCheck() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                    }

                    try {
                        /*ObjectName om = new ObjectName("Catalina:type=Server");
                        MBeanServer connection = ManagementFactory.getPlatformMBeanServer();
                        Object obj = connection.getAttribute(om, "stateName");
                        */
                        ObjectName om = new ObjectName("java.lang:type=Memory");
                        MBeanServer connection = ManagementFactory.getPlatformMBeanServer();
                        Object obj = connection.getAttribute(om, "HeapMemoryUsage");

                        System.out.println("obj: " + obj);

                        if (obj != null && (c == null || !c.isOpen())) {
                            if (c != null) System.out.println("c.isOpen()): " + c.isOpen());
                            nettyClientThread = new Thread(NettyClient.initializer);
                            System.out.println("startup NettyClient.nettyClientThread: " + NettyClient.nettyClientThread);
                            nettyClientThread.setDaemon(true);
                            nettyClientThread.start();
                        }
                    } catch (Exception e) {
                        nettyClientThread.interrupt();
                        break;
                    }


                }
            }
        });
        t.setDaemon(true);
        t.start();
        System.out.println("startAndCheck() start");
    }

    /*public static void connectMsg(String grp, String message) {
        //String host = "127.0.0.1";
        String host = "10.10.10.178";
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
                    ch.pipeline().addLast(new NettyClientChannelHandler());
                }
            });

            // Start the client.
            //ChannelFuture f = b.connect(host, port).sync(); // (5)
            //ChannelFuture f = b.connect(host, port); // (5)

            // Wait until the connection is closed.
            //f.channel().closeFuture().sync();
            //f.channel().closeFuture();

            c = b.connect(host, port).sync().channel();


            message = "{grp: '" + grp + "', data: " + message + "}";
            System.out.println("NettyClient.ctx1: " + NettyClient.ctx);
            if (c == null) {
                System.err.println("누락 NULL: " + message);
            } else if (!c.isOpen()) {
                System.err.println("누락 Close Call: " + message);
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
                    c.writeAndFlush(messageBuf);
                    System.out.println("발송: " + message);
                } catch (Exception e) {
                    System.err.println("발송 실패: " + e.getClass() + ", message: " + e.getMessage());
                }

            }




            c.close();
            c.disconnect();

        } catch (Exception e) {
            System.err.println("접속 실패: " + e.getClass() + ", message: " + e.getMessage());
        } finally {
            workerGroup.shutdownGracefully();
        }
    }*/


    public static Runnable initializer = new Runnable() {
        @Override
        public void run() {
            //String host = "127.0.0.1";
            String host = Conf.getProperty("collector.ip");
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
                        ch.pipeline().addLast(new NettyClientChannelHandler());
                    }
                });

                // Start the client.
                //ChannelFuture f = b.connect(host, port).sync(); // (5)
                //ChannelFuture f = b.connect(host, port); // (5)

                // Wait until the connection is closed.
                //f.channel().closeFuture().sync();
                //f.channel().closeFuture();

                c = b.connect(host, port).sync().channel();
                //c.closeFuture().sync();
                //while (true) {
                //while (true) {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(500);

                    /*if (c == null) {
                        throw new InterruptedException();
                    }*/
                }



            } catch (InterruptedException e) {
                // 예상했던 예외이므로 무시..
                System.err.println("이래저래");
                c.close();
                c.disconnect();
                c = null;
            }  catch (Exception e) {
                System.err.println("접속 실패: " + e.getClass() + ", message: " + e.getMessage());
            } finally {
                workerGroup.shutdownGracefully();
            }
        }
    };


    public static void send(String grp, String message) {
        long l = System.currentTimeMillis();

        message = "{\"grp\": \"" + grp + "\", \"data\": " + message + "}";
        if (c == null) {
            System.err.println("누락 NULL: " + message);
        } else if (!c.isOpen()) {
            System.err.println("누락 Close Call: " + message);
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
                c.writeAndFlush(messageBuf);
                System.out.println("발송: " + message);
            } catch (Exception e) {
                System.err.println("발송 실패: " + e.getClass() + ", message: " + e.getMessage());
            }
        }

        System.out.println("발송 ts: " + (System.currentTimeMillis()-l));
    }

    /*public static void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                            ch.pipeline().addLast(new NettyClientChannelHandler());
                        }
                    });

                    // Start the client.
                    ChannelFuture f = b.connect(host, port).sync(); // (5)
                    //ChannelFuture f = b.connect(host, port); // (5)

                    // Wait until the connection is closed.
                    f.channel().closeFuture().sync();
                    //f.channel().closeFuture();

                } catch (Exception e) {
                    System.err.println("접속 실패: " + e.getClass() + ", message: " + e.getMessage());
                } finally {
                    workerGroup.shutdownGracefully();
                }
            }
        }).start();
        System.out.println("connect end");
    }

    public static void check() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                    }
                    System.out.println("재 접속 체크 NettyClient.ctx: " + NettyClient.ctx);
                    if (NettyClient.ctx != null) {
                        System.out.println("재 접속 체크 NettyClient.ctx.isRemoved(): " + NettyClient.ctx.isRemoved());
                    }

                    if (NettyClient.ctx == null || NettyClient.ctx.isRemoved()) {
                        try {
                            System.out.println("재 접속 시도");
                            start();
                        } catch (Exception e) {
                            System.err.println("재 접속 실패: " + e.getClass() + ", message: " + e.getMessage());
                        }
                    }
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }*/

    public static String GRP_WAS_MEM = "grp_was_mem";
    public static String GRP_WAS_REQ = "grp_was_req";

    /*public static void send(String grp, String message) {
        message = "{grp: '" + grp + "', data: " + message + "}";
        System.out.println("NettyClient.ctx1: " + NettyClient.ctx);
        if (NettyClient.ctx == null) {
            System.err.println("누락 NULL: " + message);
        } else if (NettyClient.ctx.isRemoved()) {
            System.err.println("누락 Close Call: " + message);
            NettyClient.ctx.close();
            NettyClient.ctx.disconnect();
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
                System.out.println("ctx.isRemoved(): " + NettyClient.ctx.isRemoved());
                NettyClient.ctx.writeAndFlush(messageBuf);
            System.out.println("발송: " + message);
            } catch (Exception e) {
                System.err.println("발송 실패: " + e.getClass() + ", message: " + e.getMessage());
            }
        }
    }*/

    public static void init() {
        System.out.println("init");
    }
}
