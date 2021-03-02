package myorg.relex;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import myorg.relex.many2one.House;
import myorg.relex.many2one.HousePK;
import myorg.relex.many2one.Occupant;
import myorg.relex.many2one.State;
import myorg.relex.many2one.StateResident;

public class Many2OneTest extends JPATestBase {

	private static Logger log = LoggerFactory.getLogger(Many2OneTest.class);

//	@Test
	public void testSample() {
		log.info("testSample");
	}
	
//	@Test
    public void testManyToOneUniFK() {

        log.info("*** testManyToOneUniFK ***");

        State state = new State("MD", "Maryland");

        StateResident res = new StateResident(state);
        res.setName("joe");
        
        log.debug("persisting parent");
        
        em.persist(state);

        log.debug("persisting child");

        em.persist(res);

        em.flush();
        
        log.debug("getting new instances");

        em.clear();

        StateResident res2 = em.find(StateResident.class, res.getId());

        log.debug("checking child");

        assertEquals("unexpected child data", res.getName(), res2.getName());

        log.debug("checking parent");

        assertEquals("unexpected parent data", state.getName(), res2.getState().getName());
        
        log.debug("add more residents");
        
        StateResident resB = new StateResident(res2.getState());
        
        em.persist(resB);
        
        em.flush();
        
        log.debug("getting new instances of residences");
        em.clear();
        
        List<StateResident> residents = em.createQuery(
                    "select r from StateResident r " +
                    "where r.state.id=:stateId", 
                    StateResident.class)
                .setParameter("stateId", res.getState().getId())
                .getResultList();
        
        assertEquals("unexpected number of residents", 2, residents.size());
        
        log.debug("changing state/data of common parent");

        residents.get(0).getState().setName("Home State");

        assertEquals("unexpected difference in parent data", 
                residents.get(0).getState().getName(),
                residents.get(1).getState().getName());
    }
    
    @Test
    public void testManyToOneUniCompoundFK() {

        log.info("*** testManyToOneUniCompoundFK ***");

        House house = new House(new HousePK(1600,"PA Ave"),"White House");

        Occupant occupant = new Occupant("bo", house);

        log.debug("persisting parent");

        em.persist(house);

        log.debug("persisting child");

        em.persist(occupant);

        em.flush();
        
        log.debug("getting new instances");

        em.clear();

        Occupant occupant2 = em.find(Occupant.class, occupant.getId());

        log.debug("checking child");

        assertEquals("unexpected child data", occupant.getName(), occupant2.getName());

        log.debug("checking parent");

        assertEquals("unexpected parent data", house.getName(), occupant2.getResidence().getName());
        
        log.debug("add more child entities");

        Occupant occupantB = new Occupant("miss beazily", occupant2.getResidence());

        em.persist(occupantB);

        em.flush();
    }
}