package com.asn.rpc.codec;

import com.asn.rpc.protocol.MessageHeader;
import com.asn.rpc.protocol.RpcProtocol;
import com.asn.rpc.serialization.RpcSerialization;
import com.asn.rpc.serialization.factory.RpcSerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 自定义编码器：实现将自定义协议包对象转换为字节流
 **/
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol<Object>> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocol<Object> msg, ByteBuf out) throws Exception {
        MessageHeader header = msg.getHeader();
        //将协议头写入ByteBuf，注意：写入顺序要和定义的顺序一致
        out.writeShort(header.getMagic());
        out.writeByte(header.getVersion());
        out.writeByte(header.getSerializationType());
        out.writeByte(header.getMsgType());
        out.writeByte(header.getStatus());
        out.writeLong(header.getMsgId());

        //将协议体转换为字节数组
        RpcSerialization rpcSerialization = RpcSerializationFactory.getRpcSerialization(header.getSerializationType());
        byte[] data = rpcSerialization.serialize(msg.getBody());
        out.writeInt(data.length);//消息长度msgLength
        out.writeBytes(data);//消息体
    }
}
