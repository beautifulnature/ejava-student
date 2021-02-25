package myorg.relex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import myorg.relex.one2one.Coach;
import myorg.relex.one2one.Employee;
import myorg.relex.one2one.Member;
import myorg.relex.one2one.Person;
import myorg.relex.one2one.Player;

public class One2OneTest extends JPATestBase {

	private static Logger log = LoggerFactory.getLogger(One2OneTest.class);

//    @Test
    public void testSample() {

        log.info("testSample");
    }
    
//    @Test
    public void testOne2OneUniFK() {

        log.info("*** testOne2OneUniFK ***");

        Person person = new Person();
        person.setName("Johnny Unitas");

        Player player = new Player();
        player.setPerson(person);
        player.setPosition(Player.Position.OFFENSE);

        em.persist(person);
        em.persist(player); //provider will propagate person.id to player.FK

        //clear the persistence context and get new instances
        em.flush();
        em.clear();

        Player player2 = em.find(Player.class, player.getId());

        assertEquals("unexpected position", player.getPosition(), player2.getPosition());

        assertEquals("unexpected name", player.getPerson().getName(), player2.getPerson().getName());
        
        Object[] cols = (Object[]) em.createNativeQuery(
                "select person.id person_id, person.name, " +
                       "player.id player_id, player.person_id player_person_id " +
                "from RELATIONEX_PLAYER player " +
                "join RELATIONEX_PERSON person on person.id = player.person_id " +
                "where player.id = ?1")
                .setParameter(1, player.getId())
                .getSingleResult();

        log.info("row=" + Arrays.toString(cols));

        assertEquals("unexpected person_id", person.getId(), ((Number)cols[0]).intValue());

        assertEquals("unexpected person_name", person.getName(), (String)cols[1]);

        assertEquals("unexpected player_id", player.getId(), ((Number)cols[2]).intValue());

        assertEquals("unexpected player_person_id", person.getId(), ((Number)cols[3]).intValue());
        
        em.remove(player2);
        em.remove(player2.getPerson());
        em.flush();
        
        assertNull("person not deleted", em.find(Person.class, person.getId()));
        
        assertNull("player not deleted", em.find(Player.class, player.getId()));
    }
    
//    @Test
    public void testOne2OneUniJoinTable() {

        log.info("*** testOne2OneUniJoinTable ***");

        Person person = new Person();
        person.setName("Joe Smith");

        Member member = new Member(person);
        member.setRole(Member.Role.SECONDARY);

        em.persist(person);
        em.persist(member); //provider will propagate person.id to member.FK

        //clear the persistence context and get new instances
        em.flush();
        em.clear();

        Member member2 = em.find(Member.class, member.getId());

        assertEquals("unexpected role", member.getRole(), member2.getRole());

        assertEquals("unexpected name", member.getPerson().getName(), member2.getPerson().getName());
        
        Object[] cols = (Object[]) em.createNativeQuery(
                "select person.id person_id, person.name, " +
                       "member.id member_id, member.role member_role, " +
                       "link.member_id link_member, link.person_id link_person " +
                "from RELATIONEX_MEMBER member " +
                "join RELATIONEX_MEMBER_PERSON link on link.member_id = member.id " +
                "join RELATIONEX_PERSON person      on link.person_id = person.id " +
                "where member.id = ?1")
                .setParameter(1, member.getId())
                .getSingleResult();

        log.info("row=" + Arrays.toString(cols));

        assertEquals("unexpected person_id", person.getId(), ((Number)cols[0]).intValue());

        assertEquals("unexpected person_name", person.getName(), (String)cols[1]);

        assertEquals("unexpected member_id", member.getId(), ((Number)cols[2]).intValue());

        assertEquals("unexpected member_role", member.getRole().name(), (String)cols[3]);

        assertEquals("unexpected link_member_id", member.getId(), ((Number)cols[4]).intValue());

        assertEquals("unexpected link_person_id", person.getId(), ((Number)cols[5]).intValue());
        
        em.remove(member2);
        em.remove(member2.getPerson());
        em.flush();
        
        assertNull("person not deleted", em.find(Person.class, person.getId()));
        
        assertNull("member not deleted", em.find(Member.class, member.getId()));
    }
    
//    @Test
    public void testOne2OneUniPKJ() {

        log.info("*** testOne2OneUniPKJ ***");

        Person person = new Person();
        person.setName("Ozzie Newsome");

        em.persist(person);
        em.flush(); //generate the PK for the person

        Employee employee = new Employee(person);//set PK/FK -- provider will not auto propagate
        employee.setHireDate(new GregorianCalendar(1996, Calendar.JANUARY, 1).getTime());

        //em.persist(person);
        em.persist(employee);

        //clear the persistence context and get new instances
        em.flush();
        em.clear();

        Employee employee2 = em.find(Employee.class, employee.getPerson().getId());

        log.info("calling person...");

        assertEquals("unexpected name", employee.getPerson().getName(), employee2.getPerson().getName());
        
        Object[] cols = (Object[]) em.createNativeQuery(
                "select person.id person_id, person.name, " +
                        "employee.id employee_id " +
                "from RELATIONEX_EMPLOYEE employee " +
                "join RELATIONEX_PERSON person on person.id = employee.id " +
                "where employee.id = ?1")
                .setParameter(1, employee.getId())
                .getSingleResult();

        log.info("row=" + Arrays.toString(cols));

        assertEquals("unexpected person_id", person.getId(), ((Number)cols[0]).intValue());

        assertEquals("unexpected person_name", person.getName(), (String)cols[1]);

        assertEquals("unexpected employee_id", employee.getId(), ((Number)cols[2]).intValue());
        
        em.remove(employee2);
        em.remove(employee2.getPerson());
        em.flush();

        assertNull("person not deleted", em.find(Person.class, person.getId()));

        assertNull("employee not deleted", em.find(Employee.class, employee.getId()));
    }
    
    @Test
    public void testOne2OneUniMapsId() {

        log.info("*** testOne2OneUniMapsId ***");

        Person person = new Person();
        person.setName("John Harbaugh");

        Coach coach = new Coach(person);
        coach.setType(Coach.Type.HEAD);

        em.persist(person);
        em.persist(coach); //provider auto propagates person.id to coach.FK mapped to coach.PK 

        //flush commands to database, clear cache, and pull back new instance
        em.flush();
        em.clear();

        Coach coach2 = em.find(Coach.class, coach.getId());

        log.info("calling person...");

        assertEquals("unexpected name", coach.getPerson().getName(), coach2.getPerson().getName());
        
        Object[] cols = (Object[]) em.createNativeQuery(
                "select person.id person_id, person.name, " +
                        "coach.person_id coach_id " +
                "from RELATIONEX_COACH coach " +
                "join RELATIONEX_PERSON person on person.id = coach.person_id " +
                "where coach.person_id = ?1")
                .setParameter(1, coach.getId())
                .getSingleResult();

        log.info("row=" + Arrays.toString(cols));

        assertEquals("unexpected person_id", person.getId(), ((Number)cols[0]).intValue());

        assertEquals("unexpected person_name", person.getName(), (String)cols[1]);

        assertEquals("unexpected coach_id", coach.getId(), ((Number)cols[2]).intValue());
        
        em.remove(coach2);
        em.remove(coach2.getPerson());
        em.flush();

        assertNull("person not deleted", em.find(Person.class, person.getId()));

        assertNull("coach not deleted", em.find(Coach.class, coach.getId()));
    }
}
