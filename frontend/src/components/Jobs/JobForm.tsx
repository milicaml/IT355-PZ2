import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useApiCall } from '../../hooks/useApiCall';
import apiService from '../../services/api';
import type { JobDto, PaymentType, Category, Skill } from '../../types';
import { ArrowLeft, Save, X } from 'lucide-react';
import './JobForm.css';

const JobForm: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [paymentTypes, setPaymentTypes] = useState<PaymentType[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);

  
  const [formData, setFormData] = useState<JobDto>({
    title: '',
    description: '',
    dateFrom: '',
    dateTo: '',
    location: '',
    paymentAmount: 0,
    type: 'full_time',
    status: 'open',
    paymentTypeId: 1, // Will be updated dynamically when payment types are loaded
    urgent: 0,
    categoryIds: []
  });

  const isEditing = !!id;

  const { loading, error, executeApiCall, clearError } = useApiCall({
    onSuccess: (data) => {
      if (isEditing) {
        // Convert category names to category IDs
        const categoryIds: number[] = [];
        if (data.categories && data.categories.length > 0) {
          data.categories.forEach((categoryName: string) => {
            const category = categories.find(cat => cat.title === categoryName);
            if (category) {
              categoryIds.push(category.id);
            }
          });
        }
        
        setFormData({
          title: data.title,
          description: data.description,
          dateFrom: data.dateFrom,
          dateTo: data.dateTo,
          location: data.location,
          paymentAmount: data.paymentAmount,
          type: (data.type || data.jobType?.toLowerCase().replace('_', '_')) as 'full_time' | 'part_time' | 'contract' | 'temporary',
          status: (data.status || data.statusType?.toLowerCase()) as 'open' | 'in_progress' | 'completed' | 'cancelled',
          paymentTypeId: data.paymentTypeObj?.id || 1,
          urgent: data.urgent,
          categoryIds: categoryIds
        });
      }
    }
  });

  const { loading: submitLoading, error: submitError, executeApiCall: executeSubmit } = useApiCall({
    onSuccess: () => {
      if (isEditing) {
        navigate('/my-jobs');
      } else {
        navigate('/jobs');
      }
    }
  });

  useEffect(() => {
    fetchFormData();
  }, [id]);

  useEffect(() => {
    if (isEditing && categories.length > 0) {
      fetchJob();
    }
  }, [id, categories]);

  const fetchFormData = async () => {
    try {
      // Fetch payment types
      const paymentTypesData = await apiService.getPaymentTypes();
      setPaymentTypes(paymentTypesData);
      
      // Fetch categories
      const categoriesData = await apiService.getAllCategories();
      // Remove duplicates based on ID
      const uniqueCategories = categoriesData.filter((category, index, self) => 
        index === self.findIndex(c => c.id === category.id)
      );
      setCategories(uniqueCategories);
      

      
      // Set default payment type ID to the first available payment type
      if (paymentTypesData.length > 0 && !isEditing) {
        setFormData(prev => ({
          ...prev,
          paymentTypeId: paymentTypesData[0].id
        }));
      }
    } catch (error: any) {
      console.warn('Failed to load form data from API, using fallback:', error);
      // Fallback payment types if API fails
      const fallbackPaymentTypes = [
        { id: 1, title: 'Hourly' },
        { id: 2, title: 'Daily' },
        { id: 3, title: 'Weekly' },
        { id: 4, title: 'Monthly' },
        { id: 5, title: 'Project-based' }
      ];
      setPaymentTypes(fallbackPaymentTypes);
      
      // Fallback categories
      const fallbackCategories = [
        { id: 1, title: 'Software Development', description: 'Programming and development jobs' },
        { id: 2, title: 'Design', description: 'Graphic design and creative work' },
        { id: 3, title: 'Marketing', description: 'Digital marketing and advertising' }
      ];
      setCategories(fallbackCategories);
      

      
      // Set default payment type ID to fallback
      if (!isEditing) {
        setFormData(prev => ({
          ...prev,
          paymentTypeId: 1
        }));
      }
    }
  };

  const fetchJob = async () => {
    await executeApiCall(
      () => apiService.getJobById(parseInt(id!)),
      () => apiService.getJobById(parseInt(id!))
    );
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'paymentAmount' ? parseFloat(value) || 0 : value
    }));
    clearError();
  };

  const handleCategoryChange = (categoryId: number, checked: boolean) => {
    setFormData(prev => ({
      ...prev,
      categoryIds: checked 
        ? [...(prev.categoryIds || []), categoryId]
        : (prev.categoryIds || []).filter(id => id !== categoryId)
    }));
    clearError();
  };



  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // Check if user is authenticated
    const token = localStorage.getItem('token');
    if (!token) {
      alert('Please log in to create a job');
      return;
    }
    
    if (isEditing) {
      await executeSubmit(
        () => apiService.updateJob(parseInt(id!), formData),
        () => apiService.updateJob(parseInt(id!), formData)
      );
    } else {
      await executeSubmit(
        () => apiService.createJob(formData),
        () => apiService.createJob(formData)
      );
    }
  };

  const handleCancel = () => {
    navigate('/jobs');
  };

  if (loading && isEditing) {
    return (
      <div className="job-form-container">
        <div className="loading">Loading job details...</div>
      </div>
    );
  }

  return (
    <div className="job-form-container">
      <div className="job-form-header">
        <button onClick={handleCancel} className="back-button">
          <ArrowLeft size={20} />
          Back to Jobs
        </button>
        <h1>{isEditing ? 'Edit Job' : 'Create New Job'}</h1>
      </div>

      <div className="job-form-card">
        <form onSubmit={handleSubmit} className="job-form">
          {(error || submitError) && (
            <div className="error-message">
              {error || submitError}
            </div>
          )}

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="title">Job Title *</label>
              <input
                type="text"
                id="title"
                name="title"
                value={formData.title}
                onChange={handleChange}
                required
                className="form-input"
                placeholder="Enter job title"
              />
            </div>

            <div className="form-group">
              <label htmlFor="location">Location *</label>
              <input
                type="text"
                id="location"
                name="location"
                value={formData.location}
                onChange={handleChange}
                required
                className="form-input"
                placeholder="Enter job location"
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="description">Job Description *</label>
            <textarea
              id="description"
              name="description"
              value={formData.description}
              onChange={handleChange}
              required
              className="form-textarea"
              placeholder="Describe the job requirements and responsibilities"
              rows={6}
            />
          </div>

          <div className="form-group">
            <label>Job Categories</label>
            <div className="checkbox-grid">
              {categories.filter(category => category && category.id && category.title).map((category, index) => (
                <label key={`category-${category.id}-${index}`} className="checkbox-item">
                  <input
                    type="checkbox"
                    checked={formData.categoryIds?.includes(category.id) || false}
                    onChange={(e) => handleCategoryChange(category.id, e.target.checked)}
                  />
                  <span>{category.title}</span>
                </label>
              ))}
            </div>
          </div>



          <div className="form-row">
            <div className="form-group">
              <label htmlFor="dateFrom">Start Date *</label>
              <input
                type="date"
                id="dateFrom"
                name="dateFrom"
                value={formData.dateFrom}
                onChange={handleChange}
                required
                className="form-input"
              />
            </div>

            <div className="form-group">
              <label htmlFor="dateTo">End Date *</label>
              <input
                type="date"
                id="dateTo"
                name="dateTo"
                value={formData.dateTo}
                onChange={handleChange}
                required
                className="form-input"
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="type">Job Type *</label>
              <select
                id="type"
                name="type"
                value={formData.type}
                onChange={handleChange}
                required
                className="form-input"
              >
                <option value="full_time">Full Time</option>
                <option value="part_time">Part Time</option>
                <option value="contract">Contract</option>
                <option value="temporary">Temporary</option>
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="paymentTypeId">Payment Type *</label>
              <select
                id="paymentTypeId"
                name="paymentTypeId"
                value={formData.paymentTypeId}
                onChange={handleChange}
                required
                className="form-input"
              >
                {paymentTypes.map(type => (
                  <option key={type.id} value={type.id}>
                    {type.title}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="paymentAmount">Payment Amount *</label>
              <input
                type="number"
                id="paymentAmount"
                name="paymentAmount"
                value={formData.paymentAmount}
                onChange={handleChange}
                required
                min="0"
                step="0.01"
                className="form-input"
                placeholder="Enter payment amount"
              />
            </div>
          </div>

          <div className="form-group">
            <label className="checkbox-label">
              <input
                type="checkbox"
                checked={formData.urgent === 1}
                onChange={(e) => setFormData(prev => ({
                  ...prev,
                  urgent: e.target.checked ? 1 : 0
                }))}
              />
              <span>Mark as Urgent</span>
            </label>
          </div>

          <div className="form-actions">
            <button
              type="button"
              onClick={handleCancel}
              className="btn btn-secondary"
            >
              <X size={16} />
              Cancel
            </button>
            <button
              type="submit"
              disabled={submitLoading}
              className="btn btn-primary"
            >
              <Save size={16} />
              {submitLoading ? 'Saving...' : (isEditing ? 'Update Job' : 'Create Job')}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default JobForm;
