//
// Copyright 2013 Mike Friesen
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package ca.gobits.cthulhu;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * DHT Configuration class.
 */
@Configuration
public class DHTConfiguration {

    /** Thread Queue Capacity. */
    private static final int THREAD_QUEUE_CAPACITY = 25;

    /** Thread Max Pool Size. */
    private static final int THREAD_MAX_POOL_SIZE = 10;

    /** Thread Core Pool Size. */
    private static final int THREAD_CORE_POOL_SIZE = 5;

    /**
     * @return DHTRoutingTable
     */
    @Bean
    public DHTNodeRoutingTable routingTable() {
        return new DHTNodeBucketRoutingTable();
    }

    /**
     * @return DHTInfoHashRoutingTable
     */
    @Bean
    public DHTInfoHashRoutingTable infoHashRoutingTable() {
        return new DHTInfoHashRoutingTableBasic();
    }

    /**
     * @return DHTTokenTable
     */
    @Bean
    public DHTTokenTable dhtTokenTable() {
        return new DHTTokenTableBasic();
    }

    /**
     * @return DHTServer
     */
    @Bean
    public DHTServer dhtServer() {
        return new DHTServer();
    }

    /**
     * @return ThreadPoolTaskExecutor
     */
    @Bean
    public ThreadPoolTaskExecutor threadExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(THREAD_CORE_POOL_SIZE);
        executor.setMaxPoolSize(THREAD_MAX_POOL_SIZE);
        executor.setQueueCapacity(THREAD_QUEUE_CAPACITY);
        return executor;
    }

    /**
     * @return DHTProtocolHandler
     */
    @Bean
    public DHTProtocolHandler dhtProtocolHandler() {
        return new DHTProtocolHandler();
    }
}
