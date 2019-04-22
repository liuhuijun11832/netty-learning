package com.joy.chapter2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @Description 服务器
 * @Author Joy
 * @Date 2019-04-22 16:10
 */
@Slf4j
public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 1) {
            log.info("使用{}端口<port>", EchoServer.class.getSimpleName());
            return;
        }
        int port = Integer.parseInt(args[0]);
        new EchoServer(port).start();
    }

    /**
     * 步骤如下：
     * 1.创建一个ServerBootstrap实例用来引导和绑定服务器；
     * 2.创建一个NioEventLoopGroup实例进行事件的处理，如接收新连接和处理数据；
     * 3.指定服务器绑定本地的套接字地址
     * 4.每次接收一个请求都会创建一个child channel
     * 5.绑定服务器，并使用sync等待绑定完成
     * @throws InterruptedException
     */
    public void start() throws InterruptedException {
        //解决jdk 11下unsafe类缺失问题也可以在启动时加参数 -Dio.netty.noUnsafe=true
        //System.setProperty("io.netty.noUnsafe", "true");
        final EchoServerHandler serverHandler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    //当接收一个新的连接时，一个新的child channel将被创建
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(serverHandler);
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

}
