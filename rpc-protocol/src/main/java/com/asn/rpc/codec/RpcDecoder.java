package com.asn.rpc.codec;

import com.asn.rpc.constants.RpcProtocolConstants;
import com.asn.rpc.core.RpcRequest;
import com.asn.rpc.core.RpcResponse;
import com.asn.rpc.enums.MessageTypeEnum;
import com.asn.rpc.protocol.MessageHeader;
import com.asn.rpc.protocol.RpcProtocol;
import com.asn.rpc.serialization.RpcSerialization;
import com.asn.rpc.serialization.factory.RpcSerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < RpcProtocolConstants.HEADER_LENGTH){
            return;//消息不合法
        }

        //记录当前的读索引位置
        in.markReaderIndex();
        //判断魔数是否合法
        short magic = in.readShort();
        if (magic != RpcProtocolConstants.MAGIC){
            in.resetReaderIndex();
            throw new IllegalArgumentException("magic is illegal "+magic);
        }

        //读取协议头的其他信息
        byte version = in.readByte();
        byte serializationType = in.readByte();
        byte msgType = in.readByte();
        //判断消息类型是否合法
        MessageTypeEnum messageTypeEnum = MessageTypeEnum.findByType(msgType);
        if (messageTypeEnum == null){
            in.resetReaderIndex();
            throw new IllegalArgumentException("messageType is illegal "+msgType);
        }
        byte status = in.readByte();
        long msgId = in.readLong();
        int msgLength = in.readInt();

        //判断消息是否传输完毕
        if (in.readableBytes() < msgLength){
            in.resetReaderIndex();
            return;
        }

        //读取消息体
        byte[] bytes = new byte[msgLength];
        in.readBytes(bytes);

        MessageHeader header = new MessageHeader();
        header.setMagic(magic);
        header.setVersion(version);
        header.setSerializationType(serializationType);
        header.setStatus(status);
        header.setMsgType(msgType);
        header.setMsgId(msgId);
        header.setMsgLength(msgLength);
        RpcSerialization serialization = RpcSerializationFactory.getRpcSerialization(serializationType);
        switch (messageTypeEnum){
            case REQUEST:
                RpcRequest request = serialization.deserialize(bytes, RpcRequest.class);
                if (request != null){
                    RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(request);
                    out.add(protocol);
                }
                break;
            case RESPONSE:
                RpcResponse response = serialization.deserialize(bytes, RpcResponse.class);
                if (response != null){
                    RpcProtocol<RpcResponse> protocol = new RpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(response);
                    out.add(protocol);
                }
                break;
        }
    }
}
