package task.manager.app.model;

import java.time.LocalDateTime;

public class Task implements Comparable<Task> {

    private String name;
    private String description;
    private TaskStatus status;
    private Long id;
    private long duration;
    private LocalDateTime startTime;


    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return name.equals(task.name) && description.equals(task.description)
                && status.equals(task.status) && id.equals(task.id);
    }

    @Override
    public int compareTo(Task o) {
        if (this.getId().equals(o.getId())) {
            return 0;
        }
        if (o.getStartTime() == null) {
            return -1;
        }
        if (this.getStartTime().isBefore(o.getStartTime())) {
            return -1;
        } else if (this.getStartTime().isAfter(o.getStartTime())) {
            return 1;
        } else {
            return 0;
        }
    }
}