import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Button } from '../components/common/Button';
import { Card } from '../components/common/Card';
import {
  Code2,
  Terminal,
  ShieldCheck,
  CheckCircle2,
  ArrowRight,
  Database,
  Lock,
  Layers,
  Sparkles,
} from 'lucide-react';

export const LandingPage: React.FC = () => {
  const { isAuthenticated } = useAuth();

  return (
    <div className="flex flex-col min-h-screen">
      {/* Hero Section */}
      <section className="relative overflow-hidden pt-20 pb-24 md:pt-32 md:pb-36 border-b border-arena-800/60">
        {/* Background glow effects */}
        <div className="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[300px] bg-emerald-500/10 rounded-full blur-3xl pointer-events-none" />
        <div className="absolute top-1/3 left-1/3 -translate-x-1/2 w-[400px] h-[250px] bg-blue-500/5 rounded-full blur-3xl pointer-events-none" />

        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 text-center relative z-10">
          <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-arena-900 border border-arena-800 text-xs font-medium text-emerald-400 mb-6">
            <Sparkles className="w-3.5 h-3.5" />
            <span>Modern Algorithmic Platform • Powered by Spring Boot 3</span>
          </div>

          <h1 className="text-4xl sm:text-5xl md:text-6xl font-extrabold text-white tracking-tight max-w-4xl mx-auto leading-tight md:leading-none mb-6">
            Sharpen your algorithms in the{' '}
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-emerald-400 to-teal-300">
              CodeArena
            </span>
          </h1>

          <p className="text-lg md:text-xl text-arena-400 max-w-2xl mx-auto leading-relaxed mb-10">
            A developer-first coding platform engineered for high performance.
            Tackle algorithmic problems, submit solutions across multiple languages,
            and monitor your progression.
          </p>

          <div className="flex flex-col sm:flex-row items-center justify-center gap-4 max-w-md mx-auto">
            {isAuthenticated ? (
              <Link to="/dashboard" className="w-full sm:w-auto">
                <Button size="lg" className="w-full sm:w-auto" rightIcon={<ArrowRight className="w-4 h-4" />}>
                  Go to Dashboard
                </Button>
              </Link>
            ) : (
              <>
                <Link to="/register" className="w-full sm:w-auto">
                  <Button size="lg" className="w-full sm:w-auto" rightIcon={<ArrowRight className="w-4 h-4" />}>
                    Get Started Free
                  </Button>
                </Link>
                <Link to="/login" className="w-full sm:w-auto">
                  <Button variant="outline" size="lg" className="w-full sm:w-auto">
                    Sign In
                  </Button>
                </Link>
              </>
            )}
            <Link to="/problems" className="w-full sm:w-auto">
              <Button variant="secondary" size="lg" className="w-full sm:w-auto">
                Explore Problems
              </Button>
            </Link>
          </div>

          {/* Interactive terminal preview card */}
          <div className="mt-16 max-w-3xl mx-auto rounded-2xl bg-arena-900/90 border border-arena-800 shadow-2xl overflow-hidden text-left font-mono text-xs">
            <div className="flex items-center justify-between px-4 py-3 bg-arena-950/80 border-b border-arena-800">
              <div className="flex items-center gap-2">
                <div className="w-3 h-3 rounded-full bg-rose-500/80" />
                <div className="w-3 h-3 rounded-full bg-amber-500/80" />
                <div className="w-3 h-3 rounded-full bg-emerald-500/80" />
                <span className="text-arena-400 ml-2">codearena-runtime // preview</span>
              </div>
              <span className="text-arena-500 text-[11px]">Java • Python • C++ • C • JS</span>
            </div>
            <div className="p-6 space-y-2 text-arena-300">
              <p className="text-emerald-400">$ codearena solve --problem 1 --lang JAVA</p>
              <p className="text-arena-400">&gt; Loading: Problem #1 - Two Sum [EASY]</p>
              <p className="text-arena-400">&gt; Memory Limit: 256MB | Time Limit: 1000ms</p>
              <p className="text-arena-200">&gt; Submitting source code to CodeArena intake pipeline...</p>
              <p className="text-emerald-400">&gt; Status: PENDING (Recorded with UUID verification)</p>
            </div>
          </div>
        </div>
      </section>

      {/* Features Grid */}
      <section className="py-20 bg-arena-900/30 border-b border-arena-800/60">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center max-w-2xl mx-auto mb-16">
            <h2 className="text-xs uppercase font-mono tracking-wider text-emerald-400 mb-2">
              Engineered Architecture
            </h2>
            <h3 className="text-2xl sm:text-3xl font-bold text-white">
              Built on Modern Spring Boot &amp; React
            </h3>
            <p className="text-arena-400 text-sm mt-3">
              Clean separation of concerns with strict contract schemas and secure state management.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <Card className="p-6 space-y-4">
              <div className="w-10 h-10 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 flex items-center justify-center">
                <Code2 className="w-5 h-5" />
              </div>
              <h4 className="text-lg font-semibold text-white">Curated Problem Library</h4>
              <p className="text-arena-400 text-sm leading-relaxed">
                Filter challenges by difficulty (Easy, Medium, Hard), search by keyword, and inspect
                runtime requirements, constraints, and verified test cases.
              </p>
              <ul className="text-xs text-arena-400 space-y-2 pt-2 border-t border-arena-800">
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
                  Dynamic JPA pagination &amp; sorting
                </li>
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
                  Clean RFC 7807 problem details
                </li>
              </ul>
            </Card>

            <Card className="p-6 space-y-4">
              <div className="w-10 h-10 rounded-xl bg-blue-500/10 border border-blue-500/20 text-blue-400 flex items-center justify-center">
                <Terminal className="w-5 h-5" />
              </div>
              <h4 className="text-lg font-semibold text-white">Monaco Code Editor</h4>
              <p className="text-arena-400 text-sm leading-relaxed">
                VS Code-grade editing experience directly in your browser. Complete with syntax highlighting,
                intelligent indentation, and starter boilerplate code.
              </p>
              <ul className="text-xs text-arena-400 space-y-2 pt-2 border-t border-arena-800">
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
                  Supports Java, Python, C++, C, JavaScript
                </li>
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
                  One-click boilerplate reset
                </li>
              </ul>
            </Card>

            <Card className="p-6 space-y-4">
              <div className="w-10 h-10 rounded-xl bg-purple-500/10 border border-purple-500/20 text-purple-400 flex items-center justify-center">
                <ShieldCheck className="w-5 h-5" />
              </div>
              <h4 className="text-lg font-semibold text-white">Stateless JWT Security</h4>
              <p className="text-arena-400 text-sm leading-relaxed">
                Protected by HMAC-SHA signed JWT tokens, BCrypt credential hashing, and role-based
                access controls governing submissions and problem management.
              </p>
              <ul className="text-xs text-arena-400 space-y-2 pt-2 border-t border-arena-800">
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
                  Owner-only submission isolation
                </li>
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
                  Auto token expiry handling
                </li>
              </ul>
            </Card>
          </div>
        </div>
      </section>

      {/* Tech Stack Highlights */}
      <section className="py-16 bg-arena-950">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex flex-wrap items-center justify-center gap-8 md:gap-14 text-arena-500 text-xs font-mono uppercase tracking-wider">
            <span className="flex items-center gap-2 text-arena-400">
              <Layers className="w-4 h-4 text-emerald-400" /> Spring Boot 3.3.5
            </span>
            <span className="flex items-center gap-2 text-arena-400">
              <Database className="w-4 h-4 text-blue-400" /> PostgreSQL 14+
            </span>
            <span className="flex items-center gap-2 text-arena-400">
              <Lock className="w-4 h-4 text-amber-400" /> JJWT &amp; Spring Security
            </span>
            <span className="flex items-center gap-2 text-arena-400">
              <Code2 className="w-4 h-4 text-cyan-400" /> React 18 + Vite + Tailwind
            </span>
          </div>
        </div>
      </section>
    </div>
  );
};

