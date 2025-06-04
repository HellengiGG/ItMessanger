import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { TextField, Button, Box, Typography, Container } from '@mui/material';
import { LoginRequest } from '../types';
import { authService, chatService } from '../services/api';


const Login: React.FC = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState<LoginRequest>({
        username: '',
        password: '',
    });
    const [error, setError] = useState<string>('');

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await authService.login(formData);

            // ⬇️ после логина — получить список чатов
            const chats = await chatService.getChats();

            if (chats.length > 0) {
                // ⬇️ перенаправляем в первый чат (командный — будет первым)
                navigate(`/chat/${chats[0].id}`);
            } else {
                // если чатов нет — просто редирект
                navigate('/chat');
            }
        } catch (err) {
            setError('Неверное имя пользователя или пароль');
        }
    };


    return (
        <Container component="main" maxWidth="xs">
            <Box
                sx={{
                    marginTop: 8,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                }}
            >
                <Typography component="h1" variant="h5">
                    Вход в систему
                </Typography>
                <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        id="username"
                        label="Имя пользователя"
                        name="username"
                        autoComplete="username"
                        autoFocus
                        value={formData.username}
                        onChange={handleChange}
                    />
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        name="password"
                        label="Пароль"
                        type="password"
                        id="password"
                        autoComplete="current-password"
                        value={formData.password}
                        onChange={handleChange}
                    />
                    {error && (
                        <Typography color="error" sx={{ mt: 2 }}>
                            {error}
                        </Typography>
                    )}
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        sx={{ mt: 3, mb: 2 }}
                    >
                        Войти
                    </Button>
                    <Box sx={{ textAlign: 'center' }}>
                        <Typography variant="body2">
                            Нет аккаунта?{' '}
                            <Link to="/register" style={{ textDecoration: 'none' }}>
                                Зарегистрироваться
                            </Link>
                        </Typography>
                    </Box>
                </Box>
            </Box>
        </Container>
    );
};

export default Login; 