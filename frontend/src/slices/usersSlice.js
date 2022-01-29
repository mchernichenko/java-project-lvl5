/* eslint-disable no-param-reassign */
import axios from 'axios';
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import routes from '../routes.js';

import getLogger from '../lib/logger.js';

const log = getLogger('slice users');
log.enabled = true;

export const fetchUsers = createAsyncThunk(
  'users/fetchUsers',
  async () => {
    const response = await axios.get(routes.apiUsers());
    return response.data;
  },
);

const initialState = {
  users: null,
  status: 'idle',
  error: null,
};

export const usersSlice = createSlice({
  name: 'users',
  initialState,
  reducers: {
    addUsers(state, { payload }) {
      state.users = payload;
    },
    addUser(state, { payload }) {
      state.users.push(payload);
    },
    updateUser(state, { payload }) {
      const index = state.users.findIndex((user) => user.id.toString() === payload.id.toString());
      state.users[index] = payload;
    },
    removeUser(state, { payload }) {
      const id = payload;
      state.users = state.users.filter((user) => user.id.toString() !== id.toString());
    },
  },
  extraReducers: (builder) => {
    builder
      // get users
      .addCase(fetchUsers.pending, (state) => {
        log('pending users');
        state.status = 'loading';
        state.error = null;
      })
      .addCase(fetchUsers.rejected, (state, action) => {
        log('get users failed', action.error);
        state.status = 'failed';
        state.error = action.error;
      })
      .addCase(fetchUsers.fulfilled, (state, action) => {
        state.status = 'idle';
        state.error = null;
        state.users = action.payload;
      });
  },
});

export const { actions } = usersSlice;
export default usersSlice.reducer;
