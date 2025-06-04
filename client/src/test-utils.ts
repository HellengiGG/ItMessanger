import React from 'react';
import { render } from '@testing-library/react';

// Mock function for API calls
export const mockApiCall = async <T>(data: T, delay = 0): Promise<T> => {
  return new Promise((resolve) => {
    setTimeout(() => resolve(data), delay);
  });
};

// Mock function for error responses
export const mockErrorResponse = async (error: Error, delay = 0): Promise<never> => {
  return new Promise((_, reject) => {
    setTimeout(() => reject(error), delay);
  });
};

// Re-export everything from testing-library
export * from '@testing-library/react'; 