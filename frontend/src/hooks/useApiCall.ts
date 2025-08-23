import { useState, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

interface UseApiCallOptions {
  onSuccess?: (data: any) => void;
  onError?: (error: any) => void;
  showError?: boolean;
}

export const useApiCall = (options: UseApiCallOptions = {}) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const { refreshAuth } = useAuth();
  const navigate = useNavigate();

  const executeApiCall = useCallback(async <T>(
    apiCall: () => Promise<T>,
    retryApiCall?: () => Promise<T>
  ): Promise<T | null> => {
    setLoading(true);
    setError('');

    try {
      const result = await apiCall();
      options.onSuccess?.(result);
      return result;
    } catch (error: any) {
      if (error.response?.status === 401) {
        // Try to refresh authentication
        const isAuthenticated = await refreshAuth();
        if (!isAuthenticated) {
          navigate('/login');
          return null;
        }
        
        // Retry the request if retry function is provided
        if (retryApiCall) {
          try {
            const retryResult = await retryApiCall();
            options.onSuccess?.(retryResult);
            return retryResult;
          } catch (retryError: any) {
            const errorMessage = retryError.response?.data?.message || 'Request failed. Please try again.';
            setError(errorMessage);
            options.onError?.(retryError);
            return null;
          }
        }
      }
      
      const errorMessage = error.response?.data?.message || 'Request failed';
      setError(errorMessage);
      options.onError?.(error);
      return null;
    } finally {
      setLoading(false);
    }
  }, [refreshAuth, navigate, options]);

  const clearError = useCallback(() => {
    setError('');
  }, []);

  return {
    loading,
    error,
    executeApiCall,
    clearError,
  };
};

