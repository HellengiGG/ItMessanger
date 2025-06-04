import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box, TextField, Button, Typography, Paper, List, ListItem,
  ListItemText, Avatar
} from '@mui/material';
import SendIcon from '@mui/icons-material/Send';   // или { Send as SendIcon }

interface Message {
  id: number;
  content: string;
  sender: string;
  timestamp: string;
}

const Chat: React.FC = () => {
  const { chatId } = useParams<{ chatId: string }>();
  const navigate = useNavigate();
  const [messages, setMessages] = useState<Message[]>([]);
  const [newMessage, setNewMessage] = useState('');

  useEffect(() => {
    // TODO: Replace with actual API call to fetch messages for the specific chat
    const mockMessages: Message[] = [
      { id: 1, content: 'Привет!', sender: 'User1', timestamp: '12:30' },
      { id: 2, content: 'Как дела?', sender: 'User2', timestamp: '12:31' },
    ];
    setMessages(mockMessages);
  }, [chatId]);

  const handleSendMessage = () => {
    if (newMessage.trim()) {
      // TODO: Replace with actual API call to send message
      const newMsg: Message = {
        id: messages.length + 1,
        content: newMessage,
        sender: 'CurrentUser', // Replace with actual user
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      };
      setMessages([...messages, newMsg]);
      setNewMessage('');
    }
  };

  return (
    <Box sx={{ maxWidth: 800, margin: '0 auto', padding: 2 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Чат {chatId}
      </Typography>
      <Button 
        variant="outlined" 
        sx={{ mb: 2 }}
        onClick={() => navigate('/')}
      >
        Назад к списку чатов
      </Button>
      <Paper sx={{ height: '60vh', overflow: 'auto', mb: 2, p: 2 }}>
        <List>
          {messages.map((message) => (
            <ListItem key={message.id} alignItems="flex-start">
              <ListItemText
                primary={
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <Avatar sx={{ width: 24, height: 24 }}>{message.sender[0]}</Avatar>
                    <Typography component="span" variant="subtitle2">
                      {message.sender}
                    </Typography>
                    <Typography component="span" variant="caption" color="text.secondary">
                      {message.timestamp}
                    </Typography>
                  </Box>
                }
                secondary={message.content}
              />
            </ListItem>
          ))}
        </List>
      </Paper>
      <Box sx={{ display: 'flex', gap: 1 }}>
        <TextField
          fullWidth
          variant="outlined"
          placeholder="Введите сообщение..."
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          onKeyPress={(e) => e.key === 'Enter' && handleSendMessage()}
        />
        <Button
          variant="contained"
          color="primary"
          onClick={handleSendMessage}
          disabled={!newMessage.trim()}
        >
          <SendIcon />
        </Button>
      </Box>
    </Box>
  );
};

export default Chat; 