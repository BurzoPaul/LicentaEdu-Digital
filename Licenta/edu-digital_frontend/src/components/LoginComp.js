import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { FaEye, FaEyeSlash } from "react-icons/fa";
import "../styles/Login.css";

const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();

 useEffect(() => {
   // dacă există token în sessionStorage, nu ai voie la login → du-te acasă
   if (sessionStorage.getItem("token")) {
     navigate("/");
   }
 }, [navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ login: email, password }),
      });

      if (response.ok) {
        const token = await response.text();
        sessionStorage.setItem("token", token);
         // 👉 salvează token-ul
        alert("Autentificare reușită!");
        // Redirect, dacă vrei:
         window.location.href = "/";
      } else {
        const err = await response.text();
        alert("Eroare: " + err);
      }
    } catch (err) {
      alert("Eroare rețea.");
      console.error(err);
    }
  };

  return (
    <div className="login-container">
      <form className="login-form" onSubmit={handleSubmit}>
        <h2>Autentificare</h2>

        <input
          type="text"
          placeholder="Email/Username"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />

        <div className="password-field">
          <input
            type={showPassword ? "text" : "password"}
            placeholder="Parolă"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <span
            className="password-toggle-icon"
            onClick={() => setShowPassword(!showPassword)}
            title={showPassword ? "Ascunde parola" : "Arată parola"}
          >
            {showPassword ? <FaEyeSlash /> : <FaEye />}
          </span>
        </div>

        <button type="submit">Conectează-te</button>
        <p className="signup-link">
          Nu ai cont? <Link to="/register">Creează unul</Link>
        </p>
      </form>
    </div>
  );
};

export default Login;
