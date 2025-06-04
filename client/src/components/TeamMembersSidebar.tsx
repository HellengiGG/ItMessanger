import React, { useEffect, useState } from 'react';
import {
    Box,
    Typography,
    List,
    ListItem,
    ListItemButton,
    ListItemText,
    Divider,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { userService } from '../services/api';
import { User } from '../types';

interface Props {
    teamName?: string;
}

const TeamMembersSidebar: React.FC<Props> = ({ teamName }) => {
    const [teamMembers, setTeamMembers] = useState<User[]>([]);
    const [allUsers, setAllUsers] = useState<User[]>([]);
    const navigate = useNavigate();
    const me = localStorage.getItem('username');

    useEffect(() => {
        if (teamName) {
            userService.getUsersByTeam(teamName)
                .then((data: User[]) => setTeamMembers(data.filter((u: User) => u.username !== me)))
                .catch(console.error);
        }

        userService.getAllUsers()
            .then((data: User[]) => setAllUsers(data.filter((u: User) => u.username !== me)))
            .catch(console.error);
    }, [teamName, me]);

    const renderUser = (u: User) => (
        <ListItem key={u.username} disablePadding>
            <ListItemButton onClick={() => navigate(`/private/${u.username}`)}>
                <ListItemText
                    primary={`${u.firstName} ${u.lastName ?? ''}`}
                    secondary={`${u.teamRole}, ${u.experienceLevel}`}
                />
            </ListItemButton>
        </ListItem>
    );

    return (
        <Box sx={{ width: 300, bgcolor: '#f9f9f9', p: 2, borderLeft: '1px solid #ccc' }}>
            {teamName && (
                <>
                    <Typography variant="subtitle1" gutterBottom>
                        Участники команды
                    </Typography>
                    <List dense>
                        {teamMembers.map(renderUser)}
                    </List>
                    <Divider sx={{ my: 2 }} />
                </>
            )}

            <Typography variant="subtitle1" gutterBottom>
                Все пользователи
            </Typography>
            <List dense>
                {allUsers.map(renderUser)}
            </List>
        </Box>
    );
};

export default TeamMembersSidebar;
