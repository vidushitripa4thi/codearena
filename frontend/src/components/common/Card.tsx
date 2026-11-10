import React from 'react';

interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode;
  className?: string;
  hoverEffect?: boolean;
}

export const Card: React.FC<CardProps> = ({
  children,
  className = '',
  hoverEffect = false,
  ...props
}) => {
  return (
    <div
      className={`bg-arena-900/80 border border-arena-800 rounded-xl backdrop-blur-sm ${
        hoverEffect
          ? 'transition-all duration-200 hover:border-arena-700 hover:bg-arena-850 hover:shadow-lg hover:shadow-arena-950/40'
          : ''
      } ${className}`}
      {...props}
    >
      {children}
    </div>
  );
};

