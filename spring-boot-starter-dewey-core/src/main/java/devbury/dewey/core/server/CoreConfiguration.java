/*
 * Copyright 2015 devbury LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package devbury.dewey.core.server;

import devbury.dewey.core.event.MessageEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.List;

@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableCaching
@EnableScheduling
@ComponentScan
public class CoreConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(CoreConfiguration.class);

    @Autowired(required = false)
    private List<MessageEventListener> messageEventListeners;

    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    public GuavaCacheManager guavaCacheManager() {
        return new GuavaCacheManager();
    }

    @PostConstruct
    public void init() {
        logger.info("Installing plugins:");
        if (messageEventListeners != null) {
            messageEventListeners.forEach(m -> logger.info("  Installed plugin {}", m.getClass().getSimpleName()));
        } else {
            logger.info("  NONE!");
        }
    }
}