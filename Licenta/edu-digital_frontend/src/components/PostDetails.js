import React, { useEffect, useState, useContext } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import axios from "axios";
import { AuthContext } from "../context/AuthContext";
import "../styles/PostDetails.css";

export default function PostDetails() {
  const { id } = useParams();
  const { user, token } = useContext(AuthContext);
  const navigate = useNavigate();
  const [post, setPost] = useState(null);
  const [emailInput, setEmailInput] = useState("");
  const [feedback, setFeedback] = useState("");

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

  const handleTagClick = (tag) => {
    navigate(`/?keyword=${tag.trim()}`);
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const handleShare = async () => {
    setFeedback("");
    const emails = emailInput
      .split(",")
      .map(e => e.trim())
      .filter(e => e.includes("@"));

    if (!emails.length) {
      setFeedback("⚠️ Introdu cel puțin o adresă validă.");
      return;
    }

    try {
      const res = await fetch("http://localhost:8080/posts/share", {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
          Authorization: `Bearer ${token}`
        },
        body: new URLSearchParams({
          postId: id,
          emails: emails.join(",")
        })
      });

      if (!res.ok) throw new Error(await res.text());
      setFeedback("✅ Email trimis cu succes!");
      setEmailInput("");
    } catch (err) {
      setFeedback(`❌ Eroare la trimitere: ${err.message}`);
    }
  };

  if (!post) {
    return <div>Loading…</div>;
  }

  const isAuthor = user?.id === post.authorId;
  const videoID = extractVideoID(post.videoUrl || "");

  return (
    <div className="post-details">
      <h1 className="pd-title">{post.title}</h1>
      <h4 className="pd-author">Autor: {post.authorName}</h4>

      {post.tags && (
        <div className="post-tags">
          {post.tags.split(',').map(tag => (
            <span
              key={tag.trim()}
              className="tag clickable"
              onClick={() => handleTagClick(tag)}
            >
              #{tag.trim()}
            </span>
          ))}
        </div>
      )}

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

      <p className="pd-description">{post.description}</p>

      {isAuthor && (
        <Link to={`/posts/${id}/edit`} className="edit-button">
          Editează postarea
        </Link>
      )}

      {token && (
        <div className="email-share">
          <h4>Introduceti o adresa de mail pentru a impartasi postarea.</h4>
          <input
            type="text"
            value={emailInput}
            placeholder="ex: prof@utcn.ro, student@cs.utcluj.ro"
            onChange={e => setEmailInput(e.target.value)}
          />
          <button className="send-button" onClick={handleShare}>
            Trimite pe email
          </button>
          {feedback && <div className="feedback-msg">{feedback}</div>}
        </div>
      )}
    </div>
  );
}
