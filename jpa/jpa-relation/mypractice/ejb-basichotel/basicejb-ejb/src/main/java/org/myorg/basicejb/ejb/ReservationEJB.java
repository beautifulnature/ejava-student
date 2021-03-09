package org.myorg.basicejb.ejb;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReservationEJB implements ReservationLocal, ReservationRemote{
	
	Logger logger = LoggerFactory.getLogger(ReservationEJB.class);
	
	@PostConstruct
    public void init() {
        logger.debug("*** ReservationEJB.init() ***");
    }

    @PreDestroy
    public void destroy() {
        logger.debug("*** ReservationEJB.destroy() ***");
    }

	@Override
	public void ping() {
		logger.debug("ping called");
	}
}
