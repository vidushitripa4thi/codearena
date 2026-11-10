import React, { useEffect, useState } from 'react';
import { getProblems } from '../api/problems';
import { Problem, ProblemQueryFilter } from '../types/problem';
import { PageResponse } from '../types/api';
import { extractErrorMessage } from '../api/client';
import { ProblemFilter } from '../components/problem/ProblemFilter';
import { ProblemCard } from '../components/problem/ProblemCard';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { ErrorAlert } from '../components/common/ErrorAlert';
import { Card } from '../components/common/Card';
import { Button } from '../components/common/Button';
import { Code2, ChevronLeft, ChevronRight, Inbox } from 'lucide-react';

export const ProblemsPage: React.FC = () => {
  const [filter, setFilter] = useState<ProblemQueryFilter>({
    search: '',
    difficulty: '',
    page: 0,
    size: 10,
    sortBy: 'createdAt',
    sortDir: 'desc',
  });

  const [pageData, setPageData] = useState<PageResponse<Problem> | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>('');

  useEffect(() => {
    let isMounted = true;

    async function fetchProblems() {
      try {
        setIsLoading(true);
        setError('');
        const data = await getProblems(filter);
        if (isMounted) {
          setPageData(data);
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

    fetchProblems();

    return () => {
      isMounted = false;
    };
  }, [filter]);

  const handlePageChange = (newPage: number) => {
    setFilter((prev) => ({ ...prev, page: newPage }));
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-6">
      {/* Header section */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-arena-800/80">
        <div>
          <h1 className="text-2xl sm:text-3xl font-bold text-white tracking-tight flex items-center gap-2.5">
            <Code2 className="w-7 h-7 text-emerald-400" />
            Problems Library
          </h1>
          <p className="text-sm text-arena-400 mt-1">
            Browse and solve algorithmic challenges. Practice data structures, dynamic programming, and math.
          </p>
        </div>

        {pageData && (
          <div className="text-xs font-mono text-arena-400 bg-arena-900 border border-arena-800 px-3 py-1.5 rounded-lg self-start sm:self-auto">
            Total:{' '}
            <span className="text-white font-semibold">{pageData.totalElements}</span>{' '}
            problems
          </div>
        )}
      </div>

      {/* Filter and Search Bar */}
      <ProblemFilter filter={filter} onChange={setFilter} />

      {/* Error state */}
      {error && <ErrorAlert message={error} onDismiss={() => setError('')} />}

      {/* Loading state */}
      {isLoading ? (
        <LoadingSpinner label="Fetching problems catalog..." />
      ) : pageData?.content && pageData.content.length > 0 ? (
        <>
          {/* Problem Cards Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
            {pageData.content.map((problem) => (
              <ProblemCard key={problem.id} problem={problem} />
            ))}
          </div>

          {/* Pagination controls */}
          {pageData.totalPages > 1 && (
            <div className="flex items-center justify-between pt-6 border-t border-arena-800/80">
              <span className="text-xs text-arena-400 font-mono">
                Page <span className="text-white font-semibold">{pageData.page + 1}</span> of{' '}
                <span className="text-white font-semibold">{pageData.totalPages}</span>
              </span>

              <div className="flex items-center gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => handlePageChange(pageData.page - 1)}
                  disabled={!pageData.hasPrevious}
                  leftIcon={<ChevronLeft className="w-4 h-4" />}
                >
                  Previous
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => handlePageChange(pageData.page + 1)}
                  disabled={!pageData.hasNext}
                  rightIcon={<ChevronRight className="w-4 h-4" />}
                >
                  Next
                </Button>
              </div>
            </div>
          )}
        </>
      ) : (
        /* Empty state */
        <Card className="p-12 text-center space-y-4">
          <div className="w-12 h-12 rounded-full bg-arena-800 flex items-center justify-center text-arena-500 mx-auto">
            <Inbox className="w-6 h-6" />
          </div>
          <div className="space-y-1">
            <h3 className="text-base font-semibold text-white">No problems found</h3>
            <p className="text-arena-400 text-sm max-w-sm mx-auto">
              {filter.search || filter.difficulty
                ? 'Try adjusting your search criteria or clearing filters.'
                : 'There are currently no problems in the repository.'}
            </p>
          </div>
          {(filter.search || filter.difficulty) && (
            <Button
              variant="outline"
              size="sm"
              onClick={() => setFilter({ ...filter, search: '', difficulty: '', page: 0 })}
            >
              Reset Filters
            </Button>
          )}
        </Card>
      )}
    </div>
  );
};

