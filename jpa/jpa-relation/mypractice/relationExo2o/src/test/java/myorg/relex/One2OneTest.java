package myorg.relex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import javax.persistence.TemporalType;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import myorg.relex.one2one.Applicant;
import myorg.relex.one2one.Application;
import myorg.relex.one2one.BoxOffice;
import myorg.relex.one2one.Auto;
import myorg.relex.one2one.Coach;
import myorg.relex.one2one.Driver;
import myorg.relex.one2one.Employee;
import myorg.relex.one2one.Member;
import myorg.relex.one2one.Person;
import myorg.relex.one2one.Player;
import myorg.relex.one2one.ShowEvent;
import myorg.relex.one2one.ShowEventPK;
import myorg.relex.one2one.ShowTickets;

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
		em.persist(player); // provider will propagate person.id to player.FK

		// clear the persistence context and get new instances
		em.flush();
		em.clear();

		Player player2 = em.find(Player.class, player.getId());

		assertEquals("unexpected position", player.getPosition(), player2.getPosition());

		assertEquals("unexpected name", player.getPerson().getName(), player2.getPerson().getName());

		Object[] cols = (Object[]) em
				.createNativeQuery("select person.id person_id, person.name, "
						+ "player.id player_id, player.person_id player_person_id " + "from RELATIONEX_PLAYER player "
						+ "join RELATIONEX_PERSON person on person.id = player.person_id " + "where player.id = ?1")
				.setParameter(1, player.getId()).getSingleResult();

		log.info("row=" + Arrays.toString(cols));

		assertEquals("unexpected person_id", person.getId(), ((Number) cols[0]).intValue());

		assertEquals("unexpected person_name", person.getName(), (String) cols[1]);

		assertEquals("unexpected player_id", player.getId(), ((Number) cols[2]).intValue());

		assertEquals("unexpected player_person_id", person.getId(), ((Number) cols[3]).intValue());

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
		em.persist(member); // provider will propagate person.id to member.FK

		// clear the persistence context and get new instances
		em.flush();
		em.clear();

		Member member2 = em.find(Member.class, member.getId());

		assertEquals("unexpected role", member.getRole(), member2.getRole());

		assertEquals("unexpected name", member.getPerson().getName(), member2.getPerson().getName());

		Object[] cols = (Object[]) em
				.createNativeQuery("select person.id person_id, person.name, "
						+ "member.id member_id, member.role member_role, "
						+ "link.member_id link_member, link.person_id link_person " + "from RELATIONEX_MEMBER member "
						+ "join RELATIONEX_MEMBER_PERSON link on link.member_id = member.id "
						+ "join RELATIONEX_PERSON person      on link.person_id = person.id " + "where member.id = ?1")
				.setParameter(1, member.getId()).getSingleResult();

		log.info("row=" + Arrays.toString(cols));

		assertEquals("unexpected person_id", person.getId(), ((Number) cols[0]).intValue());

		assertEquals("unexpected person_name", person.getName(), (String) cols[1]);

		assertEquals("unexpected member_id", member.getId(), ((Number) cols[2]).intValue());

		assertEquals("unexpected member_role", member.getRole().name(), (String) cols[3]);

		assertEquals("unexpected link_member_id", member.getId(), ((Number) cols[4]).intValue());

		assertEquals("unexpected link_person_id", person.getId(), ((Number) cols[5]).intValue());

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
		em.flush(); // generate the PK for the person

		Employee employee = new Employee(person);// set PK/FK -- provider will not auto propagate
		employee.setHireDate(new GregorianCalendar(1996, Calendar.JANUARY, 1).getTime());

		// em.persist(person);
		em.persist(employee);

		// clear the persistence context and get new instances
		em.flush();
		em.clear();

		Employee employee2 = em.find(Employee.class, employee.getPerson().getId());

		log.info("calling person...");

		assertEquals("unexpected name", employee.getPerson().getName(), employee2.getPerson().getName());

		Object[] cols = (Object[]) em
				.createNativeQuery("select person.id person_id, person.name, " + "employee.id employee_id "
						+ "from RELATIONEX_EMPLOYEE employee "
						+ "join RELATIONEX_PERSON person on person.id = employee.id " + "where employee.id = ?1")
				.setParameter(1, employee.getId()).getSingleResult();

		log.info("row=" + Arrays.toString(cols));

		assertEquals("unexpected person_id", person.getId(), ((Number) cols[0]).intValue());

		assertEquals("unexpected person_name", person.getName(), (String) cols[1]);

		assertEquals("unexpected employee_id", employee.getId(), ((Number) cols[2]).intValue());

		em.remove(employee2);
		em.remove(employee2.getPerson());
		em.flush();

		assertNull("person not deleted", em.find(Person.class, person.getId()));

		assertNull("employee not deleted", em.find(Employee.class, employee.getId()));
	}

