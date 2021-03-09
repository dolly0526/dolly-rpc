package com.github.dolly0526.simplerpc.core.client.stubs;

import com.github.dolly0526.simplerpc.core.client.RequestIdSupport;
import com.github.dolly0526.simplerpc.core.client.ServiceStub;
import com.github.dolly0526.simplerpc.core.client.ServiceTypes;
import com.github.dolly0526.simplerpc.core.serialize.SerializeSupport;
import com.github.dolly0526.simplerpc.core.transport.Transport;
import com.github.dolly0526.simplerpc.core.transport.command.Code;
import com.github.dolly0526.simplerpc.core.transport.command.Command;
import com.github.dolly0526.simplerpc.core.transport.command.Header;
import com.github.dolly0526.simplerpc.core.transport.command.ResponseHeader;

import java.util.concurrent.ExecutionException;

/**
 * @author yusenyang
 * @create 2021/3/9 15:11
 */
public abstract class AbstractStub implements ServiceStub {

    // 桩的传输方法
    protected Transport transport;


    protected byte [] invokeRemote(RpcRequest request) {
        Header header = new Header(ServiceTypes.TYPE_RPC_REQUEST, 1, RequestIdSupport.next());
        byte [] payload = SerializeSupport.serialize(request);
        Command requestCommand = new Command(header, payload);
        try {
            Command responseCommand = transport.send(requestCommand).get();
            ResponseHeader responseHeader = (ResponseHeader) responseCommand.getHeader();
            if(responseHeader.getCode() == Code.SUCCESS.getCode()) {
                return responseCommand.getPayload();
            } else {
                throw new Exception(responseHeader.getError());
            }

        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setTransport(Transport transport) {
        this.transport = transport;
    }
}