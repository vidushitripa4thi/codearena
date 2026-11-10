import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getProblemById } from '../api/problems';
import { createSubmission } from '../api/submissions';
import { Problem } from '../types/problem';
import { Language, SubmissionResponse } from '../types/submission';
import { extractErrorMessage } from '../api/client';
import { CodeEditor, STARTER_TEMPLATES } from '../components/problem/CodeEditor';
import { DifficultyBadge, StatusBadge, LanguageBadge } from '../components/common/Badge';
import { Card } from '../components/common/Card';
import { Button } from '../components/common/Button';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { ErrorAlert } from '../components/common/ErrorAlert';
import {
  Clock,
  HardDrive,
  Send,
  AlertTriangle,
  ArrowLeft,
  CheckCircle2,
  Info,
  LogIn,
} from 'lucide-react';

export const ProblemDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const { isAuthenticated } = useAuth();

  const [problem, setProblem] = useState<Problem | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>('');

  const [language, setLanguage] = useState<Language>('JAVA');
  const [code, setCode] = useState<string>(STARTER_TEMPLATES.JAVA);
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [submissionResult, setSubmissionResult] = useState<SubmissionResponse | null>(null);
  const [submitError, setSubmitError] = useState<string>('');

  useEffect(() => {
    let isMounted = true;
    async function loadProblem() {
      if (!id || isNaN(Number(id))) {
        setError('Invalid problem ID');
        setIsLoading(false);
        return;
      }

      try {
        setIsLoading(true);
        setError('');
        const data = await getProblemById(Number(id));
        if (isMounted) {
          setProblem(data);
        }
      } catch (err) {
        if (isMounted) {
          setError(extractErrorMessage(err));
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    loadProblem();

    return () => {
      isMounted = false;
    };
  }, [id]);

  const handleSubmit = async () => {
    if (!problem) return;
    if (!code.trim()) {
      setSubmitError('Source code cannot be blank.');
      return;
    }

    try {
      setIsSubmitting(true);
      setSubmitError('');
      setSubmissionResult(null);

      const res = await createSubmission({
        problemId: problem.id,
        language,
        sourceCode: code,
      });

      setSubmissionResult(res);
    } catch (err) {
      setSubmitError(extractErrorMessage(err));
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isLoading) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-20">
        <LoadingSpinner label="Loading problem specifications..." />
      </div>
    );
  }

  if (error || !problem) {
    return (
      <div className="max-w-3xl mx-auto px-4 py-16 space-y-4">
        <ErrorAlert message={error || 'Problem not found'} />
        <Link to="/problems">
          <Button variant="outline" size="sm" leftIcon={<ArrowLeft className="w-4 h-4" />}>
            Back to Problems Library
          </Button>
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      {/* Breadcrumb / Top Bar */}
      <div className="flex items-center justify-between">
        <Link
          to="/problems"
          className="inline-flex items-center gap-1.5 text-xs text-arena-400 hover:text-white transition-colors"
        >
          <ArrowLeft className="w-3.5 h-3.5" />
          <span>Back to Problems</span>
        </Link>

        <div className="flex items-center gap-2">
          <span className="text-xs text-arena-500 font-mono">ID: #{problem.id}</span>
        </div>
      </div>

      {/* Main split grid: Problem description (left) and Code Editor (right) */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
        {/* Left Column: Problem Details */}
        <div className="lg:col-span-5 space-y-6">
          <Card className="p-6 space-y-6">
            <div>
              <div className="flex items-start justify-between gap-3 mb-3">
                <h1 className="text-xl sm:text-2xl font-bold text-white tracking-tight">
                  {problem.id}. {problem.title}
                </h1>
                <DifficultyBadge difficulty={problem.difficulty} />
              </div>

              {/* Limits */}
              <div className="flex items-center gap-4 text-xs font-mono text-arena-400 pb-4 border-b border-arena-800">
                <span className="flex items-center gap-1.5" title="Execution Time Limit">
                  <Clock className="w-3.5 h-3.5 text-emerald-400" />
                  {problem.timeLimit}s limit
                </span>
                <span className="flex items-center gap-1.5" title="Execution Memory Limit">
                  <HardDrive className="w-3.5 h-3.5 text-blue-400" />
                  {problem.memoryLimit}MB limit
                </span>
              </div>
            </div>

            {/* Description */}
            <div className="space-y-2">
              <h2 className="text-xs uppercase font-mono tracking-wider text-arena-400 font-semibold">
                Description
              </h2>
              <div className="text-sm text-arena-200 leading-relaxed whitespace-pre-line">
                {problem.description}
              </div>
            </div>

            {/* Input & Output Format if available */}
            {(problem.inputFormat || problem.outputFormat) && (
              <div className="space-y-4 pt-4 border-t border-arena-800">
                {problem.inputFormat && (
                  <div className="space-y-1">
                    <h3 className="text-xs uppercase font-mono tracking-wider text-arena-400 font-semibold">
                      Input Format
                    </h3>
                    <p className="text-xs text-arena-300 leading-relaxed font-mono bg-arena-950 p-2.5 rounded-lg border border-arena-800/80">
                      {problem.inputFormat}
                    </p>
                  </div>
                )}
                {problem.outputFormat && (
                  <div className="space-y-1">
                    <h3 className="text-xs uppercase font-mono tracking-wider text-arena-400 font-semibold">
                      Output Format
                    </h3>
                    <p className="text-xs text-arena-300 leading-relaxed font-mono bg-arena-950 p-2.5 rounded-lg border border-arena-800/80">
                      {problem.outputFormat}
                    </p>
                  </div>
                )}
              </div>
            )}

            {/* Constraints if available */}
            {problem.constraints && (
              <div className="space-y-2 pt-4 border-t border-arena-800">
                <h3 className="text-xs uppercase font-mono tracking-wider text-arena-400 font-semibold flex items-center gap-1.5">
                  <AlertTriangle className="w-3.5 h-3.5 text-amber-400" />
                  Constraints
                </h3>
                <div className="text-xs font-mono text-arena-300 bg-arena-950 p-3 rounded-lg border border-arena-800/80 whitespace-pre-wrap">
                  {problem.constraints}
                </div>
              </div>
            )}

            {/* Examples if available */}
            {problem.examples && problem.examples.length > 0 && (
              <div className="space-y-3 pt-4 border-t border-arena-800">
                <h3 className="text-xs uppercase font-mono tracking-wider text-arena-400 font-semibold">
                  Examples
                </h3>
                <div className="space-y-3">
                  {problem.examples.map((ex, idx) => (
                    <div key={ex.id || idx} className="bg-arena-950 rounded-lg p-3 border border-arena-800/80 space-y-2 text-xs font-mono">
                      <span className="text-emerald-400 font-semibold">Example #{idx + 1}</span>
                      <div>
                        <span className="text-arena-500 block text-[11px]">Input:</span>
                        <pre className="text-arena-200 mt-0.5 overflow-x-auto">{ex.input}</pre>
                      </div>
                      <div>
                        <span className="text-arena-500 block text-[11px]">Output:</span>
                        <pre className="text-arena-200 mt-0.5 overflow-x-auto">{ex.output}</pre>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </Card>
        </div>

        {/* Right Column: Code Editor & Submission Panel */}
        <div className="lg:col-span-7 space-y-4">
          <div className="h-[480px]">
            <CodeEditor
              language={language}
              onLanguageChange={setLanguage}
              code={code}
              onCodeChange={setCode}
            />
          </div>

          {/* Action Toolbar */}
          <div className="flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-4 p-4 rounded-xl bg-arena-900 border border-arena-800">
            <div className="flex items-center gap-2 text-xs text-arena-400">
              <span className="font-mono text-white">{language}</span>
              <span>•</span>
              <span>{code.length} characters</span>
            </div>

            {isAuthenticated ? (
              <Button
                variant="primary"
                size="md"
                onClick={handleSubmit}
                isLoading={isSubmitting}
                rightIcon={<Send className="w-4 h-4" />}
              >
                Submit Solution
              </Button>
            ) : (
              <Link to={`/login?redirect=/problems/${problem.id}`}>
                <Button variant="secondary" size="md" leftIcon={<LogIn className="w-4 h-4" />}>
                  Sign in to Submit
                </Button>
              </Link>
            )}
          </div>

          {/* Submit error */}
          {submitError && (
            <ErrorAlert message={submitError} onDismiss={() => setSubmitError('')} />
          )}

          {/* Submission Result / Status Panel */}
          {submissionResult && (
            <Card className="p-5 border-emerald-500/30 bg-arena-900/90 space-y-4 shadow-xl">
              <div className="flex items-start justify-between gap-3">
                <div className="flex items-center gap-2.5">
                  <div className="w-8 h-8 rounded-lg bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 flex items-center justify-center">
                    <CheckCircle2 className="w-4 h-4" />
                  </div>
                  <div>
                    <h4 className="text-sm font-semibold text-white">
                      Submission Recorded Successfully
                    </h4>
                    <span className="text-xs font-mono text-arena-400">
                      Submission ID: #{submissionResult.id}
                    </span>
                  </div>
                </div>

                <StatusBadge status={submissionResult.status} />
              </div>

              {/* Status explanation notice */}
              <div className="flex items-start gap-2.5 p-3 rounded-lg bg-arena-950 border border-arena-800 text-xs text-arena-400">
                <Info className="w-4 h-4 text-amber-400 flex-shrink-0 mt-0.5" />
                <div className="leading-relaxed">
                  <strong className="text-amber-300">Status is {submissionResult.status}:</strong> Your solution
                  was ingested and persisted in PostgreSQL. CodeArena's sandboxed test evaluation runner is
                  currently in development, so solutions remain in queued/pending state without automated execution.
                </div>
              </div>

              <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 text-xs pt-1 font-mono">
                <div className="p-2.5 rounded-lg bg-arena-950 border border-arena-800">
                  <span className="text-arena-500 block text-[10px]">Language</span>
                  <LanguageBadge language={submissionResult.language} size="sm" className="mt-1" />
                </div>
                <div className="p-2.5 rounded-lg bg-arena-950 border border-arena-800">
                  <span className="text-arena-500 block text-[10px]">Submitted By</span>
                  <span className="text-white font-medium mt-1 block">{submissionResult.username}</span>
                </div>
                <div className="p-2.5 rounded-lg bg-arena-950 border border-arena-800">
                  <span className="text-arena-500 block text-[10px]">Timestamp</span>
                  <span className="text-arena-300 mt-1 block truncate">
                    {new Date(submissionResult.submittedAt).toLocaleTimeString()}
                  </span>
                </div>
                <div className="p-2.5 rounded-lg bg-arena-950 border border-arena-800 flex items-center justify-center">
                  <Link
                    to="/submissions"
                    className="text-emerald-400 hover:text-emerald-300 text-xs font-sans font-medium"
                  >
                    View All Submissions &rarr;
                  </Link>
                </div>
              </div>
            </Card>
          )}
        </div>
      </div>
    </div>
  );
};
