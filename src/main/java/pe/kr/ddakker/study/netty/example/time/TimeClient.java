package pe.kr.ddakker.study.netty.example.time;

import org.ninedragon.extlib.io.netty.bootstrap.Bootstrap;
import org.ninedragon.extlib.io.netty.buffer.ByteBuf;
import org.ninedragon.extlib.io.netty.buffer.Unpooled;
import org.ninedragon.extlib.io.netty.channel.*;
import org.ninedragon.extlib.io.netty.channel.nio.NioEventLoopGroup;
import org.ninedragon.extlib.io.netty.channel.socket.SocketChannel;
import org.ninedragon.extlib.io.netty.channel.socket.nio.NioSocketChannel;

public class TimeClient {
    public static ChannelHandlerContext ctx = null;
    
    public static void main(String[] args) throws Exception {
    	start();
    	check();
    	
    	
    	for (int i = 0; i < 100; i++) {
    		send("하이요~ " + i);
    		try {Thread.sleep(1000);}catch(Exception e){}
		}
    }
    
    public static void check() throws Exception {
    	new Thread(new Runnable() {
            @Override
            public void run() {
            	while (true) {
            		try {Thread.sleep(1000);}catch(Exception e){}
            		
            		if (ctx == null || ctx.isRemoved()) {
            			System.out.println("재 접속");
            			
            			try {
							TimeClient.start();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
            		}
            	}
            }
    	}).start();
    }
    
    public static void start() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String msg = "zzz";

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
                            ch.pipeline().addLast(new TimeClientHandler(msg));
                        }
                    });

                    // Start the client.
                    //ChannelFuture f = b.connect(host, port).sync(); // (5)
                    ChannelFuture f = b.connect(host, port); // (5)

                    // Wait until the connection is closed.
                    f.channel().closeFuture().sync();
                    System.out.println("end");

                } catch (Exception e) {
                    System.err.println(e);
                } finally {
                    workerGroup.shutdownGracefully();
                }
            }
        }).start();
        System.out.println("connect end");
    }

    private static void send(String messageStr) {
    	if (TimeClient.ctx == null || TimeClient.ctx.isRemoved()) {
    		System.err.println("누락: " + messageStr);
    	} else {
	        System.out.println("send");
	        	
	        	
	        ByteBuf message;
	
	        //message = Unpooled.buffer(EchoClient.MESSAGE_SIZE);
	        message = Unpooled.buffer();
	        // 예제로 사용할 바이트 배열을 만듭니다.
	        byte[] str = (messageStr + "\n").getBytes();
	        // 예제 바이트 배열을 메시지에 씁니다.
	        message.writeBytes(str);
	
	        // 메시지를 쓴 후 플러쉬합니다.
	        try {
	        	System.out.println("TimeClient.ctx.isRemoved(): " + TimeClient.ctx.isRemoved());
	        		
				TimeClient.ctx.writeAndFlush(message);
			} catch (Exception e1) {
				System.err.println(e1);
			}
	        System.out.println("발송: " + messageStr);
    	}
    }
}