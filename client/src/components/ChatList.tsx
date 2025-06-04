import React, { useEffect, useState } from 'react';
import { chatService } from '../services/api';
import { List, ListItem, ListItemButton, ListItemText, Divider, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';

interface Chat {
    id: number;
    name: string;
}

const ChatList: React.FC = () => {
    const [chats, setChats] = useState<Chat[]>([]);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchChats = async () => {
            try {
                const data = await chatService.getChats();
                setChats(data);
            } catch (err) {
                console.error('Ошибка при получении чатов', err);
            }
        };
        fetchChats();
    }, []);

    return (
        <List sx={{ width: 300 }}>
            <Typography variant="h6" sx={{ m: 1 }}>Мои чаты</Typography>
            <Divider />
            {chats.map((chat) => (
                <ListItem key={chat.id} disablePadding>
                    <ListItemButton onClick={() => navigate(`/chat/${chat.id}`)}>
                        <ListItemText primary={chat.name} />
                    </ListItemButton>
                </ListItem>
            ))}
        </List>
    );
};

export default ChatList;
