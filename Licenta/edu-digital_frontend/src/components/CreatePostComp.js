// src/components/CreatePostComp.jsx
import React, { useState, useContext, useEffect } from "react";
import { useNavigate }                        from "react-router-dom";
import { AuthContext }                        from "../context/AuthContext";
import "../styles/CreatePost.css";             // your existing styles

export default function CreatePostComp() {
  const { user, token, isLoading } = useContext(AuthContext);
  const navigate                   = useNavigate();

  // redirect if not logged in
  useEffect(() => {
    if (!isLoading && !token) navigate("/login");
  }, [token, isLoading, navigate]);

  const [title, setTitle]             = useState("");
  const [description, setDescription] = useState("");
  const [videoUrl, setVideoUrl]       = useState("");     // ← new
  const [files, setFiles]             = useState([]);     // File objects
  const [previews, setPreviews]       = useState([]);     // image URLs
  const [feedback, setFeedback]       = useState("");

  // image previews
  useEffect(() => {
    if (!files.length) {
      setPreviews([]);
      return;
    }
    const urls = files.map(f => URL.createObjectURL(f));
    setPreviews(urls);
    return () => urls.forEach(u => URL.revokeObjectURL(u));
  }, [files]);

  const removeImage = i => {
    setFiles(prev => prev.filter((_, idx) => idx !== i));
  };

  // helper to extract YouTube ID
  const extractVideoID = url => {
    const m = url.match(/[?&]v=([^&]+)/);
    return m ? m[1] : null;
  };

  const handleSubmit = async e => {
    e.preventDefault();
    setFeedback("");
    if (!user?.id) {
      setFeedback("⚠️ Trebuie să te autentifici.");
      return;
    }

    const formData = new FormData();
    formData.append("title", title);
    formData.append("description", description);
    formData.append("videoUrl", videoUrl);              // ← append videoUrl
    formData.append("userId", user.id);
    files.forEach(f => formData.append("files", f));

    try {
      const res = await fetch("http://localhost:8080/posts/create", {
        method: "POST",
        headers: { Authorization: `Bearer ${token}` },
        body: formData
      });
      if (!res.ok) throw new Error(await res.text());
      const created = await res.json();
      setFeedback("✅ Postarea a fost creată!");
      setTimeout(() => navigate(`/posts/${created.id}`), 1200);
    } catch (err) {
      console.error(err);
      setFeedback(`⚠️ Eroare la creare: ${err.message}`);
    }
  };

  return (
    <div className="post-details">
      <form onSubmit={handleSubmit} className="edit-post-form">
        <div className="pd-title">
          <input
            type="text"
            placeholder="Titlu..."
            value={title}
            onChange={e => setTitle(e.target.value)}
            required
          />
        </div>

        <h4 className="pd-author">Autor: {user?.name}</h4>

        <div className="pd-description">
          <textarea
            placeholder="Descriere..."
            value={description}
            onChange={e => setDescription(e.target.value)}
            required
          />
        </div>

        {/* YouTube URL field */}
        <div className="video-field">
          <label>
            Link YouTube:
            <input
              type="url"
              placeholder="https://www.youtube.com/watch?v=..."
              value={videoUrl}
              onChange={e => setVideoUrl(e.target.value)}
            />
          </label>
        </div>

        {/* Live embedded preview */}
        {extractVideoID(videoUrl) && (
          <div className="video-preview">
            <iframe
              src={`https://www.youtube.com/embed/${extractVideoID(videoUrl)}`}
              title="YouTube preview"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowFullScreen
            />
          </div>
        )}

        {/* image previews with delete */}
        {previews.length > 0 && (
          <div className="preview-images">
            {previews.map((url, i) => (
              <div key={i} className="preview-item">
                <img src={url} alt={`preview ${i}`} className="preview-img" />
                <button
                  type="button"
                  className="remove-btn"
                  onClick={() => removeImage(i)}
                >
                  ×
                </button>
              </div>
            ))}
          </div>
        )}

        {feedback && <div className="feedback">{feedback}</div>}

        <div className="file-input">
          <label>
            Adaugă imagini:
            <input
              type="file"
              multiple
              accept="image/*"
              onChange={e => {
                const sel = Array.from(e.target.files);
                setFiles(prev => [...prev, ...sel]);
              }}
            />
          </label>
        </div>

        <button type="submit" className="edit-button">
          Creează postarea
        </button>
      </form>
    </div>
  );
}
