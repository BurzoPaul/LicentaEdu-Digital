import React, { useState } from 'react';

export default function PostFilters({ onSearch }) {
  const [title, setTitle] = useState('');
  const [author, setAuthor] = useState('');
  const [keyword, setKeyword] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    onSearch({ title, author, keyword });
  };

  return (
    <form onSubmit={handleSubmit} className="filter-bar">
      <input type="text" placeholder="Titlu" value={title} onChange={e => setTitle(e.target.value)} />
      <input type="text" placeholder="Autor" value={author} onChange={e => setAuthor(e.target.value)} />
      <input type="text" placeholder="Cuvânt cheie" value={keyword} onChange={e => setKeyword(e.target.value)} />
      <button type="submit">Caută</button>
    </form>
  );
}
