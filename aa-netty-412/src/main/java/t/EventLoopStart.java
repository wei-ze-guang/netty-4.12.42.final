package t;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class EventLoopStart {

    public static void main(String[] args) {

        EventLoopGroup group = new NioEventLoopGroup();

        EventLoop next = group.next();

        next.execute(new Runnable() {
            public void run() {
                System.out.println("Hello World");
            }
        });

    }
}
