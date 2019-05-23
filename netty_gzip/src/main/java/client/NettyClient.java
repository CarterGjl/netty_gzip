package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;
import netty.ZipUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

public class NettyClient {


    public static void main(String[] args) {

        Timer timer = new Timer();
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        try {
            URI url = new URI("ws://localhost:8088/ws");

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventExecutors)
                    .option(ChannelOption.TCP_NODELAY,true)

                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ch.pipeline()
                                    .addLast(new HttpClientCodec())
                                    .addLast(new ChunkedWriteHandler())
                                    .addLast(new HttpObjectAggregator(64 * 1024))
                                    .addLast(new WebSocketServerCompressionHandler())
                                    .addLast(new WebSocketClientProtocolHandler(WebSocketClientHandshakerFactory.newHandshaker(url, WebSocketVersion.V13, null
                                            , false, new DefaultHttpHeaders())))
                                    .addLast(new SimpleChannelInboundHandler<Object>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

                                            System.out.println("channelRead0" + msg);
                                            if(msg instanceof TextWebSocketFrame){
                                                System.out.println("收到消息："+ZipUtil.gunzip(((TextWebSocketFrame)msg).text())+"text:"+((TextWebSocketFrame) msg).text());
//                                                ctx.channel().writeAndFlush(new TextWebSocketFrame("123456"));
                                            }else if(msg instanceof BinaryWebSocketFrame){
                                                ByteBuf content = ((BinaryWebSocketFrame) msg).content();
                                                int i = content.readableBytes();
                                                byte[] bytes = new byte[i];
                                                content.readBytes(bytes);
                                                System.out.println("收到二进制消息："+ZipUtil.gunzip(bytes));
//                                                System.out.println("收到二进制消息："+bytes+((BinaryWebSocketFrame)msg).content().readableBytes());
                                                byte[] hello_server_s = ZipUtil.gzip1("hello server ");
                                                BinaryWebSocketFrame binaryWebSocketFrame=new BinaryWebSocketFrame(Unpooled.buffer().writeBytes(hello_server_s));
                                                ctx.channel().writeAndFlush(binaryWebSocketFrame);
                                            }
                                        }

                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            super.channelActive(ctx);


                                            timer.schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    String msg = "test for";
                                                    byte[] gzip = ZipUtil.gzip1(msg);
                                                    String gzip1 = ZipUtil.gzip(msg);

                                                    ByteBuf byteBuf = Unpooled.copiedBuffer(gzip);
                                                    ctx.channel().writeAndFlush(new BinaryWebSocketFrame(byteBuf)).addListener(new ChannelFutureListener() {
                                                        @Override
                                                        public void operationComplete(ChannelFuture future) throws Exception {
                                                            System.out.println("successgzipbyte" + future.isSuccess());
                                                        }
                                                    });
                                                    ctx.channel().writeAndFlush(new TextWebSocketFrame(gzip1)).addListener(new ChannelFutureListener() {
                                                        @Override
                                                        public void operationComplete(ChannelFuture future) throws Exception {
                                                            System.out.println("success" + future.isSuccess());
                                                        }
                                                    });
                                                }
                                            }, 1000, 1000);


                                            System.out.println("channelActive");
                                        }

                                        @Override
                                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                            super.channelInactive(ctx);
                                            System.out.println("channelInactive");
                                        }

                                        @Override
                                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                            super.exceptionCaught(ctx, cause);
                                            cause.printStackTrace();
                                        }
                                    });
                        }
                    });
            ChannelFuture channelFuture = null;
            try {
                channelFuture = bootstrap.connect(url.getHost(), url.getPort()).sync();
//                channelFuture.channel().closeFuture();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

}
