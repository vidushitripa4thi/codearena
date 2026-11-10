import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { SubmissionsPage } from '../pages/SubmissionsPage';
import * as submissionsApi from '../api/submissions';
import { SubmissionResponse } from '../types/submission';

vi.mock('../api/submissions', () => ({
  getMySubmissions: vi.fn(),
  createSubmission: vi.fn(),
  getSubmissionById: vi.fn(),
}));

describe('SubmissionsPage Component Tests', () => {
  const sampleSubmissions: SubmissionResponse[] = [
    {
      id: 101,
      problemId: 1,
      problemTitle: 'Two Sum',
      userId: 5,
      username: 'alice',
      language: 'PYTHON',
      status: 'PENDING',
      sourceCode: 'def solve(): pass',
      executionTime: null,
      memoryUsed: null,
      submittedAt: '2026-11-10T12:00:00Z',
    },
    {
      id: 102,
      problemId: 2,
      problemTitle: 'Valid Palindrome',
      userId: 5,
      username: 'alice',
      language: 'JAVA',
      status: 'ACCEPTED',
      sourceCode: 'public class Solution { // solved }',
      executionTime: 12,
      memoryUsed: 34,
      submittedAt: '2026-11-10T13:00:00Z',
    },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders submissions list table with problem links and status badges', async () => {
    vi.mocked(submissionsApi.getMySubmissions).mockResolvedValueOnce(sampleSubmissions);

    render(
      <BrowserRouter>
        <SubmissionsPage />
      </BrowserRouter>
    );

    expect(screen.getByText(/fetching your submission history/i)).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.getByText('#101')).toBeInTheDocument();
      expect(screen.getByText('Two Sum')).toBeInTheDocument();
      expect(screen.getByText('#102')).toBeInTheDocument();
      expect(screen.getByText('Valid Palindrome')).toBeInTheDocument();
      expect(screen.getByText('Pending (Queued)')).toBeInTheDocument();
      expect(screen.getByText('Accepted')).toBeInTheDocument();
      expect(screen.getByText('12ms')).toBeInTheDocument();
      expect(screen.getByText('34MB')).toBeInTheDocument();
    });
  });

  it('opens and closes code viewer modal when clicking View Code', async () => {
    vi.mocked(submissionsApi.getMySubmissions).mockResolvedValueOnce(sampleSubmissions);

    render(
      <BrowserRouter>
        <SubmissionsPage />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('#101')).toBeInTheDocument();
    });

    const viewCodeButtons = screen.getAllByRole('button', { name: /view code/i });
    await userEvent.click(viewCodeButtons[0]);

    // Modal should appear
    expect(screen.getByText(/submission #101 — two sum/i)).toBeInTheDocument();
    expect(screen.getByText('def solve(): pass')).toBeInTheDocument();

    // Click Close
    const closeBtn = screen.getByRole('button', { name: /^close$/i });
    await userEvent.click(closeBtn);

    expect(screen.queryByText('def solve(): pass')).not.toBeInTheDocument();
  });

  it('renders friendly empty state when no submissions exist', async () => {
    vi.mocked(submissionsApi.getMySubmissions).mockResolvedValueOnce([]);

    render(
      <BrowserRouter>
        <SubmissionsPage />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText(/no submissions yet/i)).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /browse problems library/i })).toBeInTheDocument();
    });
  });
});
