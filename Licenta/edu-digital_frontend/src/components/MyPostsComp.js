// src/pages/MyPosts.jsx
import React, { useContext, useEffect, useState } from "react";
import { AuthContext } from "../context/AuthContext";
import { Link, useNavigate } from "react-router-dom";
import "../styles/MyPosts.css";

export default function MyPosts() {
  const { user, token, isLoading } = useContext(AuthContext);
  const navigate = useNavigate();
  const [posts, setPosts] = useState([]);

  useEffect(() => {
    if (!isLoading && !token) return navigate("/login");
    if (user?.id) {
      fetch(`http://localhost:8080/posts/user/${user.id}`, {
        headers: { Authorization: `Bearer ${token}` }
      })
        .then(res => res.json())
        .then(data => setPosts(data))
        .catch(console.error);
    }
  }, [user, token, isLoading, navigate]);

  return (
    <div className="my-posts-container">
      <h2>Postările mele</h2>
      {posts.length === 0 ? (
        <p className="no-posts">Nu ai postări încă.</p>
      ) : (
        <ul className="my-posts-list">
          {posts.map(p => (
            <li key={p.id} className="my-post-item">
              <Link to={`/posts/${p.id}`}>
                {p.imagineUrl && (
                  <img src={p.imagineUrl} alt={p.titlu} className="post-thumb" />
                )}
                <strong>{p.titlu}</strong>
              </Link>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
