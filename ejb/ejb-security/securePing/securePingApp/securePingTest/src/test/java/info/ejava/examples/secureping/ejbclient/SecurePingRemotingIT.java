package info.ejava.examples.secureping.ejbclient;


import static org.junit.Assert.*;

import info.ejava.examples.secureping.ejb.SecurePing;
import info.ejava.examples.secureping.ejb.SecurePingRemote;

import java.io.IOException;
import java.util.Properties;

import javax.ejb.EJBAccessException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class demonstrates accessing the application server and EJB using
 * JBoss Remoting as different authenticated users. We will perform this
 * authentication thru the use of the credential properties of the JNDI 
 * InitialContext.
 * <pre>
java.naming.factory.initial=org.jboss.naming.remote.client.InitialContextFactory                                                                                                              
java.naming.factory.url.pkgs=                                                                                                                                      
java.naming.provider.url=http-remoting://localhost:8080                                                                                                                                       
jboss.naming.client.ejb.context=true  
</pre>
 */
public class SecurePingRemotingIT extends SecurePingTestBase {
    private static final Logger logger = LoggerFactory.getLogger(SecurePingRemotingIT.class);
    String jndiName = System.getProperty("jndi.name.secureping.remoting",
            "securePingEAR/securePingEJB/SecurePingEJB!"+SecurePingRemote.class.getName());
    
    /**
     * Sets up the proxy reference to the remote interface of the EJB.
     */
    @Before
    public void setUp() throws Exception {
    	logger.info("== setUp() ==");
        logger.debug("jndi name:" + jndiName);
    }
    
    /**
     * Changes the security context for the connection to the server 
     * by getting a new InitialContext. Note that the caller must close()
     * the returned context for future calls to this method to change the 
     * identity of the caller.
     * @param username
     * @param password
     * @return
     * @throws NamingException
     */
	private Context runAs(String[] login) throws NamingException, IOException {
        Properties env = new Properties(); //load the defaults from jndi.properties
        if (login != null) {
	        env.put(Context.SECURITY_PRINCIPAL, login[0]);
	        env.put(Context.SECURITY_CREDENTIALS, login[1]);
        }
        logger.debug(String.format("%s env=%s", login==null?"anonymous":login[0], env));
        return new InitialContext(env);
    }
    
    /**
     * Tests if the server is allowing anonymous users establish an 
     * InitialContext.
     * @throws NamingException
     */
    @Test 
    public void testAnonymousInitialContext() throws NamingException {
        logger.info("*** testAnonymousInitialContext ***");
        Context jndi=null;
        try {
        		jndi=runAs(null);
            	SecurePing ejb=(SecurePing)jndi.lookup(jndiName);
            	String response = ejb.pingAll();
            	if (!response.contains("principal=$local")) {
            		fail("did not detect anonymous InitialContext");
        		} else {
        			logger.debug("JBoss used $local user:" + response);
        		}
        	}
        catch (NamingException ex) {
            logger.info("expected error for anonymous InitialContext:" + ex);
        }
        catch (Exception ex) {
            logger.error("unexpected exception for anonymous", ex); 
            fail("unexpected exception for anonymous:" + ex); 
        } finally {
            jndi.close();
        }
    }

    /**
     * Logs in as each user type an calls a query method of the EJB that 
     * will determine if the server side believes we are in the provided
     * role.
     * @throws Exception
     */
    @Test
    public void testIsCallerInRole() throws Exception {
        logger.info("*** testIsCallerInRole ***");

        Context jndi=null;
        try {        	
            	jndi=runAs(null);
            	SecurePing ejb=(SecurePing)jndi.lookup(jndiName);
	        assertFalse("anonomous in admin role", ejb.isCallerInRole("admin"));
	        assertFalse("anonomous in user role", ejb.isCallerInRole("user"));
	        assertFalse("anonomous in internalRole role", ejb.isCallerInRole("internalRole"));
	        //fail("unexpected success with anonymous caller");
        }
        catch (Exception ex) {
            logger.info("anonymous calls to isCallerinRole failed:"+ex);
        } finally {
            jndi.close();
        }

        	jndi=runAs(knownLogin);
        	SecurePing ejb = (SecurePing)jndi.lookup(jndiName);
        assertFalse("known in admin role", ejb.isCallerInRole("admin"));
        assertFalse("known in user role", ejb.isCallerInRole("user"));
        assertFalse("known in internalRole role", ejb.isCallerInRole("internalRole"));
        jndi.close();
        
        	jndi=runAs(userLogin);
        	ejb=(SecurePing)jndi.lookup(jndiName);
        assertFalse("user in admin role", ejb.isCallerInRole("admin"));
        assertTrue("user not in user role", ejb.isCallerInRole("user"));
        assertFalse("user in internalRole role", ejb.isCallerInRole("internalRole"));
        jndi.close();
    
        	jndi=runAs(adminLogin);
        	ejb=(SecurePing)jndi.lookup(jndiName);
        assertTrue("admin not in admin role", ejb.isCallerInRole("admin"));
        assertTrue("admin not in user role", ejb.isCallerInRole("user"));
        assertTrue("user in internalRole role", ejb.isCallerInRole("internalRole"));
        jndi.close();
    }
    
