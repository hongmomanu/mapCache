package mapcatche.business.impl;

public enum TaskState {
	Begin(1),end(2),fail(0),init(-1);
	private  int state;
	private TaskState(int state){
		this.state=state;
	}
	public  int getState() {  
        return  this.state;  
    } 

}
