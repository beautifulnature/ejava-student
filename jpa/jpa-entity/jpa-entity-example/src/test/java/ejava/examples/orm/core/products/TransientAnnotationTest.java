package ejava.examples.orm.core.products;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.examples.orm.core.annotated.Tank;

/**
 * This test case provides a demo of using transient properties through
 * annotations.
 */
public class TransientAnnotationTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(BasicAnnotationTest.class);
    
    /**
     * This test provides a demo of persisting a class that has mapped
     * a getMakeModel() getter as @Transient so that it can be ignored
     * when persisting to the database.
     */
    @Test
    public void testTransient() {
        logger.info("testTransient");
        ejava.examples.orm.core.annotated.Tank tank = new Tank(1);
        tank.setMake("acme");
        tank.setModel("great guns");

        //insert a row in the database
        em.persist(tank);
        logger.info("created tank: {}", tank);         
    }
    
}
