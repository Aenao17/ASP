import asp.database.DAO;
import asp.model.Task;
import asp.model.Status;
import asp.service.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceTest {

    @Mock
    private DAO dao;

    @InjectMocks
    private Service service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new Service() {
            // inject DAO manually because it is final in original class
            {
                // Replace final DAO field via reflection (for test purposes)
                try {
                    var field = Service.class.getDeclaredField("dao");
                    field.setAccessible(true);
                    field.set(this, dao);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Test
    void getAllTasks_shouldReturnListFromDAO() {
        Task task = new Task();
        when(dao.getAllTasks()).thenReturn(List.of(task));

        List<Task> result = service.getAllTasks();

        assertEquals(1, result.size());
        verify(dao).getAllTasks();
    }

    @Test
    void addTask_shouldCallDAO() {
        Task task = new Task();
        when(dao.addTask(task)).thenReturn(42);

        Integer id = service.addTask(task);

        assertEquals(42, id);
        verify(dao).addTask(task);
    }

    @Test
    void assignUserToTask_shouldParseAndCallDAO() {
        String body = "{\"taskId\":5,\"user\":\"john\"}".replace("\"", ""); // simulate format: taskId:5,user:"john"

        String requestBody = "taskId:5,user:\"john\"";  // manually prepared as expected by the method
        service.assignUserToTask(requestBody);

        verify(dao).assignTaskToUser(5, "john");
    }

    @Test
    void completeTask_shouldCallDAO() {
        service.completeTask(7);
        verify(dao).completeTask(7);
    }

    @Test
    void computePointsForUser_shouldSumPointsOfCompletedTasks() {
        Task t1 = new Task();
        t1.setPoints(10);
        t1.setStatus(Status.COMPLETED);
        t1.setVolunteers(List.of("ana", "john"));

        Task t2 = new Task();
        t2.setPoints(5);
        t2.setStatus(Status.COMPLETED);
        t2.setVolunteers(List.of("john"));

        Task t3 = new Task();
        t3.setPoints(3);
        t3.setStatus(Status.IN_PROGRESS);
        t3.setVolunteers(List.of("john"));

        when(dao.getAllTasks()).thenReturn(Arrays.asList(t1, t2, t3));

        Integer points = service.computePointsForUser("john");

        assertEquals(15, points); // only t1 and t2 are counted
    }

    @Test
    void computePointsForUser_shouldReturnZeroIfNoneMatch() {
        when(dao.getAllTasks()).thenReturn(Collections.emptyList());

        Integer points = service.computePointsForUser("someone");

        assertEquals(0, points);
    }
}
