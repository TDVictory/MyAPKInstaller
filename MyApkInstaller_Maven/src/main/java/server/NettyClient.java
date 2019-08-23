package server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

public class NettyClient {
    private String HOST;
    private int PORT;

    public NettyClient(String HOST, int PORT) {
        this.HOST = HOST;
        this.PORT = PORT;
    }

    public void run(){
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast("encoder", new ProtobufEncoder());
                            socketChannel.pipeline().addLast(new NettyClientHandler());
                        }
                    });
            ChannelFuture cf = bootstrap.connect(HOST,PORT).sync();
            cf.channel().closeFuture().sync();

        }catch (Exception e){

        }finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new NettyClient("127.0.0.1",9999).run();
    }
}
