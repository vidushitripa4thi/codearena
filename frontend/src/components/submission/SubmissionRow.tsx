import React from 'react';
import { Link } from 'react-router-dom';
import { SubmissionResponse } from '../../types/submission';
import { StatusBadge, LanguageBadge } from '../common/Badge';
import { Clock, ExternalLink } from 'lucide-react';

interface SubmissionRowProps {
  submission: SubmissionResponse;
  onViewCode?: (submission: SubmissionResponse) => void;
}

export const SubmissionRow: React.FC<SubmissionRowProps> = ({ submission, onViewCode }) => {
  const formattedDate = new Date(submission.submittedAt).toLocaleString(undefined, {
    dateStyle: 'medium',
    timeStyle: 'short',
  });

  return (
    <tr className="border-b border-arena-800/60 hover:bg-arena-900/40 transition-colors text-sm">
      <td className="py-3.5 px-4 font-mono text-arena-400 text-xs">
        #{submission.id}
      </td>
      <td className="py-3.5 px-4 font-medium text-white">
        <Link
          to={`/problems/${submission.problemId}`}
          className="hover:text-emerald-400 transition-colors inline-flex items-center gap-1.5"
        >
          <span>{submission.problemTitle}</span>
          <ExternalLink className="w-3.5 h-3.5 text-arena-500" />
        </Link>
      </td>
      <td className="py-3.5 px-4">
        <LanguageBadge language={submission.language} />
      </td>
      <td className="py-3.5 px-4">
        <StatusBadge status={submission.status} />
      </td>
      <td className="py-3.5 px-4 text-xs font-mono text-arena-400">
        {submission.executionTime != null ? `${submission.executionTime}ms` : '—'}
      </td>
      <td className="py-3.5 px-4 text-xs font-mono text-arena-400">
        {submission.memoryUsed != null ? `${submission.memoryUsed}MB` : '—'}
      </td>
      <td className="py-3.5 px-4 text-xs text-arena-400 whitespace-nowrap">
        <span className="flex items-center gap-1.5">
          <Clock className="w-3.5 h-3.5 text-arena-500" />
          {formattedDate}
        </span>
      </td>
      <td className="py-3.5 px-4 text-right">
        {onViewCode && (
          <button
            onClick={() => onViewCode(submission)}
            className="text-xs text-emerald-400 hover:text-emerald-300 font-medium hover:underline"
          >
            View Code
          </button>
        )}
      </td>
    </tr>
  );
};