//    @Test
	public void testOne2OneUniMapsId() {

		log.info("*** testOne2OneUniMapsId ***");

		Person person = new Person();
		person.setName("John Harbaugh");

		Coach coach = new Coach(person);
		coach.setType(Coach.Type.HEAD);

		em.persist(person);
		em.persist(coach); // provider auto propagates person.id to coach.FK mapped to coach.PK

		// flush commands to database, clear cache, and pull back new instance
		em.flush();
		em.clear();

		Coach coach2 = em.find(Coach.class, coach.getId());

		log.info("calling person...");

		assertEquals("unexpected name", coach.getPerson().getName(), coach2.getPerson().getName());

		Object[] cols = (Object[]) em.createNativeQuery("select person.id person_id, person.name, "
				+ "coach.person_id coach_id " + "from RELATIONEX_COACH coach "
				+ "join RELATIONEX_PERSON person on person.id = coach.person_id " + "where coach.person_id = ?1")
				.setParameter(1, coach.getId()).getSingleResult();

		log.info("row=" + Arrays.toString(cols));

		assertEquals("unexpected person_id", person.getId(), ((Number) cols[0]).intValue());

		assertEquals("unexpected person_name", person.getName(), (String) cols[1]);

		assertEquals("unexpected coach_id", coach.getId(), ((Number) cols[2]).intValue());

		em.remove(coach2);
		em.remove(coach2.getPerson());
		em.flush();

		assertNull("person not deleted", em.find(Person.class, person.getId()));

		assertNull("coach not deleted", em.find(Coach.class, coach.getId()));
	}

//    @Test
	public void testOne2OneUniIdClass() {

		log.info("*** testOneToOneUniIdClass ***");

		Date showDate = new GregorianCalendar(1975 + new Random().nextInt(100), Calendar.JANUARY, 1).getTime();
		Date showTime = new GregorianCalendar(0, 0, 0, 0, 0, 0).getTime();
		ShowEvent show = new ShowEvent(showDate, showTime);
		show.setName("Rocky Horror");
		ShowTickets tickets = new ShowTickets(show); // parent already has natural PK by this point
		tickets.setTicketsLeft(300);

		em.persist(show);
		em.persist(tickets);

		// flush commands to database, clear cache, and pull back new instance
		em.flush();
		em.clear();

		ShowTickets tickets2 = em.find(ShowTickets.class, new ShowEventPK(tickets.getDate(), tickets.getTime()));

		log.info("calling parent...");

		assertEquals("unexpected name", tickets.getShow().getName(), tickets2.getShow().getName());

		Object[] cols = (Object[]) em.createNativeQuery("select show.date show_date, show.time show_time, "
				+ "tickets.ticket_date ticket_date, tickets.ticket_time ticket_time, tickets.tickets "
				+ "from RELATIONEX_SHOWEVENT show "
				+ "join RELATIONEX_SHOWTICKETS tickets on show.date = tickets.ticket_date and show.time = tickets.ticket_time "
				+ "where tickets.ticket_date = ?1 and tickets.ticket_time = ?2")
				.setParameter(1, tickets.getShow().getDate(), TemporalType.DATE)
				.setParameter(2, toCalendar(tickets.getShow().getTime()), TemporalType.TIME).getSingleResult();

		log.info("row=" + Arrays.toString(cols));

		assertEquals("unexpected show_date", tickets2.getShow().getDate(), (Date) cols[0]);

		assertEquals("unexpected show_time", tickets2.getShow().getTime(), (Date) cols[1]);

		assertEquals("unexpected ticket_date", tickets2.getDate(), (Date) cols[2]);

		assertEquals("unexpected ticket_time", tickets2.getTime(), (Date) cols[3]);

		assertEquals("unexpected ticketsLeft", tickets2.getTicketsLeft(), ((Number) cols[4]).intValue());

		em.remove(tickets2);
		em.remove(tickets2.getShow());
		em.flush();

		assertNull("tickets not deleted", em.find(ShowEvent.class, new ShowEventPK(show.getDate(), show.getTime())));

		assertNull("show not deleted",
				em.find(ShowTickets.class, new ShowEventPK(tickets.getDate(), tickets.getTime())));
	}

	private Calendar toCalendar(Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		return cal;
	}

