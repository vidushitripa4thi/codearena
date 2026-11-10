import React from 'react';
import { Difficulty, ProblemQueryFilter } from '../../types/problem';
import { Search, X, SlidersHorizontal } from 'lucide-react';

interface ProblemFilterProps {
  filter: ProblemQueryFilter;
  onChange: (newFilter: ProblemQueryFilter) => void;
}

export const ProblemFilter: React.FC<ProblemFilterProps> = ({ filter, onChange }) => {
  const difficulties: { label: string; value: Difficulty | '' }[] = [
    { label: 'All Difficulties', value: '' },
    { label: 'Easy', value: 'EASY' },
    { label: 'Medium', value: 'MEDIUM' },
    { label: 'Hard', value: 'HARD' },
  ];

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    onChange({ ...filter, search: e.target.value, page: 0 });
  };

  const handleClearSearch = () => {
    onChange({ ...filter, search: '', page: 0 });
  };

  const handleDifficultySelect = (val: Difficulty | '') => {
    onChange({ ...filter, difficulty: val, page: 0 });
  };

  const handleSortChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const val = e.target.value;
    if (val === 'newest') {
      onChange({ ...filter, sortBy: 'createdAt', sortDir: 'desc', page: 0 });
    } else if (val === 'oldest') {
      onChange({ ...filter, sortBy: 'createdAt', sortDir: 'asc', page: 0 });
    } else if (val === 'title_asc') {
      onChange({ ...filter, sortBy: 'title', sortDir: 'asc', page: 0 });
    } else if (val === 'title_desc') {
      onChange({ ...filter, sortBy: 'title', sortDir: 'desc', page: 0 });
    } else if (val === 'difficulty') {
      onChange({ ...filter, sortBy: 'difficulty', sortDir: 'asc', page: 0 });
    }
  };

  return (
    <div className="flex flex-col md:flex-row gap-4 items-stretch md:items-center justify-between mb-6">
      {/* Search Input */}
      <div className="relative flex-1 max-w-md">
        <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-arena-500" />
        <input
          type="text"
          placeholder="Search problems by title or keyword..."
          value={filter.search || ''}
          onChange={handleSearchChange}
          className="w-full bg-arena-900 border border-arena-800 rounded-xl pl-10 pr-9 py-2.5 text-sm text-white placeholder-arena-500 focus:outline-none focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 transition-all"
        />
        {filter.search && (
          <button
            onClick={handleClearSearch}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-arena-400 hover:text-white"
            aria-label="Clear search"
          >
            <X className="w-4 h-4" />
          </button>
        )}
      </div>

      {/* Filter and Sort controls */}
      <div className="flex flex-wrap items-center gap-2.5">
        {/* Difficulty pills */}
        <div className="inline-flex rounded-xl p-1 bg-arena-900 border border-arena-800 text-xs">
          {difficulties.map((d) => {
            const active = (filter.difficulty || '') === d.value;
            return (
              <button
                key={d.label}
                type="button"
                onClick={() => handleDifficultySelect(d.value)}
                className={`px-3 py-1.5 rounded-lg font-medium transition-all ${
                  active
                    ? 'bg-emerald-500 text-arena-950 font-semibold shadow-sm'
                    : 'text-arena-400 hover:text-white hover:bg-arena-800/60'
                }`}
              >
                {d.label}
              </button>
            );
          })}
        </div>

        {/* Sort selector */}
        <div className="relative inline-flex items-center">
          <SlidersHorizontal className="absolute left-3 w-3.5 h-3.5 text-arena-400 pointer-events-none" />
          <select
            onChange={handleSortChange}
            value={
              filter.sortBy === 'title' && filter.sortDir === 'asc'
                ? 'title_asc'
                : filter.sortBy === 'title' && filter.sortDir === 'desc'
                ? 'title_desc'
                : filter.sortBy === 'createdAt' && filter.sortDir === 'asc'
                ? 'oldest'
                : filter.sortBy === 'difficulty'
                ? 'difficulty'
                : 'newest'
            }
            className="bg-arena-900 border border-arena-800 rounded-xl pl-8 pr-8 py-2 text-xs font-medium text-arena-300 focus:outline-none focus:border-emerald-500 cursor-pointer appearance-none"
            aria-label="Sort problems"
          >
            <option value="newest">Sort: Newest</option>
            <option value="oldest">Sort: Oldest</option>
            <option value="title_asc">Sort: Title (A-Z)</option>
            <option value="title_desc">Sort: Title (Z-A)</option>
            <option value="difficulty">Sort: Difficulty</option>
          </select>
        </div>
      </div>
    </div>
  );
};

