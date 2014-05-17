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

import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/**
 * JMS Configuration.
 */
@Configuration
public class JmsConfiguration {

    /** Minimum space requirement, 10 GB. */
    private static final long TEMP_USAGE_LIMIT = 1024L * 1024 * 1024 * 10;

    /**
     * @return BrokerService
     */
    @Bean
    public BrokerService brokerFactory() {
        BrokerService factory = new BrokerService();
        factory.setPersistent(false);
        factory.getSystemUsage().getTempUsage().setLimit(TEMP_USAGE_LIMIT);
        return factory;
    }

    /**
     * @return ConnectionFactory
     */
    @Bean
    @DependsOn("brokerFactory")
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory f = new ActiveMQConnectionFactory();
        f.setBrokerURL("vm://localhost");
        return f;
    }

    /**
     * @return DefaultMessageListenerContainer for determining
     * DHTNode's status.
     */
    @Bean
    public DefaultMessageListenerContainer messageListenerContainer() {
        DefaultMessageListenerContainer dc =
                new DefaultMessageListenerContainer();
        dc.setConnectionFactory(connectionFactory());
        dc.setDestination(nodeStatusQueue());
        dc.setMessageListener(nodeStatusQueueListener());
        return dc;
    }

    /**
     * @return JmsTemplate
     */
    @Bean
    @DependsOn("brokerFactory")
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(connectionFactory());
    }

    /**
     * @return Queue  queue used for finding an DHTNode's status.
     */
    @Bean
    public Queue nodeStatusQueue() {
        return new ActiveMQQueue("DHTNodeStatus");
    }

    /**
     * @return DHTNodeStatusQueueListener
     */
    @Bean
    public DHTNodeStatusQueueListener nodeStatusQueueListener() {
        return new DHTNodeStatusQueueListener();
    }
}