    /**
     * Logs in as each use and calls the pingAll() method which permits 
     * all users to call it. Nobody should get rejected with the exception
     * of the anonymous user if the server requires an authenticated user 
     * identity to connect.
     * @throws Exception
     */
    @Test
    public void testPingAll() throws Exception {
        logger.info("*** testPingAll ***");
        Context jndi=null;
        try {
        	    jndi=runAs(null);
        	    SecurePing ejb=(SecurePing)jndi.lookup(jndiName);
            String result = ejb.pingAll();
            logger.info(result);
            assertEquals("unexpected result for known",
        		"called pingAll, principal=$local, isUser=false, isAdmin=false, isInternalRole=false",
        		result);
        }
        catch (NamingException ex) {
            logger.info("expected error calling pingAll:" + ex);
        }
        catch (Exception ex) {
            logger.error("unexpected exception for anonymous", ex); 
            fail("unexpected exception for anonymous:" + ex); 
        } finally {
            jndi.close();
        }

        try {
        	    jndi = runAs(knownLogin);
        	    SecurePing ejb=(SecurePing)jndi.lookup(jndiName);
            String result = ejb.pingAll();
            logger.info(result);
            assertEquals("unexpected result for known",
        		"called pingAll, principal=known, isUser=false, isAdmin=false, isInternalRole=false",
        		result);
        }
        catch (Exception ex) {
            logger.info("error calling pingAll for known", ex);
            fail("error calling pingAll for known:" +ex);
        } finally {
            jndi.close();
        }

        try {
        	    jndi = runAs(userLogin);
        	    SecurePing ejb=(SecurePing)jndi.lookup(jndiName);
            String result = ejb.pingAll();
            logger.info(result);
            assertEquals("unexpected result for admin",
        		String.format("called pingAll, principal=%s, isUser=true, isAdmin=false, isInternalRole=false", userUser),
        		result);
        }
        catch (Exception ex) {
            logger.info("error calling pingAll for user", ex);
            fail("error calling pingAll for user:" +ex);
        } finally {
            jndi.close();
        }

        try {
        	jndi = runAs(adminLogin);
        	SecurePing ejb=(SecurePing)jndi.lookup(jndiName);
            String result=ejb.pingAll();
            logger.info(result);
            assertEquals("unexpected result for admin",
        		String.format("called pingAll, principal=%s, isUser=true, isAdmin=true, isInternalRole=true", adminUser),
        		result);
        }
        catch (Exception ex) {
            logger.info("error calling pingAll:" + ex, ex);
            fail("error calling pingAll:" +ex);
        } finally {
            jndi.close();
        }
        
//        jndi=runAs(null);
//        SecurePing ejb=(SecurePing)jndi.lookup(jndiName);
//        @SuppressWarnings("unused")
//        String result = ejb.pingAll();
//        logger.info(result);
    }
    
    /**
     * Logs in as each user and attempts to invoke a method that requires
     * the caller to have the user role. This should fail for all but the 
     * user and admin.
     * @throws Exception
     */
    @Test
    public void testPingUser() throws Exception {    	
        logger.info("*** testPingUser ***");
        Context jndi=null;
        try {
        	jndi=runAs(null);
        	SecurePing ejb=(SecurePing)jndi.lookup(jndiName);
            logger.info(ejb.pingUser());
            fail("didn't detect anonymous user");
        }
        //depending on how we are invoking this test, we either get denied 
        //at the JNDI or EJB level -- but at least one will stop us
        catch (NamingException ex) {
            logger.info("expected exception thrown:" + ex);
        }
        catch (EJBAccessException ex) {
            logger.info("expected exception thrown:" + ex);
        }
        catch (Exception ex) {
        	    //fail("unexpected exception type:" + ex);
        }        

        try {
        	jndi=runAs(knownLogin);
        	SecurePing ejb=(SecurePing)jndi.lookup(jndiName);
            logger.info(ejb.pingUser());
            fail("didn't detect known, but non-user");
        }
        catch (EJBAccessException ex) {
            logger.info("expected exception thrown:" + ex);
        }
        catch (Exception ex) {
        	fail("unexpected exception type:" + ex);
        }        
        
        try {
            jndi = runAs(userLogin);
        	SecurePing ejb=(SecurePing)jndi.lookup(jndiName);
            logger.info(ejb.pingUser());
        }
        catch (Exception ex) {
            logger.info("error calling pingUser:" + ex, ex);
            fail("error calling pingUser:" +ex);
        }        

        try {
            jndi = runAs(adminLogin);
        	SecurePing ejb=(SecurePing)jndi.lookup(jndiName);
            logger.info(ejb.pingUser());
        }
        catch (Exception ex) {
            logger.info("error calling pingUser:" + ex, ex);
            fail("error calling pingUser:" +ex);
        }        
    }

