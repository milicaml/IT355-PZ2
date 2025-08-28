import axios from 'axios';
import type { AxiosInstance, AxiosResponse } from 'axios';
import type {
  User,
  UserUpdate,
  LoginDto,
  RegisterDto,
  JWTAuthResponse,
  Job,
  JobDto,
  JobResponse,
  Application,
  ApplicationDto,
  Category,
  Skill,
  SkillResponse,
  PaymentType,
  JobFilters
} from '../types';

const API_BASE_URL = 'http://localhost:8080/api';

class ApiService {
  private api: AxiosInstance;

  constructor() {
    this.api = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.api.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('token');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    this.api.interceptors.response.use(
      (response) => response,
      (error) => {
        console.log('API Error:', error.response?.status, error.response?.data);
        console.log('API Error URL:', error.config?.url);
        console.log('API Error Method:', error.config?.method);
        
        if (error.response?.status === 401) {
          const requestUrl = error.config?.url || '';
          
          if (requestUrl.includes('/auth/validate')) {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
          }
        }
        return Promise.reject(error);
      }
    );
  }

  // Authentication
  async login(credentials: LoginDto): Promise<JWTAuthResponse> {
    const response: AxiosResponse<JWTAuthResponse> = await this.api.post('/auth/login', credentials);
    return response.data;
  }

  async register(userData: RegisterDto): Promise<JWTAuthResponse> {
    const response: AxiosResponse<JWTAuthResponse> = await this.api.post('/auth/register', userData);
    return response.data;
  }

  async validateToken(): Promise<boolean> {
    const response: AxiosResponse<boolean> = await this.api.get('/auth/validate');
    return response.data;
  }

  async debugAuth(): Promise<any> {
    const response: AxiosResponse<any> = await this.api.get('/auth/debug');
    return response.data;
  }

  async getTokenInfo(): Promise<any> {
    const response: AxiosResponse<any> = await this.api.get('/auth/token-info');
    return response.data;
  }

  async canApplyForJobs(): Promise<boolean> {
    try {
      const debugInfo = await this.debugAuth();
      return debugInfo.authenticated && debugInfo.userType === 'freelancer';
    } catch (error) {
      console.error('Error checking if user can apply for jobs:', error);
      return false;
    }
  }

  async checkTokenValidity(): Promise<boolean> {
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        console.log('API Service - No token found');
        return false;
      }
      
