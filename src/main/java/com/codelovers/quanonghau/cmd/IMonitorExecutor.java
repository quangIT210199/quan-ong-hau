package com.codelovers.quanonghau.cmd;

public interface IMonitorExecutor {
	public Object[] getParams();
	public void setParams(Object[] params);
	public String getCode();
	public void setCode(String code);
	public void execute(MonitorCommand cmd);
}
