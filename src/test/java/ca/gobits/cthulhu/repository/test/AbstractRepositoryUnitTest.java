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

package ca.gobits.cthulhu.repository.test;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.neo4j.kernel.impl.util.FileUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Helper Repository Methods.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestRepositoryConfiguration.class)
public abstract class AbstractRepositoryUnitTest {

    /**
     * BeforeClass().
     * @throws Exception  Exception
     */
    @BeforeClass
    public static void beforeClass() throws Exception {
        cleanup();
    }

    /**
     * Cleanup after tests.
     * @throws IOException  IOException
     */
    protected static void cleanup() throws IOException {
        FileUtils.deleteRecursively(new File(
                TestRepositoryConfiguration.DATABASE_FILE));
    }
}