//	@Test
	public void testOne2OneUniEmbeddedId() {

		log.info("*** testOne2OneUniEmbedded ***");

		Date showDate = new GregorianCalendar(1975 + new Random().nextInt(100), Calendar.JANUARY, 1).getTime();
		Date showTime = new GregorianCalendar(0, 0, 0, 0, 0, 0).getTime();

		ShowEvent show = new ShowEvent(showDate, showTime);
		show.setName("Rocky Horror");

		BoxOffice boxOffice = new BoxOffice(show);
		boxOffice.setTicketsLeft(500);

		em.persist(show);
		em.persist(boxOffice); // provider auto propagates parent.cid to dependent.FK mapped to dependent.cid

		// flush commands to database, clear cache, and pull back new instance
		em.flush();
		em.clear();

		BoxOffice boxOffice2 = em.find(BoxOffice.class, new ShowEventPK(boxOffice.getDate(), boxOffice.getTime()));

		log.info("calling parent...");

		assertEquals("unexpected name", boxOffice.getShow().getName(), boxOffice2.getShow().getName());

		Object[] cols = (Object[]) em.createNativeQuery("select show.date show_date, show.time show_time, "
				+ "tickets.show_date ticket_date, tickets.show_time ticket_time, tickets.tickets "
				+ "from RELATIONEX_SHOWEVENT show "
				+ "join RELATIONEX_BOXOFFICE tickets on show.date = tickets.show_date and show.time = tickets.show_time "
				+ "where tickets.show_date = ?1 and tickets.show_time = ?2")
				.setParameter(1, boxOffice.getShow().getDate(), TemporalType.DATE)
				.setParameter(2, toCalendar(boxOffice.getShow().getTime()), TemporalType.TIME).getSingleResult();

		log.info("row=" + Arrays.toString(cols));

		assertEquals("unexpected show_date", boxOffice2.getShow().getDate(), (Date) cols[0]);

		assertEquals("unexpected show_time", boxOffice2.getShow().getTime(), (Date) cols[1]);

		assertEquals("unexpected ticket_date", boxOffice2.getDate(), (Date) cols[2]);

		assertEquals("unexpected ticket_time", boxOffice2.getTime(), (Date) cols[3]);

		assertEquals("unexpected ticketsLeft", boxOffice2.getTicketsLeft(), ((Number) cols[4]).intValue());

		em.remove(boxOffice2);
		em.remove(boxOffice2.getShow());
		em.flush();

		assertNull("tickets not deleted", em.find(ShowEvent.class, new ShowEventPK(show.getDate(), show.getTime())));

		assertNull("show not deleted",
				em.find(BoxOffice.class, new ShowEventPK(boxOffice.getDate(), boxOffice.getTime())));
	}

	//@Test
	public void testOne2OneBiPKJ() {

		log.info("*** testOne2OneBiPKJ() ***");

		Applicant applicant = new Applicant();
		applicant.setName("Jason Garret");

		Application application = new Application(applicant);
		application.setDesiredStartDate(new GregorianCalendar(2008, Calendar.JANUARY, 1).getTime());

		em.persist(applicant); // provider will generate a PK
		em.persist(application); // provider will propogate parent.PK to dependent.FK/PK

		em.flush();
		em.clear();

		log.info("finding dependent...");

		Application application2 = em.find(Application.class, application.getId());

		log.info("found dependent...");

		assertTrue("unexpected startDate",
				application.getDesiredStartDate().equals(application2.getDesiredStartDate()));

		log.info("calling parent...");

		assertEquals("unexpected name", application.getApplicant().getName(), application2.getApplicant().getName());

		em.flush();
		em.clear();

		log.info("finding parent...");

		Applicant applicant2 = em.find(Applicant.class, applicant.getId());

		log.info("found parent...");

		assertEquals("unexpected name", applicant.getName(), applicant2.getName());

		log.info("calling dependent...");

		assertTrue("unexpected startDate", applicant.getApplication().getDesiredStartDate()
				.equals(applicant2.getApplication().getDesiredStartDate()));

		em.remove(applicant2.getApplication());
		em.remove(applicant2);
		em.flush();

		assertNull("applicant not deleted", em.find(Applicant.class, applicant2.getId()));

		assertNull("application not deleted", em.find(Application.class, applicant2.getApplication().getId()));
	}
	
	@Test
    public void testOne2OneBiOwningOptional() {

        log.info("*** testOne2OneBiOwningOptional() ***");

        Auto auto = new Auto();           //auto is inverse/parent side
        auto.setType(Auto.Type.CAR);

        Driver driver = new Driver(auto); //driver is owning/dependent side
        driver.setName("Danica Patrick");

        auto.setDriver(driver); //application must maintain inverse side

        em.persist(auto);
        em.persist(driver);
        
        em.flush();
        em.clear();

        log.info("finding dependent...");

        Driver driver2 = em.find(Driver.class, driver.getId());

        log.info("found dependent...");

        assertEquals("unexpected name", driver.getName(), driver2.getName());

        log.info("calling parent...");

        assertEquals("unexpected name", driver.getAuto().getType(), driver2.getAuto().getType());
        
        em.flush();
        em.clear();
        log.info("finding parent...");
        Auto auto2 = em.find(Auto.class, auto.getId());
        log.info("found parent...");
        assertEquals("unexpected type", auto.getType(), auto2.getType());
        log.info("calling dependent...");
        assertEquals("unexpected name", auto.getDriver().getName(), auto2.getDriver().getName());
        
        Auto truck = new Auto();
        truck.setType(Auto.Type.TRUCK);
        em.persist(truck);
        driver = em.find(Driver.class, driver.getId()); //get the managed instance
        driver.setAuto(truck);
        truck.setDriver(driver);
        
        em.flush();
        em.clear();
        Auto auto3 = em.find(Auto.class, auto.getId());
        Driver driver3 = em.find(Driver.class, driver.getId());
        Auto truck3 = em.find(Auto.class, truck.getId());
        assertNull("driver not removed from auto", auto3.getDriver());
        assertEquals("driver not assigned to truck", truck.getId(), driver3.getAuto().getId());
        assertEquals("truck not assigned to driver", driver.getId(), truck3.getDriver().getId());
        
        em.remove(truck3.getDriver());
        em.remove(truck3);
        em.remove(auto3);
        em.flush();
        assertNull("driver not deleted", em.find(Driver.class, truck3.getDriver().getId()));
        assertNull("auto not deleted", em.find(Auto.class, auto.getId()));
        assertNull("truck not deleted", em.find(Auto.class, truck.getId()));
    }
}
