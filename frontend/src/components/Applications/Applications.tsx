import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import apiService from '../../services/api';
import type { Application } from '../../types';
import { Calendar, Eye } from 'lucide-react';
import './Applications.css';

const Applications: React.FC = () => {
  const [applications, setApplications] = useState<Application[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchApplications();
  }, []);

  const fetchApplications = async () => {
    try {
      setLoading(true);
      const data = await apiService.getUserApplications();
      setApplications(data);
    } catch (error: any) {
      setError('Failed to load applications');
    } finally {
      setLoading(false);
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



  if (loading) {
    return (
      <div className="applications-container">
        <div className="loading">Loading applications...</div>
      </div>
    );
  }

  return (
    <div className="applications-container">
      <div className="applications-header">
        <h1>My Applications</h1>
        <p>Track your job applications and their status</p>
      </div>

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {applications.length === 0 ? (
        <div className="no-applications">
          <p>You haven't applied to any jobs yet.</p>
          <Link to="/jobs" className="btn btn-primary">
            Browse Jobs
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
                  {application.status}
                </span>
              </div>

              <div className="application-details">
                <div className="detail-item">
                  <Calendar size={16} />
                  <span>Status: {application.status}</span>
                </div>
              </div>

              {application.description && (
                <div className="application-message">
                  <h4>Your Message:</h4>
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
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Applications;
