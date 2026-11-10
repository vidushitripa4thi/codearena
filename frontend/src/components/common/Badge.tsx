import React from 'react';
import { Difficulty } from '../../types/problem';
import { SubmissionStatus, Language } from '../../types/submission';

interface BadgeProps {
  children: React.ReactNode;
  variant?: 'easy' | 'medium' | 'hard' | 'pending' | 'success' | 'error' | 'neutral' | 'language';
  className?: string;
  size?: 'sm' | 'md';
}

export const Badge: React.FC<BadgeProps> = ({
  children,
  variant = 'neutral',
  className = '',
  size = 'md',
}) => {
  const sizeClasses = {
    sm: 'text-xs px-2 py-0.5',
    md: 'text-xs font-medium px-2.5 py-1',
  };

  const variantClasses = {
    easy: 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20',
    medium: 'bg-amber-500/10 text-amber-400 border border-amber-500/20',
    hard: 'bg-rose-500/10 text-rose-400 border border-rose-500/20',
    pending: 'bg-amber-500/10 text-amber-300 border border-amber-500/30 animate-pulse',
    success: 'bg-emerald-500/15 text-emerald-300 border border-emerald-500/30',
    error: 'bg-rose-500/15 text-rose-300 border border-rose-500/30',
    neutral: 'bg-arena-800 text-arena-300 border border-arena-700',
    language: 'bg-blue-500/10 text-blue-300 border border-blue-500/20 font-mono',
  };

  return (
    <span className={`inline-flex items-center rounded-md ${sizeClasses[size]} ${variantClasses[variant]} ${className}`}>
      {children}
    </span>
  );
};

export const DifficultyBadge: React.FC<{ difficulty?: Difficulty; className?: string }> = ({
  difficulty,
  className = '',
}) => {
  if (!difficulty) {
    return <Badge variant="neutral" className={className}>Unknown</Badge>;
  }

  const map: Record<Difficulty, 'easy' | 'medium' | 'hard'> = {
    EASY: 'easy',
    MEDIUM: 'medium',
    HARD: 'hard',
  };

  return (
    <Badge variant={map[difficulty]} className={className}>
      {difficulty.charAt(0) + difficulty.slice(1).toLowerCase()}
    </Badge>
  );
};

export const StatusBadge: React.FC<{ status: SubmissionStatus; className?: string }> = ({
  status,
  className = '',
}) => {
  switch (status) {
    case 'ACCEPTED':
      return <Badge variant="success" className={className}>Accepted</Badge>;
    case 'PENDING':
      return <Badge variant="pending" className={className}>Pending (Queued)</Badge>;
    case 'WRONG_ANSWER':
      return <Badge variant="error" className={className}>Wrong Answer</Badge>;
    case 'TIME_LIMIT_EXCEEDED':
      return <Badge variant="error" className={className}>Time Limit Exceeded</Badge>;
    case 'COMPILATION_ERROR':
      return <Badge variant="error" className={className}>Compile Error</Badge>;
    case 'RUNTIME_ERROR':
      return <Badge variant="error" className={className}>Runtime Error</Badge>;
    default:
      return <Badge variant="neutral" className={className}>{status}</Badge>;
  }
};

export const LanguageBadge: React.FC<{
  language: Language;
  className?: string;
  size?: 'sm' | 'md';
}> = ({ language, className = '', size = 'md' }) => {
  return (
    <Badge variant="language" size={size} className={className}>
      {language}
    </Badge>
  );
};
