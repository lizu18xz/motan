/*
 *  Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.weibo.motan.demo.client;

import com.weibo.api.motan.config.ProtocolConfig;
import com.weibo.api.motan.config.RefererConfig;
import com.weibo.api.motan.config.RegistryConfig;
import com.weibo.api.motan.rpc.ResponseFuture;
import com.weibo.motan.demo.service.MotanDemoService;

import java.util.ArrayList;
import java.util.List;

public class MotanApiClientDemo {

    public static void main(String[] args) {
        RefererConfig<MotanDemoService> motanDemoServiceReferer = new RefererConfig<MotanDemoService>();
        // 设置接口及实现类
        motanDemoServiceReferer.setInterface(MotanDemoService.class);
        // 配置服务的group以及版本号
        motanDemoServiceReferer.setGroup("motan-demo-rpc");
        motanDemoServiceReferer.setVersion("1.0");
        motanDemoServiceReferer.setRequestTimeout(1000);
        // 配置注册中心为zk
        List<RegistryConfig>registryConfigList=new ArrayList<>();
        RegistryConfig registry1 = new RegistryConfig();
        registry1.setRegProtocol("zookeeper");
        registry1.setAddress("192.168.88.129:2181");
        registryConfigList.add(registry1);
        motanDemoServiceReferer.setRegistries(registryConfigList);
        // 配置RPC协议
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setId("motan");
        protocol.setName("motan");
        //设置负载策略和Ha策略,使用默认
        /*protocol.setHaStrategy();
        protocol.setLoadbalance();*/
        motanDemoServiceReferer.setProtocol(protocol);
        // 请求服务
        MotanDemoService service = motanDemoServiceReferer.getRef();
        System.out.println(service.hello("motan"));


        for (int i=0;i<100000;i++){
            try {

                /*motan异步调用详解

                1.使用ResponseFuture接口来接收远程调用结果，ResponseFuture具备future和callback能力
                ResponseFuture future =service.hello("motan");*/

                        System.out.println(service.hello("motan"));
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        System.exit(0);
    }
}
