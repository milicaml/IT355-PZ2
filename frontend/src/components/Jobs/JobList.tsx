import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import apiService from '../../services/api';
import type { Job, JobFilters } from '../../types';
import { Search, Filter, MapPin, DollarSign, Calendar } from 'lucide-react';
import './Jobs.css';

const JobList: React.FC = () => {
  const { user } = useAuth();
  const [jobs, setJobs] = useState<Job[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filters, setFilters] = useState<JobFilters>({
    page: 0,
    size: 6 
  });
  const [totalPages, setTotalPages] = useState(0);
  const [searchValue, setSearchValue] = useState('');
  const [selectedType, setSelectedType] = useState('');

  useEffect(() => {
    const load = async () => {
      await fetchJobs();
    };
    load().catch((err) => {
      console.error('Error in fetchJobs:', err);
    });
    // eslint-disable-next-line
  }, [filters]);

  const fetchJobs = async () => {
    try {
      setLoading(true);
      const jobsResponse = await apiService.getJobs(filters);
      // Backend now returns paginated response
      setJobs(jobsResponse.content || []);
      setTotalPages(jobsResponse.totalPages || 1);
    } catch (error: any) {
      setError('Failed to load jobs. Please try again.');
      setJobs([]); // Defensive: jobs is always an array
      setTotalPages(0);
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (key: keyof JobFilters, value: string | number | undefined) => {
    setFilters(prev => ({
      ...prev,
      [key]: value,
      page: 0 // Reset to first page when filters change
    }));
  };

  const handleSearch = () => {
    handleFilterChange('search', searchValue || undefined);
  };

  const handleTypeChange = (value: string) => {
    setSelectedType(value);
    handleFilterChange('type', value || undefined);
  };

  const clearFilters = () => {
    setSearchValue('');
    setSelectedType('');
    setFilters({
      page: 0,
      size: 6
    });
  };

  const handlePageChange = (page: number) => {
    setFilters(prev => ({
      ...prev,
      page
    }));
  };

  const formatDate = (dateString: string) => {
    if (!dateString) return '';
    return new Date(dateString).toLocaleDateString();
  };

  const getJobTypeColor = (jobType: string) => {
    const colors = {
      'full_time': '#10b981',
      'part_time': '#f59e0b',
      'contract': '#8b5cf6',
      'temporary': '#ef4444'
    };
    return colors[jobType as keyof typeof colors] || '#6b7280';
  };

  const getStatusColor = (status: string) => {
    const colors = {
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
          <div className="loading">Loading jobs...</div>
        </div>
    );
  }

  return (
      <div className="jobs-container">
        <div className="jobs-header">
          <h1>Available Jobs</h1>
          {user?.userType === 'employer' && (
              <Link to="/jobs/create" className="btn btn-primary">
                Post New Job
              </Link>
          )}
        </div>

        {/* Filters */}
        <div className="filters-section">
          <div className="filters-grid">
            <div className="filter-group">
              <Search size={20} />
              <input
                  type="text"
                  placeholder="Search by title or location..."
                  className="filter-input"
                  value={searchValue}
                  onChange={(e) => setSearchValue(e.target.value)}
                  onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
              />
              <button onClick={handleSearch} className="btn btn-secondary filter-btn">
                Search
              </button>
            </div>

            <div className="filter-group">
              <Filter size={20} />
              <select
                  className="filter-select"
                  onChange={(e) => handleTypeChange(e.target.value)}
                  value={selectedType}
              >
                <option value="">All Job Types</option>
                <option value="full_time">Full Time</option>
                <option value="part_time">Part Time</option>
                <option value="contract">Contract</option>
                <option value="temporary">Temporary</option>
              </select>
            </div>
            <div className="filter-group">
              <button onClick={clearFilters} className="btn btn-danger">
                Clear Filters
              </button>
            </div>
          </div>
        </div>

        {error && (
            <div className="error-message">
              {error}
            </div>
        )}

        {/* Jobs Grid */}
        <div className="jobs-grid">
          {jobs.length > 0 ? jobs.map((job) => (
              <div key={job.id} className="job-card">
                <div className="job-header">
                  <h3 className="job-title">
                    <Link to={`/jobs/${job.id}`}>{job.title}</Link>
                  </h3>
                  {job.urgent === 1 && (
                      <span className="urgent-badge">Urgent</span>
                  )}
                </div>

                <div className="job-company">
                  <span>{job.createdByName || job.createdByUser?.fullName}</span>
                </div>

                <div className="job-details">
                  <div className="job-detail">
                    <MapPin size={16} />
                    <span>{job.location}</span>
                  </div>
                  <div className="job-detail">
                    <DollarSign size={16} />
                    <span>${job.paymentAmount?.toLocaleString?.() ?? job.paymentAmount ?? ''}</span>
                  </div>
                  <div className="job-detail">
                    <Calendar size={16} />
                    <span>{formatDate(job.dateFrom)} - {formatDate(job.dateTo)}</span>
                  </div>
                </div>

                <div className="job-tags">
              <span
                  className="job-type-tag"
                  style={{ backgroundColor: getJobTypeColor(job.jobType || job.type || '') }}
              >
                {(job.jobType || job.type || '')?.replace('_', ' ')}
              </span>
                  <span
                      className="status-tag"
                      style={{ backgroundColor: getStatusColor(job.statusType || job.status || '') }}
                  >
                {job.statusType || job.status}
              </span>
                </div>

                <div className="job-categories">
                  {job.categories && job.categories.length > 0 && job.categories.map((category, index) => (
                      <span key={index} className="category-tag">
                        {category}
                      </span>
                  ))}
                </div>



                <div className="job-actions">
                  <Link to={`/jobs/${job.id}`} className="btn btn-secondary">
                    View Details
                  </Link>
                  {user?.userType === 'freelancer' && (job.statusType || job.status) === 'open' && (
                      <Link to={`/jobs/${job.id}/apply`} className="btn btn-primary">
                        Apply Now
                      </Link>
                  )}
                </div>
              </div>
          )) : (
              <div className="no-jobs">
                <p>No jobs found.</p>
              </div>
          )}
        </div>

        {/* Pagination */}
        {totalPages > 1 && (
            <div className="pagination">
              <button
                  onClick={() => handlePageChange((filters.page || 0) - 1)}
                  disabled={(filters.page || 0) === 0}
                  className="pagination-btn"
              >
                Previous
              </button>

              <div className="pagination-info">
                Page {(filters.page || 0) + 1} of {totalPages}
              </div>

              <button
                  onClick={() => handlePageChange((filters.page || 0) + 1)}
                  disabled={(filters.page || 0) === totalPages - 1}
                  className="pagination-btn"
              >
                Next
              </button>
            </div>
        )}

      </div>
  );
};

export default JobList;