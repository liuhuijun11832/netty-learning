package com.joy.chapter2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description echo服务器客户端 标记为sharable表示可以被多个channel共享
 * @Author Joy
 * @Date 2019-04-22 18:54
 */
@Slf4j
@ChannelHandler.Sharable
public class EchoClientHadler extends SimpleChannelInboundHandler<ByteBuf> {
    
    /**
     * 当从服务器接收到一条消息时调用
     * @param channelHandlerContext
     * @param in
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf in) throws Exception {
        log.info("客户端接收到消息:{}",in.toString(CharsetUtil.UTF_8));
    }

    /**
     * 与服务器的连接建立之后将会调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("与服务器连接建立", CharsetUtil.UTF_8));
    }

    /**
     * 处理过程中引发异常时调用,如果不重写，则会传递到channel pipeline尾端并记录
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端处理异常:{}",cause);
        ctx.close();
    }
}
