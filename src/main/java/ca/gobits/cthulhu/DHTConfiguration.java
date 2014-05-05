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

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;

/**
 * DHT Configuration class.
 */
@Configuration
@EnableNeo4jRepositories(basePackages = {"ca.gobits.cthulhu.repository" })
public class DHTConfiguration extends Neo4jConfiguration {

    /** Default Database Filename. */
    public static final String DATABASE_FILE = "cthulhu.db";

    /**
     * default constructor.
     */
    public DHTConfiguration() {
        setBasePackage("ca.gobits.cthulhu.domain");
    }

    //CHECKSTYLE:OFF
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
     * @return DHTProtocolHandler
     */
    @Bean
    public DHTProtocolHandler dhtProtocolHandler() {
        return new DHTProtocolHandler();
    }

    /**
     * @return GraphDatabaseService
     */
    @Bean
    public GraphDatabaseService graphDatabaseService() {
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase(DATABASE_FILE);
        return db;
    }
    //CHECKSTYLE:ON
}
