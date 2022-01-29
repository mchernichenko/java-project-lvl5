/* eslint-disable no-param-reassign */
import axios from 'axios';
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import routes from '../routes.js';

import getLogger from '../lib/logger.js';

const log = getLogger('slice tasks');
log.enabled = true;

export const fetchTasks = createAsyncThunk(
  'tasks/fetchTasks',
  async (auth) => {
    const response = await axios.get(routes.apiTasks(), { headers: auth.getAuthHeader() });
    return response.data;
  },
);

const initialState = {
  tasks: null,
  task: null,
  filteredTasks: null,
  status: 'idle',
  error: null,
};

export const tasksSlice = createSlice({
  name: 'tasks',
  initialState,
  reducers: {
    addTasks(state, { payload }) {
      state.tasks = payload;
    },
    addTask(state, { payload }) {
      state.tasks.push(payload);
    },
    updateTask(state, { payload }) {
      const index = state.tasks.findIndex((task) => task.id.toString() === payload.id.toString());
      state.tasks[index] = payload;
    },
    removeTask(state, { payload }) {
      const id = payload;
      state.tasks = state.tasks.filter((task) => task.id.toString() !== id.toString());
    },
  },
  extraReducers: (builder) => {
    builder
      // get tasks
      .addCase(fetchTasks.pending, (state) => {
        log('pending tasks');
        state.status = 'loading';
        state.error = null;
      })
      .addCase(fetchTasks.rejected, (state, action) => {
        log('get tasks failed', action.error);
        state.status = 'failed';
        state.error = action.error;
      })
      .addCase(fetchTasks.fulfilled, (state, action) => {
        state.status = 'idle';
        state.error = null;
        state.tasks = action.payload;
      });
  },
});

export const { actions } = tasksSlice;
export default tasksSlice.reducer;
