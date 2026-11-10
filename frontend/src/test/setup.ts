import '@testing-library/jest-dom';
import React from 'react';
import { afterEach, vi } from 'vitest';
import { cleanup } from '@testing-library/react';

// Mock Monaco Editor for JSDOM test environment
vi.mock('@monaco-editor/react', () => ({
  default: ({
    value,
    onChange,
  }: {
    value: string;
    onChange: (val: string) => void;
  }) =>
    React.createElement('textarea', {
      'data-testid': 'monaco-editor',
      value: value || '',
      onChange: (e: React.ChangeEvent<HTMLTextAreaElement>) => onChange(e.target.value),
    }),
}));

afterEach(() => {
  cleanup();
  localStorage.clear();
});
