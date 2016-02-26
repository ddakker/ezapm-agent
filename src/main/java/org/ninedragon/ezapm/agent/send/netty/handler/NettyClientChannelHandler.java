package org.ninedragon.ezapm.agent.send.netty.handler;

import org.ninedragon.extlib.io.netty.buffer.ByteBuf;
import org.ninedragon.extlib.io.netty.buffer.Unpooled;
import org.ninedragon.extlib.io.netty.channel.ChannelHandlerContext;
import org.ninedragon.extlib.io.netty.channel.ChannelInboundHandlerAdapter;
import org.ninedragon.ezapm.agent.send.netty.NettyClient;

/**
 * Created by ddakker on 2016-02-19.
 */
public class NettyClientChannelHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String msgStr = "FIRST CONNECT t: " + System.currentTimeMillis();
        ByteBuf message;

        //message = Unpooled.buffer(EchoClient.MESSAGE_SIZE);
        message = Unpooled.buffer();
        // 예제로 사용할 바이트 배열을 만듭니다.
        byte[] str = (msgStr + "\n").getBytes();
        // 예제 바이트 배열을 메시지에 씁니다.
        message.writeBytes(str);

        // 메시지를 쓴 후 플러쉬합니다.
        ctx.writeAndFlush(message);
        //ctx.writeAndFlush("dsfsdf");
        System.out.println(msgStr);

        NettyClient.ctx = ctx;
        if (NettyClient.ctx != null) {
            System.out.println("FIRST CONNECT ctx: " + NettyClient.ctx);
            System.out.println("FIRST CONNECT ctx: " + NettyClient.ctx.isRemoved());
        }
        //ctx.close();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println(cause);
        ctx.close();
    }

    /*@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("msg: " + msg);
        ctx.fireChannelRead(msg);
    }*/
}
