
import React, { useState, useCallback, useEffect } from 'react';
import { MOCK_MESSAGES } from './constants';
import { Message as MessageType } from './types';
import Header from './components/Header';
import ChatWindow from './components/ChatWindow';
import MessageInput from './components/MessageInput';
import { summarizeMessages } from './services/geminiService';

const App: React.FC = () => {
  const [messages] = useState<MessageType[]>(MOCK_MESSAGES);
  const [summary, setSummary] = useState<string>('');
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [isSummaryEnabled, setIsSummaryEnabled] = useState<boolean>(false);

  const handleToggleSummary = useCallback(() => {
    setIsSummaryEnabled(prev => !prev);
  }, []);

  useEffect(() => {
    const generateSummary = async () => {
      setIsLoading(true);
      setError(null);
      setSummary('');

      try {
        const messagesToSummarize = messages.slice(-50);
        const result = await summarizeMessages(messagesToSummarize);
        setSummary(result);
      } catch (err) {
        setError('Failed to generate summary. Please try again.');
        console.error(err);
      } finally {
        setIsLoading(false);
      }
    };

    if (isSummaryEnabled) {
      generateSummary();
    } else {
      setSummary('');
      setError(null);
      setIsLoading(false);
    }
  }, [isSummaryEnabled, messages]);

  return (
    <div className="flex justify-center items-center min-h-screen bg-gray-100 p-4">
      <div className="w-full max-w-lg lg:max-w-4xl h-[95vh] flex flex-col bg-white shadow-2xl rounded-lg overflow-hidden">
        <Header 
          summaryEnabled={isSummaryEnabled}
          onToggleSummary={handleToggleSummary}
          summary={summary}
          isLoading={isLoading}
          error={error}
        />
        <div className="flex-1 relative">
          <div className="absolute inset-0 whatsapp-bg"></div>
          <ChatWindow messages={messages} />
        </div>
        <MessageInput />
      </div>
    </div>
  );
};

export default App;