    /**
     * Logs in as each of the users and attempts to invoke a method that
     * has been restricted to just admins. All but the admin login should
     * fail.
     * @throws Exception
     */
    @Test
    public void testPingAdmin() throws Exception {
        logger.info("*** testPingAdmin ***");
        Context jndi=null;
        try {
        	jndi=runAs(null);
        	SecurePing ejb=(SecurePing)jndi.lookup(jndiName);
            logger.info(ejb.pingAdmin());
            fail("didn't detect anonymous user");
        }
        catch (NamingException ex) {
            logger.info("expected exception thrown:" + ex);
        }
        catch (EJBAccessException ex) {
            logger.info("expected exception thrown:" + ex);
        }
        catch (Exception ex) {
        	    //fail("unexpected exception type:" + ex);
        }        

        try {
            jndi=runAs(knownLogin);
        	SecurePing ejb=(SecurePing)jndi.lookup(jndiName);
            logger.info(ejb.pingAdmin());
            fail("didn't detect known, but non-admin user");
        }
        catch (EJBAccessException ex) {
            logger.info("expected exception thrown:" + ex);
        }
        catch (Exception ex) {
        	fail("unexpected exception type:" + ex);
        }        
        
        try {
            jndi = runAs(userLogin);
        	SecurePing ejb=(SecurePing)jndi.lookup(jndiName);
            logger.info(ejb.pingAdmin());
            fail("didn't detect non-admin user");
        }
        catch (EJBAccessException ex) {
            logger.info("expected exception thrown:" + ex);
        }
        catch (Exception ex) {
        	fail("unexpected exception type:" + ex);
        }        

        try {
            jndi = runAs(adminLogin);
        	SecurePing ejb=(SecurePing)jndi.lookup(jndiName);
            logger.info(ejb.pingAdmin());
        }
        catch (Exception ex) {
            logger.info("error calling pingAdmin:" + ex, ex);
            fail("error calling pingAdmin:" +ex);
        }
    }

    /**
     * Logs in as each of the users and attempts to invoke a method that 
     * has been excluded by all users to call it. All should fail.
     * @throws Exception
     */
    @Test
    public void testPingExcluded() throws Exception {
        logger.info("*** testPingExcluded ***");
        Context jndi=null;
        try {
        	jndi=runAs(null);
        	SecurePing ejb=(SecurePing)jndi.lookup(jndiName);
            logger.info(ejb.pingExcluded());
            fail("didn't detect excluded");
        }
        catch (NamingException ex) {
            logger.info("expected exception thrown:" + ex);
        }
        catch (EJBAccessException ex) {
            logger.info("expected exception thrown:" + ex);
        }
        catch (Exception ex) {
        	    //fail("unexpected exception type:" + ex);
        }        

        try {
            jndi=runAs(knownLogin);
        	SecurePing ejb=(SecurePing)jndi.lookup(jndiName);
            logger.info(ejb.pingExcluded());
            fail("didn't detect excluded");
        }
        catch (EJBAccessException ex) {
            logger.info("expected exception thrown:" + ex);
        }
        catch (Exception ex) {
        	fail("unexpected exception type:" + ex);
        }        
        
        try {
            jndi = runAs(userLogin);
        	SecurePing ejb=(SecurePing)jndi.lookup(jndiName);
            logger.info(ejb.pingExcluded());
            fail("didn't detect excluded");
        }
        catch (EJBAccessException ex) {
            logger.info("expected exception thrown:" + ex);
        }
        catch (Exception ex) {
        	fail("unexpected exception type:" + ex);
        }        

        try {
            jndi = runAs(adminLogin);
        	SecurePing ejb=(SecurePing)jndi.lookup(jndiName);
            logger.info(ejb.pingExcluded());
            fail("didn't detect excluded");
        }
        catch (EJBAccessException ex) {
            logger.info("expected exception thrown:" + ex);
        }
        catch (Exception ex) {
        	fail("unexpected exception type:" + ex);
        }                
    } 
}
