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

package com.weibo.motan.demo.server;

import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.config.ProtocolConfig;
import com.weibo.api.motan.config.RegistryConfig;
import com.weibo.api.motan.config.ServiceConfig;
import com.weibo.api.motan.util.MotanSwitcherUtil;
import com.weibo.motan.demo.service.MotanDemoService;

public class MotanApiExportDemo {

    public static void main(String[] args) throws InterruptedException {

            //1-初始化一个服务的配置
            ServiceConfig<MotanDemoService> motanDemoService = new ServiceConfig<MotanDemoService>();
            //2-设置接口及实现类
            motanDemoService.setInterface(MotanDemoService.class);
            motanDemoService.setRef(new MotanDemoServiceImpl());
            // 配置服务的group以及版本号
            motanDemoService.setGroup("motan-demo-rpc");
            motanDemoService.setVersion("1.0");

            //3-配置注册中心zk
            RegistryConfig registry = new RegistryConfig();
            registry.setRegProtocol("zookeeper");
            registry.setAddress("192.168.88.129:2181");
            motanDemoService.setRegistry(registry);

            //4-配置RPC协议
            ProtocolConfig protocol = new ProtocolConfig();
            protocol.setId("motan");
            protocol.setName("motan");

            motanDemoService.setProtocol(protocol);
            //5-设置暴露的端口号
            motanDemoService.setExport("motan:8004");
            //6-暴露服务,等待客户端调用
            motanDemoService.export();
            MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, true);
            System.out.println("server start...");


    }

}
