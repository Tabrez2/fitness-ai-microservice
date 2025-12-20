// src/store/store.js

import { configureStore } from '@reduxjs/toolkit';
import { authReducer } from './authSlice.js'; // Example import

export const store = configureStore({ // <-- MUST BE 'export const store'
  reducer: {
    auth: authReducer,
    // ... other reducers
  },
});
// No other 'export' is needed for the store itself.