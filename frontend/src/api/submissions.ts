import { apiClient } from './client';
import { CreateSubmissionRequest, SubmissionResponse } from '../types/submission';

export async function createSubmission(request: CreateSubmissionRequest): Promise<SubmissionResponse> {
  const response = await apiClient.post<SubmissionResponse>('/submissions', request);
  return response.data;
}

export async function getMySubmissions(): Promise<SubmissionResponse[]> {
  const response = await apiClient.get<SubmissionResponse[]>('/submissions/my');
  return response.data;
}

export async function getSubmissionById(id: number): Promise<SubmissionResponse> {
  const response = await apiClient.get<SubmissionResponse>(`/submissions/${id}`);
  return response.data;
}

