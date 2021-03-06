package myorg.entityex;

import static org.junit.Assert.assertNotNull;

import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import myorg.entityex.mapped.Animal;


public class AnimalTest {
    private static final Logger logger = LoggerFactory.getLogger(Animal.class);
    private static final String PERSISTENCE_UNIT = "entityEx-test";
    private static EntityManagerFactory emf;
    private EntityManager em;    

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
        em.getTransaction().begin();
    }

    @After
    public void tearDown() throws Exception {
        logger.debug("tearDown() started, em={}", em);
        if (em!=null) {
            EntityTransaction tx = em.getTransaction();
            if (tx.isActive()) {
                if (tx.getRollbackOnly() == true) { tx.rollback(); }
                else                              { tx.commit(); }
            } else {
                tx.begin();
                tx.commit();
            }
            em.close();
            em=null;
        }
        logger.debug("tearDown() complete, em={}", em);
     }
    
    @AfterClass
    public static void tearDownClass() {
        logger.debug("closing entity manager factory");
        if (emf!=null) { 
            emf.close();
            emf=null;
        }
    }
    
    public void cleanup() {
        em.getTransaction().begin();
        List<Animal> animals = em.createQuery("select a from Animal a", Animal.class)
            .getResultList();
        for (Animal a: animals) {
            em.remove(a);
        }
        em.getTransaction().commit();
        logger.info("removed {} animals", animals.size());
    }

    @Test
    public void testCreateAnimal() {

        logger.info("testCreateAnimal");

        Animal animal = new Animal("bessie", new GregorianCalendar(1960, 1, 1).getTime(), 1400.2);
        em.persist(animal);

        assertNotNull("animal not found", em.find(Animal.class,animal.getId()));
        
        em.flush(); //make sure all writes were issued to DB

        em.clear(); //purge the local entity manager entity cache to cause new instance

        assertNotNull("animal not found", em.find(Animal.class,animal.getId()));
    }
    
    @Test
    public void testCreateAnimalAnnotated() {

        logger.info("testCreateAnimalAnnotated");

        myorg.entityex.annotated.Animal2 animal = new myorg.entityex.annotated.Animal2("bessie", new GregorianCalendar(1960, 1, 1).getTime(), 1400.2);

        em.persist(animal);        

        assertNotNull("animal not found", em.find(myorg.entityex.annotated.Animal2.class,animal.getId()));

        em.flush(); //make sure all writes were issued to DB
        em.clear(); //purge the local entity manager entity cache to cause new instance

        assertNotNull("animal not found", em.find(myorg.entityex.annotated.Animal2.class,animal.getId()));
    }
    
    @Test
    public void testCreateCatAnnotated() {

        logger.info("testCreateCatAnnotated");

        myorg.entityex.annotated.Cat2 cat = new myorg.entityex.annotated.Cat2("fluffy", null, 99.9);
        em.persist(cat);                                                 //get provider to call getters
        em.flush();
        em.detach(cat);

        cat = em.find(myorg.entityex.annotated.Cat2.class, cat.getId()); //get provider to call setters
    }
}