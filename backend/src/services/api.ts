import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const authService = {
  login: async (username: string, password: string) => {
    const response = await api.post('/auth/login', { username, password });
    localStorage.setItem('token', response.data.token);
    return response.data;
  },

  register: async (userData: any) => {
    const response = await api.post('/auth/register', userData);
    localStorage.setItem('token', response.data.token);
    return response.data;
  },

  logout: () => {
    localStorage.removeItem('token');
  },
};

export const chatService = {
  getUserChats: async () => {
    const response = await api.get('/chats');
    return response.data;
  },

  getChat: async (chatId: number) => {
    const response = await api.get(`/chats/${chatId}`);
    return response.data;
  },

  createChat: async (chatData: any) => {
    const response = await api.post('/chats', chatData);
    return response.data;
  },
};

export const messageService = {
  getChatMessages: async (chatId: number) => {
    const response = await api.get(`/messages/chat/${chatId}`);
    return response.data;
  },

  sendMessage: async (messageData: any) => {
    const response = await api.post('/messages', messageData);
    return response.data;
  },
}; 