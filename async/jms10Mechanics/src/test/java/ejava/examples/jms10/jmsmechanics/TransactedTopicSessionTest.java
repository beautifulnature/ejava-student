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
 * This test case performs a test of a transacted session using a topic. 
 * Receivers should not receive messages until they are committed by the 
 * sender.
 */
public class TransactedTopicSessionTest extends JMSTestBase {
    static final Logger logger = LoggerFactory.getLogger(TransactedTopicSessionTest.class);
    protected Destination destination;        
    protected MessageCatcher catcher1;
    protected MessageCatcher catcher2;

    @Before
    public void setUp() throws Exception {
        destination = (Topic) lookup(topicJNDI);
        assertNotNull("null destination:" + topicJNDI, destination);
        
        catcher1 = createCatcher("subscriber1", destination);
        catcher2 = createCatcher("subscriber2", destination);
        
        //topics will only deliver messages to subscribers that are 
        //successfully registered prior to the message being published. We
        //need to wait for the catcher to start so it doesn't miss any 
        //messages.
        startCatcher(catcher1);
        startCatcher(catcher2);
    }
    
    @After
    public void tearDown() throws Exception {
        shutdownCatcher(catcher1);
    	    shutdownCatcher(catcher2);
    }

    @Test
    public void testTransactedTopicSessionSend() throws Exception {
        logger.info("*** testTransactedTopicSessionSend ***");
        Session session = null;
        MessageProducer producer = null;
        try {
            connection.stop();
            session = connection.createSession(
                    true, Session.AUTO_ACKNOWLEDGE);  //<!-- TRUE=transacted
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            
            catcher1.clearMessages();
            catcher2.clearMessages();
            producer.send(message);
            logger.info("sent msgId={}", message.getJMSMessageID());
            assertEquals(0, catcher1.getMessages().size());
            assertEquals(0, catcher2.getMessages().size());
            session.commit(); //<!-- COMMITTING SESSION TRANSACTION
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
            if (session != null)  { session.close(); }
        }
    }

    @Test
    public void testRollbackTransactedTopicSessionSend() throws Exception {
        logger.info("*** testRollbackTransactedTopicSessionSend ***");
        Session session = null;
        MessageProducer producer = null;
        try {
            connection.stop();
            session = connection.createSession(
                    true, Session.AUTO_ACKNOWLEDGE);  //<!-- TRUE=transacted
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            
            catcher1.clearMessages();
            catcher2.clearMessages();
            producer.send(message);
            logger.info("sent msgId={}", message.getJMSMessageID());
            assertEquals(0, catcher1.getMessages().size());
            assertEquals(0, catcher2.getMessages().size());
            session.rollback(); //<!-- ROLLING BACK SESSION TRANSACTION
            for(int i=0; i<10 && 
                (catcher1.getMessages().size() < 1 ||
                catcher2.getMessages().size() < 1); i++) {
                logger.debug("waiting for rolled back messages...");
                Thread.sleep(1000);
            }
            assertEquals(0, catcher1.getMessages().size());
            assertEquals(0, catcher2.getMessages().size());
        }
        finally {
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
        }
    }
    
    @Test
    public void testTransactedTopicSessionMultiSend() throws Exception {
        logger.info("*** testTransactedTopicSessionMultiSend ***");
        Session session = null;
        MessageProducer producer = null;
        try {
            connection.stop();
            session = connection.createSession(
                    true, Session.AUTO_ACKNOWLEDGE); //<!-- TRUE=transacted
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            
            catcher1.clearMessages();
            catcher2.clearMessages();
            for(int i=0; i<msgCount; i++) {
                producer.send(message);
                logger.info("sent msgId={}", message.getJMSMessageID());
            }
            assertEquals(0, catcher1.getMessages().size());
            assertEquals(0, catcher2.getMessages().size());
            session.commit(); //<!-- COMMITTING SESSION TRANSACTION
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
            if (session != null)  { session.close(); }
        }
    }

    @Test
    public void testRolledbackTransactedTopicSessionMultiSend() throws Exception {
        logger.info("*** testRolledbackTransactedTopicSessionMultiSend ***");
        Session session = null;
        MessageProducer producer = null;
        try {
            connection.stop();
            session = connection.createSession(
                    true, Session.AUTO_ACKNOWLEDGE); //<!-- TRUE=transacted
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            
            catcher1.clearMessages();
            catcher2.clearMessages();
            for(int i=0; i<msgCount; i++) {
                producer.send(message);
                logger.info("sent msgId={}", message.getJMSMessageID());
            }
            assertEquals(0, catcher1.getMessages().size());
            assertEquals(0, catcher2.getMessages().size());
            session.rollback(); //<!-- ROLLBACK SESSION TRANSACTION
            for(int i=0; i<10 && 
                (catcher1.getMessages().size() < msgCount ||
                catcher2.getMessages().size() < msgCount); i++) {
                logger.debug("waiting for rolledback messages...");
                Thread.sleep(1000);
            }
            assertEquals(0, catcher1.getMessages().size());
            assertEquals(0, catcher2.getMessages().size());
        }
        finally {
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
        }
    }
}
