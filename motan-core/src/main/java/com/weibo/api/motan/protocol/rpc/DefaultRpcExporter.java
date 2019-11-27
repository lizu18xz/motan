/*
 *
 *   Copyright 2009-2016 Weibo, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package com.weibo.api.motan.protocol.rpc;

import com.weibo.api.motan.common.URLParamType;
import com.weibo.api.motan.core.extension.ExtensionLoader;
import com.weibo.api.motan.rpc.AbstractExporter;
import com.weibo.api.motan.rpc.Exporter;
import com.weibo.api.motan.rpc.Provider;
import com.weibo.api.motan.rpc.URL;
import com.weibo.api.motan.transport.EndpointFactory;
import com.weibo.api.motan.transport.ProviderMessageRouter;
import com.weibo.api.motan.transport.ProviderProtectedMessageRouter;
import com.weibo.api.motan.transport.Server;
import com.weibo.api.motan.util.LoggerUtil;
import com.weibo.api.motan.util.MotanFrameworkUtil;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhanglei28 on 2017/9/1.
 */
public class DefaultRpcExporter<T> extends AbstractExporter<T> {

    protected Server server;
    protected EndpointFactory endpointFactory;
    protected final ConcurrentHashMap<String, ProviderMessageRouter> ipPort2RequestRouter;
    protected final ConcurrentHashMap<String, Exporter<?>> exporterMap;

    //很重要
    public DefaultRpcExporter(Provider<T> provider, URL url, ConcurrentHashMap<String, ProviderMessageRouter> ipPort2RequestRouter,
                              ConcurrentHashMap<String, Exporter<?>> exporterMap) {
        super(provider, url);
        this.exporterMap = exporterMap;
        this.ipPort2RequestRouter = ipPort2RequestRouter;

        //将DefaultProvider加入到ProviderMessageRouter
        ProviderMessageRouter requestRouter = initRequestRouter(url);
        endpointFactory =
                ExtensionLoader.getExtensionLoader(EndpointFactory.class).getExtension(
                        url.getParameter(URLParamType.endpointFactory.getName(), URLParamType.endpointFactory.getValue()));
        //根据服务的url创建NettyServer
        server = endpointFactory.createServer(url, requestRouter);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void unexport() {
        String protocolKey = MotanFrameworkUtil.getProtocolKey(url);
        String ipPort = url.getServerPortStr();

        Exporter<T> exporter = (Exporter<T>) exporterMap.remove(protocolKey);

        if (exporter != null) {
            exporter.destroy();
        }
        ProviderMessageRouter requestRouter = ipPort2RequestRouter.get(ipPort);

        if (requestRouter != null) {
            requestRouter.removeProvider(provider);
        }

        LoggerUtil.info("DefaultRpcExporter unexport Success: url={}", url);
    }

    @Override
    protected boolean doInit() {
        boolean result = server.open();

        return result;
    }

    @Override
    public boolean isAvailable() {
        return server.isAvailable();
    }

    @Override
    public void destroy() {
        endpointFactory.safeReleaseResource(server, url);
        LoggerUtil.info("DefaultRpcExporter destory Success: url={}", url);
    }

    protected ProviderMessageRouter initRequestRouter(URL url) {
        //根据主机和端口 找到ProviderMessageRouter
        String ipPort = url.getServerPortStr();

        //处理客户端请求的时候会用到ProviderMessageRouter
        ProviderMessageRouter requestRouter = ipPort2RequestRouter.get(ipPort);

        if (requestRouter == null) {
            ipPort2RequestRouter.putIfAbsent(ipPort, new ProviderProtectedMessageRouter());
            requestRouter = ipPort2RequestRouter.get(ipPort);
        }
        //把当前服务的provider加入到providers中    providers.put(serviceKey, provider);
        requestRouter.addProvider(provider);

        return requestRouter;
    }
}
