import React, { useEffect, useState } from 'react';
import {
    Box,
    Typography,
    Paper,
    List,
    ListItem,
    ListItemText,
    TextField,
    IconButton,
    Button,
    ListItemSecondaryAction,
} from '@mui/material';
import SendIcon from '@mui/icons-material/Send';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import SaveIcon from '@mui/icons-material/Save';
import { useParams } from 'react-router-dom';
import { privateMessageService } from '../services/api';
import { PrivateMessage } from '../types';

const PrivateChat: React.FC = () => {
    const { username } = useParams<{ username: string }>();
    const me = localStorage.getItem('username')!;
    const [messages, setMessages] = useState<PrivateMessage[]>([]);
    const [text, setText] = useState('');
    const [editingId, setEditingId] = useState<number | null>(null);
    const [editText, setEditText] = useState('');

    const load = async () => {
        if (username) {
            setMessages(await privateMessageService.getMessagesWith(username));
        }
    };

    useEffect(() => {
        load();
        const id = setInterval(load, 3000);
        return () => clearInterval(id);
    }, [username,load]);

    const send = async () => {
        if (!text.trim() || !username) return;
        await privateMessageService.sendMessage(username, text);
        setText('');
        load();
    };

    const saveEdit = async (id: number, content: string) => {
        await privateMessageService.updateMessage(id, content);
        setEditingId(null);
        setEditText('');
        load();
    };

    const remove = async (id: number) => {
        if (window.confirm('Удалить сообщение?')) {
            await privateMessageService.deleteMessage(id);
            load();
        }
    };

    return (
        <Box sx={{ p: 2, height: '100%' }}>
            <Typography variant="h6" gutterBottom>
                Личная переписка с {username}
            </Typography>

            <Paper sx={{ flex: 1, overflow: 'auto', mb: 1, p: 1, height: 400 }}>
                <List>
                    {messages.map(m => {
                        const mine = m.senderUsername === me;
                        const isEditing = editingId === m.id;
                        return (
                            <ListItem
                                key={m.id}
                                sx={{
                                    bgcolor: mine ? 'rgba(25,118,210,0.08)' : 'inherit',
                                    borderRadius: 1,
                                    mb: 0.5,
                                }}
                            >
                                {isEditing ? (
                                    <TextField
                                        fullWidth
                                        value={editText}
                                        onChange={e => setEditText(e.target.value)}
                                        size="small"
                                        onKeyDown={e =>
                                            e.key === 'Enter' && saveEdit(m.id, editText)
                                        }
                                    />
                                ) : (
                                    <ListItemText
                                        primary={m.fileUrl ? (
                                            <a href={m.fileUrl} target="_blank" rel="noreferrer">
                                                📎 {m.content}
                                            </a>
                                        ) : (
                                            m.content
                                        )}
                                        secondary={`${m.senderUsername}, ${new Date(
                                            m.updatedAt ?? m.timestamp,
                                        ).toLocaleString()}`}
                                    />
                                )}

                                {mine && (
                                    <ListItemSecondaryAction>
                                        {isEditing ? (
                                            <IconButton
                                                edge="end"
                                                aria-label="save"
                                                onClick={() => saveEdit(m.id, editText)}
                                            >
                                                <SaveIcon fontSize="small" />
                                            </IconButton>
                                        ) : (
                                            <>
                                                <IconButton
                                                    edge="end"
                                                    aria-label="edit"
                                                    onClick={() => {
                                                        setEditingId(m.id);
                                                        setEditText(m.content);
                                                    }}
                                                >
                                                    <EditIcon fontSize="small" />
                                                </IconButton>
                                                <IconButton
                                                    edge="end"
                                                    aria-label="delete"
                                                    onClick={() => remove(m.id)}
                                                >
                                                    <DeleteIcon fontSize="small" />
                                                </IconButton>
                                            </>
                                        )}
                                    </ListItemSecondaryAction>
                                )}
                            </ListItem>
                        );
                    })}
                </List>
            </Paper>

            <Box sx={{ display: 'flex', gap: 1 }}>
                <TextField
                    fullWidth
                    placeholder="Сообщение…"
                    value={text}
                    onChange={e => setText(e.target.value)}
                    onKeyDown={e => e.key === 'Enter' && send()}
                />
                <Button variant="contained" endIcon={<SendIcon />} onClick={send}>
                    Отпр.
                </Button>
            </Box>
        </Box>
    );
};

export default PrivateChat;
