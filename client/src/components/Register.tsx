import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, TextField, Button, Typography, MenuItem, Select, FormControl, InputLabel } from '@mui/material';
import { authService } from '../services/api';
import { RegisterRequest } from '../types';

const Register: React.FC = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState<RegisterRequest>({
        username: '',
        password: '',
        email: '',
        firstName: '',
        lastName: '',
        teamRole: '',
        experienceLevel: '',
        teamName: ''
    });
    const [error, setError] = useState<string>('');

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await authService.register(formData);
            navigate('/login');
        } catch (err) {
            setError('Ошибка при регистрации');
        }
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement> | any) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    return (
        <Box sx={{ maxWidth: 400, margin: 'auto', p: 2 }}>
            <Typography variant="h4" sx={{ mb: 2 }}>Регистрация</Typography>
            
            {error && (
                <Typography color="error" sx={{ mb: 2 }}>
                    {error}
                </Typography>
            )}

            <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <TextField
                    required
                    label="Имя пользователя"
                    name="username"
                    value={formData.username}
                    onChange={handleChange}
                />
                <TextField
                    required
                    label="Пароль"
                    name="password"
                    type="password"
                    value={formData.password}
                    onChange={handleChange}
                />
                <TextField
                    required
                    label="Email"
                    name="email"
                    type="email"
                    value={formData.email}
                    onChange={handleChange}
                />
                <TextField
                    label="Имя"
                    name="firstName"
                    value={formData.firstName}
                    onChange={handleChange}
                />
                <TextField
                    label="Фамилия"
                    name="lastName"
                    value={formData.lastName}
                    onChange={handleChange}
                />
                <FormControl required>
                    <InputLabel>Роль в команде</InputLabel>
                    <Select
                        name="teamRole"
                        value={formData.teamRole}
                        onChange={handleChange}
                        label="Роль в команде"
                    >
                        <MenuItem value="DEVELOPER">Разработчик</MenuItem>
                        <MenuItem value="PM">Менеджер проекта</MenuItem>
                        <MenuItem value="ANALYST">Аналитик</MenuItem>
                        <MenuItem value="QA">Тестировщик</MenuItem>
                        <MenuItem value="DEVOPS">DevOps инженер</MenuItem>
                        <MenuItem value="DESIGNER">Дизайнер</MenuItem>
                    </Select>
                </FormControl>
                <FormControl required>
                    <InputLabel>Уровень</InputLabel>
                    <Select
                        name="experienceLevel"
                        value={formData.experienceLevel}
                        onChange={handleChange}
                        label="Уровень"
                    >
                        <MenuItem value="JUNIOR">Junior</MenuItem>
                        <MenuItem value="MIDDLE">Middle</MenuItem>
                        <MenuItem value="SENIOR">Senior</MenuItem>
                    </Select>
                </FormControl>
                <TextField
                    required
                    label="Название команды"
                    name="teamName"
                    value={formData.teamName}
                    onChange={handleChange}
                />
                <Button type="submit" variant="contained">
                    Зарегистрироваться
                </Button>
            </Box>
        </Box>
    );
};

export default Register; 