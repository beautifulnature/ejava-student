package myorg.mypackage.ex1;

import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppTest{

	private static Logger logger = LoggerFactory.getLogger(AppTest.class);

	@Test
	public void testApp(){
		// System.out.println("testApp");
		logger.info("testApp");
		App app = new App();
		assertTrue("app didn't return 1", app.returnOne() == 1);
	}
	
	// @Test
	public void testFail(){
		System.out.println("testFail");
		App app = new App();
		assertTrue("app didn't return 0", app.returnOne() == 0);
	}


}
