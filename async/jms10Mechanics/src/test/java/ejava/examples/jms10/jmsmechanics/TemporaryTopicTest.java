package ejava.examples.jms10.jmsmechanics;

import static org.junit.Assert.*;


import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.jms10.jmsmechanics.MessageCatcher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This test case tests the ability to create and send/receive messages 
 * to/from a temporary topic. 
 */
public class TemporaryTopicTest extends JMSTestBase {
    static final Logger logger = LoggerFactory.getLogger(TemporaryTopicTest.class);
    protected Session session;
    protected MessageCatcher catcher1;
    protected MessageCatcher catcher2;
    protected int msgCount;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    	shutdownCatcher(catcher1);
    	shutdownCatcher(catcher2);
        if (session != null)  { session.close(); }
    }
    
    protected void startCatchers(Session session, Destination destination) 
    		throws Exception {
        catcher1 = createCatcher("subscriber1", destination).setSession(session);
        catcher2 = createCatcher("subscriber2", destination).setSession(session);
        
        //topics will only deliver messages to subscribers that are 
        //successfully registered prior to the message being published. We
        //need to wait for the catcher to start so it doesn't miss any 
        //messages.
        startCatcher(catcher1);
        startCatcher(catcher2);
    }

    @Test
    public void testTemporaryTopicSend() throws Exception {
        logger.info("*** testTemporaryTopicSend ***");
        MessageProducer producer = null;
        try {
            connection.stop();
            session = connection.createSession(
                    false, Session.CLIENT_ACKNOWLEDGE);
            Topic destination = session.createTemporaryTopic();
            logger.debug("created temporary topic={}", destination);
            startCatchers(session, destination);
            connection.start();
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            
            catcher1.clearMessages();
            catcher2.clearMessages();
            producer.send(message);
            logger.info("sent msgId={}", message.getJMSMessageID());
            for(int i=0; i<10 && 
                (catcher1.getMessages().size() < 1 ||
                catcher2.getMessages().size() < 1); i++) {
                logger.debug("waiting for messages...");
                Thread.sleep(1000);
            }
            assertEquals(1, catcher1.getMessages().size());
            assertEquals(1, catcher2.getMessages().size());
        }
        finally {
            if (producer != null) { producer.close(); }
        }
    }
    
    @Test
    public void testTemporaryTopicMultiSend() throws Exception {
        logger.info("*** testTemporaryTopicMultiSend ***");
        MessageProducer producer = null;
        try {
            connection.stop();
            session = connection.createSession(
                    false, Session.CLIENT_ACKNOWLEDGE);
            Topic destination = session.createTemporaryTopic();
            logger.debug("created temporary topic={}", destination);
            startCatchers(session, destination);
            connection.start();
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            
            catcher1.clearMessages();
            catcher2.clearMessages();
            for(int i=0; i<msgCount; i++) {
                producer.send(message);
                logger.info("sent msgId={}", message.getJMSMessageID());
            }
            for(int i=0; i<10 && 
                (catcher1.getMessages().size() < msgCount ||
                catcher2.getMessages().size() < msgCount); i++) {
                logger.debug("waiting for messages...");
                Thread.sleep(1000);
            }
            assertEquals(msgCount, catcher1.getMessages().size());
            assertEquals(msgCount, catcher2.getMessages().size());
        }
        finally {
            if (producer != null) { producer.close(); }
        }
    }
}
