// src/components/Header.jsx
import React, { useContext } from "react";
import { AuthContext }        from "../context/AuthContext";
import "../styles/Header.css";

export default function Header() {
  const { user } = useContext(AuthContext);

  return (
    <header className="app-header">
      {user?.name && (
        <div className="user-greeting">
          Bine ai venit, <strong>{user.name}</strong>!
        </div>
      )}
    </header>
  );
}