      const response = await this.api.get('/auth/validate');
      console.log('API Service - Token validation result:', response.data);
      return response.data;
    } catch (error) {
      console.error('API Service - Token validation failed:', error);
      return false;
    }
  }

  clearInvalidToken(): void {
    console.log('API Service - Clearing invalid token');
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    // Redirect to login page
    window.location.href = '/login';
  }

  // User Management
  async getUserProfile(): Promise<User> {
    const response: AxiosResponse<User> = await this.api.get('/users/profile');
    return response.data;
  }

  async getUserById(id: number): Promise<User> {
    const response: AxiosResponse<User> = await this.api.get(`/users/${id}`);
    return response.data;
  }

  async updateUserProfile(userData: UserUpdate): Promise<User> {
    const response: AxiosResponse<User> = await this.api.put('/users/profile', userData);
    return response.data;
  }

  // Job Management
  async getJobs(filters?: JobFilters): Promise<JobResponse> {
    const params = new URLSearchParams();
    if (filters) {
      Object.entries(filters).forEach(([key, value]) => {
        if (value !== undefined && value !== null) {
          params.append(key, value.toString());
        }
      });
    }
    
    const response: AxiosResponse<JobResponse> = await this.api.get(`/jobs?${params.toString()}`);
    return response.data;
  }

  async getJobById(id: number): Promise<Job> {
    const response: AxiosResponse<Job> = await this.api.get(`/jobs/${id}`);
    return response.data;
  }

  async createJob(jobData: JobDto): Promise<Job> {
    const response: AxiosResponse<Job> = await this.api.post('/jobs', jobData);
    return response.data;
  }

  async updateJob(id: number, jobData: JobDto): Promise<Job> {
    const response: AxiosResponse<Job> = await this.api.put(`/jobs/${id}`, jobData);
    return response.data;
  }

  async deleteJob(id: number): Promise<void> {
    await this.api.delete(`/jobs/${id}`);
  }

  async getJobsByUser(userId: number): Promise<Job[]> {
    console.log('API Service - Getting jobs for user ID:', userId);
    try {
      const response: AxiosResponse<Job[]> = await this.api.get(`/users/${userId}/jobs`);
      console.log('API Service - Response received:', response.data);
      return response.data;
    } catch (error) {
      console.error('API Service - Error getting jobs for user:', error);
      throw error;
    }
  }

  // Application Management
  async getUserApplications(): Promise<Application[]> {
    const response: AxiosResponse<Application[]> = await this.api.get('/applications');
    return response.data;
  }

  async getJobApplicationsForEmployer(): Promise<Application[]> {
    const response: AxiosResponse<Application[]> = await this.api.get('/applications/employer');
    return response.data;
  }

  async applyForJob(applicationData: ApplicationDto): Promise<Application> {
    console.log('API Service - Applying for job with data:', applicationData);
    console.log('API Service - Token present:', !!localStorage.getItem('token'));
    
    const response: AxiosResponse<Application> = await this.api.post('/applications', applicationData);
    return response.data;
  }

  async hasAppliedForJob(jobId: number): Promise<boolean> {
    try {
      const applications = await this.getUserApplications();
      return applications.some(app => app.jobId === jobId);
    } catch (error) {
      console.error('Error checking if user has applied for job:', error);
      return false;
    }
  }

  async updateApplicationStatus(id: number, status: string): Promise<Application> {
    const response: AxiosResponse<Application> = await this.api.put(`/applications/${id}/status`, { status });
    return response.data;
  }

  // Skill Management
  async getAllSkills(): Promise<Skill[]> {
    const response: AxiosResponse<Skill[]> = await this.api.get('/skills');
    return response.data;
  }

  async getUserSkills(): Promise<SkillResponse[]> {
    const response: AxiosResponse<SkillResponse[]> = await this.api.get('/users/skills');
    return response.data;
  }

  async addUserSkill(skillId: number, proficiencyLevel: string): Promise<SkillResponse> {
    const response: AxiosResponse<SkillResponse> = await this.api.post('/users/skills', {
      skillId,
      proficiencyLevel
    });
    return response.data;
  }

  async updateUserSkill(skillId: number, proficiencyLevel: string): Promise<SkillResponse> {
    const response: AxiosResponse<SkillResponse> = await this.api.put(`/users/skills/${skillId}`, {
      proficiencyLevel
    });
    return response.data;
  }

  async removeUserSkill(skillId: number): Promise<void> {
    await this.api.delete(`/users/skills/${skillId}`);
  }

  // Get skills for a specific user (for employers viewing freelancer profiles)
  async getUserSkillsById(userId: number): Promise<SkillResponse[]> {
    const response: AxiosResponse<SkillResponse[]> = await this.api.get(`/users/${userId}/skills`);
    return response.data;
  }

  // Category Management
  async getAllCategories(): Promise<Category[]> {
    const response: AxiosResponse<Category[]> = await this.api.get('/categories/');
    return response.data;
  }

  // Payment Types
  async getPaymentTypes(): Promise<PaymentType[]> {
    const response: AxiosResponse<PaymentType[]> = await this.api.get('/payment-types/');
    return response.data;
  }
}

export const apiService = new ApiService();

// Debug function to check authentication status
(window as any).debugAuth = async () => {
  const token = localStorage.getItem('token');
  const user = localStorage.getItem('user');
  
  console.log('=== Authentication Debug ===');
  console.log('Token present:', !!token);
  console.log('User present:', !!user);
  
  if (token) {
    console.log('Token preview:', token.substring(0, 20) + '...');
    
    try {
      const response = await fetch('http://localhost:8080/api/auth/validate', {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      const isValid = await response.json();
      console.log('Token validation response:', response.status, isValid);
      
      if (!isValid) {
        console.error('Token is invalid! Please log in again.');
      }
    } catch (error) {
      console.error('Token validation error:', error);
    }
  } else {
    console.error('No token found! Please log in.');
  }
  
  if (user) {
    console.log('User data:', JSON.parse(user));
  }
};

// Debug function to test profile update
(window as any).testProfileUpdate = async () => {
  const token = localStorage.getItem('token');
  
  if (!token) {
    console.error('No token found! Please log in first.');
    return;
  }
  
  try {
    const testData = {
      fullName: 'Test User',
      email: 'test@example.com',
      phone: '123456789',
      city: 'Test City',
      bio: 'Test bio'
    };
    
    console.log('Testing profile update with data:', testData);
    
    const response = await fetch('http://localhost:8080/api/users/profile', {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(testData)
    });
    
    console.log('Profile update response status:', response.status);
    
    if (response.ok) {
      const result = await response.json();
      console.log('Profile update successful:', result);
    } else {
      const error = await response.text();
      console.error('Profile update failed:', error);
    }
  } catch (error) {
    console.error('Profile update test error:', error);
  }
};

export default apiService;
