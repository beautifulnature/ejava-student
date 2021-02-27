package myorg.relex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import myorg.relex.one2many.Bus;
import myorg.relex.one2many.Rider;

public class One2ManyTest extends JPATestBase {

	private static Logger log = LoggerFactory.getLogger(One2ManyTest.class);

	//@Test
	public void testSample() {
		log.info("testSample");
	}
	
	@Test
    public void testOneToManyUniJoinTable() {

        log.info("*** testOneToManyUniJoinTable ***");

        Bus bus = new Bus(302);

        em.persist(bus);

        List<Rider> riders = new ArrayList<Rider>();
        for (int i=0; i<2; i++) {
            Rider rider = new Rider();
            rider.setName("rider" + i);

            em.persist(rider);
            riders.add(rider);
        }

        log.debug("relating entities");

        bus.getPassengers().addAll(riders);

        em.flush();
        em.clear();

        log.debug("verify we have expected objects");

        Bus bus2 = em.find(Bus.class, bus.getNumber());

        assertNotNull("bus not found", bus2);

        for (Rider r: bus.getPassengers()) {
            assertNotNull("rider not found", em.find(Rider.class, r.getId()));
        }

        log.debug("verify they are related");

        assertEquals("unexpected number of riders", bus.getPassengers().size(), bus2.getPassengers().size());
    }
}