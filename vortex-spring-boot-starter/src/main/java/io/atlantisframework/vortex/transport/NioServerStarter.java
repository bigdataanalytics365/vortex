/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package io.atlantisframework.vortex.transport;

import java.net.InetSocketAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;

import io.atlantisframework.tridenter.ApplicationInfo;
import io.atlantisframework.tridenter.multicast.ApplicationMulticastEvent;
import io.atlantisframework.tridenter.multicast.ApplicationMulticastGroup;
import io.atlantisframework.tridenter.multicast.ApplicationMulticastEvent.MulticastEventType;
import io.atlantisframework.tridenter.utils.BeanLifeCycle;
import io.atlantisframework.vortex.NioTransportContext;
import io.atlantisframework.vortex.ServerInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * NioServerStarter
 *
 * @author Fred Feng
 * @since 2.0.1
 */
@Slf4j
public class NioServerStarter implements BeanLifeCycle, ApplicationListener<ApplicationMulticastEvent> {

	@Value("${spring.application.cluster.name}")
	private String clusterName;

	@Autowired
	private NioServer nioServer;

	@Autowired
	private ApplicationMulticastGroup applicationMulticastGroup;

	private InetSocketAddress localAddress;

	@Override
	public void configure() throws Exception {
		localAddress = (InetSocketAddress) nioServer.start();
	}

	@Override
	public void onApplicationEvent(ApplicationMulticastEvent event) {
		if (event.getMulticastEventType() == MulticastEventType.ON_ACTIVE) {
			ApplicationInfo applicationInfo = event.getApplicationInfo();
			applicationMulticastGroup.send(applicationInfo.getId(), NioTransportContext.class.getName(), new ServerInfo(localAddress));
			log.info("Application '{}' join transport cluster '{}'", applicationInfo, clusterName);
		}
	}

	@Override
	public void destroy() {
		nioServer.stop();
	}

}
