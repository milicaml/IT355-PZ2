import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import apiService from '../../services/api';
import type { Job } from '../../types';
import { Plus, Edit, Trash2, Eye, MapPin, DollarSign, Calendar } from 'lucide-react';
import './Jobs.css';

const MyJobs: React.FC = () => {
  const { user } = useAuth();
  const [jobs, setJobs] = useState<Job[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchMyJobs();
  }, []);

  // Refresh jobs when component comes into focus (e.g., when navigating back from edit)
  useEffect(() => {
    const handleFocus = () => {
      fetchMyJobs();
    };

    window.addEventListener('focus', handleFocus);
    return () => window.removeEventListener('focus', handleFocus);
  }, []);

  const fetchMyJobs = async () => {
    try {
      setLoading(true);
      console.log('MyJobs - Current user:', user);
      if (!user?.id) {
        console.log('MyJobs - User not authenticated, user ID:', user?.id);
        setError('User not authenticated');
        return;
      }
      console.log('MyJobs - Fetching jobs for user ID:', user.id);
      const myJobs = await apiService.getJobsByUser(user.id);
      console.log('MyJobs - Received jobs:', myJobs);
      setJobs(myJobs);
    } catch (error: any) {
      console.error('Error fetching my jobs:', error);
      setError('Failed to load your jobs');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (jobId: number) => {
    if (!window.confirm('Are you sure you want to delete this job?')) {
      return;
    }

    try {
      await apiService.deleteJob(jobId);
      fetchMyJobs(); // Refresh the list
    } catch (error: any) {
      setError('Failed to delete job');
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString();
  };

  const getJobTypeColor = (jobType: string) => {
    const colors = {
      'FULL_TIME': '#10b981',
      'PART_TIME': '#f59e0b',
      'CONTRACT': '#8b5cf6',
      'TEMPORARY': '#ef4444',
      'full_time': '#10b981',
      'part_time': '#f59e0b',
      'contract': '#8b5cf6',
      'temporary': '#ef4444'
    };
    return colors[jobType as keyof typeof colors] || '#6b7280';
  };

  const getStatusColor = (status: string) => {
    const colors = {
      'ACTIVE': '#10b981',
      'COMPLETED': '#6b7280',
      'CANCELLED': '#ef4444',
      'open': '#10b981',
      'in_progress': '#f59e0b',
      'completed': '#6b7280',
      'cancelled': '#ef4444'
    };
    return colors[status as keyof typeof colors] || '#6b7280';
  };

  if (loading) {
    return (
      <div className="jobs-container">
        <div className="loading">Loading your jobs...</div>
      </div>
    );
  }

  return (
    <div className="jobs-container">
      <div className="jobs-header">
        <h1>My Posted Jobs</h1>
        <Link to="/jobs/create" className="btn btn-primary">
          <Plus size={16} />
          Post New Job
        </Link>
      </div>

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {jobs.length === 0 ? (
        <div className="no-jobs">
          <p>You haven't posted any jobs yet.</p>
          <Link to="/jobs/create" className="btn btn-primary">
            Post Your First Job
          </Link>
        </div>
      ) : (
        <div className="jobs-grid">
          {jobs.map((job) => (
            <div key={job.id} className="job-card">
              <div className="job-header">
                <h3 className="job-title">
                  <Link to={`/jobs/${job.id}`}>{job.title}</Link>
                </h3>
                {job.urgent === 1 && (
                  <span className="urgent-badge">Urgent</span>
                )}
              </div>

              <div className="job-details">
                <div className="job-detail">
                  <MapPin size={16} />
                  <span>{job.location}</span>
                </div>
                <div className="job-detail">
                  <DollarSign size={16} />
                  <span>${job.paymentAmount.toLocaleString()}</span>
                </div>
                <div className="job-detail">
                  <Calendar size={16} />
                  <span>{formatDate(job.dateFrom)} - {formatDate(job.dateTo)}</span>
                </div>
              </div>

              <div className="job-tags">
                <span 
                  className="job-type-tag"
                  style={{ backgroundColor: getJobTypeColor((job.jobType || job.type) || '') }}
                >
                  {(job.jobType || job.type || '').replace('_', ' ')}
                </span>
                <span 
                  className="status-tag"
                  style={{ backgroundColor: getStatusColor((job.statusType || job.status) || '') }}
                >
                  {job.statusType || job.status || ''}
                </span>
              </div>

              <div className="job-actions">
                <Link to={`/jobs/${job.id}`} className="btn btn-secondary">
                  <Eye size={16} />
                  View
                </Link>
                <Link to={`/jobs/${job.id}/edit`} className="btn btn-secondary">
                  <Edit size={16} />
                  Edit
                </Link>
                <button 
                  onClick={() => handleDelete(job.id!)}
                  className="btn btn-danger"
                >
                  <Trash2 size={16} />
                  Delete
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default MyJobs;
