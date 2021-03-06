//
// Copyright 2014 Mike Friesen
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

package ca.gobits.test.dht.integration;

import org.apache.commons.codec.binary.Base64;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.util.ReflectionTestUtils;

import ca.gobits.dht.server.DHTServerConfig;

/**
 * Integration Test Configuration.
 *
 */
@Configuration
@EnableAsync
public class IntegrationTestConfiguration {

    /**
     * @return DHTServerAsync
     */
    @Bean
    public DHTServerAsync asyncServer() {
        return new DHTServerAsync();
    }

    /**
     * @return DHTServerConfig
     */
    @Bean
    public DHTServerConfig serverConfig() {
        DHTServerConfig config = new DHTServerConfig();
        ReflectionTestUtils.setField(config, "nodeId",
                Base64.decodeBase64("UcskDNtZ7xTdlIoJY7KwuA2cg3c="));
        return config;
    }
}
