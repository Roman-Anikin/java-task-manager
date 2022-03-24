package task.manager.app.manager;

public class TaskNode<T> {

    public TaskNode<T> prev;
    public T task;
    public TaskNode<T> next;

    public TaskNode(TaskNode<T> prev, T task, TaskNode<T> next) {
        this.prev = prev;
        this.task = task;
        this.next = next;
    }
}
