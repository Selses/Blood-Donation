import React, { createContext, useState, useContext, useEffect } from 'react';
import { authService } from '../services/authService';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(authService.getCurrentUser());
  const [loading, setLoading] = useState(false);

  const login = async (email, password) => {
    setLoading(true);
    try {
      const data = await authService.login(email, password);
      // data typically has: { accessToken, tokenType, email, role, name, id }
      const authUser = {
        email: data.email,
        role: data.role,
        name: data.name || data.email,
        id: data.id
      };
      localStorage.setItem('token', data.accessToken);
      localStorage.setItem('user', JSON.stringify(authUser));
      setUser(authUser);
      return authUser;
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    authService.logout();
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, loading, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
