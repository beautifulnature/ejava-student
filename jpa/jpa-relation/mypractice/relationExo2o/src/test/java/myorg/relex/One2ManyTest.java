package myorg.relex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import myorg.relex.one2many.Basket;
import myorg.relex.one2many.Bus;
import myorg.relex.one2many.Produce;
import myorg.relex.one2many.Rider;
import myorg.relex.one2many.Route;
import myorg.relex.one2many.Stop;
import myorg.relex.one2many.Suspect;
import myorg.relex.one2many.Todo;
import myorg.relex.one2many.TodoList;

public class One2ManyTest extends JPATestBase {

	private static Logger log = LoggerFactory.getLogger(One2ManyTest.class);

	//@Test
	public void testSample() {
		log.info("testSample");
	}
	
	//@Test
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
        
        log.debug("remove one of the child objects");

        em.remove(bus2.getPassengers().get(0)); //ouch!!! this will violate a FK-constraint

        em.flush();
        
        log.debug("remove one of the child objects");

        Rider rider = bus2.getPassengers().get(0);

        log.debug("removing the relationship");

        assertTrue("ride not found in relation", bus2.getPassengers().remove(rider));

        em.flush();

        log.debug("removing the object");

        em.remove(rider);
        em.flush();
    }
    
    //@Test
    public void testOneToManyUniFK() {

        log.info("*** testOneToManyUniFK ***");

        Route route = new Route(302);

        em.persist(route);

        List<Stop> stops = new ArrayList<Stop>();

        for (int i=0; i<2; i++) {
            Stop stop = new Stop();
            stop.setName("stop" + i);

            em.persist(stop);

            stops.add(stop);
        }

        log.debug("relating entities");

        route.getStops().addAll(stops);

        em.flush();
        em.clear();

        log.debug("verify we have expected objects");

        Route route2 = em.find(Route.class, route.getNumber());

        assertNotNull("route not found", route2);

        for (Stop s: route.getStops()) {
            assertNotNull("stop not found", em.find(Stop.class, s.getId()));
        }

        log.debug("verify they are related");

        assertEquals("unexpected number of stops", route.getStops().size(), route2.getStops().size());
        
        log.debug("remove one of the child objects");

        log.debug("removing the object and relationship");

        em.remove(route2.getStops().get(0)); 

        em.flush();
    }
    
//    @Test
    public void testOneToManyUniElementCollection() {

        log.info("*** testOneToManyUniElementCollection ***");

        Suspect suspect = new Suspect();
        suspect.setName("william");

        em.persist(suspect);

        suspect.getAliases().add("bill");
        suspect.getAliases().add("billy");

        em.flush();
        em.clear();

        log.debug("verify we have expected objects");

        Suspect suspect2 = em.find(Suspect.class,  suspect.getId());

        assertNotNull("suspect not found", suspect2);

        for (String a: suspect.getAliases()) {
            assertNotNull("alias not found", suspect2.getAliases().contains(a));
        }
    }
    
//    @Test
    public void testOneToManyUniEmbeddableElementCollection() {

        log.info("*** testOneToManyUniEmbeddableElementCollection ***");

        Basket basket = new Basket();
        basket.setName("assorted fruit");

        em.persist(basket);

        basket.getContents()
            .add(new Produce("apple", Produce.Color.RED, new Date(System.currentTimeMillis()+(3600000*24*30))));
        basket.getContents()
            .add(new Produce("banana", Produce.Color.YELLOW, new Date(System.currentTimeMillis()+(3600000*24*14))));

        em.flush();
        em.clear();

        log.debug("verify we have expected objects");

        Basket basket2 = em.find(Basket.class, basket.getId());

        assertNotNull("basket not found", basket2);

        assertEquals("unexpected contents", basket.getContents().size(), basket2.getContents().size());
        
        log.debug("remove one of the child objects");

        Produce produce = basket2.getContents().get(0);

        assertTrue("produce not found", basket2.getContents().remove(produce));

        em.flush();
    }
    
//    @Test
    public void testOneToManyUniOrphanRemoval() {

        log.info("*** testOneToManyUniEmbeddableElementCollection ***");

        //check how many child entities exist to start with
        int startCount = em.createQuery("select count(t) from Todo t", Number.class).getSingleResult().intValue();

        log.debug("create new TODO list with first entry");

        TodoList list = new TodoList();
        list.getTodos().add(new Todo("get up"));
        list.getTodos().add(new Todo("brush"));

        em.persist(list);
        em.flush();
        
        log.debug("verifying we have new child entity");

        assertEquals("new child not found", startCount +1,
            em.createQuery("select count(t) from Todo t", Number.class).getSingleResult().intValue());
        
        log.debug("removing child from list");

        list.getTodos().clear();

        em.flush();

        assertEquals("orphaned child not deleted", startCount,
                em.createQuery("select count(t) from Todo t", Number.class).getSingleResult().intValue());
    }
}