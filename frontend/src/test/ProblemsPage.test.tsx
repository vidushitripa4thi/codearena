import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { ProblemsPage } from '../pages/ProblemsPage';
import * as problemsApi from '../api/problems';
import { PageResponse } from '../types/api';
import { Problem } from '../types/problem';

vi.mock('../api/problems', () => ({
  getProblems: vi.fn(),
  getProblemById: vi.fn(),
}));

describe('ProblemsPage Component Tests', () => {
  const sampleProblems: Problem[] = [
    {
      id: 1,
      title: 'Two Sum',
      description: 'Find two numbers adding up to target.',
      difficulty: 'EASY',
      timeLimit: 1,
      memoryLimit: 256,
      examples: [{ input: '[2,7,11,15], 9', output: '[0,1]' }],
      testCases: [{ id: 1, input: '[2,7,11,15]' }],
    },
    {
      id: 2,
      title: 'Median of Two Sorted Arrays',
      description: 'Find the median of the two sorted arrays.',
      difficulty: 'HARD',
      timeLimit: 2,
      memoryLimit: 512,
      examples: [],
      testCases: [],
    },
  ];

  const mockPageResponse: PageResponse<Problem> = {
    content: sampleProblems,
    page: 0,
    size: 10,
    totalElements: 2,
    totalPages: 1,
    hasNext: false,
    hasPrevious: false,
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders loading state and then displays problem cards', async () => {
    vi.mocked(problemsApi.getProblems).mockResolvedValueOnce(mockPageResponse);

    render(
      <BrowserRouter>
        <ProblemsPage />
      </BrowserRouter>
    );

    expect(screen.getByText(/fetching problems catalog/i)).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.getByText('1. Two Sum')).toBeInTheDocument();
      expect(screen.getByText('2. Median of Two Sorted Arrays')).toBeInTheDocument();
      expect(screen.getAllByText('Easy').length).toBeGreaterThan(0);
      expect(screen.getAllByText('Hard').length).toBeGreaterThan(0);
    });
  });

  it('allows searching problems and updates API call', async () => {
    vi.mocked(problemsApi.getProblems).mockResolvedValue(mockPageResponse);

    render(
      <BrowserRouter>
        <ProblemsPage />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('1. Two Sum')).toBeInTheDocument();
    });

    const searchInput = screen.getByPlaceholderText(/search problems by title/i);
    await userEvent.type(searchInput, 'Median');

    await waitFor(() => {
      expect(problemsApi.getProblems).toHaveBeenCalledWith(
        expect.objectContaining({ search: 'Median' })
      );
    });
  });

  it('filters by difficulty when clicking filter buttons', async () => {
    vi.mocked(problemsApi.getProblems).mockResolvedValue(mockPageResponse);

    render(
      <BrowserRouter>
        <ProblemsPage />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('1. Two Sum')).toBeInTheDocument();
    });

    const easyBtn = screen.getByRole('button', { name: 'Easy' });
    await userEvent.click(easyBtn);

    await waitFor(() => {
      expect(problemsApi.getProblems).toHaveBeenCalledWith(
        expect.objectContaining({ difficulty: 'EASY' })
      );
    });
  });

  it('displays error alert when API call fails', async () => {
    vi.mocked(problemsApi.getProblems).mockRejectedValueOnce(new Error('Network connection timeout'));

    render(
      <BrowserRouter>
        <ProblemsPage />
      </BrowserRouter>
    );

    expect(await screen.findByText(/network connection timeout/i)).toBeInTheDocument();
  });
});
