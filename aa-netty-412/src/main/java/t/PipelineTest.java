package t;

import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PipelineTest {

    // 自定义一个入站handler（测试 channelRead）
    static class InboundHandlerA extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("InboundHandlerA 收到消息: " + msg);
            super.channelRead(ctx, msg); // 传给下一个 handler
        }
    }

    // 自定义一个入站handler（终点处理）
    static class InboundHandlerB extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            System.out.println("InboundHandlerB 收到消息: " + msg);
            // 不调用 super，表示消费掉
        }
    }

    // 自定义一个出站handler（测试 write）
    static class OutboundHandlerA extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println("OutboundHandlerA 处理写: " + msg);
            super.write(ctx, msg, promise);
        }
    }

    @Test
    public void testPipelineInboundAndOutbound() {
        // 用 EmbeddedChannel 模拟一个 Channel，pipeline 就可以直接测试
        EmbeddedChannel channel = new EmbeddedChannel(
                new InboundHandlerA(),
                new InboundHandlerB(),
                new OutboundHandlerA()
        );

        // 模拟入站消息
        channel.writeInbound("Hello Netty");

        // 模拟出站消息
        channel.writeOutbound("Bye Netty");

        // 检查 inbound 最终有没有进来
        String inboundMsg = channel.readInbound();
        assertNull(inboundMsg); // 因为 InboundHandlerB 消费掉了

        // 检查 outbound 最终有没有出去
        String outboundMsg = channel.readOutbound();
        assertEquals("Bye Netty", outboundMsg);

        channel.finish();
    }
}

