import axios, { AxiosError } from 'axios';
import { ProblemDetail } from '../types/api';

const baseURL = import.meta.env.VITE_API_BASE_URL || '/api';

export const apiClient = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to attach JWT token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('codearena_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor to handle token expiry / unauthenticated state
apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ProblemDetail>) => {
    if (error.response?.status === 401) {
      // Clear expired credentials
      localStorage.removeItem('codearena_token');
      localStorage.removeItem('codearena_user');
      // Dispatch custom event so AuthContext can sync state immediately
      window.dispatchEvent(new Event('codearena_auth_expired'));
    }
    return Promise.reject(error);
  }
);

/**
 * Extracts a human-readable error message from backend ProblemDetail or Axios error.
 */
export function extractErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data;
    if (typeof data === 'object' && data !== null) {
      const problemDetail = data as ProblemDetail;
      if (problemDetail.detail) {
        return problemDetail.detail;
      }
      if (problemDetail.title) {
        return problemDetail.title;
      }
    } else if (typeof data === 'string' && data.trim()) {
      return data;
    }
    if (error.response?.status === 403) {
      return 'You do not have permission to access this resource.';
    }
    if (error.response?.status === 404) {
      return 'The requested resource was not found.';
    }
    if (error.response?.status === 500) {
      return 'Backend server error (HTTP 500). Please check that the Spring Boot server and database are healthy.';
    }
    if (error.code === 'ERR_NETWORK' || !error.response) {
      return 'Unable to reach the CodeArena server. Please check your network connection and ensure the backend is running.';
    }
    return error.message || 'Network request failed';
  }
  if (error instanceof Error) {
    return error.message;
  }
  return 'An unexpected error occurred';
}

