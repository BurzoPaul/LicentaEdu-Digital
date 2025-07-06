// src/components/PostCard.js
import React from "react";
import { Link } from "react-router-dom";
import "../styles/PostCard.css";

const PostCard = ({ id, title, imageUrl }) => {
return ( <div className="post-card">
<Link to={`/posts/${id}`} className="post-card-link"><img
       className="post-image"
       src={imageUrl}
       alt={title}
     /> <div className="post-title">{title}</div> </Link> </div>
);
};

export default PostCard;

