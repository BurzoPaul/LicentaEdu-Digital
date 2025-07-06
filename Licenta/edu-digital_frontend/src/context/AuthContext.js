// src/context/AuthContext.js
import React, { createContext, useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";

export const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser]         = useState(null);
  const [token, setToken]       = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const savedToken = sessionStorage.getItem("token");
    if (savedToken) {
      try {
        const payload = jwtDecode(savedToken)
        console.log("🚀 decoded newToken on login:", payload);
        const { name, exp,  id } = payload;
        if (exp * 1000 > Date.now()) {
          setToken(savedToken);
          setUser({ id, name });
        } else {
          sessionStorage.removeItem("token");
        }
      } catch {
        sessionStorage.removeItem("token");
      }
    }
    setIsLoading(false);
  }, []);

  const login = (newToken) => {
    sessionStorage.setItem("token", newToken);
    setToken(newToken);
    const payload = jwtDecode(newToken)
    const { name,id } = payload;
    setUser({ id, name });
  };

  const logout = () => {
    sessionStorage.removeItem("token");
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, token, login, logout, isLoading }}>
      {children}
    </AuthContext.Provider>
  );
}
