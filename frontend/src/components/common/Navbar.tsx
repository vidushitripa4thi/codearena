import React, { useState } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { Logo } from './Logo';
import { Button } from './Button';
import {
  Code2,
  LayoutDashboard,
  History,
  LogOut,
  User as UserIcon,
  Menu,
  X,
  Shield,
} from 'lucide-react';

export const Navbar: React.FC = () => {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/');
    setMobileMenuOpen(false);
  };

  const navLinkClasses = ({ isActive }: { isActive: boolean }) =>
    `inline-flex items-center gap-2 text-sm font-medium transition-colors px-3 py-1.5 rounded-lg ${
      isActive
        ? 'text-emerald-400 bg-emerald-500/10 border border-emerald-500/20'
        : 'text-arena-300 hover:text-white hover:bg-arena-800/60'
    }`;

  return (
    <header className="sticky top-0 z-50 w-full bg-arena-950/80 backdrop-blur-md border-b border-arena-800/80">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        {/* Brand */}
        <Logo size="md" />

        {/* Desktop Nav Links */}
        <nav className="hidden md:flex items-center gap-1.5">
          <NavLink to="/problems" className={navLinkClasses}>
            <Code2 className="w-4 h-4" />
            Problems
          </NavLink>

          {isAuthenticated && (
            <>
              <NavLink to="/dashboard" className={navLinkClasses}>
                <LayoutDashboard className="w-4 h-4" />
                Dashboard
              </NavLink>

              <NavLink to="/submissions" className={navLinkClasses}>
                <History className="w-4 h-4" />
                My Submissions
              </NavLink>
            </>
          )}
        </nav>

        {/* Desktop User / Auth CTA */}
        <div className="hidden md:flex items-center gap-3">
          {isAuthenticated && user ? (
            <div className="flex items-center gap-3">
              <div className="flex items-center gap-2 px-3 py-1.5 rounded-lg bg-arena-900 border border-arena-800 text-xs">
                <div className="w-6 h-6 rounded-full bg-emerald-500/20 text-emerald-400 flex items-center justify-center font-semibold">
                  {user.username.charAt(0).toUpperCase()}
                </div>
                <div className="flex flex-col">
                  <span className="font-semibold text-white leading-none">{user.username}</span>
                  <span className="text-[10px] text-arena-400 flex items-center gap-1">
                    {user.role === 'ADMIN' ? (
                      <span className="text-amber-400 flex items-center gap-0.5">
                        <Shield className="w-2.5 h-2.5" /> Admin
                      </span>
                    ) : (
                      'Coder'
                    )}
                  </span>
                </div>
              </div>

              <Button
                variant="ghost"
                size="sm"
                onClick={handleLogout}
                className="text-arena-400 hover:text-rose-400"
                leftIcon={<LogOut className="w-4 h-4" />}
                aria-label="Log out"
              >
                Sign Out
              </Button>
            </div>
          ) : (
            <div className="flex items-center gap-2">
              <Link to="/login">
                <Button variant="ghost" size="sm" leftIcon={<UserIcon className="w-4 h-4" />}>
                  Sign In
                </Button>
              </Link>
              <Link to="/register">
                <Button variant="primary" size="sm">
                  Get Started
                </Button>
              </Link>
            </div>
          )}
        </div>

        {/* Mobile menu button */}
        <div className="md:hidden flex items-center">
          <button
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="p-2 rounded-lg text-arena-400 hover:text-white hover:bg-arena-800/60"
            aria-label="Toggle navigation menu"
          >
            {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
          </button>
        </div>
      </div>

      {/* Mobile drawer */}
      {mobileMenuOpen && (
        <div className="md:hidden bg-arena-900 border-b border-arena-800 px-4 pt-3 pb-5 space-y-2">
          <Link
            to="/problems"
            onClick={() => setMobileMenuOpen(false)}
            className="flex items-center gap-2 px-3 py-2 rounded-lg text-arena-200 hover:bg-arena-800 text-sm font-medium"
          >
            <Code2 className="w-4 h-4 text-emerald-400" />
            Problems
          </Link>

          {isAuthenticated ? (
            <>
              <Link
                to="/dashboard"
                onClick={() => setMobileMenuOpen(false)}
                className="flex items-center gap-2 px-3 py-2 rounded-lg text-arena-200 hover:bg-arena-800 text-sm font-medium"
              >
                <LayoutDashboard className="w-4 h-4 text-emerald-400" />
                Dashboard
              </Link>
              <Link
                to="/submissions"
                onClick={() => setMobileMenuOpen(false)}
                className="flex items-center gap-2 px-3 py-2 rounded-lg text-arena-200 hover:bg-arena-800 text-sm font-medium"
              >
                <History className="w-4 h-4 text-emerald-400" />
                My Submissions
              </Link>
              <div className="pt-2 border-t border-arena-800">
                <div className="px-3 py-1.5 text-xs text-arena-400">
                  Signed in as <span className="text-white font-medium">{user?.username}</span>
                </div>
                <button
                  onClick={handleLogout}
                  className="w-full flex items-center gap-2 px-3 py-2 rounded-lg text-rose-400 hover:bg-rose-500/10 text-sm font-medium mt-1"
                >
                  <LogOut className="w-4 h-4" />
                  Sign Out
                </button>
              </div>
            </>
          ) : (
            <div className="pt-3 border-t border-arena-800 flex flex-col gap-2">
              <Link to="/login" onClick={() => setMobileMenuOpen(false)}>
                <Button variant="outline" size="md" className="w-full">
                  Sign In
                </Button>
              </Link>
              <Link to="/register" onClick={() => setMobileMenuOpen(false)}>
                <Button variant="primary" size="md" className="w-full">
                  Get Started
                </Button>
              </Link>
            </div>
          )}
        </div>
      )}
    </header>
  );
};

