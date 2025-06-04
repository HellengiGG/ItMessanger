import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { List, ListItem, ListItemText, ListItemAvatar, Avatar, Typography, Box, Button } from '@mui/material';
import { Chat as ChatIcon } from '@mui/icons-material';
import { chatService, authService } from '../services/api';

interface Chat {
  id: number;
  name: string;
  lastMessage?: string;
  lastMessageTime?: string;
}

const ChatList: React.FC = () => {
  const [chats, setChats] = useState<Chat[]>([]);
  const [error, setError] = useState<string>('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchChats = async () => {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          authService.logout();
          navigate('/login');
          return;
        }

        const response = await chatService.getUserChats();
        setChats(response);
      } catch (err: any) {
        if (err.response?.status === 401) {
          authService.logout();
          navigate('/login');
        } else {
          setError('Ошибка при загрузке чатов');
        }
      }
    };

    fetchChats();
  }, [navigate]);

  const handleChatSelect = (chatId: number) => {
    navigate(`/chat/${chatId}`);
  };

  const handleLogout = () => {
    authService.logout();
    navigate('/login');
  };

  return (
    <Box sx={{ maxWidth: 600, margin: '0 auto', padding: 2 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
        <Typography variant="h4" component="h1">
          Мои чаты
        </Typography>
        <Button 
          variant="outlined" 
          color="error"
          onClick={handleLogout}
        >
          Выйти
        </Button>
      </Box>
      {error && (
        <Typography color="error" sx={{ mb: 2 }}>
          {error}
        </Typography>
      )}
      <List>
        {chats.map((chat) => (
          <ListItem
            key={chat.id}
            button
            onClick={() => handleChatSelect(chat.id)}
            sx={{
              mb: 1,
              borderRadius: 1,
              '&:hover': {
                backgroundColor: 'rgba(0, 0, 0, 0.04)',
              },
            }}
          >
            <ListItemAvatar>
              <Avatar>
                <ChatIcon />
              </Avatar>
            </ListItemAvatar>
            <ListItemText
              primary={chat.name}
              secondary={
                <>
                  {chat.lastMessage && (
                    <Typography component="span" variant="body2" color="text.primary">
                      {chat.lastMessage}
                    </Typography>
                  )}
                  {chat.lastMessageTime && ` — ${new Date(chat.lastMessageTime).toLocaleTimeString()}`}
                </>
              }
            />
          </ListItem>
        ))}
      </List>
    </Box>
  );
};

export default ChatList; 