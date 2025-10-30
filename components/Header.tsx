
import React from 'react';

interface HeaderProps {
  summaryEnabled: boolean;
  onToggleSummary: () => void;
  summary: string;
  isLoading: boolean;
  error: string | null;
}

const LoadingSpinner: React.FC = () => (
  <div className="flex items-center justify-center space-x-2">
    <div className="w-2 h-2 rounded-full bg-teal-500 animate-pulse"></div>
    <div className="w-2 h-2 rounded-full bg-teal-500 animate-pulse [animation-delay:0.1s]"></div>
    <div className="w-2 h-2 rounded-full bg-teal-500 animate-pulse [animation-delay:0.2s]"></div>
    <span className="text-sm text-gray-600">Gemini is thinking...</span>
  </div>
);

const Header: React.FC<HeaderProps> = ({ summaryEnabled, onToggleSummary, summary, isLoading, error }) => {
  return (
    <header className="bg-gray-100 flex-shrink-0">
      <div className="flex items-center justify-between p-3 bg-slate-200 border-b border-gray-300">
        <div className="flex items-center">
          <img className="w-10 h-10 rounded-full mr-3" src="https://picsum.photos/100/100?random=1" alt="Group Avatar" />
          <div>
            <h2 className="text-md font-semibold text-gray-800">Friend Group</h2>
            <p className="text-xs text-gray-500">Alice, Bob, Charlie, Dave...</p>
          </div>
        </div>
        <div className="flex items-center space-x-3">
          <span className="text-sm font-medium text-gray-700 select-none">Summarize</span>
          <button
            type="button"
            onClick={onToggleSummary}
            className={`${
              summaryEnabled ? 'bg-teal-500' : 'bg-gray-300'
            } relative inline-flex h-6 w-11 flex-shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-teal-500 focus:ring-offset-2`}
            role="switch"
            aria-checked={summaryEnabled}
            aria-label="Toggle chat summary"
          >
            <span
              aria-hidden="true"
              className={`${
                summaryEnabled ? 'translate-x-5' : 'translate-x-0'
              } pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out`}
            />
          </button>
        </div>
      </div>

      {summaryEnabled && (
        <div className="p-3 bg-emerald-50 border-b border-emerald-200 transition-all duration-300 ease-in-out">
          <div className="flex items-center justify-between min-h-[2rem]">
            <div className="flex-1 min-w-0">
            {isLoading ? (
                <LoadingSpinner />
            ) : error ? (
                <p className="text-sm text-red-600">{error}</p>
            ) : summary ? (
                <p className="text-sm text-gray-800 leading-relaxed">{summary}</p>
            ) : (
                <p className="text-sm text-gray-600">Generating summary...</p>
            )}
            </div>
          </div>
        </div>
      )}
    </header>
  );
};

export default Header;
