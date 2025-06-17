package com.udea.fe.service;

import com.udea.fe.DTO.NotificationDTO;
import com.udea.fe.DTO.SubmissionRequestDTO;
import com.udea.fe.DTO.SubmissionResponseDTO;
import com.udea.fe.entity.*;
import com.udea.fe.repository.SubmissionRepository;
import com.udea.fe.repository.TaskRepository;
import com.udea.fe.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubmissionServiceTest {

    private SubmissionRepository submissionRepository;
    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private NotificationService notificationService;
    private SubmissionService submissionService;

    @BeforeEach
    void setUp() {
        submissionRepository = mock(SubmissionRepository.class);
        taskRepository = mock(TaskRepository.class);
        userRepository = mock(UserRepository.class);
        notificationService = mock(NotificationService.class);
        submissionService = new SubmissionService(submissionRepository, taskRepository, userRepository, notificationService);
    }

    @Test
    void createSubmission_success() {
        SubmissionRequestDTO request = new SubmissionRequestDTO();
        request.setContent("Contenido");
        request.setFileUrl("archivo.pdf");
        request.setTaskId(1L);
        request.setUserId(2L);

        Task task = new Task();
        task.setTaskId(1L);

        User user = new User();
        user.setUserId(2L);
        user.setName("Test");

        Submission saved = new Submission();
        saved.setSubmissionId(10L);
        saved.setContent("Contenido");
        saved.setFileUrl("archivo.pdf");
        saved.setSubmittedAt(LocalDateTime.now());
        saved.setTask(task);
        saved.setUser(user);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(submissionRepository.save(any())).thenReturn(saved);

        SubmissionResponseDTO result = submissionService.createSubmission(request);

        assertEquals(10L, result.getSubmissionId());
        assertEquals("Contenido", result.getContent());
        verify(notificationService).createNotification(any(NotificationDTO.class));
    }

    @Test
    void getAllSubmissions_success() {
        Submission s1 = new Submission();
        s1.setSubmissionId(1L);
        s1.setContent("A");
        s1.setFileUrl("a.pdf");
        s1.setSubmittedAt(LocalDateTime.now());
        Task task = new Task(); task.setTaskId(1L);
        User user = new User(); user.setUserId(2L);
        s1.setTask(task);
        s1.setUser(user);

        when(submissionRepository.findAll()).thenReturn(List.of(s1));

        List<SubmissionResponseDTO> list = submissionService.getAllSubmissions();

        assertEquals(1, list.size());
        assertEquals("A", list.get(0).getContent());
    }

    @Test
    void getSubmissionById_success() {
        Submission s = new Submission();
        s.setSubmissionId(1L);
        s.setContent("Test");
        s.setFileUrl("file.pdf");
        s.setSubmittedAt(LocalDateTime.now());
        Task task = new Task(); task.setTaskId(1L);
        User user = new User(); user.setUserId(2L);
        s.setTask(task);
        s.setUser(user);

        when(submissionRepository.findById(1L)).thenReturn(Optional.of(s));

        SubmissionResponseDTO dto = submissionService.getSubmissionById(1L);
        assertEquals("Test", dto.getContent());
    }

    @Test
    void getSubmissionsByTaskId_asTeacher() {
        Long taskId = 1L;
        String email = "prof@mail.com";

        User teacher = new User();
        teacher.setUserId(99L);
        teacher.setRole(Role.TEACHER);
        teacher.setEmail(email);

        Submission s = new Submission();
        s.setSubmissionId(5L);
        s.setContent("Trabajo");
        Task task = new Task(); task.setTaskId(taskId);
        User user = new User(); user.setUserId(2L); user.setName("Juan");
        s.setTask(task); s.setUser(user);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(teacher));
        when(submissionRepository.findByTask_TaskId(taskId)).thenReturn(List.of(s));

        List<SubmissionResponseDTO> list = submissionService.getSubmissionsByTaskId(taskId, email);
        assertEquals(1, list.size());
        assertEquals("Trabajo", list.get(0).getContent());
        assertEquals("Juan", list.get(0).getUserName());
    }

    @Test
    void getSubmissionsByTaskId_asStudent() {
        Long taskId = 1L;
        String email = "student@mail.com";

        User student = new User();
        student.setUserId(77L);
        student.setRole(Role.STUDENT);
        student.setEmail(email);

        Submission s = new Submission();
        s.setSubmissionId(5L);
        s.setContent("Trabajo Estudiante");
        Task task = new Task(); task.setTaskId(taskId);
        User user = new User(); user.setUserId(77L); user.setName("Maria");
        s.setTask(task); s.setUser(user);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(student));
        when(submissionRepository.findByTask_TaskIdAndUser_UserId(taskId, 77L)).thenReturn(List.of(s));

        List<SubmissionResponseDTO> list = submissionService.getSubmissionsByTaskId(taskId, email);
        assertEquals(1, list.size());
        assertEquals("Trabajo Estudiante", list.get(0).getContent());
        assertEquals("Maria", list.get(0).getUserName());
    }
}