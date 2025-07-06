import React, { useState } from "react";
import { FaEye, FaEyeSlash } from "react-icons/fa";
import "../styles/Register.css";

function Register() {
  const [email, setEmail] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const handleRegister = async (e) => {
  e.preventDefault();

  try {
    const response = await fetch("http://localhost:8080/auth/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        email: email,
        name: username,
        password: password,
      }),
    });

    if (response.ok) {
      alert("Cont creat cu succes! Te poți autentifica.");
      // Poți face redirect aici
    } else {
      const contentType = response.headers.get("content-type");

      if (contentType && contentType.includes("application/json")) {
        const errJson = await response.json();
        const mesaj = Object.values(errJson).join("\n");
        alert("Eroare la înregistrare:\n" + mesaj);
      } else {
        const errText = await response.text();
        alert("Eroare la înregistrare: " + errText);
      }
    }
  } catch (error) {
    console.error("Eroare de rețea:", error);
    alert("Eroare de rețea.");
  }
};


  return (
    <div className="register-container">
      <form className="register-form" onSubmit={handleRegister}>
        <h2>Creare cont</h2>

        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />

        <input
          type="text"
          placeholder="Nume utilizator"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
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

        <button type="submit">Înregistrează-te</button>
      </form>
    </div>
  );
}

export default Register;
