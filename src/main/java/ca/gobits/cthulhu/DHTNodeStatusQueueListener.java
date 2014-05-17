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

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import ca.gobits.cthulhu.domain.DHTNode;

/**
 * JMS Message Listener for determining the status of an DHTNode.
 *
 */
public class DHTNodeStatusQueueListener implements MessageListener {

    /** LOGGER. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTNodeStatusQueueListener.class);

    @Override
    public void onMessage(final Message message) {

        if (message instanceof ObjectMessage) {
            ObjectMessage om = (ObjectMessage) message;

            try {
                DHTNode node = (DHTNode) om.getObject();
                LOGGER.info("determining status of " + node.getInfoHash());
            } catch (JMSException e) {
                LOGGER.warn(e, e);
            }
        }
    }
}
