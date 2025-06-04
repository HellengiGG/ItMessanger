import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material';
import CssBaseline from '@mui/material/CssBaseline';
import Login from './components/Login';
import Register from './components/Register';
import ChatLayout from './components/ChatLayout';
import Chat from './components/Chat';
import PrivateChat from './components/PrivateChat';
import UserList from './components/UserList';


const theme = createTheme({
    palette: {
        mode: 'light',
        primary: {
            main: '#1976d2',
        },
        secondary: {
            main: '#dc004e',
        },
    },
});

const App: React.FC = () => {
    return (
        <ThemeProvider theme={theme}>
            <CssBaseline />
            <Router>
                <Routes>
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                    <Route path="/chat" element={<ChatLayout />}>
                        <Route path=":chatId" element={<Chat />} />
                    </Route>
                    <Route path="/" element={<Navigate to="/login" replace />} />
                    <Route path="/users" element={<UserList />} />
                    <Route path="/private/:username" element={<ChatLayout />}>
                        <Route index element={<PrivateChat />} />
                    </Route>
                </Routes>
            </Router>
        </ThemeProvider>
    );
};

export default App;
