import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getMySubmissions } from '../api/submissions';
import { SubmissionResponse } from '../types/submission';
import { extractErrorMessage } from '../api/client';
import { SubmissionRow } from '../components/submission/SubmissionRow';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { ErrorAlert } from '../components/common/ErrorAlert';
import { Card } from '../components/common/Card';
import { Button } from '../components/common/Button';
import { History, Inbox, X, Code2 } from 'lucide-react';

export const SubmissionsPage: React.FC = () => {
  const [submissions, setSubmissions] = useState<SubmissionResponse[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>('');

  const [activeCodeModal, setActiveCodeModal] = useState<SubmissionResponse | null>(null);

  useEffect(() => {
    let isMounted = true;
    async function fetchSubmissions() {
      try {
        setIsLoading(true);
        setError('');
        const data = await getMySubmissions();
        if (isMounted) {
          setSubmissions(data);
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

    fetchSubmissions();

    return () => {
      isMounted = false;
    };
  }, []);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-arena-800/80">
        <div>
          <h1 className="text-2xl sm:text-3xl font-bold text-white tracking-tight flex items-center gap-2.5">
            <History className="w-7 h-7 text-emerald-400" />
            My Submissions
          </h1>
          <p className="text-sm text-arena-400 mt-1">
            Review all your submitted solutions and intake timestamps.
          </p>
        </div>

        <div className="text-xs font-mono text-arena-400 bg-arena-900 border border-arena-800 px-3 py-1.5 rounded-lg self-start sm:self-auto">
          Total Submissions: <span className="text-white font-semibold">{submissions.length}</span>
        </div>
      </div>

      {error && <ErrorAlert message={error} onDismiss={() => setError('')} />}

      {isLoading ? (
        <LoadingSpinner label="Fetching your submission history..." />
      ) : submissions.length > 0 ? (
        <Card className="overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-arena-950/80 border-b border-arena-800 text-xs font-mono text-arena-400 uppercase tracking-wider">
                  <th className="py-3 px-4">#ID</th>
                  <th className="py-3 px-4">Problem</th>
                  <th className="py-3 px-4">Language</th>
                  <th className="py-3 px-4">Status</th>
                  <th className="py-3 px-4">Time</th>
                  <th className="py-3 px-4">Memory</th>
                  <th className="py-3 px-4">Submitted At</th>
                  <th className="py-3 px-4 text-right">Action</th>
                </tr>
              </thead>
              <tbody>
                {submissions.map((sub) => (
                  <SubmissionRow
                    key={sub.id}
                    submission={sub}
                    onViewCode={(s) => setActiveCodeModal(s)}
                  />
                ))}
              </tbody>
            </table>
          </div>
        </Card>
      ) : (
        /* Empty State */
        <Card className="p-12 text-center space-y-4">
          <div className="w-12 h-12 rounded-full bg-arena-800 flex items-center justify-center text-arena-500 mx-auto">
            <Inbox className="w-6 h-6" />
          </div>
          <div className="space-y-1">
            <h3 className="text-base font-semibold text-white">No submissions yet</h3>
            <p className="text-arena-400 text-sm max-w-sm mx-auto">
              You haven't submitted any solutions yet. Pick a challenge and submit your code!
            </p>
          </div>
          <Link to="/problems">
            <Button variant="primary" size="sm">
              Browse Problems Library
            </Button>
          </Link>
        </Card>
      )}

      {/* Code Viewer Modal */}
      {activeCodeModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm">
          <div className="bg-arena-900 border border-arena-800 rounded-2xl w-full max-w-2xl max-h-[85vh] flex flex-col shadow-2xl overflow-hidden animate-in fade-in zoom-in-95 duration-150">
            {/* Modal Header */}
            <div className="flex items-center justify-between px-5 py-4 bg-arena-950 border-b border-arena-800">
              <div className="flex items-center gap-2">
                <Code2 className="w-5 h-5 text-emerald-400" />
                <h3 className="text-sm font-semibold text-white">
                  Submission #{activeCodeModal.id} — {activeCodeModal.problemTitle}
                </h3>
              </div>
              <button
                onClick={() => setActiveCodeModal(null)}
                className="text-arena-400 hover:text-white transition-colors p-1 rounded"
                aria-label="Close modal"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Modal Subheader */}
            <div className="px-5 py-2.5 bg-arena-900 border-b border-arena-800 flex items-center justify-between text-xs text-arena-400 font-mono">
              <span>Language: <strong className="text-white">{activeCodeModal.language}</strong></span>
              <span>Status: <strong className="text-amber-300">{activeCodeModal.status}</strong></span>
              <span>{new Date(activeCodeModal.submittedAt).toLocaleString()}</span>
            </div>

            {/* Code Body */}
            <div className="p-5 flex-1 overflow-y-auto bg-arena-950 font-mono text-xs text-arena-200">
              <pre className="whitespace-pre-wrap leading-relaxed">
                {activeCodeModal.sourceCode}
              </pre>
            </div>

            {/* Modal Footer */}
            <div className="px-5 py-3 bg-arena-950 border-t border-arena-800 flex justify-end">
              <Button variant="outline" size="sm" onClick={() => setActiveCodeModal(null)}>
                Close
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

