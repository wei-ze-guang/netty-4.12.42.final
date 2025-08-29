package t;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.concurrent.TimeUnit;

public class NettyServer {

    private final int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(4); // 负责接收连接
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // 负责处理连接的数据
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());
                            pipeline.addLast(new SimpleChannelInboundHandler<String>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                                    System.out.println("Server received: " + msg);
                                    // 传递给下一个 handler,这里为什么是channelRead0 是因为他是一个模板方法，他自己实现了channelRead
                                    // 如果调用使用这个的话这里，这里执行完channelRead 他会自动释放byteBuf
                                    ctx.fireChannelRead(msg);
//                                    ctx.writeAndFlush("Hello from server!\n");
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                    cause.printStackTrace();
                                    ctx.close();
                                }
                            });
                            for (int i = 0; i < 6; i++) {
                                pipeline.addLast("handler-"+i,new MyServerHandler());
                            }
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();


            // 获取eventLoop  f.channel() 返回的是绑定到端口的 Channel，类型是 NioServerSocketChannel。
            EventLoop eventExecutors = f.channel().eventLoop();

            eventExecutors.submit(()->{System.out.println("Server 提交了一个普通任务 started");});

            // 提交定时任务
            final  int  a = 600;
            eventExecutors.scheduleAtFixedRate(() -> System.out.println("定时任务执行啦"+a+"秒运行一次"), 0, a, TimeUnit.SECONDS);

            System.out.println("Server started on port " + port);
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new NettyServer(8080).start();
    }


}

