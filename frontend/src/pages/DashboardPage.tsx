import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getProblems } from '../api/problems';
import { getMySubmissions } from '../api/submissions';
import { Problem } from '../types/problem';
import { SubmissionResponse } from '../types/submission';
import { extractErrorMessage } from '../api/client';
import { Card } from '../components/common/Card';
import { Button } from '../components/common/Button';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { ErrorAlert } from '../components/common/ErrorAlert';
import { StatusBadge, LanguageBadge } from '../components/common/Badge';
import {
  Code2,
  CheckCircle,
  Clock,
  ArrowRight,
  Terminal,
  FileCode,
  User,
  Info,
} from 'lucide-react';

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();

  const [totalProblems, setTotalProblems] = useState<number | null>(null);
  const [recentProblems, setRecentProblems] = useState<Problem[]>([]);
  const [recentSubmissions, setRecentSubmissions] = useState<SubmissionResponse[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>('');

  useEffect(() => {
    let isMounted = true;

    async function loadDashboardData() {
      try {
        setIsLoading(true);
        setError('');

        // Fetch problems summary
        const problemsData = await getProblems({ page: 0, size: 5, sortBy: 'createdAt', sortDir: 'desc' });

        // Fetch user's submissions
        const submissionsData = await getMySubmissions();

        if (isMounted) {
          setTotalProblems(problemsData.totalElements);
          setRecentProblems(problemsData.content);
          setRecentSubmissions(submissionsData.slice(0, 5));
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

    loadDashboardData();

    return () => {
      isMounted = false;
    };
  }, []);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-8">
      {/* Welcome Banner */}
      <div className="relative overflow-hidden rounded-2xl bg-gradient-to-r from-arena-900 to-arena-850 border border-arena-800 p-6 sm:p-8">
        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div className="space-y-2">
            <div className="inline-flex items-center gap-2 text-xs font-mono text-emerald-400 bg-emerald-500/10 border border-emerald-500/20 px-2.5 py-1 rounded-md">
              <Terminal className="w-3.5 h-3.5" />
              <span>Workspace Active</span>
            </div>
            <h1 className="text-2xl sm:text-3xl font-bold text-white tracking-tight">
              Welcome back, <span className="text-emerald-400">{user?.username}</span>
            </h1>
            <p className="text-arena-400 text-sm max-w-xl">
              Track your algorithmic challenges, review solution code, and submit new answers.
            </p>
          </div>

          <div className="flex items-center gap-3">
            <Link to="/problems">
              <Button variant="primary" size="md" rightIcon={<ArrowRight className="w-4 h-4" />}>
                Browse Problems
              </Button>
            </Link>
          </div>
        </div>
      </div>

      {error && <ErrorAlert message={error} onDismiss={() => setError('')} />}

      {/* Info notice about intake pipeline */}
      <div className="flex items-start gap-3 p-4 rounded-xl bg-arena-900/90 border border-arena-800 text-xs text-arena-400">
        <Info className="w-4 h-4 text-emerald-400 flex-shrink-0 mt-0.5" />
        <div className="leading-relaxed">
          <span className="text-white font-medium">Submission Intake Mode:</span> Solutions submitted to
          CodeArena are securely persisted in the database with status <span className="text-amber-300 font-mono">PENDING</span>.
          Automated sandboxed code execution and real-time judging are planned in subsequent architectural phases.
        </div>
      </div>

      {isLoading ? (
        <LoadingSpinner label="Loading dashboard summary..." />
      ) : (
        <>
          {/* Quick Stats Grid */}
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-5">
            <Card className="p-5 flex items-center justify-between">
              <div className="space-y-1">
                <span className="text-xs text-arena-400 uppercase tracking-wider font-mono">Available Problems</span>
                <p className="text-2xl font-bold text-white">
                  {totalProblems !== null ? totalProblems : '—'}
                </p>
              </div>
              <div className="w-11 h-11 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 flex items-center justify-center">
                <Code2 className="w-5 h-5" />
              </div>
            </Card>

            <Card className="p-5 flex items-center justify-between">
              <div className="space-y-1">
                <span className="text-xs text-arena-400 uppercase tracking-wider font-mono">Your Submissions</span>
                <p className="text-2xl font-bold text-white">
                  {recentSubmissions.length > 0 ? recentSubmissions.length : 0}
                </p>
              </div>
              <div className="w-11 h-11 rounded-xl bg-blue-500/10 border border-blue-500/20 text-blue-400 flex items-center justify-center">
                <FileCode className="w-5 h-5" />
              </div>
            </Card>

            <Card className="p-5 flex items-center justify-between">
              <div className="space-y-1">
                <span className="text-xs text-arena-400 uppercase tracking-wider font-mono">Account Role</span>
                <p className="text-xl font-bold text-white capitalize">
                  {user?.role === 'ADMIN' ? 'Administrator' : 'Standard Coder'}
                </p>
              </div>
              <div className="w-11 h-11 rounded-xl bg-purple-500/10 border border-purple-500/20 text-purple-400 flex items-center justify-center">
                <User className="w-5 h-5" />
              </div>
            </Card>
          </div>

          {/* Two-column layout: Recent Problems & Recent Submissions */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            {/* Recent Problems Section */}
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <h2 className="text-base font-semibold text-white flex items-center gap-2">
                  <Code2 className="w-4 h-4 text-emerald-400" />
                  Problems Catalog
                </h2>
                <Link to="/problems" className="text-xs text-emerald-400 hover:text-emerald-300 font-medium">
                  View All &rarr;
                </Link>
              </div>

              <div className="space-y-3">
                {recentProblems.length === 0 ? (
                  <Card className="p-8 text-center text-arena-400 text-sm">
                    No problems found in the database.
                  </Card>
                ) : (
                  recentProblems.map((prob) => (
                    <Card key={prob.id} hoverEffect className="p-4 flex items-center justify-between">
                      <div className="space-y-1 pr-4">
                        <Link
                          to={`/problems/${prob.id}`}
                          className="font-medium text-white hover:text-emerald-400 text-sm line-clamp-1"
                        >
                          {prob.id}. {prob.title}
                        </Link>
                        <div className="flex items-center gap-2 text-xs text-arena-400">
                          <span>Limit: {prob.timeLimit}s / {prob.memoryLimit}MB</span>
                        </div>
                      </div>
                      <Link to={`/problems/${prob.id}`}>
                        <Button variant="outline" size="sm">
                          Solve
                        </Button>
                      </Link>
                    </Card>
                  ))
                )}
              </div>
            </div>

            {/* Recent Submissions Section */}
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <h2 className="text-base font-semibold text-white flex items-center gap-2">
                  <CheckCircle className="w-4 h-4 text-emerald-400" />
                  Recent Submissions
                </h2>
                <Link to="/submissions" className="text-xs text-emerald-400 hover:text-emerald-300 font-medium">
                  View All &rarr;
                </Link>
              </div>

              <div className="space-y-3">
                {recentSubmissions.length === 0 ? (
                  <Card className="p-8 text-center text-arena-400 text-sm">
                    You haven't submitted any solutions yet.{' '}
                    <Link to="/problems" className="text-emerald-400 hover:underline">
                      Solve a problem now
                    </Link>
                    .
                  </Card>
                ) : (
                  recentSubmissions.map((sub) => (
                    <Card key={sub.id} hoverEffect className="p-4 flex items-center justify-between">
                      <div className="space-y-1">
                        <Link
                          to={`/problems/${sub.problemId}`}
                          className="font-medium text-white hover:text-emerald-400 text-sm"
                        >
                          {sub.problemTitle}
                        </Link>
                        <div className="flex items-center gap-2 text-xs text-arena-400">
                          <LanguageBadge language={sub.language} size="sm" />
                          <span className="flex items-center gap-1">
                            <Clock className="w-3 h-3" />
                            {new Date(sub.submittedAt).toLocaleDateString()}
                          </span>
                        </div>
                      </div>

                      <StatusBadge status={sub.status} />
                    </Card>
                  ))
                )}
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
};

