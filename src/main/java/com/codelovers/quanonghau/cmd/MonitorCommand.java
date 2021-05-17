package com.codelovers.quanonghau.cmd;

import java.util.StringTokenizer;

public class MonitorCommand extends AbstractQueueMessage {
	private final static String DILIMITER = " ";
	private static final String CR_LF = "\r\n";
		
	public final static String EXIT = "exit";
	public final static String START_AGENT = "start_agent";
	public final static String STOP_AGENT = "stop_agent";
	public final static String RESTART_AGENT = "restart_agent";
	public final static String KILL = "kill";
	public final static String VIEW = "view";
	public final static String HELP = "help";
	public final static String LOG = "log";
	public final static String QUEUE = "queue";
	public final static String SHUTDOWN = "shutdown";
	public final static String RELOAD = "reload";
	public final static String EXPORT = "export";	
	public final static String EDIT = "edit";
	public final static String CONNECT = "open";
	public final static String DISCONNECT = "close";
	public final static String KILLALL = "killall";
	public final static String LIST = "list";

	public final static String STRING_COMMAND[] = {EXIT, START_AGENT, STOP_AGENT, 
													KILL, VIEW,	HELP, LOG, 
													QUEUE, SHUTDOWN, RELOAD, EXPORT, 
												    EDIT, RESTART_AGENT,
													CONNECT, DISCONNECT, KILLALL, LIST};
	protected boolean remote = false;
	protected String param;	
	
	public boolean isRemote() {
		return remote;
	}
	public void setRemote(boolean remote) {
		this.remote = remote;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	
	private MonitorCommand(){
	}
	private MonitorCommand(String code, String param) {
		super();
		this.code = code;
		this.param = param;
	}
	
	public String toString(){
		StringBuffer buff = new StringBuffer("Usage: [command] [option]").append(CR_LF);
		for(int i=0; i< STRING_COMMAND.length; i++){
			buff.append(STRING_COMMAND[i]).append(CR_LF);
		}
		return buff.toString();
	}
	
	public boolean equals(String code){
		if (this.code == null) return false;
		return (this.code.equalsIgnoreCase(code));		
	}
	
	public static MonitorCommand parse(String command){
		if ((command == null) || ("".equals(command))){
			return null;
		}
		StringTokenizer tokenizer = new StringTokenizer(command, DILIMITER);
		int tokenNum = tokenizer.countTokens(); 
		if ((tokenNum <= 0) || (tokenNum > 2)){
			return null;
		}
		MonitorCommand cmd = null;
		
		String tmp[] = new String[tokenNum];
		for(int i=0; i < tokenNum; i++){
			tmp[i] = tokenizer.nextToken().trim();
		}
		
		for(int i=0; i< STRING_COMMAND.length; i++){
			if ((tmp[0].equalsIgnoreCase(STRING_COMMAND[i])) || 
									(tmp[0].equals(Integer.valueOf(i+1).toString())) ){
				cmd = new MonitorCommand();				
				cmd.setCode(STRING_COMMAND[i]);
				if (tokenNum >=2){
					if (tmp[1] != null){
						cmd.setParam(tmp[1]);
					}
				}
				break;
			}
		}

		return cmd;
	}
}

