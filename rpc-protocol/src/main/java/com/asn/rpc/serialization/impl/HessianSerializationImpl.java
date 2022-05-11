package com.asn.rpc.serialization.impl;

import com.asn.rpc.serialization.RpcSerialization;
import com.asn.rpc.serialization.exception.RpcSerializationException;
import com.caucho.hessian.io.HessianSerializerInput;
import com.caucho.hessian.io.HessianSerializerOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSerializationImpl implements RpcSerialization {
    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        if (obj == null){
            throw new NullPointerException();
        }
        byte[] result;
        HessianSerializerOutput hessianSerializerOutput;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
             hessianSerializerOutput = new HessianSerializerOutput(outputStream);
             hessianSerializerOutput.writeObject(obj);
             hessianSerializerOutput.flush();

             result = outputStream.toByteArray();
        }catch (Exception e){
            throw new RpcSerializationException(e);
        }
        return result;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        if (data == null){
            throw new NullPointerException();
        }
        T result;
        HessianSerializerInput hessianSerializerInput;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)){
            hessianSerializerInput = new HessianSerializerInput(inputStream);
            result = (T) hessianSerializerInput.readObject(clazz);
        }catch (Exception e){
            throw new RpcSerializationException(e);
        }
        return result;
    }
}
