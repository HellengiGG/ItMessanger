import React, { useEffect, useState } from 'react';
import { List, ListItem, ListItemButton, ListItemText, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { userService } from '../services/api';
import { User } from '../types';

const UserList: React.FC = () => {
    const [users, setUsers] = useState<User[]>([]);
    const navigate = useNavigate();
    const currentUsername = localStorage.getItem('username');

    useEffect(() => {
        userService.getAllUsers().then(setUsers).catch(console.error);
    }, []);

    return (
        <div style={{ padding: 20 }}>
            <Typography variant="h5" gutterBottom>
                Пользователи
            </Typography>

            <List>
                {users
                    .filter(u => u.username !== currentUsername) // исключаем себя
                    .map(u => (
                        <ListItem key={u.username} disablePadding>
                            <ListItemButton onClick={() => navigate(`/private/${u.username}`)}>
                                <ListItemText primary={`${u.firstName} ${u.lastName ?? ''} (${u.username})`} />
                            </ListItemButton>
                        </ListItem>
                    ))}
            </List>
        </div>
    );
};

export default UserList;
