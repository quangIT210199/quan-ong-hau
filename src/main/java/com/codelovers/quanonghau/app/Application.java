package com.codelovers.quanonghau.app;

import com.codelovers.quanonghau.cmd.IMonitorExecutor;
import com.codelovers.quanonghau.cmd.MonitorCommand;
import com.codelovers.quanonghau.utils.SingletonGeneral;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootApplication
public class Application {
	private static final long THREAD_SLEEP_TIME = 1000/12;
	private static boolean running = true;
	public static void main(String[] args) {

		// Load properties file
		try {
			SystemConfig.loadAllProperties();
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(-1);
		} catch (IllegalArgumentException e1){
			e1.printStackTrace();
			System.exit(-1);
		}

		// init Singleton
		SingletonGeneral.getInstance();

		// run application
		MainThread mainThread =new MainThread();
		mainThread.setArgs(args);
		mainThread.start();

		// CMD init

		// Open up standard input
		BufferedReader bufferReader = new BufferedReader(new InputStreamReader(System.in));
		String command = null;

		while(running) {
			try {
				//Read the command from the command-line;
				command = bufferReader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
//
			// Parse & process command
			MonitorCommand cmd = MonitorCommand.parse(command);
			if (cmd != null){
				IMonitorExecutor executor = getMonitorCommandExecutor(cmd);
				System.out.print(cmd.getCode());
				if (executor != null){
					executor.setParams(new Object[]{cmd.getParam()});
					executor.execute(cmd);
				} else {
					System.out.println("[Main] Cannot execute command. Input command may be wrong format or params. Try again!");
				}
			} else {
				if (command!=null && !"".equals(command)){
					System.out.println("[Main] Your command may not be correct.");
				}
			}

			try {
				Thread.sleep(THREAD_SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	private static IMonitorExecutor getMonitorCommandExecutor(MonitorCommand cmd){
		IMonitorExecutor executor = null;
		for(int i=0; i<MonitorCommand.STRING_COMMAND.length; i++){
			if (MonitorCommand.STRING_COMMAND[i].equalsIgnoreCase(cmd.getCode())){
				try {
					Class<?> c = Class.forName("com.cls.core.cmd."
							+ MonitorCommand.STRING_COMMAND[i].toUpperCase());
					executor = (IMonitorExecutor) c.newInstance();
					executor.setCode(cmd.getCode());
				} catch (ClassNotFoundException e) {
					return null;
				} catch (InstantiationException e) {
					return null;
				} catch (IllegalAccessException e) {
					return null;
				}
				break;
			}
		}
		return executor;
	}

}
