import React from 'react';
import { Link } from 'react-router-dom';
import { Problem } from '../../types/problem';
import { Card } from '../common/Card';
import { DifficultyBadge } from '../common/Badge';
import { Clock, HardDrive, ArrowRight } from 'lucide-react';

interface ProblemCardProps {
  problem: Problem;
}

export const ProblemCard: React.FC<ProblemCardProps> = ({ problem }) => {
  return (
    <Card hoverEffect className="p-5 flex flex-col justify-between">
      <div>
        <div className="flex items-start justify-between gap-3 mb-2">
          <Link
            to={`/problems/${problem.id}`}
            className="text-base font-semibold text-white hover:text-emerald-400 transition-colors line-clamp-1"
          >
            {problem.id}. {problem.title}
          </Link>
          <DifficultyBadge difficulty={problem.difficulty} />
        </div>

        <p className="text-arena-400 text-xs line-clamp-2 mb-4 leading-relaxed">
          {problem.description}
        </p>
      </div>

      <div className="pt-3 border-t border-arena-800/80 flex items-center justify-between text-xs text-arena-400">
        <div className="flex items-center gap-3">
          <span className="flex items-center gap-1" title="Time limit">
            <Clock className="w-3.5 h-3.5 text-arena-500" />
            {problem.timeLimit}s
          </span>
          <span className="flex items-center gap-1" title="Memory limit">
            <HardDrive className="w-3.5 h-3.5 text-arena-500" />
            {problem.memoryLimit}MB
          </span>
        </div>

        <Link
          to={`/problems/${problem.id}`}
          className="inline-flex items-center gap-1 text-emerald-400 hover:text-emerald-300 font-medium group text-xs"
        >
          <span>Solve</span>
          <ArrowRight className="w-3.5 h-3.5 transition-transform group-hover:translate-x-0.5" />
        </Link>
      </div>
    </Card>
  );
};

