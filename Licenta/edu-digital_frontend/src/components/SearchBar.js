// src/components/SearchBar.js
import React, { useState } from "react";
import "../styles/SearchBar.css";

const SearchBar = ({ onSearch }) => {
  const [query, setQuery] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    onSearch({ keyword: query }); // Trimitem doar cuvântul cheie
  };

  return (
    <form className="search-bar" onSubmit={handleSubmit}>
      <input
        type="text"
        placeholder="Caută în titlu sau descriere..."
        value={query}
        onChange={(e) => setQuery(e.target.value)}
      />
      <button type="submit">Caută</button>
    </form>
  );
};

export default SearchBar;
