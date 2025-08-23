import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import apiService from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import type { Job, ApplicationDto } from '../../types';
import { ArrowLeft, Send } from 'lucide-react';
import './Applications.css';

const ApplicationForm: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [job, setJob] = useState<Job | null>(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  
  const [formData, setFormData] = useState<ApplicationDto>({
    jobId: 0,
    message: ''
  });

  useEffect(() => {
    if (id) {
      fetchJob();
    }
  }, [id]);

  // Check if user is a freelancer
  const isFreelancer = user?.userType === 'freelancer';

  const fetchJob = async () => {
    try {
      setLoading(true);
      const jobData = await apiService.getJobById(parseInt(id!));
      setJob(jobData);
      setFormData(prev => ({
        ...prev,
        jobId: jobData.id || 0
      }));
    } catch (error: any) {
      setError('Failed to load job details');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setFormData(prev => ({
      ...prev,
      message: e.target.value
    }));
    setError('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setError('');

    try {
      await apiService.applyForJob(formData);
      navigate(`/jobs/${id}?applied=success`);
    } catch (error: any) {
      console.error('Application submission error:', error);
      
      if (error.response?.status === 409) {
        setError('You have already applied for this job. You cannot apply multiple times.');
      } else if (error.response?.status === 403) {
        // Check if it's an authentication issue
        if (error.response?.data?.message?.includes('Access denied') || 
            error.response?.data?.message?.includes('freelancer')) {
          setError('Access denied. Only freelancers can apply for jobs. Please check your account type.');
        } else {
          // Likely an authentication issue
          setError('Authentication failed. Please log in again.');
          apiService.clearInvalidToken();
        }
      } else if (error.response?.status === 401) {
        setError('Authentication required. Please log in again.');
        apiService.clearInvalidToken();
      } else if (error.response?.data?.message) {
        setError(error.response.data.message);
      } else {
        setError('Failed to submit application. Please try again.');
      }
    } finally {
      setSubmitting(false);
    }
  };

  const handleCancel = () => {
    navigate(`/jobs/${id}`);
  };

  if (loading) {
    return (
      <div className="application-form-container">
        <div className="loading">Loading job details...</div>
      </div>
    );
  }

  if (error || !job) {
    return (
      <div className="application-form-container">
        <div className="error-message">
          {error || 'Job not found'}
        </div>
        <button onClick={() => navigate('/jobs')} className="btn btn-secondary">
          <ArrowLeft size={16} />
          Back to Jobs
        </button>
      </div>
    );
  }

  // Check if user is a freelancer
  if (!isFreelancer) {
    return (
      <div className="application-form-container">
        <div className="error-message">
          <h2>Access Denied</h2>
          <p>Only freelancers can apply for jobs. Your account type is: <strong>{user?.userType || 'Unknown'}</strong></p>
          <p>If you believe this is an error, please contact support or update your account type.</p>
        </div>
        <button onClick={() => navigate('/jobs')} className="btn btn-secondary">
          <ArrowLeft size={16} />
          Back to Jobs
        </button>
      </div>
    );
  }

  return (
    <div className="application-form-container">
      <div className="application-form-header">
        <button onClick={handleCancel} className="back-button">
          <ArrowLeft size={20} />
          Back to Job
        </button>
        <h1>Apply for Job</h1>
      </div>

      <div className="application-form-content">
        <div className="job-summary">
          <h2>{job.title}</h2>
          <div className="job-summary-details">
            <p><strong>Location:</strong> {job.location}</p>
            <p><strong>Payment:</strong> ${job.paymentAmount.toLocaleString()}</p>
            <p><strong>Type:</strong> {(job.jobType || job.type || '').replace('_', ' ')}</p>
            <p><strong>Posted by:</strong> {job.createdByUser?.fullName || job.createdByName}</p>
          </div>
        </div>

        <div className="application-form-card">
          <form onSubmit={handleSubmit} className="application-form">
            {error && (
              <div className="error-message">
                {error}
              </div>
            )}

            <div className="form-group">
              <label htmlFor="message">
                Cover Letter / Application Message *
              </label>
              <textarea
                id="message"
                name="message"
                value={formData.message}
                onChange={handleChange}
                required
                className="form-textarea"
                rows={8}
                placeholder="Introduce yourself and explain why you're interested in this position. Include your relevant experience and skills..."
              />
              <div className="char-count">
                {formData.message.length} characters
              </div>
            </div>

            <div className="form-actions">
              <button
                type="button"
                onClick={handleCancel}
                className="btn btn-secondary"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={submitting || !formData.message.trim()}
                className="btn btn-primary"
              >
                <Send size={16} />
                {submitting ? 'Submitting...' : 'Submit Application'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default ApplicationForm;
