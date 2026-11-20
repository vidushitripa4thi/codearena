export type Language = 'JAVA' | 'PYTHON' | 'CPP' | 'C' | 'JAVASCRIPT';

export type SubmissionStatus =
  | 'PENDING'
  | 'ACCEPTED'
  | 'WRONG_ANSWER'
  | 'TIME_LIMIT_EXCEEDED'
  | 'COMPILATION_ERROR'
  | 'RUNTIME_ERROR'
  | 'INTERNAL_ERROR';

export interface CreateSubmissionRequest {
  problemId: number;
  language: Language;
  sourceCode: string;
}

export interface TestCaseResult {
  caseNumber: number;
  input: string;
  expectedOutput: string;
  actualOutput: string;
  passed: boolean;
  status: SubmissionStatus;
  executionTimeMs: number | null;
  errorMessage?: string | null;
}

export interface ExecutionResponse {
  status: SubmissionStatus;
  compileError?: string | null;
  executionTimeMs: number | null;
  totalTestCases: number;
  passedTestCases: number;
  testCaseResults: TestCaseResult[];
}

export interface SubmissionResponse {
  id: number;
  problemId: number;
  problemTitle: string;
  userId: number;
  username: string;
  language: Language;
  status: SubmissionStatus;
  sourceCode: string;
  executionTime: number | null;
  memoryUsed: number | null;
  submittedAt: string;
  compileError?: string | null;
  totalTestCases?: number | null;
  passedTestCases?: number | null;
  testCaseResults?: TestCaseResult[];
}

