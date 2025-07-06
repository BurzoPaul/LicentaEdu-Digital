// src/components/PostDetails.jsx
import React, { useEffect, useState, useContext } from "react";
import { useParams, Link, useNavigate }           from "react-router-dom";
import axios                                       from "axios";
import { AuthContext }                             from "../context/AuthContext";
import "../styles/PostDetails.css";

export default function PostDetails() {
  const { id }           = useParams();
  const { user, token }  = useContext(AuthContext);
  const navigate         = useNavigate();
  const [post, setPost]  = useState(null);

  // Extrage ID-ul YouTube (parametrul v=...)
  const extractVideoID = url => {
    const m = url.match(/[?&]v=([^&]+)/);
    return m ? m[1] : null;
  };

  useEffect(() => {
    if (!token) {
      navigate("/login");
      return;
    }
    axios
      .get(`http://localhost:8080/posts/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      .then(res => setPost(res.data))
      .catch(() => navigate("/"));
  }, [id, token, navigate]);

  if (!post) {
    return <div>Loading…</div>;
  }

  const isAuthor = user?.id === post.authorId;
  const videoID  = extractVideoID(post.videoUrl || "");

  return (
    <div className="post-details">
      {/* Titlu */}
      <h1 className="pd-title">{post.title}</h1>

      {/* Autor */}
      <h4 className="pd-author">Autor: {post.authorName}</h4>

      {/* Player YouTube */}
      {videoID && (
        <div className="pd-video">
          <iframe
            src={`https://www.youtube.com/embed/${videoID}`}
            title="YouTube video player"
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
            allowFullScreen
          />
        </div>
      )}

      {/* Galerie imagini */}
      {post.imageUrls?.length > 0 && (
        <div className="pd-images">
          {post.imageUrls.map((url, i) => (
            <img
              key={i}
              className="pd-image"
              src={url}
              alt={`media ${i}`}
            />
          ))}
        </div>
      )}

      {/* Descriere */}
      <p className="pd-description">{post.description}</p>

      {/* Buton de edit (dacă ești autor) */}
      {isAuthor && (
        <Link to={`/posts/${id}/edit`} className="edit-button">
          Editează postarea
        </Link>
      )}
    </div>
  );
}
