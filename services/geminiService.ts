
import { GoogleGenAI } from "@google/genai";
import { Message } from '../types';

const ai = new GoogleGenAI({ apiKey: process.env.API_KEY as string });

export async function summarizeMessages(messages: Message[]): Promise<string> {
  const model = 'gemini-2.5-flash';
  
  const chatHistory = messages
    .map(msg => `${msg.sender}: ${msg.text}`)
    .join('\n');

  const prompt = `
    You are a helpful assistant integrated into a WhatsApp chat. 
    Your task is to summarize the following chat conversation in two or three concise sentences.
    Focus only on the most crucial information, such as plans being made, important questions asked, or key decisions.
    Ignore casual chit-chat, jokes, memes, and off-topic conversations.
    The goal is to provide a quick "at-a-glance" update for someone who doesn't want to read the whole conversation.

    Here is the chat history:
    ---
    ${chatHistory}
    ---

    Provide the summary now.
  `;

  try {
    const response = await ai.models.generateContent({
      model: model,
      contents: prompt,
    });
    return response.text.trim();
  } catch (error) {
    console.error("Error generating summary with Gemini API:", error);
    throw new Error("Failed to communicate with the Gemini API.");
  }
}
