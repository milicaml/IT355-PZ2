import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Header from './components/Layout/Header';
import LoginForm from './components/Auth/LoginForm';
import RegisterForm from './components/Auth/RegisterForm';
import JobList from './components/Jobs/JobList';
import JobForm from './components/Jobs/JobForm';
import JobDetail from './components/Jobs/JobDetail';
import MyJobs from './components/Jobs/MyJobs';
import Profile from './components/Profile/Profile';
import Applications from './components/Applications/Applications';
import ApplicationForm from './components/Applications/ApplicationForm';
import JobApplications from './components/Applications/JobApplications';
import UserProfile from './components/Profile/UserProfile';
import './App.css';

// Protected Route Component
const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return <div className="loading">Loading...</div>;
  }

  return isAuthenticated ? <>{children}</> : <Navigate to="/login" />;
};

// Main App Content
const AppContent: React.FC = () => {
  const { isAuthenticated } = useAuth();

  return (
    <div className="app">
      <Header />
      <main className="main-content">
        <Routes>
          <Route path="/" element={<Navigate to="/jobs" />} />
          <Route path="/login" element={!isAuthenticated ? <LoginForm /> : <Navigate to="/jobs" />} />
          <Route path="/register" element={!isAuthenticated ? <RegisterForm /> : <Navigate to="/jobs" />} />
          <Route path="/jobs" element={<JobList />} />
          <Route path="/jobs/:id" element={<JobDetail />} />
          <Route 
            path="/jobs/create" 
            element={
              <ProtectedRoute>
                <JobForm />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/jobs/:id/edit" 
            element={
              <ProtectedRoute>
                <JobForm />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/jobs/:id/apply" 
            element={
              <ProtectedRoute>
                <ApplicationForm />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/profile" 
            element={
              <ProtectedRoute>
                <Profile />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/my-jobs" 
            element={
              <ProtectedRoute>
                <MyJobs />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/applications" 
            element={
              <ProtectedRoute>
                <Applications />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/job-applications" 
            element={
              <ProtectedRoute>
                <JobApplications />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/profile/:userId" 
            element={
              <ProtectedRoute>
                <UserProfile />
              </ProtectedRoute>
            } 
          />
          <Route path="*" element={<Navigate to="/jobs" />} />
        </Routes>
      </main>
    </div>
  );
};

// Main App Component
const App: React.FC = () => {
  return (
    <Router>
      <AuthProvider>
        <AppContent />
      </AuthProvider>
    </Router>
  );
};

export default App;
