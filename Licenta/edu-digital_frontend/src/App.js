// src/App.js
import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import Header from "./components/Header";
import Sidebar from "./components/Sidebar";
import Home from "./pages/Home";
import About from "./pages/About";
import Contact from "./pages/Contact";
import Login from "./pages/Login";
import Register from "./pages/Register";
import CreatePost from "./pages/CreatePost";
import PostDetails from "./components/PostDetails";
import MyPosts      from "./pages/MyPosts";
import EditPost from "./pages/EditPost";
import "./styles.css";

const App = () => (
  <AuthProvider>
    <Router>
      <Header />
      <div className="app-container">
        <Sidebar />
        <div className="main-content">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/despre" element={<About />} />
            <Route path="/contact" element={<Contact />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/create-post" element={<CreatePost />} /> 
            <Route path="/posts/:id" element={<PostDetails />} />
            <Route path="/my-posts"    element={<MyPosts />} />
            <Route path="/posts/:id/edit"    element={<EditPost />} />
          </Routes>
        </div>
      </div>
    </Router>
  </AuthProvider>
);

export default App;
