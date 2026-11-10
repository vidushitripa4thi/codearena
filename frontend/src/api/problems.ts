import { apiClient } from './client';
import { Problem, ProblemQueryFilter } from '../types/problem';
import { PageResponse } from '../types/api';

export async function getProblems(filter?: ProblemQueryFilter): Promise<PageResponse<Problem>> {
  const response = await apiClient.get<PageResponse<Problem>>('/problems', {
    params: {
      search: filter?.search || undefined,
      difficulty: filter?.difficulty || undefined,
      page: filter?.page ?? 0,
      size: filter?.size ?? 10,
      sortBy: filter?.sortBy ?? 'createdAt',
      sortDir: filter?.sortDir ?? 'desc',
    },
  });
  return response.data;
}

export async function getProblemById(id: number): Promise<Problem> {
  const response = await apiClient.get<Problem>(`/problems/${id}`);
  return response.data;
}

