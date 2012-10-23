package api;
import java.io.Serializable;

public class Result implements Serializable
{
    static final long serialVersionUID = 227L; // Was missing 
	private Object taskReturnValue;
    private long taskRunTime;

    /**
     * 
     * @param taskReturnValue the return value computed from a certain task. Task type dependent.
     * @param taskRunTime the time it took from the computers point of view to compute the task.
     */
    public Result( Object taskReturnValue, long taskRunTime)
    {
        assert taskReturnValue != null;
        assert taskRunTime >= 0;
        this.taskReturnValue = taskReturnValue;
        this.taskRunTime = taskRunTime;
    }

    public Object getTaskReturnValue() { return taskReturnValue; }

    public long getTaskRunTime() { return taskRunTime; }
    
}