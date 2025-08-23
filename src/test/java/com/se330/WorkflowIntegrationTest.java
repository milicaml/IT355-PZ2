package com.se330;

import com.it355pz2.dto.ApplicationDto;
import com.it355pz2.dto.JobDto;
import com.it355pz2.dto.JobResponse;
import com.it355pz2.dto.LoginDto;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.it355pz2.Main.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WorkflowIntegrationTest {
    
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

    private User employer;
    private User freelancer;
    private Job testJob;
    private PaymentType paymentType;
    private String employerToken;
    private String freelancerToken;
    
    // Test data tracking for cleanup
    private String testEmployerUsername;
    private String testFreelancerUsername;
    private Long testJobId;
    private Long testPaymentTypeId;

    @BeforeAll
    void setupTestData() {
        String testSuffix = UUID.randomUUID().toString().substring(0, 8);
        testEmployerUsername = "employer_test_" + testSuffix;
        testFreelancerUsername = "freelancer_test_" + testSuffix;
        
        List<PaymentType> existingPaymentTypes = paymentTypeRepository.findAll();
        if (existingPaymentTypes.isEmpty()) {
            paymentType = new PaymentType();
            paymentType.setTitle("Test Payment Type");
            paymentType.setDescription("Payment type for testing");
            paymentType.setDeleted(false);
            paymentType.setCreatedAt(new Date().toString());
            paymentType.setUpdatedAt(new Date().toString());
            paymentType = paymentTypeRepository.save(paymentType);
        } else {
            paymentType = existingPaymentTypes.get(0);
        }
        testPaymentTypeId = paymentType.getId();
    }

    @AfterAll
    void cleanupTestData() {
        try {
            List<Application> testApplications = applicationRepository.findAll();
            for (Application app : testApplications) {
                if (app.getUser() != null && 
                    (app.getUser().getUsername().startsWith("employer_test_") || 
                     app.getUser().getUsername().startsWith("freelancer_test_"))) {
                    applicationRepository.delete(app);
                }
            }
            
            List<Job> testJobs = jobRepository.findAll();
            for (Job job : testJobs) {
                if (job.getCreateByUser() != null && 
                    job.getCreateByUser().getUsername().startsWith("employer_test_")) {
                    jobRepository.delete(job);
                }
            }
            
            List<User> testUsers = userRepository.findAll();
            for (User user : testUsers) {
                if (user.getUsername().startsWith("employer_test_") || 
                    user.getUsername().startsWith("freelancer_test_")) {
                    userRepository.delete(user);
                }
            }
        } catch (Exception e) {
            System.err.println("Error during test cleanup: " + e.getMessage());
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test slučaj 1: Kompletan workflow registracije i objavljivanja posla")
    void testCompleteRegistrationAndJobPostingWorkflow() throws Exception {
        // 1. Registracija poslodavca
        RegisterDto employerRegisterDto = new RegisterDto();
        employerRegisterDto.setUsername(testEmployerUsername);
        employerRegisterDto.setPassword("password123");
        employerRegisterDto.setEmail("employer_" + UUID.randomUUID().toString().substring(0, 8) + "@test.com");
        employerRegisterDto.setFullName("Test Employer");
        employerRegisterDto.setPhone("123456789");
        employerRegisterDto.setCity("Beograd");
        employerRegisterDto.setUserType(UserType.employer);

        String employerPayload = objectMapper.writeValueAsString(employerRegisterDto);

        MvcResult employerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employerPayload))
                .andExpect(status().isOk())
                .andReturn();

        String employerResponse = employerResult.getResponse().getContentAsString();
        employerToken = objectMapper.readTree(employerResponse).get("accessToken").asText();

        // Verifikacija da je korisnik kreiran
        employer = userRepository.findByUsername(testEmployerUsername).orElseThrow();
        Assertions.assertEquals(UserType.employer, employer.getUserType());

        // 2. Objavljivanje posla
        JobDto jobDto = new JobDto();
        jobDto.setTitle("Test Developer Position");
        jobDto.setDescription("Tražimo iskusnog developera za React projekat");
        jobDto.setDateFrom("2024-02-01");
        jobDto.setDateTo("2024-06-01");
        jobDto.setLocation("Beograd");
        jobDto.setPaymentAmount(5000.0);
        jobDto.setType(JobType.full_time);
        jobDto.setPaymentTypeId(testPaymentTypeId);
        jobDto.setUrgent(1);
        jobDto.setStatus(JobStatusType.open);

        String jobPayload = objectMapper.writeValueAsString(jobDto);

        MvcResult jobResult = mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jobPayload)
                        .header("Authorization", "Bearer " + employerToken))
                .andExpect(status().isOk())
                .andReturn();

        String jobResponse = jobResult.getResponse().getContentAsString();
        Long jobId = objectMapper.readTree(jobResponse).get("id").asLong();
        testJobId = jobId;
        
        // Get the actual Job entity from repository
        testJob = jobRepository.findById(jobId).orElseThrow();

        // Verifikacija da je posao kreiran
        Assertions.assertEquals("Test Developer Position", testJob.getTitle());
        Assertions.assertEquals(JobStatusType.open, testJob.getStatusType());
        Assertions.assertEquals(1, testJob.getUrgent());
        Assertions.assertEquals(employer.getId(), testJob.getCreateByUser().getId());

        // 3. Verifikacija da se posao može pronaći
        mockMvc.perform(get("/api/jobs/" + testJob.getId())
                        .header("Authorization", "Bearer " + employerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Developer Position"))
                .andExpect(jsonPath("$.urgent").value(1));
    }

    @Test
    @Order(2)
    @DisplayName("Test slučaj 2: Kompletan workflow prijave na posao")
    void testCompleteJobApplicationWorkflow() throws Exception {
        // Ensure testJob is not null
        Assertions.assertNotNull(testJob, "testJob should not be null");
        
        // 1. Registracija freelancera
        RegisterDto freelancerRegisterDto = new RegisterDto();
        freelancerRegisterDto.setUsername(testFreelancerUsername);
        freelancerRegisterDto.setPassword("password123");
        freelancerRegisterDto.setEmail("freelancer_" + UUID.randomUUID().toString().substring(0, 8) + "@test.com");
        freelancerRegisterDto.setFullName("Test Freelancer");
        freelancerRegisterDto.setPhone("987654321");
        freelancerRegisterDto.setCity("Novi Sad");
        freelancerRegisterDto.setUserType(UserType.freelancer);

        String freelancerPayload = objectMapper.writeValueAsString(freelancerRegisterDto);

        MvcResult freelancerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(freelancerPayload))
                .andExpect(status().isOk())
                .andReturn();

        String freelancerResponse = freelancerResult.getResponse().getContentAsString();
        freelancerToken = objectMapper.readTree(freelancerResponse).get("accessToken").asText();

        // Verifikacija da je korisnik kreiran
        freelancer = userRepository.findByUsername(testFreelancerUsername).orElseThrow();
        Assertions.assertEquals(UserType.freelancer, freelancer.getUserType());

        // 2. Pregled dostupnih poslova
        mockMvc.perform(get("/api/jobs")
                        .header("Authorization", "Bearer " + freelancerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").exists());

        // 3. Prijava na posao
        ApplicationDto applicationDto = new ApplicationDto();
        applicationDto.setJobId(testJob.getId());
        applicationDto.setMessage("Interesovan sam za ovu poziciju. Imam 3 godine iskustva u React-u.");

        String applicationPayload = objectMapper.writeValueAsString(applicationDto);

        MvcResult applicationResult = mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(applicationPayload)
                        .header("Authorization", "Bearer " + freelancerToken))
                .andExpect(status().isOk())
                .andReturn();

        String applicationResponse = applicationResult.getResponse().getContentAsString();
        Long applicationId = objectMapper.readTree(applicationResponse).get("id").asLong();
        Application createdApplication = applicationRepository.findById(applicationId).orElseThrow();

        // Verifikacija da je prijava kreirana
        Assertions.assertEquals(ApplicationStatus.pending, createdApplication.getStatus());
        Assertions.assertEquals(freelancer.getId(), createdApplication.getUser().getId());
        Assertions.assertEquals(testJob.getId(), createdApplication.getJob().getId());

        // 4. Pregled prijava za poslodavca
        mockMvc.perform(get("/api/applications/employer")
                        .header("Authorization", "Bearer " + employerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("pending"));

        // 5. Ažuriranje statusa prijave
        String statusUpdateJson = "{\"status\":\"accepted\"}";
        
        mockMvc.perform(put("/api/applications/" + createdApplication.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusUpdateJson)
                        .header("Authorization", "Bearer " + employerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("accepted"));

        // 6. Pregled statusa prijave za freelancera
        mockMvc.perform(get("/api/applications")
                        .header("Authorization", "Bearer " + freelancerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("accepted"));
    }

    @Test
    @Order(3)
    @DisplayName("Test slučaj 3: Integracija autentifikacije i autorizacije")
    void testAuthenticationAndAuthorizationIntegration() throws Exception {
        // 1. Test neuspešne autentifikacije sa pogrešnim kredencijalima
        LoginDto invalidLoginDto = new LoginDto();
        invalidLoginDto.setUsername("nonexistent");
        invalidLoginDto.setPassword("wrongpassword");

        String invalidLoginPayload = objectMapper.writeValueAsString(invalidLoginDto);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidLoginPayload))
                .andExpect(status().isForbidden());

        // 2. Test uspešne autentifikacije
        LoginDto validLoginDto = new LoginDto();
        validLoginDto.setUsername(testEmployerUsername);
        validLoginDto.setPassword("password123");

        String validLoginPayload = objectMapper.writeValueAsString(validLoginDto);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginPayload))
                .andExpect(status().isOk())
                .andReturn();

        String loginResponse = loginResult.getResponse().getContentAsString();
        String newToken = objectMapper.readTree(loginResponse).get("accessToken").asText();
        Assertions.assertNotNull(newToken);

        // 3. Test autorizacije - samo poslodavci mogu da kreiraju poslove
        JobDto unauthorizedJobDto = new JobDto();
        unauthorizedJobDto.setTitle("Unauthorized Job");
        unauthorizedJobDto.setDescription("This should fail");
        unauthorizedJobDto.setDateFrom("2024-02-01");
        unauthorizedJobDto.setDateTo("2024-06-01");
        unauthorizedJobDto.setLocation("Beograd");
        unauthorizedJobDto.setPaymentAmount(1000.0);
        unauthorizedJobDto.setType(JobType.part_time);
        unauthorizedJobDto.setPaymentTypeId(testPaymentTypeId);
        unauthorizedJobDto.setUrgent(0);
        unauthorizedJobDto.setStatus(JobStatusType.open);

        String unauthorizedJobPayload = objectMapper.writeValueAsString(unauthorizedJobDto);

        // Pokušaj kreiranja posla sa freelancer token-om (treba da ne uspe)
        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(unauthorizedJobPayload)
                        .header("Authorization", "Bearer " + freelancerToken))
                .andExpect(status().isForbidden());

        // 4. Test validacije JWT token-a
        mockMvc.perform(get("/api/auth/validate")
                        .header("Authorization", "Bearer " + newToken))
                .andExpect(status().isOk());

        // 5. Test pristupa javnim resursima bez token-a
        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isOk());

        // 6. Test pristupa sa nevažećim token-om
        mockMvc.perform(get("/api/jobs")
                        .header("Authorization", "Bearer invalid_token"))
                .andExpect(status().isOk());

        // 7. Test autorizacije za ažuriranje posla - samo vlasnik može da ažurira
        JobDto updateJobDto = new JobDto();
        updateJobDto.setTitle("Updated Job Title");
        updateJobDto.setDescription("Updated description");
        updateJobDto.setDateFrom("2024-02-01");
        updateJobDto.setDateTo("2024-06-01");
        updateJobDto.setLocation("Beograd");
        updateJobDto.setPaymentAmount(6000.0);
        updateJobDto.setType(JobType.full_time);
        updateJobDto.setPaymentTypeId(testPaymentTypeId);
        updateJobDto.setUrgent(1);
        updateJobDto.setStatus(JobStatusType.open);

        String updateJobPayload = objectMapper.writeValueAsString(updateJobDto);

        // Ažuriranje sa vlasnikom (treba da uspe)
        mockMvc.perform(put("/api/jobs/" + testJob.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJobPayload)
                        .header("Authorization", "Bearer " + newToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Job Title"));

        // Pokušaj ažuriranja sa drugim korisnikom (treba da ne uspe)
        mockMvc.perform(put("/api/jobs/" + testJob.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJobPayload)
                        .header("Authorization", "Bearer " + freelancerToken))
                .andExpect(status().isForbidden());
    }


}
