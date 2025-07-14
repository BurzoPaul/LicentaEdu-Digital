import React, { useContext, useEffect, useState } from "react";
import { AuthContext } from "../context/AuthContext";
import { Link, useNavigate } from "react-router-dom";
import "../styles/MyPosts.css";

export default function MyPosts() {
  const { user, token, isLoading } = useContext(AuthContext);
  const navigate = useNavigate();
  const [posts, setPosts] = useState([]);
  const [emailLogs, setEmailLogs] = useState([]);
  const [selectedIds, setSelectedIds] = useState([]);
  const [emailInput, setEmailInput] = useState("");
  const [feedback, setFeedback] = useState("");

  useEffect(() => {
    if (!isLoading && !token) return navigate("/login");
    if (user?.id) {
      fetch(`http://localhost:8080/posts/user/${user.id}`, {
        headers: { Authorization: `Bearer ${token}` }
      })
        .then(res => res.json())
        .then(data => setPosts(data))
        .catch(console.error);

      fetch(`http://localhost:8080/posts/email-log/${user.id}`, {
        headers: { Authorization: `Bearer ${token}` }
      })
        .then(res => res.json())
        .then(data => setEmailLogs(data))
        .catch(console.error);
    }
  }, [user, token, isLoading, navigate]);

  const togglePost = (postId) => {
    setSelectedIds((prev) =>
      prev.includes(postId) ? prev.filter(id => id !== postId) : [...prev, postId]
    );
  };

  const handleSend = async () => {
    setFeedback("");
    const emailList = emailInput.split(",").map(e => e.trim()).filter(Boolean);
    if (emailList.length === 0 || selectedIds.length === 0) {
      setFeedback("⚠️ Trebuie să selectezi postări și cel puțin o adresă de email.");
      return;
    }
    const formData = new URLSearchParams();
    formData.append("postIds", selectedIds.join(","));
    formData.append("emails", emailList.join(","));

    try {
      const res = await fetch("http://localhost:8080/posts/share-multiple", {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
          Authorization: `Bearer ${token}`
        },
        body: formData.toString()
      });
      if (!res.ok) throw new Error(await res.text());
      setFeedback("✅ Emailurile au fost trimise cu succes.");
      setSelectedIds([]);
      setEmailInput("");

      // Reload logs
      const logRes = await fetch(`http://localhost:8080/posts/email-log/${user.id}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      const logData = await logRes.json();
      setEmailLogs(logData);

    } catch (err) {
      setFeedback(`❌ Eroare la trimitere: ${err.message}`);
    }
  };

  const getEmailsForPost = (postId) => {
    return emailLogs
      .filter(log => log.postId === postId)
      .map(log => log.recipientEmail);
  };

  return (
    <div className="my-posts-container">
      <h2>Postările mele</h2>
      {posts.length === 0 ? (
        <p className="no-posts">Nu ai postări încă.</p>
      ) : (
        <>
          <ul className="my-posts-list">
            {posts.map(p => (
              <li key={p.id} className="my-post-item">
                <label>
                  <input
                    type="checkbox"
                    checked={selectedIds.includes(p.id)}
                    onChange={() => togglePost(p.id)}
                  />
                  <Link to={`/posts/${p.id}`}>
                    {p.imagineUrl && (
                      <img src={p.imagineUrl} alt={p.titlu} className="post-thumb" />
                    )}
                    <strong>{p.titlu}</strong>
                  </Link>
                </label>

                {/* Emailuri trimise */}
                {getEmailsForPost(p.id).length > 0 && (
                  <div className="sent-emails">
                    <p style={{ fontSize: "0.85rem", marginTop: "8px", color: "#aaa" }}>
                      Trimis către:
                    </p>
                    <ul style={{ paddingLeft: "16px", fontSize: "0.85rem", color: "#ccc" }}>
                      {getEmailsForPost(p.id).map((email, i) => (
                        <li key={i}>{email}</li>
                      ))}
                    </ul>
                  </div>
                )}
              </li>
            ))}
          </ul>

          {selectedIds.length > 0 && (
            <div className="email-section">
              <h4>Trimite prin email</h4>
              <input
                type="text"
                value={emailInput}
                placeholder="ex: student1@utcn.ro, prof2@utcn.ro"
                onChange={(e) => setEmailInput(e.target.value)}
              />
              <button className="send-button" onClick={handleSend}>
                Trimite postările selectate
              </button>
            </div>
          )}

          {feedback && <div className="feedback-msg">{feedback}</div>}
        </>
      )}
    </div>
  );
}
