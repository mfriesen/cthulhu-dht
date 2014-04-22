package ca.gobits.cthulhu;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * DHT Configuration class.
 */
@Configuration
@ComponentScan({"ca.gobits.cthulhu" })
public class DHTConfiguration {

    /**
     * Creates the routing table to use.
     * @return DHTRoutingTable
     */

    //CHECKSTYLE:OFF
    @Bean
    public DHTRoutingTable routingTable() {
        return new DHTBucketRoutingTable();
    }
    //CHECKSTYLE:ON
}
