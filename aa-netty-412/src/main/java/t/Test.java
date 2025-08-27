package t;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Test {

    @org.junit.jupiter.api.Test
    public void test() {
        //EventLoopGroup group = new NioEventLoopGroup();

        NioEventLoopGroup nioGroup = new NioEventLoopGroup(5);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(5);

        ServerBootstrap bootstrap = new ServerBootstrap();

        ServerBootstrap group = bootstrap.group(nioGroup, workerGroup);
        group.channel(NioServerSocketChannel.class);  //  它内部有一个channel工厂，给他指定他内部channel工厂生产法类型

    }
}
