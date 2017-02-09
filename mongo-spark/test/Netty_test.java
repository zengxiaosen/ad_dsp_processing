package test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by Administrator on 2017/2/7.
 */
public class Netty_test {
    public static void main(String[] args) throws Exception{
        EventLoopGroup boosLoop = new NioEventLoopGroup();
        EventLoopGroup workerLoop = new NioEventLoopGroup();

    }
}
