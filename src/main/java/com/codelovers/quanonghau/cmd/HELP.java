package com.codelovers.quanonghau.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HELP extends AbstractMonitorExecutor {
	static final Logger log = LoggerFactory.getLogger(HELP.class);
	public void execute(MonitorCommand cmd) {
		log.info("\n exit: exit program");
	}

}
