package task.manager.app.subtask;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.stereotype.Component;
import task.manager.app.epictask.EpicTask;
import task.manager.app.task.TaskStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class SubtaskDeserializer extends StdDeserializer<Subtask> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public SubtaskDeserializer() {
        this(null);
    }

    public SubtaskDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Subtask deserialize(JsonParser jp, DeserializationContext context) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        Subtask subtask = new Subtask();
        Optional.ofNullable(node.get("id")).ifPresent(
                id -> subtask.setId(Long.valueOf(id.asText())));
        Optional.ofNullable(node.get("name")).ifPresent(
                name -> subtask.setName(name.asText()));
        Optional.ofNullable(node.get("description")).ifPresent(
                text -> subtask.setDescription(text.asText()));
        Optional.ofNullable(node.get("status").asText()).ifPresent(
                status -> subtask.setStatus(TaskStatus.valueOf(status)));
        Optional.ofNullable(node.get("epicTaskId")).ifPresent(
                epicId -> {
                    EpicTask epicTask = new EpicTask();
                    epicTask.setId(Long.valueOf(epicId.asText()));
                    subtask.setEpicTask(epicTask);
                });
        Optional.ofNullable(node.get("duration")).ifPresent(
                duration -> subtask.setDuration(LocalTime.parse(duration.asText())));
        Optional.ofNullable(node.get("startTime")).ifPresent(
                startTime -> subtask.setStartTime(LocalDateTime.parse(startTime.asText(), formatter)));
        Optional.ofNullable(node.get("endTime")).ifPresent(
                endTime -> subtask.setEndTime(LocalDateTime.parse(endTime.asText(), formatter)));
        return subtask;
    }
}
