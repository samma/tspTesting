package system;

import api.Result;
import api.Task;

public interface proxy {
	void putResultQueue(Result result); // 
	void putTaskQueue(Task task); // In case of remote exception
	Task takeTaskQueue() throws InterruptedException; 
	void unRegister(ComputerProxyImpl computerProxyImpl);
}
