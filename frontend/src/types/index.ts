// User Types
export interface User {
  id: number;
  username: string;
  fullName: string;
  email: string;
  phone: string;
  city: string;
  bio?: string;
  userType: 'employer' | 'freelancer' | 'admin';
  createdAt: string;
  updatedAt: string;
}

export interface UserUpdate {
  fullName: string;
  email: string;
  phone: string;
  city: string;
  bio?: string;
}

// Authentication Types
export interface LoginDto {
  username: string;
  password: string;
}

export interface RegisterDto {
  username: string;
  password: string;
  fullName: string;
  email: string;
  phone: string;
  city: string;
  userType: 'employer' | 'freelancer' | 'admin';
}

export interface JWTAuthResponse {
  accessToken: string;
  tokenType: string;
}

// Job Types
export interface Job {
  id?: number;
  title: string;
  description: string;
  dateFrom: string;
  dateTo: string;
  location: string;
  paymentAmount: number;
  jobType?: 'FULL_TIME' | 'PART_TIME' | 'CONTRACT' | 'TEMPORARY';
  statusType?: 'ACTIVE' | 'COMPLETED' | 'CANCELLED';
  status?: 'open' | 'in_progress' | 'completed' | 'cancelled';
  type?: 'full_time' | 'part_time' | 'contract' | 'temporary';
  urgent: number;
  createdBy?: number;
  createdByName?: string;
  paymentType?: string;
  createdByUser?: {
    id: number;
    username: string;
    fullName: string;
  };
  paymentTypeObj?: {
    id: number;
    title: string;
  };
  categories?: string[];
}

export interface JobDto {
  title: string;
  description: string;
  dateFrom: string;
  dateTo: string;
  location: string;
  paymentAmount: number;
  type: 'full_time' | 'part_time' | 'contract' | 'temporary';
  status?: 'open' | 'in_progress' | 'completed' | 'cancelled';
  paymentTypeId: number;
  urgent: number;
  categoryIds?: number[];
}

export interface JobResponse {
  content: Job[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  size: number;
}

// Application Types
export interface Application {
  id?: number;
  userId: number;
  userFullName: string;
  jobId: number;
  jobTitle: string;
  description: string;
  status: 'pending' | 'accepted' | 'rejected';
  createdAt?: string;
  message?: string;
}

export interface ApplicationDto {
  jobId: number;
  message: string;
}

// Category Types
export interface Category {
  id: number;
  title: string;
  description?: string;
}

export interface CategoriesResponse {
  content: Category[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  size: number;
}

// Skill Types
export interface Skill {
  id: number;
  title: string;
  category?: {
    id: number;
    title: string;
  };
}

export interface SkillResponse {
  id: number;
  title: string;
  proficiencyLevel?: 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'EXPERT';
}

// Payment Type
export interface PaymentType {
  id: number;
  title: string;
  description?: string;
}

// API Response Types
export interface ApiResponse<T> {
  data: T;
  message?: string;
  error?: string;
}

// Filter Types
export interface JobFilters {
  page?: number;
  size?: number;
  search?: string;
  type?: 'full_time' | 'part_time' | 'contract' | 'temporary';
  location?: string;
}
