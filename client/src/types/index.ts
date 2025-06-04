export interface User {
    id: number;
    username: string;
    role: string;
    firstName?: string;
    lastName?: string;
    email: string;
    teamRole: string;
    experienceLevel: string;
    team: Team;
}

export interface Team {
    id: number;
    name: string;
    description: string;
}

export interface Message {
    id: number;
    content: string;
    fileUrl?: string;       // ← ссылка, если это файл
    senderUsername: string;
    senderRole: string;
    createdAt: string;
    updatedAt?: string;
    chatId: number;
}


export interface MessageRequest {
    content: string;
    senderUsername: string;
    chatId: number;
}


export interface LoginRequest {
    username: string;
    password: string;
}

export interface RegisterRequest {
    username: string;
    password: string;
    email: string;
    firstName?: string;
    lastName?: string;
    teamRole: string;
    experienceLevel: string;
    teamName: string;
}

export interface MessageUpdateRequest {
    content: string;
}

export interface PrivateMessage {
    id: number;
    senderUsername: string;
    receiverUsername: string;
    content: string;          // текст *или* название файла
    timestamp: string;
    updatedAt?: string | null;
    fileUrl?: string | null;  // приходит с бэка, если это файл
}


