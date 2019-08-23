package Handler;

import com.sun.nio.sctp.MessageInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import server.MyMessageInfo;

import java.util.ArrayList;
import java.util.List;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MyMessageInfo.DeviceInfo deviceInfo = (MyMessageInfo.DeviceInfo) msg;
        List<MyMessageInfo.Device> devices = deviceInfo.getDevicesList();
        for (MyMessageInfo.Device device:devices
             ) {
            System.out.println(device.getDeviceID() + "内有：\n");
            List<String> apkNames = device.getApkNameList();
            for (String apkName:apkNames
                 ) {
                System.out.println(apkName);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exceptionCaught");
    }
}
