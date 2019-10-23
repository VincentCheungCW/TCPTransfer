package ch.TCPTransfer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class Server {
    //boss is a listener
    private final EventLoopGroup boss = new NioEventLoopGroup();
    //worker is for reading and writing
    private final EventLoopGroup worker = new NioEventLoopGroup();
    private InetSocketAddress localAddress = null;
    public static volatile ArrayList<Channel> serverChannels = new ArrayList<>();
    public static volatile int serverChannelCounts = -1;


    public Server(String inetHost, int inetPort) {
        this.localAddress = new InetSocketAddress(inetHost, inetPort);
    }

    void startServer() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker);
        //socket factory
        bootstrap.channel(NioServerSocketChannel.class);
        //pipeline factory
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
//                    pipeline.addLast(new StringDecoder());
//                    pipeline.addLast(new StringEncoder());
                //handler class
                pipeline.addLast(new ServerHandler());
            }
        });
        //TCP setups
        //size of channel pool
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        //keep active and kill dead connections
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        //No delay
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        //bind port
        ChannelFuture future = bootstrap
                .bind(localAddress)
                .sync();
        System.out.println("程序启动 ... ");
        //waiting for shutdown
        future.channel().closeFuture().sync();
    }

    void stopServer() {
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }
}
