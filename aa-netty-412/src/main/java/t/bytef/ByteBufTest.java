package t.bytef;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

public class ByteBufTest {

    public static void main(String[] args) {

        PooledByteBufAllocator allocator = new PooledByteBufAllocator();

        ByteBuf buf = allocator.buffer(10);

        buf.retain().release();
    }
}
