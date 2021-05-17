package com.codelovers.quanonghau.cmd;

public abstract class AbstractMonitorExecutor implements IMonitorExecutor {
	protected String code;
	protected Object params[];
	
	public String getCode(){
		return code;
	}
	
	public void setCode(String code){
		this.code = code;
	}	
	
	public Object[] getParams() {
		return params;
	}
	
	public void setParams(Object[] params) {
		this.params = params;		
	}
}

