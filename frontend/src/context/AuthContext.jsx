import { createContext, useCallback, useContext, useMemo, useState } from 'react';
import { api, clearSession, loadSession, saveSession } from '../api.js';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  // session shape: { token, message, user: { firstName, lastName, userType } }
  const [session, setSession] = useState(loadSession);

  const login = useCallback(async (email, password) => {
    const res = await api.login(email, password);
    const next = {
      token: res.token,
      message: res.message,
      user: { firstName: res.firstName, lastName: res.lastName, userType: res.userType },
    };
    saveSession(next);
    setSession(next);
  }, []);

  const logout = useCallback(() => {
    clearSession();
    setSession(null);
  }, []);

  const value = useMemo(() => ({
    token: session?.token,
    message: session?.message,
    user: session?.user,
    login,
    logout,
  }), [session, login, logout]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
