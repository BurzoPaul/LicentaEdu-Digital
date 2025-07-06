// src/components/Sidebar.jsx
import React from "react";
import { Link, useNavigate } from "react-router-dom";
import logo from "../assets/logoEdu-digital.png"
import "../styles/Sidebar.css";

const Sidebar = () => {
  const navigate = useNavigate();
  const isAuth = !!sessionStorage.getItem("token");

  const handleLogout = () => {
   sessionStorage.removeItem("token");
   navigate("/login");
 };
  return (
    <div className="sidebar">
      <div className="sidebar-brand">
        <img src={logo} alt="Edu-Digital logo" className="sidebar-logo" />
        <span className="sidebar-title">Edu-Digital</span>
      </div>
      <nav>
        <ul>
          <li>< Link to="/">Acasă</Link></li>
          <li><Link to="/despre">Despre</Link></li>
          {isAuth && (
            <li>
              <Link to="/create-post">Creeate post</Link>
            </li>
          )}
            {isAuth && (
            <li>
              <Link to="/my-posts">Postările mele</Link>
            </li>
          )}
          {isAuth ? (
            <>
              {/* Link-uri pentru utilizatorii logați */}
               <li>
              {/* Styled ca link, dar e button */}
              <button 
                onClick={handleLogout} 
                className="sidebar-link"
              >
                Logout
              </button>
            </li>
            </>
          ) : (
            <>
              {/* Link-uri pentru invitați */}
              <li>
                <Link to="/login">Login</Link>
              </li>
              <li>
                <Link to="/register">Register</Link>
              </li>
            </>
          )}
          <li><Link to="/contact">Contact</Link></li>
        </ul>
      </nav>
    </div>
  );
};

export default Sidebar;
