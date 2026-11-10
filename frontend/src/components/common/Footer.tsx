import React from 'react';
import { Logo } from './Logo';
import { ShieldCheck, Cpu } from 'lucide-react';

export const Footer: React.FC = () => {
  return (
    <footer className="w-full bg-arena-950 border-t border-arena-800/80 text-arena-400 text-sm mt-auto">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">
          {/* Brand col */}
          <div className="md:col-span-2 space-y-3">
            <Logo size="md" />
            <p className="text-arena-400 text-sm max-w-sm leading-relaxed">
              CodeArena is a modern algorithmic coding platform powered by Spring Boot 3,
              PostgreSQL, and React. Practice problem-solving, track solutions, and prepare for interviews.
            </p>
            <div className="flex items-center gap-2 pt-1 text-xs text-emerald-400">
              <span className="flex h-2 w-2 relative">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
                <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
              </span>
              <span>Backend API operational on Spring Boot 3.3.5</span>
            </div>
          </div>

          {/* Quick links */}
          <div>
            <h4 className="text-white font-semibold text-xs uppercase tracking-wider mb-3">Platform</h4>
            <ul className="space-y-2 text-sm">
              <li>
                <a href="/problems" className="hover:text-emerald-400 transition-colors">
                  Problems Library
                </a>
              </li>
              <li>
                <a href="/dashboard" className="hover:text-emerald-400 transition-colors">
                  User Dashboard
                </a>
              </li>
              <li>
                <a href="/submissions" className="hover:text-emerald-400 transition-colors">
                  Submission History
                </a>
              </li>
              <li>
                <a
                  href="/swagger-ui/index.html"
                  target="_blank"
                  rel="noreferrer"
                  className="hover:text-emerald-400 transition-colors inline-flex items-center gap-1"
                >
                  <Cpu className="w-3.5 h-3.5" />
                  Swagger API Docs
                </a>
              </li>
            </ul>
          </div>

          {/* Technology & Security */}
          <div>
            <h4 className="text-white font-semibold text-xs uppercase tracking-wider mb-3">Security & Architecture</h4>
            <ul className="space-y-2 text-sm">
              <li className="flex items-center gap-1.5 text-arena-300">
                <ShieldCheck className="w-4 h-4 text-emerald-400" />
                Stateless JWT Security
              </li>
              <li className="flex items-center gap-1.5 text-arena-300">
                <ShieldCheck className="w-4 h-4 text-emerald-400" />
                BCrypt Password Hashing
              </li>
              <li className="flex items-center gap-1.5 text-arena-300">
                <ShieldCheck className="w-4 h-4 text-emerald-400" />
                Role-Based Access Control
              </li>
              <li className="text-xs text-arena-500 pt-1">
                Monaco Editor + Tailwind Dark Theme
              </li>
            </ul>
          </div>
        </div>

        <div className="pt-8 border-t border-arena-800/60 flex flex-col sm:flex-row items-center justify-between gap-4 text-xs text-arena-500">
          <p>© {new Date().getFullYear()} CodeArena. All rights reserved.</p>
          <p className="font-mono text-arena-600">Built for high-performance developer workflows</p>
        </div>
      </div>
    </footer>
  );
};

