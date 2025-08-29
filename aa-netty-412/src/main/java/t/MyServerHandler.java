package t;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
//  不建议继承这个，应该单独的继承in  或者 out
@Slf4j
public class MyServerHandler extends ChannelDuplexHandler {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("连接创建阶段每个handler channelActive   ServerHandler的hash={}",this.hashCode());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String message = (String) msg;
        log.info("读取消息 channelRead  ServerHandler的hash={}",this.hashCode());
//        ctx.writeAndFlush("收到: " + message );// flush的话直接写的

        if("handler-3".equals(ctx.name())){
            ctx.write("收到: " + message);// flush的话直接写的
        }
        //  加入这里write或者writeAndFlush  他是在当前handler 往head方面递归写的，不会走到最后
//        ctx.write("收到: " + message);// flush的话直接写的

        ctx.fireChannelRead(msg);
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        log.info("数据读完 只回执行一次 每次消息来的时候一般只要执行一次，加入我们前面write了几次，我可以在这里flush ServerHandler的hash={}",this.hashCode());
        ctx.fireChannelReadComplete();
//        throw new RuntimeException("ca1");

//        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.info(" 异常发生 → 依次传递到各个 handler 的 exceptionCaught()，直到有一个处理了。 exceptionCaugh  ServerHandler的hash={}",this.hashCode());
        if (Objects.equals("ca",cause.getMessage())) {
            log.error("收到异常信息式ca");
            ctx.close();
        }
        log.error("收到的异常信息 不是ca,如果整合handler 走到最后都没处理这个异常的是那么 到最后他会有一个默认的");
        ctx.fireExceptionCaught(cause);

    }

    // ================= Outbound =================
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        //  如果继承单个 里面式这样的    @Override
        //    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        //        ctx.write(msg, promise);
        //    }
        log.error("handlername={}Outbound - write消息",ctx.name());
        super.write(ctx, msg, promise); // 继续调用 pipeline 中下一个 outbound handler
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        log.info("handlername={}Outbound - flush 消息",ctx.name());
        super.flush(ctx); // 继续调用 pipeline 中下一个 outbound handler
    }
}

