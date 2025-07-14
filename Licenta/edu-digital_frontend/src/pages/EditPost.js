import React, { useState, useEffect, useContext } from "react";
import { useParams, useNavigate }               from "react-router-dom";
import { AuthContext }                           from "../context/AuthContext";
import "../styles/EditPost.css";

export default function EditPost() {
  const { id } = useParams();
  const { user, token } = useContext(AuthContext);
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [videoUrl, setVideoUrl] = useState("");
  const [tags, setTags] = useState(""); // ✅ NOU
  const [existingImages, setExistingImages] = useState([]);
  const [toDelete, setToDelete] = useState([]);
  const [newFiles, setNewFiles] = useState([]);
  const [previews, setPreviews] = useState([]);
  const [feedback, setFeedback] = useState("");

  const extractVideoID = url => {
    const m = url.match(/[?&]v=([^&]+)/);
    return m ? m[1] : null;
  };

  useEffect(() => {
    if (!token) return navigate("/login");
    fetch(`http://localhost:8080/posts/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(r => {
        if (!r.ok) throw new Error("Postare negăsită");
        return r.json();
      })
      .then(dto => {
        setTitle(dto.title);
        setDescription(dto.description);
        setVideoUrl(dto.videoUrl || "");
        setTags(dto.tags || ""); // ✅ preluăm tagurile
        const imgs = dto.imageUrls.map(url => {
          const m = url.match(/\/media\/(\d+)\/view/);
          return { id: m ? Number(m[1]) : null, url };
        });
        setExistingImages(imgs);
      })
      .catch(() => navigate("/"));
  }, [id, token, navigate]);

  useEffect(() => {
    if (!newFiles.length) {
      setPreviews([]);
      return;
    }
    const urls = newFiles.map(f => URL.createObjectURL(f));
    setPreviews(urls);
    return () => urls.forEach(u => URL.revokeObjectURL(u));
  }, [newFiles]);

  const removeExisting = idx => {
    setToDelete(prev => [...prev, existingImages[idx].id]);
    setExistingImages(prev => prev.filter((_, i) => i !== idx));
  };

  const removeNew = idx => {
    setNewFiles(prev => prev.filter((_, i) => i !== idx));
  };

  const handleSubmit = async e => {
    e.preventDefault();
    setFeedback("");

    for (const mediaId of toDelete) {
      await fetch(`http://localhost:8080/media/${mediaId}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` }
      });
    }

    const textForm = new URLSearchParams();
    textForm.append("title", title);
    textForm.append("description", description);
    textForm.append("videoUrl", videoUrl);
    textForm.append("tags", tags); // ✅ trimitem tagurile

    const res1 = await fetch(`http://localhost:8080/posts/update/${id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
        Authorization: `Bearer ${token}`
      },
      body: textForm.toString()
    });

    if (!res1.ok) {
      const err = await res1.text();
      setFeedback(`⚠️ Eroare la actualizare text: ${err}`);
      return;
    }

    if (newFiles.length) {
      const mediaForm = new FormData();
      newFiles.forEach(f => mediaForm.append("files", f));
      const res2 = await fetch(`http://localhost:8080/posts/${id}/add-media`, {
        method: "POST",
        headers: { Authorization: `Bearer ${token}` },
        body: mediaForm
      });
      if (!res2.ok) {
        const err = await res2.text();
        setFeedback(`⚠️ Eroare media: ${err}`);
        return;
      }
    }

    setFeedback("✅ Postarea a fost actualizată cu succes!");
    setTimeout(() => navigate(`/posts/${id}`), 1200);
  };

  if (!token) return null;
  if (!title && !description && existingImages.length === 0 && feedback === "")
    return <div>Loading…</div>;

  return (
    <div className="edit-post-page">
      <form onSubmit={handleSubmit} className="edit-post-form">
        <div className="title-field">
          <input
            type="text"
            value={title}
            onChange={e => setTitle(e.target.value)}
            required
          />
        </div>

        <div className="author-field">Autor: {user.name}</div>

        <div className="preview-images">
          {existingImages.map((img, i) => (
            <div key={`old-${i}`} className="preview-item">
              <img src={img.url} className="preview-img" alt="" />
              <button type="button" className="remove-btn" onClick={() => removeExisting(i)}>×</button>
            </div>
          ))}
          {previews.map((url, i) => (
            <div key={`new-${i}`} className="preview-item">
              <img src={url} className="preview-img" alt="" />
              <button type="button" className="remove-btn" onClick={() => removeNew(i)}>×</button>
            </div>
          ))}
        </div>

        <div className="description-field">
          <textarea
            value={description}
            onChange={e => setDescription(e.target.value)}
            required
          />
        </div>

        {/* 🔹 INPUT TAGURI */}
        <div className="tag-field">
          <label>
            Taguri (ex: game,unity,lab):
            <input
              type="text"
              value={tags}
              onChange={e => setTags(e.target.value)}
              placeholder="ex: game,lab,react"
            />
          </label>
        </div>

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

        <div className="file-input">
          <label>
            Adaugă imagini:
            <input
              type="file"
              multiple
              accept="image/*"
              onChange={e => {
                const sel = Array.from(e.target.files);
                setNewFiles(prev => [...prev, ...sel]);
              }}
            />
          </label>
        </div>

        {feedback && (
          <div className={`feedback ${feedback.startsWith("✅") ? "success" : "error"}`}>
            {feedback}
          </div>
        )}

        <button type="submit" className="edit-button">
          Salvează modificările
        </button>
      </form>
    </div>
  );
}
