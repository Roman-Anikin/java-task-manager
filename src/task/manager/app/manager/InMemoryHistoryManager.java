package task.manager.app.manager;

import task.manager.app.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private static Map<Long, TaskNode<Task>> taskNode = new HashMap<>();
    private static TaskLinkedList taskLinkedList = new TaskLinkedList();

    // Вложенный класс связный список
    private static class TaskLinkedList {
        private TaskNode<Task> head;
        private TaskNode<Task> tail;
        private int size = 0;


        // Метод для добавления задачи в историю
        private void linkLast(Task task) {
            final TaskNode<Task> oldTail = tail;
            final TaskNode<Task> newTaskNode = new TaskNode<>(oldTail, task, null);
            tail = newTaskNode;
            if (oldTail == null) {
                head = newTaskNode;
            } else {
                oldTail.next = newTaskNode;
            }
            size++;
            taskNode.put(task.getId(), newTaskNode);
        }

        // Метод для получения списка истории задач
        private List<Task> getTasks() {
            List<Task> taskList = new ArrayList<>();
            TaskNode<Task> taskNode = taskLinkedList.head;
            for (int i = 0; i < size; i++) {
                taskList.add(taskNode.task);
                if (taskNode.next != null) {
                    taskNode = taskNode.next;
                }
            }
            return taskList;
        }
    }

    @Override
    public void add(Task task) {
        if (!taskNode.containsKey(task.getId())) {
            taskLinkedList.linkLast(task);
        }
    }

    @Override
    public void remove(Long id) {
        removeNode(taskNode.get(id));
        taskNode.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return taskLinkedList.getTasks();
    }

    // Метод для удаления узла
    private static void removeNode(TaskNode<Task> taskNode) {
        taskNode.task = null;
        if (taskNode.prev != null) {
            taskNode.prev.next = taskNode.next;
        } else {
            taskLinkedList.head = taskNode.next;
        }
        if (taskNode.next != null) {
            taskNode.next.prev = taskNode.prev;
        } else {
            taskLinkedList.tail = taskNode.prev;
        }
        taskLinkedList.size--;
    }
}
