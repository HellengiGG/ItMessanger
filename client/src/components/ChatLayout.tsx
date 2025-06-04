import React, { useEffect, useState } from 'react';
import {
    Box,
    Divider,
    Typography,
    List,
    ListItem,
    ListItemButton,
    ListItemText,
    TextField,
} from '@mui/material';
import { Outlet, useNavigate, useParams} from 'react-router-dom';
import { chatService, privateMessageService, userService } from '../services/api';
import TeamMembersSidebar from './TeamMembersSidebar';

interface ChatItem {
    id: number | string;        // group → number, private → `private‑username`
    name: string;
    type: 'group' | 'private';
    teamName?: string;
}

const REFRESH_MS = 10_000;      // ← как часто опрашивать (10 секунд)

const ChatLayout: React.FC = () => {
    const [chats, setChats] = useState<ChatItem[]>([]);
    const [filtered, setFiltered] = useState<ChatItem[]>([]);
    const [search, setSearch] = useState('');
    const [activeTeam, setActiveTeam] = useState<string | undefined>();
    const navigate  = useNavigate();
    const { chatId } = useParams();
    const me        = localStorage.getItem('username');

    const load = async () => {
        try {
            /* групповые */
            const group: ChatItem[] = (await chatService.getChats()).map((c: any) => ({
                id: c.id,
                name: c.name,
                type: 'group',
                teamName: c.name,
            }));

            const all = await userService.getAllUsers();
            const priv: ChatItem[] = [];

            for (const u of all) {
                if (u.username === me) continue;
                const msgs = await privateMessageService.getMessagesWith(u.username);
                if (msgs.length > 0) {
                    priv.push({
                        id: `private-${u.username}`,
                        name: `${u.firstName} ${u.lastName ?? ''}`.trim() || u.username,
                        type: 'private',
                    });
                }
            }

            const combined = [...group, ...priv];
            setChats(combined);
        } catch (err) {
            console.error('Ошибка загрузки чатов:', err);
        }
    };

    /* ---------- первый рендер + авто‑обновление ---------- */
    useEffect(() => {
        load();                                 // сразу
        const id = setInterval(load, REFRESH_MS);
        return () => clearInterval(id);         // очищаем при размонтировании
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    /* ---------- поиск ---------- */
    useEffect(() => {
        setFiltered(
            chats.filter(c =>
                c.name.toLowerCase().includes(search.toLowerCase()),
            ),
        );
    }, [search, chats]);

    /* ---------- активная команда (для правой панели) ---------- */
    useEffect(() => {
        const current = chats.find(c => String(c.id) === chatId);
        setActiveTeam(current?.type === 'group' ? current.teamName : undefined);
    }, [chatId, chats]);

    const openChat = (chat: ChatItem) => {
        navigate(
            chat.type === 'private'
                ? `/private/${(chat.id as string).split('-')[1]}`
                : `/chat/${chat.id}`,
        );
    };

    return (
        <Box sx={{ display: 'flex', height: '100vh' }}>
            {/* слева: чаты + поиск */}
            <Box sx={{ width: 280, bgcolor: '#f5f5f5', p: 2, borderRight: '1px solid #ddd' }}>
                <Typography variant="h6">Мои чаты</Typography>

                <TextField
                    size="small"
                    fullWidth
                    placeholder="Поиск…"
                    sx={{ my: 1 }}
                    value={search}
                    onChange={e => setSearch(e.target.value)}
                />
                <Divider sx={{ mb: 1 }} />

                <List dense>
                    {filtered.map(chat => (
                        <ListItem key={chat.id} disablePadding>
                            <ListItemButton
                                selected={String(chat.id) === chatId}
                                onClick={() => openChat(chat)}
                            >
                                <ListItemText
                                    primary={chat.name}
                                    secondary={chat.type === 'private' ? 'личный' : 'команда'}
                                />
                            </ListItemButton>
                        </ListItem>
                    ))}
                </List>
            </Box>

            {/* центр – активный чат */}
            <Box sx={{ flex: 1, bgcolor: 'white' }}>
                <Outlet />
            </Box>

            {/* справа – участники команды (только для group) */}
            <TeamMembersSidebar teamName={activeTeam} />
        </Box>
    );
};

export default ChatLayout;
