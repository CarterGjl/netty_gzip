package netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

public class WebSocketHandle extends SimpleChannelInboundHandler<Object> {
    private WebSocketServerHandshaker handshaker;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive    ");
    }

    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("channelRead0    ");
        if(msg instanceof TextWebSocketFrame){
            System.out.println("收到消息："+ZipUtil.gunzip(((TextWebSocketFrame)msg).text())+"text:"+((TextWebSocketFrame) msg).text());
//            ctx.channel().writeAndFlush(new TextWebSocketFrame("123456"));
        }else if(msg instanceof BinaryWebSocketFrame){
            ByteBuf content = ((BinaryWebSocketFrame) msg).content();
            int i = content.readableBytes();
            byte[] bytes = new byte[i];
            content.readBytes(bytes);
            System.out.println("收到二进制消息："+ZipUtil.gunzip(bytes));
            System.out.println("收到二进制消息："+bytes+((BinaryWebSocketFrame)msg).content().readableBytes());
            byte[] gunzip = ZipUtil.gzip1("hello client ");

            BinaryWebSocketFrame binaryWebSocketFrame=new BinaryWebSocketFrame(Unpooled.buffer().writeBytes(gunzip));
            ctx.channel().writeAndFlush(binaryWebSocketFrame);
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelUnregistered");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive");
    }
}
