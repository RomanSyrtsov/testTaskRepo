package com.testtask.project;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.testtask.project.controller.UserController;
import com.testtask.project.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void getUsers_ReturnsListOfUsers() throws Exception {
        List<User> userList = new ArrayList<>();

        when(userController.getUsers()).thenReturn(ResponseEntity.ok(userList));

        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

    @Test
    public void createUser_ValidUser_ReturnsCreated() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(LocalDate.of(1990, 1, 1));

        when(userController.createUser(any(User.class))).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("User created successfully."));
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void updateUser_ValidUser_ReturnsOk() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(LocalDate.of(1990, 1, 1));

        when(userController.updateUser(any(Long.class), any(User.class))).thenReturn(ResponseEntity.ok("User updated successfully."));
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void deleteUser_ExistingUser_ReturnsOk() throws Exception {
        when(userController.deleteUser(any(Long.class))).thenReturn(ResponseEntity.ok("User deleted successfully."));

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void searchUsersByBirthDateRange_ValidRange_ReturnsListOfUsers() throws Exception {
        LocalDate fromDate = LocalDate.of(1990, 1, 1);
        LocalDate toDate = LocalDate.of(1995, 1, 1);

        List<User> userList = new ArrayList<>();

        when(userController.searchUsersByBirthDateRange(any(LocalDate.class), any(LocalDate.class))).thenReturn(ResponseEntity.ok(userList));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/search")
                        .param("from", fromDate.toString())
                        .param("to", toDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }
}

