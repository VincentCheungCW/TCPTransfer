package ch.TCPTransfer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static ch.TCPTransfer.Server.serverChannels;

public class ServerHandler extends SimpleChannelInboundHandler {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("这行不应该显示，否则代表GDC在向我发数 ...");
        //ctx.writeAndFlush("i am server !").addListener(ChannelFutureListener.CLOSE);
    }

    //new connection
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (!serverChannels.contains(ctx.channel())) {
            System.out.println("检测到新的GDC客户端加入: " + ctx.channel().remoteAddress());
            serverChannels.add(ctx.channel());
        }
    }

    //new disconnection
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (serverChannels.contains(ctx.channel())) {
            System.out.println("检测到有GDC客户端断开连接: " + ctx.channel().remoteAddress());
            serverChannels.remove(ctx.channel());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //ctx.channel().close();
        System.out.println("始料未及的异常发生...server...");
        //cause.printStackTrace();
    }
}
