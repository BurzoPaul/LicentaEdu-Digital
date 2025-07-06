// src/components/Posts.js
import React, { useEffect, useState } from "react";
import PostCard from "./PostCard";
import "../styles/Post.css";
import axios from "axios";

const Posts = () => {
  const [posts, setPosts] = useState([]);

  useEffect(() => {
    axios.get("http://localhost:8080/posts/all")
      .then(res => setPosts(res.data))
      .catch(err => console.error("Error loading posts:", err));
  }, []);

  return (
    <div className="posts-container">
      {posts.map((post) => (
        <PostCard
        key={post.id}
        id={post.id}                // ← aici adaugi id-ul
        title={post.titlu}
        imageUrl={post.imagineUrl}
        />
      ))}
    </div>
  );
};

export default Posts;
