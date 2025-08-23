import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useApiCall } from '../../hooks/useApiCall';
import apiService from '../../services/api';
import type { Job } from '../../types';
import { ArrowLeft, MapPin, DollarSign, Calendar, Clock, Edit, Trash2, User } from 'lucide-react';
import './JobDetail.css';

const JobDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [job, setJob] = useState<Job | null>(null);
  const [hasApplied, setHasApplied] = useState<boolean>(false);
  const [checkingApplication, setCheckingApplication] = useState<boolean>(false);
  const [showSuccessMessage, setShowSuccessMessage] = useState<boolean>(false);

  const { loading, error, executeApiCall } = useApiCall({
    onSuccess: (data) => {
      setJob(data);
    }
  });

  const { executeApiCall: executeDelete } = useApiCall({
    onSuccess: () => {
      navigate('/jobs');
    }
  });

  const fetchJob = async () => {
    if (!id) {
      return;
    }
    
    await executeApiCall(
      () => apiService.getJobById(parseInt(id)),
      () => apiService.getJobById(parseInt(id))
    );
  };

  const checkIfApplied = async () => {
    if (!id || !user) {
      return;
    }
    
    try {
      setCheckingApplication(true);
      const applied = await apiService.hasAppliedForJob(parseInt(id));
      setHasApplied(applied);
    } catch (error) {
      console.error('Error checking if user has applied:', error);
    } finally {
      setCheckingApplication(false);
    }
  };

  useEffect(() => {
    if (id) {
      fetchJob();
    }
  }, [id]);

  useEffect(() => {
    if (job && user) {
      checkIfApplied();
    }
  }, [job, user]);

  useEffect(() => {
    // Check if user just successfully applied (from URL params)
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('applied') === 'success') {
      setShowSuccessMessage(true);
      setHasApplied(true);
      // Remove the parameter from URL
      window.history.replaceState({}, document.title, window.location.pathname);
      
      // Hide success message after 5 seconds
      setTimeout(() => {
        setShowSuccessMessage(false);
      }, 5000);
    }
  }, []);

  const handleDelete = async () => {
    if (!job || !window.confirm('Are you sure you want to delete this job?')) {
      return;
    }

    await executeDelete(
      () => apiService.deleteJob(job.id!),
      () => apiService.deleteJob(job.id!)
    );
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
      <div className="job-detail-container">
        <div className="loading">Loading job details...</div>
      </div>
    );
  }

  if (error || !job) {
    return (
      <div className="job-detail-container">
        <div className="error-message">
          {error || 'Job not found'}
        </div>
        <Link to="/jobs" className="btn btn-secondary">
          <ArrowLeft size={16} />
          Back to Jobs
        </Link>
      </div>
    );
  }

  const isOwner = user?.id === job.createdBy;
  const canApply = (user?.userType === 'freelancer') && 
                   (job.statusType === 'ACTIVE' || job.status === 'open') && 
                   !hasApplied;
  
  // Debug information
  console.log('JobDetail - User:', user);
  console.log('JobDetail - User type:', user?.userType);
  console.log('JobDetail - Job status:', job?.statusType || job?.status);
  console.log('JobDetail - Has applied:', hasApplied);
  console.log('JobDetail - Can apply:', canApply);

  return (
    <div className="job-detail-container">
      <div className="job-detail-header">
        <button onClick={() => navigate('/jobs')} className="back-button">
          <ArrowLeft size={20} />
          Back to Jobs
        </button>
        
        {showSuccessMessage && (
          <div className="success-message">
            <p>✅ Application submitted successfully!</p>
          </div>
        )}

        <div className="job-actions">
          {isOwner && (
            <>
              <Link to={`/jobs/${job.id}/edit`} className="btn btn-secondary">
                <Edit size={16} />
                Edit
              </Link>
              <button onClick={handleDelete} className="btn btn-danger">
                <Trash2 size={16} />
                Delete
              </button>
            </>
          )}
          {canApply && (
            <Link to={`/jobs/${job.id}/apply`} className="btn btn-primary">
              Apply Now
            </Link>
          )}
          {!canApply && user && (
            <div className="apply-info">
              {checkingApplication ? (
                <p className="apply-message">Checking application status...</p>
              ) : user.userType !== 'freelancer' ? (
                <p className="apply-message">Only freelancers can apply for jobs. Your account type is: <strong>{user.userType}</strong></p>
              ) : hasApplied ? (
                <p className="apply-message">You have already applied for this job.</p>
              ) : (
                <p className="apply-message">This job is not currently accepting applications.</p>
              )}
            </div>
          )}
        </div>
      </div>

      <div className="job-detail-content">
        <div className="job-main-info">
          <div className="job-title-section">
            <h1>{job.title}</h1>
            {job.urgent === 1 && (
              <span className="urgent-badge">Urgent</span>
            )}
          </div>

          <div className="job-company">
            <User size={16} />
            <span>{job.createdByName || job.createdByUser?.fullName}</span>
          </div>

          <div className="job-meta">
            <div className="job-meta-item">
              <MapPin size={16} />
              <span>{job.location}</span>
            </div>
            <div className="job-meta-item">
              <DollarSign size={16} />
              <span>${job.paymentAmount.toLocaleString()}</span>
            </div>
            <div className="job-meta-item">
              <Calendar size={16} />
              <span>{formatDate(job.dateFrom)} - {formatDate(job.dateTo)}</span>
            </div>
            <div className="job-meta-item">
              <Clock size={16} />
              <span>{job.paymentType || job.paymentTypeObj?.title}</span>
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
        </div>

        <div className="job-description">
          <h2>Job Description</h2>
          <div className="description-content">
            {job.description.split('\n').map((paragraph, index) => (
              <p key={index}>{paragraph}</p>
            ))}
          </div>
        </div>

        <div className="job-categories">
          <h2>Categories</h2>
          <div className="categories-list">
            {job.categories && job.categories.length > 0 ? (
              job.categories.map((category, index) => (
                <span key={index} className="category-tag">
                  {category}
                </span>
              ))
            ) : (
              <span className="no-categories">No categories specified</span>
            )}
          </div>
        </div>



        <div className="job-footer">
          <div className="job-created-info">
            <p>Posted by {job.createdByName || job.createdByUser?.fullName}</p>
            <p>Created on {formatDate(job.dateFrom)}</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default JobDetail;
