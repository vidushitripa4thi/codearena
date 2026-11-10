import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter, MemoryRouter, Routes, Route } from 'react-router-dom';
import { AxiosError, AxiosHeaders } from 'axios';
import { AuthProvider } from '../context/AuthContext';
import { LoginPage } from '../pages/LoginPage';
import { RegisterPage } from '../pages/RegisterPage';
import { ProtectedRoute } from '../components/common/ProtectedRoute';
import * as authApi from '../api/auth';

vi.mock('../api/auth', () => ({
  login: vi.fn(),
  register: vi.fn(),
  getProfile: vi.fn(),
}));

describe('AuthFlow & ProtectedRoute Tests', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();
  });

  it('renders login form and validates required fields', async () => {
    render(
      <BrowserRouter>
        <AuthProvider>
          <LoginPage />
        </AuthProvider>
      </BrowserRouter>
    );

    expect(screen.getByRole('heading', { name: /sign in to codearena/i })).toBeInTheDocument();

    const submitBtn = screen.getByRole('button', { name: /sign in/i });
    await userEvent.click(submitBtn);

    expect(await screen.findByText(/username is required/i)).toBeInTheDocument();
  });

  it('handles successful login and stores token', async () => {
    const mockAuthResponse = {
      token: 'jwt-token-xyz',
      username: 'alice',
      email: 'alice@example.com',
      role: 'USER' as const,
    };
    const mockProfile = {
      id: 1,
      username: 'alice',
      email: 'alice@example.com',
      role: 'USER' as const,
      createdAt: '2026-01-01T00:00:00Z',
    };

    vi.mocked(authApi.login).mockResolvedValueOnce(mockAuthResponse);
    vi.mocked(authApi.getProfile).mockResolvedValueOnce(mockProfile);

    render(
      <BrowserRouter>
        <AuthProvider>
          <LoginPage />
        </AuthProvider>
      </BrowserRouter>
    );

    const usernameInput = screen.getByLabelText(/username/i);
    const passwordInput = screen.getByLabelText(/password/i);

    await userEvent.type(usernameInput, 'alice');
    await userEvent.type(passwordInput, 'password123');

    const submitBtn = screen.getByRole('button', { name: /sign in/i });
    await userEvent.click(submitBtn);

    await waitFor(() => {
      expect(authApi.login).toHaveBeenCalledWith({
        username: 'alice',
        password: 'password123',
      });
      expect(localStorage.getItem('codearena_token')).toBe('jwt-token-xyz');
    });
  });

  it('validates registration form password mismatch', async () => {
    render(
      <BrowserRouter>
        <AuthProvider>
          <RegisterPage />
        </AuthProvider>
      </BrowserRouter>
    );

    await userEvent.type(screen.getByLabelText(/username/i), 'bob_user');
    await userEvent.type(screen.getByLabelText(/email address/i), 'bob@example.com');
    await userEvent.type(screen.getByLabelText(/^password/i), 'password123');
    await userEvent.type(screen.getByLabelText(/confirm password/i), 'differentpassword');

    await userEvent.click(screen.getByRole('button', { name: /create account/i }));

    expect(await screen.findByText(/passwords do not match/i)).toBeInTheDocument();
    expect(authApi.register).not.toHaveBeenCalled();
  });

  it('handles successful registration and stores credentials', async () => {
    const mockAuthResponse = {
      token: 'jwt-reg-token-abc',
      username: 'new_coder',
      email: 'new@example.com',
      role: 'USER' as const,
    };
    const mockProfile = {
      id: 2,
      username: 'new_coder',
      email: 'new@example.com',
      role: 'USER' as const,
      createdAt: '2026-01-01T00:00:00Z',
    };

    vi.mocked(authApi.register).mockResolvedValueOnce(mockAuthResponse);
    vi.mocked(authApi.getProfile).mockResolvedValueOnce(mockProfile);

    render(
      <BrowserRouter>
        <AuthProvider>
          <RegisterPage />
        </AuthProvider>
      </BrowserRouter>
    );

    await userEvent.type(screen.getByLabelText(/username/i), 'new_coder');
    await userEvent.type(screen.getByLabelText(/email address/i), 'new@example.com');
    await userEvent.type(screen.getByLabelText(/^password/i), 'password123');
    await userEvent.type(screen.getByLabelText(/confirm password/i), 'password123');

    await userEvent.click(screen.getByRole('button', { name: /create account/i }));

    await waitFor(() => {
      expect(authApi.register).toHaveBeenCalledWith({
        username: 'new_coder',
        email: 'new@example.com',
        password: 'password123',
      });
      expect(localStorage.getItem('codearena_token')).toBe('jwt-reg-token-abc');
    });
  });

  it('displays backend duplicate error message when registration fails', async () => {
    const error = new AxiosError(
      'Request failed with status code 400',
      'ERR_BAD_REQUEST',
      undefined,
      undefined,
      {
        status: 400,
        statusText: 'Bad Request',
        headers: {},
        config: { headers: new AxiosHeaders() },
        data: {
          type: 'about:blank',
          title: 'Bad Request',
          status: 400,
          detail: 'Username already exists',
          instance: '/api/auth/register',
        },
      }
    );

    vi.mocked(authApi.register).mockRejectedValueOnce(error);

    render(
      <BrowserRouter>
        <AuthProvider>
          <RegisterPage />
        </AuthProvider>
      </BrowserRouter>
    );

    await userEvent.type(screen.getByLabelText(/username/i), 'existing_user');
    await userEvent.type(screen.getByLabelText(/email address/i), 'existing@example.com');
    await userEvent.type(screen.getByLabelText(/^password/i), 'password123');
    await userEvent.type(screen.getByLabelText(/confirm password/i), 'password123');

    await userEvent.click(screen.getByRole('button', { name: /create account/i }));

    expect(await screen.findByText('Username already exists')).toBeInTheDocument();
  });

  it('redirects unauthenticated users in ProtectedRoute', () => {
    render(
      <MemoryRouter initialEntries={['/dashboard']}>
        <AuthProvider>
          <Routes>
            <Route
              path="/dashboard"
              element={
                <ProtectedRoute>
                  <div>Secret Dashboard Content</div>
                </ProtectedRoute>
              }
            />
            <Route path="/login" element={<div>Login Page Redirected</div>} />
          </Routes>
        </AuthProvider>
      </MemoryRouter>
    );

    expect(screen.getByText('Login Page Redirected')).toBeInTheDocument();
    expect(screen.queryByText('Secret Dashboard Content')).not.toBeInTheDocument();
  });
});

