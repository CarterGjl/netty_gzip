package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ClientMain {

    public static void main(String[] args) throws URISyntaxException, InterruptedException {

        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
//                .option(ChannelOption.SO_BACKLOG,1024*1024*10)
                .group(eventExecutors)
                .handler(new LoggingHandler(LogLevel.INFO))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new HttpServerCodec())
                                .addLast(new ChunkedWriteHandler())
                                .addLast(new HttpObjectAggregator(1024 * 1024 * 10))
                                .addLast("hookedHandler", new WebSocketClientHandler());
                    }
                });
        URI websocketURI = new URI("ws://localhost:8088/ws");
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        //进行握手
        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(websocketURI, WebSocketVersion.V13, (String) null, true, httpHeaders);
        System.out.println("connect");
        final Channel channel = bootstrap.connect(websocketURI.getHost(), websocketURI.getPort()).sync().channel();
        WebSocketClientHandler handler = (WebSocketClientHandler) channel.pipeline().get("hookedHandler");
        handler.setHandshaker(handshaker);
        handshaker.handshake(channel);
        //阻塞等待是否握手成功
        handler.handshakeFuture().sync();
        Thread text = new Thread(new Runnable() {
            public void run() {
                int i = 30;
                while (i > 0) {
                    System.out.println("text send");
                    TextWebSocketFrame frame = new TextWebSocketFrame("我是文本");
                    channel.writeAndFlush(frame).addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture channelFuture) throws Exception {
                            if (channelFuture.isSuccess()) {
                                System.out.println("text send success");
                            } else {
                                System.out.println("text send failed  " + channelFuture.cause().getMessage());
                            }
                        }
                    });
                }

            }
        });

//        Thread bina=new Thread(new Runnable() {
//            public void run() {
//                File file=new File("C:\\Users\\Administrator\\Desktop\\test.wav");
//                FileInputStream fin= null;
//                try {
//                    fin = new FileInputStream(file);
//                    int len=0;
//                    byte[] data=new byte[1024];
//                    while ((len=fin.read(data))>0){
//                        ByteBuf bf= Unpooled.buffer().writeBytes(data);
//                        BinaryWebSocketFrame binaryWebSocketFrame=new BinaryWebSocketFrame(bf);
//                        channel.writeAndFlush(binaryWebSocketFrame).addListener(new ChannelFutureListener() {
//                            public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                                if(channelFuture.isSuccess()){
//                                    System.out.println("bina send success");
//                                }else{
//                                    System.out.println("bina send failed  "+channelFuture.cause().toString());
//                                }
//                            }
//                        });
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        text.start();
//        bina.start();

    }
}
