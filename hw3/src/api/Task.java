package api;
/**
 * Interface to execute a task
 * @author torgel
 *
 * @param <T> is the object task that will be executed remotely by a computer
 * @see TspTask for an example of a task
 */

public interface Task<T> {
	T execute();
}
