package myorg.relex;

import javax.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.*;

public class JPATestBase {

    private static Logger log = LoggerFactory.getLogger(JPATestBase.class);

    private static final String PERSISTENCE_UNIT = "relationEx-test";
    private static EntityManagerFactory emf;
    protected EntityManager em;

    @BeforeClass
    public static void setUpClass() {
        log.debug("creating entity manager factory");

        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    }

    @Before
    public void setUp() throws Exception {

        log.debug("creating entity manager");

        em = emf.createEntityManager();
        cleanup();
        em.getTransaction().begin();
    }

    @After
    public void tearDown() throws Exception {

        log.debug("tearDown() started, em={}", em);

        if (em!=null) {
            EntityTransaction tx = em.getTransaction();

            if (tx.isActive()) {
                if (tx.getRollbackOnly() == true) {
                    tx.rollback();
                } else {
                    tx.commit();
                }
            } else {
                tx.begin();
                tx.commit();
            }

            em.close();
            em=null;
        }

        log.debug("tearDown() complete, em={}", em);
     }

    @AfterClass
    public static void tearDownClass() {

        log.debug("closing entity manager factory");

        if (emf!=null) {
            emf.close();
        }
    }

    public void cleanup() {
        em.getTransaction().begin();
        em.getTransaction().commit();
    }

    protected EntityManager createEm() {
        return emf.createEntityManager();
    }
}