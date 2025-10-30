
import React from 'react';
import { Message as MessageType } from '../types';
import Message from './Message';

interface ChatWindowProps {
  messages: MessageType[];
}

const ChatWindow: React.FC<ChatWindowProps> = ({ messages }) => {
  return (
    <div className="flex-1 p-4 overflow-y-auto">
      <div className="flex flex-col space-y-2">
        {messages.map((msg) => (
          <Message key={msg.id} message={msg} />
        ))}
      </div>
    </div>
  );
};

export default ChatWindow;
