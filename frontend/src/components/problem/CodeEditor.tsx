import React from 'react';
import Editor from '@monaco-editor/react';
import { Language } from '../../types/submission';
import { Code, RotateCcw } from 'lucide-react';

interface CodeEditorProps {
  language: Language;
  onLanguageChange: (lang: Language) => void;
  code: string;
  onCodeChange: (code: string) => void;
  readOnly?: boolean;
}

export const STARTER_TEMPLATES: Record<Language, string> = {
  JAVA: `public class Solution {
    public static void main(String[] args) {
        // Write your solution here
        System.out.println("Hello, CodeArena!");
    }
}
`,
  PYTHON: `import sys

def solve():
    # Read input from standard input
    # lines = sys.stdin.read().split()
    print("Hello, CodeArena!")

if __name__ == '__main__':
    solve()
`,
  CPP: `#include <iostream>
using namespace std;

int main() {
    // Write your solution here
    cout << "Hello, CodeArena!" << endl;
    return 0;
}
`,
  C: `#include <stdio.h>

int main() {
    // Write your solution here
    printf("Hello, CodeArena!\\n");
    return 0;
}
`,
  JAVASCRIPT: `// Read input from process.stdin
function solve() {
    console.log("Hello, CodeArena!");
}

solve();
`,
};

const MONACO_LANG_MAP: Record<Language, string> = {
  JAVA: 'java',
  PYTHON: 'python',
  CPP: 'cpp',
  C: 'c',
  JAVASCRIPT: 'javascript',
};

export const CodeEditor: React.FC<CodeEditorProps> = ({
  language,
  onLanguageChange,
  code,
  onCodeChange,
  readOnly = false,
}) => {
  const languages: Language[] = ['JAVA', 'PYTHON', 'CPP', 'C', 'JAVASCRIPT'];

  const handleResetToTemplate = () => {
    onCodeChange(STARTER_TEMPLATES[language]);
  };

  return (
    <div className="flex flex-col h-full bg-arena-950 border border-arena-800 rounded-xl overflow-hidden shadow-xl">
      {/* Editor toolbar */}
      <div className="flex items-center justify-between px-4 py-2.5 bg-arena-900 border-b border-arena-800 text-xs">
        <div className="flex items-center gap-2">
          <Code className="w-4 h-4 text-emerald-400" />
          <span className="font-semibold text-white font-mono">Editor</span>
          <select
            value={language}
            onChange={(e) => {
              const newLang = e.target.value as Language;
              onLanguageChange(newLang);
              // If current code is empty or matches previous template, update to new template
              if (!code.trim() || Object.values(STARTER_TEMPLATES).includes(code)) {
                onCodeChange(STARTER_TEMPLATES[newLang]);
              }
            }}
            disabled={readOnly}
            className="bg-arena-800 border border-arena-700 text-arena-200 rounded-lg px-2.5 py-1 text-xs font-mono focus:outline-none focus:border-emerald-500 cursor-pointer"
            aria-label="Select Programming Language"
          >
            {languages.map((lang) => (
              <option key={lang} value={lang}>
                {lang}
              </option>
            ))}
          </select>
        </div>

        {!readOnly && (
          <button
            type="button"
            onClick={handleResetToTemplate}
            className="flex items-center gap-1 text-arena-400 hover:text-arena-200 transition-colors px-2 py-1 rounded hover:bg-arena-800 text-xs"
            title="Reset to starter boilerplate"
          >
            <RotateCcw className="w-3.5 h-3.5" />
            <span>Reset Boilerplate</span>
          </button>
        )}
      </div>

      {/* Monaco editor container */}
      <div className="flex-1 min-h-[380px]">
        <Editor
          height="100%"
          language={MONACO_LANG_MAP[language]}
          value={code}
          onChange={(val) => onCodeChange(val || '')}
          theme="vs-dark"
          options={{
            readOnly,
            minimap: { enabled: false },
            fontSize: 14,
            fontFamily: "'JetBrains Mono', 'Fira Code', monospace",
            lineNumbers: 'on',
            scrollBeyondLastLine: false,
            automaticLayout: true,
            tabSize: 4,
            padding: { top: 12, bottom: 12 },
            renderLineHighlight: 'all',
          }}
          loading={
            <div className="flex items-center justify-center h-full text-arena-500 text-sm">
              Loading editor environment...
            </div>
          }
        />
      </div>
    </div>
  );
};

