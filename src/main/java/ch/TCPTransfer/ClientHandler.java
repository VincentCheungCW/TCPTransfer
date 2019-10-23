package ch.TCPTransfer;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.concurrent.TimeUnit;

import static ch.TCPTransfer.Server.serverChannelCounts;
import static ch.TCPTransfer.Server.serverChannels;

public class ClientHandler extends SimpleChannelInboundHandler {
    private Client client;

    public ClientHandler(Client client) {
        this.client = client;
    }

    //receive message
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        int n = serverChannels.size();
        if(serverChannelCounts != n && n!= 0){
            System.out.println("收到差分码，正在转发 ...");
        }
        ByteBuf m = (ByteBuf) msg;
        ByteBuf buf = ctx.alloc().buffer(m.readableBytes() * 2);
        buf.writeBytes(m);
        for (Channel serverChannel : serverChannels) {
            if(serverChannelCounts != n && n!= 0){
                System.out.println("正转发至: " + serverChannel.remoteAddress());
            }
            try {
                //注意！ByteBuf引用计数先加1，因为ByteBuf引用计数到0时会报错
                buf.retain();
                serverChannel.writeAndFlush(buf).sync();
            } catch (Exception e) {
                System.out.println("writeAndFlush出错: " + serverChannel);
            }
        }

        //这里应该是1才不会报错，因为SimpleChannelInboundHandler最后会自动将ByteBuf引用计数减1
        //System.out.println("ByteBuf引用计数: " + buf.refCnt());
        buf = null;
        serverChannelCounts = n;
    }

    //connect to the server
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("已连接到差分码服务器,准备接收差分码 ...");
    }

    //disconnect from the the server
    //断线重连
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("与差分码服务器的连接断开,正在尝试重连 ...");
        final EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(new Runnable() {
            @Override
            public void run() {
                client.createBootstrap(new Bootstrap(), eventLoop);
            }
        }, 1L, TimeUnit.SECONDS);
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        ctx.channel().close();
        System.out.println("始料未及的异常发生...client...");
        //cause.printStackTrace();
    }
}
