
import { Message } from './types';

export const MOCK_MESSAGES: Message[] = [
  { id: 1, sender: 'Alice', text: 'Hey everyone, what\'s up?', timestamp: '10:00 AM' },
  { id: 2, sender: 'Bob', text: 'Not much, just working. You?', timestamp: '10:01 AM' },
  { id: 3, sender: 'You', text: 'Same here. Anyone free this weekend?', timestamp: '10:02 AM' },
  { id: 4, sender: 'Charlie', text: 'I might be! What are you thinking?', timestamp: '10:03 AM' },
  { id: 5, sender: 'Alice', text: 'Maybe a hike on Saturday morning?', timestamp: '10:05 AM' },
  { id: 6, sender: 'Bob', text: 'I\'m in for a hike!', timestamp: '10:06 AM' },
  { id: 7, sender: 'Dave', text: 'Check out this funny cat video! 😂 https://picsum.photos/200/300', timestamp: '10:10 AM' },
  { id: 8, sender: 'You', text: 'Haha, classic Dave. So, for the hike, should we meet at the usual spot at 9 AM?', timestamp: '10:11 AM' },
  { id: 9, sender: 'Alice', text: '9 AM works for me.', timestamp: '10:12 AM' },
  { id: 10, sender: 'Charlie', text: 'I can\'t make 9, how about 10?', timestamp: '10:15 AM' },
  { id: 11, sender: 'Bob', text: '10 is a bit late for me, the trail gets crowded.', timestamp: '10:16 AM' },
  { id: 12, sender: 'Eve', text: 'Hey guys, sorry I\'m late. What are we talking about?', timestamp: '10:20 AM' },
  { id: 13, sender: 'You', text: 'Planning a hike for Saturday. Trying to decide on a time. 9 AM or 10 AM?', timestamp: '10:21 AM' },
  { id: 14, sender: 'Eve', text: 'I can do either!', timestamp: '10:22 AM' },
  { id: 15, sender: 'Alice', text: 'Let\'s stick to 9 AM. Charlie, can you make it work?', timestamp: '10:25 AM' },
  { id: 16, sender: 'Charlie', text: 'Alright, I\'ll make it work for 9. Where are we meeting?', timestamp: '10:26 AM' },
  { id: 17, sender: 'You', text: 'Pine Ridge trail head. I\'ll bring snacks.', timestamp: '10:27 AM' },
  { id: 18, sender: 'Bob', text: 'Perfect! I\'ll bring extra water.', timestamp: '10:28 AM' },
  { id: 19, sender: 'Dave', text: 'Another meme for your consideration', timestamp: '10:35 AM' },
  { id: 20, sender: 'Alice', text: 'Dave, focus! 😂', timestamp: '10:36 AM' },
  { id: 21, sender: 'Frank', text: 'Is there space for one more for the hike?', timestamp: '11:00 AM' },
  { id: 22, sender: 'You', text: 'Of course Frank! The more the merrier. We are meeting at Pine Ridge trail head at 9 AM Saturday.', timestamp: '11:01 AM' },
  { id: 23, sender: 'Frank', text: 'Awesome, see you all there!', timestamp: '11:02 AM' },
  { id: 24, sender: 'Eve', text: 'Also, don\'t forget my cousin\'s birthday party is on Saturday evening. We need to confirm RSVP by today.', timestamp: '11:05 AM' },
  { id: 25, sender: 'Bob', text: 'Oh right! I completely forgot. I can go.', timestamp: '11:06 AM' },
  { id: 26, sender: 'You', text: 'Me too! What\'s the dress code?', timestamp: '11:07 AM' },
  { id: 27, sender: 'Eve', text: 'It\'s a casual backyard BBQ. Starts at 7 PM.', timestamp: '11:08 AM' },
  { id: 28, sender: 'Alice', text: 'I\'ll be there. Should we bring anything?', timestamp: '11:09 AM' },
  { id: 29, sender: 'Eve', text: 'Just yourselves! But if you want, a side dish or dessert is always welcome.', timestamp: '11:10 AM' },
  { id: 30, sender: 'Charlie', text: 'I\'m not sure if I can make the party after the hike.', timestamp: '11:12 AM' },
  { id: 31, sender: 'Dave', text: 'I\'ve got another meme for you all.', timestamp: '11:15 AM' },
  { id: 32, sender: 'You', text: 'Okay, so to confirm: Hike at 9 AM Saturday, Pine Ridge. Eve\'s cousin\'s party at 7 PM Saturday. Let Eve know if you are coming to the party.', timestamp: '11:20 AM' },
  { id: 33, sender: 'Bob', text: 'Sounds good!', timestamp: '11:21 AM' },
  { id: 34, sender: 'Alice', text: 'Got it.', timestamp: '11:22 AM' },
  // ... adding more messages to reach over 50
  ...Array.from({ length: 25 }, (_, i) => ({
    id: 35 + i,
    sender: ['Dave', 'Charlie', 'Bob'][i % 3],
    text: `Just random chatter and memes #${i + 1}`,
    timestamp: `${11 + Math.floor(i / 10)}:${30 + (i % 10) * 2} AM`
  }))
];
