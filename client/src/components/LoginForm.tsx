import React, { useState } from 'react';
import { TextField, Button, Box, Typography } from '@mui/material';

interface LoginFormProps {
  onLogin?: (credentials: { username: string; password: string }) => Promise<void>;
}

const LoginForm: React.FC<LoginFormProps> = ({ onLogin }) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    try {
      if (onLogin) {
        await onLogin({ username, password });
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'An error occurred');
    }
  };

  return (
    <Box component="form" onSubmit={handleSubmit} sx={{ maxWidth: 400, mx: 'auto', p: 3 }}>
      <Typography variant="h5" component="h1" gutterBottom>
        Login
      </Typography>
      
      {error && (
        <Typography color="error" sx={{ mb: 2 }}>
          {error}
        </Typography>
      )}

      <TextField
        fullWidth
        label="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        margin="normal"
        required
      />

      <TextField
        fullWidth
        label="Password"
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        margin="normal"
        required
      />

      <Button
        type="submit"
        variant="contained"
        fullWidth
        sx={{ mt: 3 }}
      >
        Login
      </Button>
    </Box>
  );
};

export default LoginForm; 