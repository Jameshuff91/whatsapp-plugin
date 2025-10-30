
import React from 'react';
import { Message as MessageType } from '../types';

interface MessageProps {
  message: MessageType;
}

const Message: React.FC<MessageProps> = ({ message }) => {
  const isUser = message.sender === 'You';

  const alignmentClass = isUser ? 'justify-end' : 'justify-start';
  const bubbleClass = isUser 
    ? 'bg-emerald-200' 
    : 'bg-white';

  return (
    <div className={`flex ${alignmentClass}`}>
      <div className={`rounded-lg px-3 py-2 max-w-sm shadow-sm ${bubbleClass}`}>
        {!isUser && (
          <p className="text-xs font-bold text-teal-600 mb-1">{message.sender}</p>
        )}
        <p className="text-sm text-gray-800">{message.text}</p>
        <p className="text-right text-xs text-gray-400 mt-1">{message.timestamp}</p>
      </div>
    </div>
  );
};

export default Message;
