package com.it355pz2;

import com.it355pz2.dto.RegisterDto;
import com.it355pz2.entity.Application;
import com.it355pz2.entity.Job;
import com.it355pz2.entity.PaymentType;
import com.it355pz2.entity.User;
import com.it355pz2.entity.enums.ApplicationStatus;
import com.it355pz2.entity.enums.JobStatusType;
import com.it355pz2.entity.enums.JobType;
import com.it355pz2.entity.enums.UserType;
import com.it355pz2.repository.ApplicationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.it355pz2.repository.JobRepository;
import com.it355pz2.repository.PaymentTypeRepository;
import com.it355pz2.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApplicationIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PaymentTypeRepository paymentTypeRepository;

    private User user;
    private Job job;
    private PaymentType paymentType;
    private Application testApplication;
    private String jwtToken;

    @BeforeAll
    void cleanAll() {
        applicationRepository.deleteAll();
        userRepository.deleteAll();
        jobRepository.deleteAll();
        paymentTypeRepository.deleteAll();
    }

    @Test
    @Order(1)
    void shouldRegisterUserAndGetToken() throws Exception {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("milica");
        registerDto.setPassword("1234");
        registerDto.setEmail("milica@email.com");
        registerDto.setFullName("Milica");
        registerDto.setPhone("123");
        registerDto.setCity("Niš");

        String payload = objectMapper.writeValueAsString(registerDto);

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        jwtToken = objectMapper.readTree(response).get("accessToken").asText();

        user = userRepository.findByUsername("milica").orElseThrow();
        user = userRepository.save(user);

        paymentType = new PaymentType();
        paymentType.setTitle("Po satu");
        paymentType.setDescription("Plaćanje po satu rada");
        paymentType.setDeleted(false);
        paymentType.setCreatedAt(new Date().toString());
        paymentType.setUpdatedAt(new Date().toString());
        paymentType = paymentTypeRepository.save(paymentType);

        job = new Job();
        job.setTitle("Test Job");
        job.setDescription("Opis posla");
        job.setDateFrom("2022-01-01");
        job.setDateTo("2022-01-01");
        job.setCreateByUser(user);
        job.setStatusType(JobStatusType.open);
        job.setType(JobType.full_time);
        job.setLocation("Novi Sad");
        job.setPaymentAmount(1000);
        job.setPaymentType(paymentType);
        job.setUrgent(0);
        job.setCreatedAt(new Date().toString());
        job.setUpdatedAt(new Date().toString());
        job = jobRepository.save(job);

        testApplication = new Application();
        testApplication.setDescription("Test application");
        testApplication.setStatus(ApplicationStatus.pending);
        testApplication.setDeleted(false);
        testApplication.setCreatedAt(new Date().toString());
        testApplication.setUpdatedAt(new Date().toString());
        testApplication.setUser(user);
        testApplication.setJob(job);
        testApplication = applicationRepository.save(testApplication);
    }

    @Test
    @Order(2)
    void shouldFetchApplicationById() throws Exception {
        mockMvc.perform(get("/api/applications/" + testApplication.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Test application"))
                .andExpect(jsonPath("$.status").value("pending"));
    }

    @Test
    @Order(3)
    void shouldUpdateApplicationStatus() throws Exception {
        mockMvc.perform(patch("/api/applications/" + testApplication.getId())
                        .param("status", "accepted")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("accepted"));
    }

    @Test
    @Order(4)
    void shouldDeleteApplication() throws Exception {
        mockMvc.perform(delete("/api/applications/" + testApplication.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Application deleted"));
    }

}
