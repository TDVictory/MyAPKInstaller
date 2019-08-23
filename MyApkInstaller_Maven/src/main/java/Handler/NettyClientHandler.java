package Handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import server.MyMessageInfo;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        MyMessageInfo.Device device = MyMessageInfo.Device.newBuilder()
                .setDeviceID("MyDevice")
                .addApkName("MyApk01")
                .addApkName("MyApk02")
                .build();
        MyMessageInfo.DeviceInfo deviceInfo = MyMessageInfo.DeviceInfo.newBuilder()
                .addDevices(device)
                .build();

        ctx.channel().writeAndFlush(deviceInfo);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
