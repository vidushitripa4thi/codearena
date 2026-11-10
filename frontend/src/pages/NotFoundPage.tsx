import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '../components/common/Button';
import { Card } from '../components/common/Card';
import { Home, Code2 } from 'lucide-react';

export const NotFoundPage: React.FC = () => {
  return (
    <div className="min-h-[70vh] flex items-center justify-center px-4 py-16">
      <Card className="max-w-md w-full p-8 text-center space-y-5">
        <div className="text-6xl font-extrabold font-mono text-emerald-400">404</div>
        <div className="space-y-2">
          <h1 className="text-xl font-bold text-white">Page Not Found</h1>
          <p className="text-sm text-arena-400">
            The page or resource you are looking for does not exist or has been moved.
          </p>
        </div>
        <div className="flex items-center justify-center gap-3 pt-2">
          <Link to="/">
            <Button variant="outline" size="sm" leftIcon={<Home className="w-4 h-4" />}>
              Home
            </Button>
          </Link>
          <Link to="/problems">
            <Button variant="primary" size="sm" leftIcon={<Code2 className="w-4 h-4" />}>
              Problems Library
            </Button>
          </Link>
        </div>
      </Card>
    </div>
  );
};

