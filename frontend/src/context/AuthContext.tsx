import { createContext, useContext, useState, useEffect, ReactNode, useCallback } from 'react';
import { UserProfile, LoginRequest, RegisterRequest } from '../types/auth';
import * as authApi from '../api/auth';

interface AuthContextType {
  user: UserProfile | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (credentials: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => void;
  refreshProfile: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserProfile | null>(() => {
    const saved = localStorage.getItem('codearena_user');
    return saved ? JSON.parse(saved) : null;
  });
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('codearena_token'));
  const [isLoading, setIsLoading] = useState<boolean>(true);

  const logout = useCallback(() => {
    localStorage.removeItem('codearena_token');
    localStorage.removeItem('codearena_user');
    setToken(null);
    setUser(null);
  }, []);

  const refreshProfile = useCallback(async () => {
    try {
      const profile = await authApi.getProfile();
      setUser(profile);
      localStorage.setItem('codearena_user', JSON.stringify(profile));
    } catch {
      logout();
    }
  }, [logout]);

  useEffect(() => {
    const initAuth = async () => {
      const storedToken = localStorage.getItem('codearena_token');
      if (storedToken) {
        try {
          const profile = await authApi.getProfile();
          setUser(profile);
          localStorage.setItem('codearena_user', JSON.stringify(profile));
        } catch {
          logout();
        }
      }
      setIsLoading(false);
    };

    initAuth();

    const handleAuthExpired = () => {
      logout();
    };

    window.addEventListener('codearena_auth_expired', handleAuthExpired);
    return () => {
      window.removeEventListener('codearena_auth_expired', handleAuthExpired);
    };
  }, [logout]);

  const handleLogin = async (credentials: LoginRequest) => {
    const response = await authApi.login(credentials);
    localStorage.setItem('codearena_token', response.token);
    setToken(response.token);
    // Fetch full profile
    const profile = await authApi.getProfile();
    setUser(profile);
    localStorage.setItem('codearena_user', JSON.stringify(profile));
  };

  const handleRegister = async (data: RegisterRequest) => {
    const response = await authApi.register(data);
    localStorage.setItem('codearena_token', response.token);
    setToken(response.token);
    // Fetch full profile
    const profile = await authApi.getProfile();
    setUser(profile);
    localStorage.setItem('codearena_user', JSON.stringify(profile));
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!token && !!user,
        isLoading,
        login: handleLogin,
        register: handleRegister,
        logout,
        refreshProfile,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextType {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
