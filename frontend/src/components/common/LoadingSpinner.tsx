import React from 'react';
import { Loader2 } from 'lucide-react';

interface LoadingSpinnerProps {
  label?: string;
  className?: string;
}

export const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({
  label = 'Loading data...',
  className = '',
}) => {
  return (
    <div className={`flex flex-col items-center justify-center py-16 gap-3 text-arena-400 ${className}`}>
      <Loader2 className="w-8 h-8 animate-spin text-emerald-400" />
      <span className="text-sm font-medium">{label}</span>
    </div>
  );
};

