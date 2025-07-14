import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import PostCard from "./PostCard";
import "../styles/Post.css";
import axios from "axios";
import SearchBar from "./SearchBar";

const Posts = ({ showSearchBar = false }) => {
  const [posts, setPosts] = useState([]);
  const [searchActive, setSearchActive] = useState(false);
  const location = useLocation();

  // Detectează query din URL (?keyword=...)
  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const keyword = params.get("keyword");

    if (keyword) {
      handleSearch({ keyword });
    } else {
      fetchAllPosts();
    }
  }, [location.search]);

  // Încarcă toate postările
  const fetchAllPosts = async () => {
    try {
      const res = await axios.get("http://localhost:8080/posts/all");
      setPosts(res.data);
      setSearchActive(false);
    } catch (err) {
      console.error("Eroare la încărcarea postărilor:", err);
    }
  };

  // Caută postări după keyword (titlu, descriere, tag)
  const handleSearch = async ({ keyword }) => {
    try {
      const cleanedKeyword = keyword.startsWith("#")
        ? keyword.slice(1).trim().toLowerCase()
        : keyword.trim().toLowerCase();

      const res = await axios.get("http://localhost:8080/posts/search", {
        params: { keyword: cleanedKeyword },
      });

      setPosts(res.data);
      setSearchActive(true);
    } catch (err) {
      console.error("Eroare la filtrare:", err);
    }
  };

  // Clic pe tag → activează filtrarea
  const handleTagClick = async (tag) => {
    await handleSearch({ keyword: tag });
  };

  return (
    <div>
      {showSearchBar && (
        <div style={{ display: "flex", justifyContent: "center", marginTop: "30px" }}>
          <SearchBar onSearch={handleSearch} />
        </div>
      )}

      <div className="posts-container">
        {posts.length === 0 && searchActive ? (
          <div style={{ textAlign: "center", marginTop: "40px", color: "#aaa", fontSize: "1.1rem" }}>
            Nicio postare găsită pentru criteriul introdus.
          </div>
        ) : (
          posts.map((post) => (
            <PostCard
              key={post.id}
              id={post.id}
              title={post.title || post.titlu}
              imageUrl={post.imageUrl || post.imagineUrl || (post.imageUrls?.[0] ?? "")}
              tags={post.tags}
              onTagClick={handleTagClick}
            />
          ))
        )}
      </div>
    </div>
  );
};

export default Posts;
