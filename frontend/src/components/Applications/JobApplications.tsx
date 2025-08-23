import React, { useState, useEffect } from 'react';
import { Link, Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import apiService from '../../services/api';
import type { Application } from '../../types';
import { Eye, User, Calendar, CheckCircle, XCircle, Clock } from 'lucide-react';
import './JobApplications.css';

const JobApplications: React.FC = () => {
  const { user } = useAuth();
  const [applications, setApplications] = useState<Application[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Redirect if user is not an employer
  if (!user || user.userType !== 'employer') {
    return <Navigate to="/jobs" replace />;
  }

  useEffect(() => {
    fetchJobApplications();
  }, []);

  const fetchJobApplications = async () => {
    try {
      setLoading(true);
      console.log('Fetching job applications for employer...');
      console.log('Token:', localStorage.getItem('token') ? 'Present' : 'Missing');
      console.log('User:', localStorage.getItem('user'));
      
      // We'll need to create this API endpoint for employers to get applications to their jobs
      const data = await apiService.getJobApplicationsForEmployer();
      setApplications(data);
    } catch (error: any) {
      console.error('Error fetching job applications:', error);
      setError('Failed to load job applications: ' + (error.response?.data || error.message));
    } finally {
      setLoading(false);
    }
  };

  const handleStatusChange = async (applicationId: number, newStatus: 'accepted' | 'rejected' | 'pending') => {
    try {
      if (!applicationId) {
        setError('Invalid application ID');
        return;
      }
      
      await apiService.updateApplicationStatus(applicationId, newStatus);
      // Refresh the applications list
      fetchJobApplications();
    } catch (error: any) {
      setError('Failed to update application status');
    }
  };

  const getStatusColor = (status: string) => {
    const colors = {
      'pending': '#f59e0b',
      'accepted': '#10b981',
      'rejected': '#ef4444'
    };
    return colors[status as keyof typeof colors] || '#6b7280';
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'pending':
        return <Clock size={16} />;
      case 'accepted':
        return <CheckCircle size={16} />;
      case 'rejected':
        return <XCircle size={16} />;
      default:
        return <Clock size={16} />;
    }
  };

  const formatDate = (dateString: string) => {
    if (!dateString) {
      return 'N/A';
    }
    
    try {
      const date = new Date(dateString);
      if (isNaN(date.getTime())) {
        return 'N/A';
      }
      return date.toLocaleDateString();
    } catch (error) {
      console.error('Error formatting date:', dateString, error);
      return 'N/A';
    }
  };

  if (loading) {
    return (
      <div className="job-applications-container">
        <div className="loading">Loading job applications...</div>
      </div>
    );
  }

  return (
    <div className="job-applications-container">
      <div className="job-applications-header">
        <h1>Applications to My Jobs</h1>
        <p>Review and manage applications from freelancers</p>
      </div>

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {applications.length === 0 ? (
        <div className="no-applications">
          <p>No applications received yet.</p>
          <Link to="/my-jobs" className="btn btn-primary">
            View My Jobs
          </Link>
        </div>
      ) : (
        <div className="applications-grid">
          {applications.map((application, index) => (
            <div key={application.id || index} className="application-card">
              <div className="application-header">
                <h3 className="job-title">
                  <Link to={`/jobs/${application.jobId}`}>
                    {application.jobTitle}
                  </Link>
                </h3>
                <span 
                  className="status-badge"
                  style={{ backgroundColor: getStatusColor(application.status) }}
                >
                  {getStatusIcon(application.status)}
                  {application.status}
                </span>
              </div>

              <div className="applicant-info">
                <div className="applicant-header">
                  <User size={16} />
                  <h4>Applicant: {application.userFullName}</h4>
                </div>
                <div className="applicant-actions">
                  <Link 
                    to={`/profile/${application.userId}`} 
                    className="btn btn-secondary btn-sm"
                  >
                    <Eye size={14} />
                    View Profile
                  </Link>
                </div>
              </div>

              <div className="application-details">
                <div className="detail-item">
                  <Calendar size={16} />
                  <span>Applied on {application.createdAt ? formatDate(application.createdAt) : 'N/A'}</span>
                </div>
              </div>

              {application.description && (
                <div className="application-message">
                  <h4>Applicant's Message:</h4>
                  <p>{application.description}</p>
                </div>
              )}

              <div className="application-actions">
                <Link 
                  to={`/jobs/${application.jobId}`} 
                  className="btn btn-secondary"
                >
                  <Eye size={16} />
                  View Job
                </Link>
                
                {application.status === 'pending' && (
                  <div className="status-actions">
                    <button 
                      onClick={() => handleStatusChange(application.id!, 'accepted')}
                      className="btn btn-success"
                    >
                      <CheckCircle size={16} />
                      Accept
                    </button>
                    <button 
                      onClick={() => handleStatusChange(application.id!, 'rejected')}
                      className="btn btn-danger"
                    >
                      <XCircle size={16} />
                      Reject
                    </button>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default JobApplications;


