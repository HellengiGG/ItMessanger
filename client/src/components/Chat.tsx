import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
    Box, TextField, Button, Typography, Paper, List, ListItem,
    ListItemText, ListItemAvatar, Avatar, IconButton, Menu, MenuItem
} from '@mui/material';
import MoreVert from '@mui/icons-material/MoreVert';
import AttachFile from '@mui/icons-material/AttachFile';
import { messageService, authService } from '../services/api';
import { Message, MessageRequest } from '../types';

const Chat: React.FC = () => {
    const navigate = useNavigate();
    const { chatId } = useParams<{ chatId: string }>();

    const [messages, setMessages] = useState<Message[]>([]);
    const [newMessage, setNewMessage] = useState('');
    const [error, setError] = useState('');
    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
    const [editingMsg, setEditingMsg] = useState<Message | null>(null);

    const username = localStorage.getItem('username') || '';

    /* ───────── загрузка сообщений ───────── */
    useEffect(() => {
        const token = localStorage.getItem('token');
        if (!token) { navigate('/login'); return; }

        const fetch = async () => {
            try {
                if (chatId) {
                    setMessages(await messageService.getMessagesByChatId(+chatId));
                }
            } catch (err: any) {
                if (err.response?.status === 401) { authService.logout(); navigate('/login'); }
                else setError('Ошибка при загрузке сообщений');
            }
        };
        fetch();
        const id = setInterval(fetch, 3000);
        return () => clearInterval(id);
    }, [navigate, chatId]);

    /* ───────── отправка / редактирование ───────── */
    const handleSendMessage = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!newMessage.trim() || !chatId) return;

        try {
            if (editingMsg) {
                await messageService.updateMessage(editingMsg.id, { content: newMessage.trim() });
                setEditingMsg(null);
            } else {
                const req: MessageRequest = { content: newMessage.trim(), senderUsername: username, chatId: +chatId };
                await messageService.sendMessage(req);
            }
            setNewMessage('');
            setMessages(await messageService.getMessagesByChatId(+chatId));
        } catch { setError('Ошибка при отправке сообщения'); }
    };

    /* ───────── загрузка файла ───────── */
    const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (!file || !chatId) return;

        const form = new FormData();
        form.append('file', file);
        form.append('chatId', chatId);

        try {
            await messageService.uploadFile(form);// ⬅️ вот сюда
            setMessages(await messageService.getMessagesByChatId(+chatId));
        } catch {
            setError('Ошибка загрузки файла');
        }
    };

    /* ───────── меню «⋮» ───────── */
    const openMenu = Boolean(anchorEl);
    const handleOpenMenu = (e: React.MouseEvent<HTMLButtonElement>, m: Message) => { setAnchorEl(e.currentTarget); setEditingMsg(m); };
    const handleCloseMenu = () => { setAnchorEl(null); setEditingMsg(null); };

    const handleDelete = async () => {
        if (editingMsg) {
            await messageService.deleteMessage(editingMsg.id);
            setMessages(messages.filter(m => m.id !== editingMsg.id));
        }
        handleCloseMenu();
    };

    const handleEdit = () => { if (editingMsg) setNewMessage(editingMsg.content); setAnchorEl(null); };

    const handleLogout = () => { authService.logout(); navigate('/login'); };

    return (
        <Box sx={{ maxWidth: 800, m: 'auto', p: 2 }}>
            {/* Header */}
            <Box sx={{ display:'flex', justifyContent:'space-between', mb:2 }}>
                <Typography variant="h4">Чат</Typography>
                <Button variant="outlined" onClick={handleLogout}>Выйти</Button>
            </Box>

            {error && <Typography color="error" sx={{ mb:2 }}>{error}</Typography>}

            {/* Список сообщений */}
            <Paper sx={{ height:'60vh', overflow:'auto', mb:2 }}>
                <List>
                    {messages.map(m => {
                        const mine = m.senderUsername === username;
                        return (
                            <ListItem key={m.id} alignItems="flex-start" sx={mine ? { bgcolor:'#e3f2fd' } : {}}>
                                <ListItemAvatar><Avatar>{m.senderUsername.charAt(0)}</Avatar></ListItemAvatar>

                                <ListItemText
                                    primary={`${m.senderUsername} (${m.senderRole})`}
                                    secondary={
                                        m.fileUrl ? (
                                                /\.(png|jpe?g|gif)$/i.test(m.fileUrl) ? (
                                                    <a href={`http://localhost:8083${m.fileUrl}`} target="_blank" rel="noreferrer">
                                                        <img src={`http://localhost:8083${m.fileUrl}`} alt={m.content}
                                                             style={{ maxWidth: 200, maxHeight: 200 }} />
                                                    </a>
                                                ) : (
                                                    <a href={`http://localhost:8083${m.fileUrl}`} target="_blank" rel="noreferrer">
                                                        {m.content || 'Скачать файл'}
                                                    </a>
                                                )
                                            ) : (
                                                <Typography component="span" variant="body2" color="text.primary">
                                                    {m.content}
                                                </Typography>
                                            )
                                    }
                                />

                                {mine &&
                                    <IconButton edge="end" onClick={e => handleOpenMenu(e,m)}>
                                        <MoreVert/>
                                    </IconButton>}
                            </ListItem>
                        );
                    })}
                </List>
            </Paper>

            {/* меню ⋮ */}
            <Menu open={openMenu} anchorEl={anchorEl} onClose={handleCloseMenu}>
                <MenuItem onClick={handleEdit}>Редактировать</MenuItem>
                <MenuItem onClick={handleDelete}>Удалить</MenuItem>
            </Menu>

            {/* форма ввода */}
            <Box component="form" onSubmit={handleSendMessage} sx={{ display:'flex', gap:1 }}>
                <Button component="label" variant="outlined">
                    <AttachFile/>
                    <input type="file" hidden onChange={handleFileUpload}/>
                </Button>

                <TextField fullWidth placeholder="Введите сообщение…"
                           value={newMessage} onChange={e=>setNewMessage(e.target.value)}/>
                <Button type="submit" variant="contained">
                    {editingMsg ? 'Сохранить' : 'Отправить'}
                </Button>
            </Box>
        </Box>
    );
};

export default Chat;
