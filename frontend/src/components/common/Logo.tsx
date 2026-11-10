import React from 'react';
import { Terminal } from 'lucide-react';
import { Link } from 'react-router-dom';

interface LogoProps {
  className?: string;
  size?: 'sm' | 'md' | 'lg';
  linkTo?: string;
}

export const Logo: React.FC<LogoProps> = ({ className = '', size = 'md', linkTo = '/' }) => {
  const iconSizes = {
    sm: 'w-5 h-5',
    md: 'w-6 h-6',
    lg: 'w-8 h-8',
  };

  const textSizes = {
    sm: 'text-lg',
    md: 'text-xl',
    lg: 'text-2xl',
  };

  const content = (
    <div className={`flex items-center gap-2.5 font-bold tracking-tight text-white ${className}`}>
      <div className="relative flex items-center justify-center p-2 rounded-xl bg-gradient-to-br from-emerald-500/20 to-emerald-500/5 border border-emerald-500/30 text-emerald-400 shadow-sm shadow-emerald-950/50">
        <Terminal className={iconSizes[size]} strokeWidth={2.5} />
        <span className="absolute -top-1 -right-1 flex h-2 w-2">
          <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
          <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
        </span>
      </div>
      <span className={`font-mono ${textSizes[size]}`}>
        Code<span className="text-emerald-400">Arena</span>
      </span>
    </div>
  );

  if (linkTo) {
    return <Link to={linkTo} className="inline-block transition-opacity hover:opacity-90">{content}</Link>;
  }

  return content;
};

