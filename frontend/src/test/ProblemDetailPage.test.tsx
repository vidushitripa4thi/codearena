import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from '../context/AuthContext';
import { ProblemDetailPage } from '../pages/ProblemDetailPage';
import * as problemsApi from '../api/problems';
import * as submissionsApi from '../api/submissions';
import * as authApi from '../api/auth';
import { Problem } from '../types/problem';
import { SubmissionResponse } from '../types/submission';

vi.mock('../api/problems', () => ({
  getProblemById: vi.fn(),
  getProblems: vi.fn(),
}));

vi.mock('../api/submissions', () => ({
  createSubmission: vi.fn(),
  getMySubmissions: vi.fn(),
  getSubmissionById: vi.fn(),
}));

vi.mock('../api/auth', () => ({
  login: vi.fn(),
  register: vi.fn(),
  getProfile: vi.fn(),
}));

describe('ProblemDetailPage Component Tests', () => {
  const sampleProblem: Problem = {
    id: 42,
    title: 'Valid Palindrome',
    description: 'Given a string s, return true if it is a palindrome.',
    difficulty: 'EASY',
    constraints: '1 <= s.length <= 2 * 10^5',
    inputFormat: 'A single string s.',
    outputFormat: 'Boolean true or false.',
    timeLimit: 1,
    memoryLimit: 256,
    examples: [
      { id: 1, input: '"race a car"', output: 'false' },
    ],
    testCases: [{ id: 1, input: '"race a car"' }],
  };

  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();
  });

  it('renders problem specification details', async () => {
    vi.mocked(problemsApi.getProblemById).mockResolvedValueOnce(sampleProblem);

    render(
      <MemoryRouter initialEntries={['/problems/42']}>
        <AuthProvider>
          <Routes>
            <Route path="/problems/:id" element={<ProblemDetailPage />} />
          </Routes>
        </AuthProvider>
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('42. Valid Palindrome')).toBeInTheDocument();
      expect(screen.getByText(/given a string s, return true/i)).toBeInTheDocument();
      expect(screen.getByText('1s limit')).toBeInTheDocument();
      expect(screen.getByText('256MB limit')).toBeInTheDocument();
      expect(screen.getByText('A single string s.')).toBeInTheDocument();
      expect(screen.getByText('"race a car"')).toBeInTheDocument();
    });
  });

  it('prompts unauthenticated user to sign in before submitting', async () => {
    vi.mocked(problemsApi.getProblemById).mockResolvedValueOnce(sampleProblem);

    render(
      <MemoryRouter initialEntries={['/problems/42']}>
        <AuthProvider>
          <Routes>
            <Route path="/problems/:id" element={<ProblemDetailPage />} />
          </Routes>
        </AuthProvider>
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('42. Valid Palindrome')).toBeInTheDocument();
    });

    expect(screen.getByRole('button', { name: /sign in to submit/i })).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /submit solution/i })).not.toBeInTheDocument();
  });

  it('submits solution when authenticated and renders PENDING intake status', async () => {
    vi.mocked(problemsApi.getProblemById).mockResolvedValueOnce(sampleProblem);
    vi.mocked(authApi.getProfile).mockResolvedValueOnce({
      id: 5,
      username: 'coder_alice',
      email: 'alice@example.com',
      role: 'USER',
      createdAt: '2026-01-01T00:00:00Z',
    });

    localStorage.setItem('codearena_token', 'mock-token');

    const mockSubmissionResponse: SubmissionResponse = {
      id: 99,
      problemId: 42,
      problemTitle: 'Valid Palindrome',
      userId: 5,
      username: 'coder_alice',
      language: 'JAVA',
      status: 'PENDING',
      sourceCode: 'public class Solution {}',
      executionTime: null,
      memoryUsed: null,
      submittedAt: '2026-11-10T12:00:00Z',
    };

    vi.mocked(submissionsApi.createSubmission).mockResolvedValueOnce(mockSubmissionResponse);

    render(
      <MemoryRouter initialEntries={['/problems/42']}>
        <AuthProvider>
          <Routes>
            <Route path="/problems/:id" element={<ProblemDetailPage />} />
          </Routes>
        </AuthProvider>
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /submit solution/i })).toBeInTheDocument();
    });

    const submitBtn = screen.getByRole('button', { name: /submit solution/i });
    await userEvent.click(submitBtn);

    await waitFor(() => {
      expect(submissionsApi.createSubmission).toHaveBeenCalledWith(
        expect.objectContaining({
          problemId: 42,
          language: 'JAVA',
        })
      );
      expect(screen.getByText(/submission recorded successfully/i)).toBeInTheDocument();
      expect(screen.getByText(/submission id: #99/i)).toBeInTheDocument();
      expect(screen.getByText(/status is pending/i)).toBeInTheDocument();
    });
  });

  it('renders multiple examples with inputs and outputs as well as constraints', async () => {
    const multiExampleProblem: Problem = {
      ...sampleProblem,
      title: 'Two Sum Pairs',
      constraints: '2 <= nums.length <= 10^4\n-10^9 <= target <= 10^9',
      examples: [
        { id: 1, input: 'nums = [2,7,11,15], target = 9', output: '[0,1]' },
        { id: 2, input: 'nums = [3,2,4], target = 6', output: '[1,2]' },
      ],
    };

    vi.mocked(problemsApi.getProblemById).mockResolvedValueOnce(multiExampleProblem);

    render(
      <MemoryRouter initialEntries={['/problems/42']}>
        <AuthProvider>
          <Routes>
            <Route path="/problems/:id" element={<ProblemDetailPage />} />
          </Routes>
        </AuthProvider>
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('42. Two Sum Pairs')).toBeInTheDocument();
      expect(screen.getByText(/2 <= nums\.length <= 10\^4/i)).toBeInTheDocument();
      expect(screen.getByText('nums = [2,7,11,15], target = 9')).toBeInTheDocument();
      expect(screen.getByText('[0,1]')).toBeInTheDocument();
      expect(screen.getByText('nums = [3,2,4], target = 6')).toBeInTheDocument();
      expect(screen.getByText('[1,2]')).toBeInTheDocument();
    });
  });
});

