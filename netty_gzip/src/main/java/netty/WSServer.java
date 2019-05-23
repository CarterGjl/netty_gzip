package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class WSServer {


    private static class SingletionWSServer {

        static final WSServer instance = new WSServer();
    }

    public static WSServer getInstance() {

        return SingletionWSServer.instance;
    }

    private NioEventLoopGroup mainGroup;
    private NioEventLoopGroup subGroup;
    private ServerBootstrap bootstrap;
    private ChannelFuture channelFuture;

    public WSServer() {

        mainGroup = new NioEventLoopGroup();


        subGroup = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();
        bootstrap.group(mainGroup, subGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.SO_KEEPALIVE,true)
                .childHandler(new WSServerInitializer());
    }

    public void start(){
        try {
            this.channelFuture = bootstrap.bind(8088).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.err.println("netty websocket server 启动完毕。。。");
    }
    public void close(){

    }

    public static void main(String[] args) {
//        WSServer.getInstance().start();
        new WSServer().start();
    }
}
