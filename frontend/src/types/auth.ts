export type Role = 'USER' | 'ADMIN';

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  username: string;
  email: string;
  role: Role;
}

export interface UserProfile {
  id: number;
  username: string;
  email: string;
  role: Role;
  createdAt: string;
}

