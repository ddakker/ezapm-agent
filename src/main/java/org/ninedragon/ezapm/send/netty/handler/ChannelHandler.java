package org.ninedragon.ezapm.send.netty.handler;

import org.ninedragon.extlib.io.netty.buffer.ByteBuf;
import org.ninedragon.extlib.io.netty.buffer.Unpooled;
import org.ninedragon.extlib.io.netty.channel.ChannelHandlerContext;
import org.ninedragon.extlib.io.netty.channel.ChannelInboundHandlerAdapter;
import org.ninedragon.ezapm.send.netty.NettyClient;

/**
 * Created by ddakker on 2016-02-19.
 */
public class ChannelHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String msgStr = "접속";
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
        System.out.println("ctx.isRemoved(): " + ctx.isRemoved());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("연결 종료");
        System.err.println(cause);
        ctx.close();
    }
}
