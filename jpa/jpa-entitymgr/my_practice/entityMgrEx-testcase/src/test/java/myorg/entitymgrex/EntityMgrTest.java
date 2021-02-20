package myorg.entitymgrex;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityMgrTest {

	private static final Logger logger = LoggerFactory.getLogger(EntityMgrTest.class);

	private static final String PERSISTENCE_UNIT = "entityMgrEx";
	private static EntityManagerFactory emf;

	private EntityManager em;

	@Test
	public void testTemplate() {
		logger.info("testTemplate");
	}

	@BeforeClass
	public static void setUpClass() {
		logger.debug("creating entity manager factory");
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
	}

	@Before
	public void setUp() throws Exception {
		logger.debug("creating entity manager");

		em = emf.createEntityManager();
		cleanup();
	}
	
	public void cleanup() {
        em.getTransaction().begin();
        Query query = em.createNativeQuery("delete from EM_AUTO");
        int rows = query.executeUpdate();
        em.getTransaction().commit();
        logger.info("removed {} rows", rows);
    }

	@After
	public void tearDown() throws Exception {

		if (em != null) {
			logger.debug("tearDown() started, em={}", em);

			em.getTransaction().begin();
			em.flush();
			logAutos();
			em.getTransaction().commit();
			em.close();

			logger.debug("tearDown() complete, em={}", em);

			em = null;
		}
	}
	
	public void logAutos() {
        Query query = em.createQuery("select a from Auto as a");

        for (Object o: query.getResultList()) {
            logger.info("EM_AUTO: {}", o);
        }
    }

	@AfterClass
	public static void tearDownClass() {

		if (emf != null) {
			logger.debug("closing entity manager factory");

			emf.close();
			emf = null;
		}
	}
}
