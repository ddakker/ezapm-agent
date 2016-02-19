package pe.kr.ddakker.study.netty.example.time;

import org.ninedragon.extlib.io.netty.buffer.ByteBuf;
import org.ninedragon.extlib.io.netty.buffer.Unpooled;
import org.ninedragon.extlib.io.netty.channel.ChannelHandlerContext;
import org.ninedragon.extlib.io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeClientHandler extends ChannelInboundHandlerAdapter {
    private String msg = null;
    public TimeClientHandler(String msg) {
        this.msg = msg;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String msgStr = this.msg;
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

        TimeClient.ctx = ctx;
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	System.err.println("asdfasdfasfd");
    	ctx.close();
        System.err.println(cause);
    }
}