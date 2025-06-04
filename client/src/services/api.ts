import axios from 'axios';
import {
    LoginRequest,
    RegisterRequest,
    MessageRequest,
    MessageUpdateRequest,
    Message,
    PrivateMessage,
} from '../types';

const API_URL = 'http://localhost:8083/api';

const api = axios.create({
    baseURL: API_URL,
    headers: { 'Content-Type': 'application/json' },
    withCredentials: true,
});

/* ——— автоматическая передача JWT ——— */
api.interceptors.request.use(cfg => {
    const token = localStorage.getItem('token');
    if (token) cfg.headers.Authorization = `Bearer ${token}`;
    return cfg;
});

/* ──────────── auth ─────────────────── */
export const authService = {
    login: async (data: LoginRequest) => {
        const { data: res } = await api.post('/auth/login', data);
        localStorage.setItem('token', res.token);
        localStorage.setItem('username', res.username);
        return res;
    },
    register: (d: RegisterRequest) => api.post('/auth/register', d).then(r => r.data),
    logout: () => localStorage.removeItem('token'),
};

/* ──────────── групповые чаты ────────── */
export const chatService = {
    getChats: () => api.get('/chats').then(r => r.data),
};

/* ──────────── пользователи ──────────── */
export const userService = {
    getAllUsers: () => api.get('/users').then(r => r.data),
    getUsersByTeam: (teamName: string) =>
        api.get(`/users/by-team/${teamName}`).then(r => r.data),
};

/* ──────────── приватные сообщения ───── */
export const privateMessageService = {
    /* чтение */
    getMessagesWith: (username: string) =>
        api.get<PrivateMessage[]>(`/private-messages/${username}`).then(r => r.data),

    /* отправка текста */
    sendMessage: (receiver: string, content: string) =>
        api.post('/private-messages', { receiverUsername: receiver, content }),

    /* редактирование */
    updateMessage: (id: number, content: string) =>
        api.patch(`/private-messages/${id}`, content),

    /* удаление */
    deleteMessage: (id: number) => api.delete(`/private-messages/${id}`),

    /* отправка файла */
    uploadFile: (file: File, receiver: string) => {
        const fd = new FormData();
        fd.append('file', file);
        fd.append('receiver', receiver);

        return api.post('/private-messages/upload', fd, {
            headers: { 'Content-Type': 'multipart/form-data' },
            withCredentials: true, // ← это важно!
        });
    }
,
};

/* ──────────── групповые сообщения ───── */
export const messageService = {
    getMessagesByChatId: (chatId: number): Promise<Message[]> =>
        api.get(`/messages/chat/${chatId}`).then(r => r.data),

    sendMessage: (d: MessageRequest) => api.post('/messages', d).then(r => r.data),

    updateMessage: (id: number, d: MessageUpdateRequest) =>
        api.patch(`/messages/${id}`, d).then(r => r.data),

    deleteMessage: (id: number) => api.delete(`/messages/${id}`),

    uploadFile: (form: FormData) =>
        api.post('/messages/upload', form, {
            headers: { 'Content-Type': 'multipart/form-data' },
        }),
};
