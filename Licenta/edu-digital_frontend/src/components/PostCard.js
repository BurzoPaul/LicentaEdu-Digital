import React from "react";
import { Link } from "react-router-dom";
import "../styles/PostCard.css";

const PostCard = ({ id, title, imageUrl, tags, onTagClick }) => {
  return (
    <Link to={`/posts/${id}`} className="post-card-link">
      <div className="post-card">
        {imageUrl && (
          <img
            className="post-image"
            src={imageUrl}
            alt={title}
          />
        )}

        <div className="post-title">{title}</div>

        {/* Taguri afișate sub titlu */}
        {tags && (
          <div className="post-tags">
            {tags.split(',').map(tag => (
              <span
                key={tag.trim()}
                className="tag clickable"
                onClick={(e) => {
                  e.preventDefault();    // evită redirect
                  e.stopPropagation();   // evită propagarea către <Link>
                  onTagClick?.(tag.trim());
                }}
              >
                #{tag.trim()}
              </span>
            ))}
          </div>
        )}
      </div>
    </Link>
  );
};

export default PostCard;
