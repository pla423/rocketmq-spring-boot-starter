/**
 * Copyright 2019 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.thierrysquirrel.autoconfigure;

import com.github.thierrysquirrel.annotation.EnableRocketMQ;
import com.github.thierrysquirrel.aspect.RocketAspect;
import com.github.thierrysquirrel.container.RocketConsumerContainer;
import com.github.thierrysquirrel.container.RocketProducerContainer;
import com.github.thierrysquirrel.core.serializer.GsonSerializer;
import com.github.thierrysquirrel.core.serializer.MqSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * ClassName: RocketProperties
 * Description:
 * date: 2019/4/25 15:57
 *
 * @author ThierrySquirrel
 * @since JDK 1.8
 */
@Configuration
@EnableConfigurationProperties(RocketProperties.class)
@ConditionalOnBean(annotation = EnableRocketMQ.class)
public class RocketAutoConfiguration {
	@Resource
	private RocketProperties rocketProperties;
	@Resource
	private Map<String, Object> consumerContainer;
	private final List<MqSerializer> mqSerializers;

	public RocketAutoConfiguration(List<MqSerializer> mqSerializers) {
		this.mqSerializers = mqSerializers;
	}

	@Bean
	@ConditionalOnMissingBean(RocketConsumerContainer.class)
	public RocketConsumerContainer rocketConsumerContainer() {
		MqSerializer mqSerializer = (mqSerializers == null || mqSerializers.size() == 0) ? new GsonSerializer() : mqSerializers.get(0);
		return new RocketConsumerContainer(rocketProperties, mqSerializer);
	}

	@Bean
	@ConditionalOnMissingBean(Map.class)
	public Map<String, Object> consumerContainer() {
		return new ConcurrentHashMap<>(16);
	}

	@Bean
	@ConditionalOnMissingBean(RocketProducerContainer.class)
	public RocketProducerContainer rocketProducerContainer() {
		return new RocketProducerContainer(consumerContainer, rocketProperties);
	}

	@Bean
	@ConditionalOnMissingBean(RocketAspect.class)
	public RocketAspect rockerAspect() {
		return new RocketAspect(consumerContainer, rocketProperties);
	}
}
