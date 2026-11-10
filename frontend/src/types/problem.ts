export type Difficulty = 'EASY' | 'MEDIUM' | 'HARD';

export interface Example {
  id?: number;
  input: string;
  output: string;
}

export interface TestCase {
  id?: number;
  input: string;
}

export interface Problem {
  id: number;
  title: string;
  description: string;
  difficulty?: Difficulty;
  constraints?: string;
  inputFormat?: string;
  outputFormat?: string;
  timeLimit: number;
  memoryLimit: number;
  createdAt?: string;
  examples: Example[];
  testCases: TestCase[];
}

export interface ProblemQueryFilter {
  search?: string;
  difficulty?: Difficulty | '';
  page?: number;
  size?: number;
  sortBy?: 'createdAt' | 'difficulty' | 'title' | 'id';
  sortDir?: 'asc' | 'desc';
}

