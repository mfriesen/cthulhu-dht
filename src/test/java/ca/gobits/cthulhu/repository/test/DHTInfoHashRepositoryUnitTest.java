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

import static org.junit.Assert.assertNotNull;

import java.math.BigInteger;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scala.util.Random;
import ca.gobits.cthulhu.domain.DHTInfoHash;
import ca.gobits.cthulhu.repository.DHTInfoHashRepository;

/**
 * DHTInfoHashRepository Unit Tests.
 *
 */
public final class DHTInfoHashRepositoryUnitTest extends
        AbstractRepositoryUnitTest {

    /** DHTInfoHash Repository. */
    @Autowired
    private DHTInfoHashRepository repo;

    /**
     * testFindByInfoHash01().
     */
    @Test
    public void testFindByInfoHash01() {
        // given
        createDHTInfoHash(10);

        BigInteger infoHashId = new BigInteger("123");
        DHTInfoHash h = new DHTInfoHash(infoHashId);
        repo.save(h);

        // when
        DHTInfoHash result = repo.findByInfoHash(infoHashId);

        // then
        assertNotNull(result);
    }

    /**
     * Creates random DHTInfoHash objects.
     * @param count number of object to create
     */
    private void createDHTInfoHash(final int count) {

        Random rn = new Random();

        for (int i = 0; i < count; i++) {
            DHTInfoHash h = new DHTInfoHash();
            h.setInfoHash(new BigInteger("" + rn.nextInt()));
            h.setLatitude(rn.nextDouble());
            h.setLongitude(rn.nextDouble());
            repo.save(h);
        }
    }
}
