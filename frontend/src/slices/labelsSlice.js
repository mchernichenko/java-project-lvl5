/* eslint-disable no-param-reassign */
import axios from 'axios';
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import routes from '../routes.js';

import getLogger from '../lib/logger.js';

const log = getLogger('slice labels');
log.enabled = true;

export const fetchLabels = createAsyncThunk(
  'labels/fetchLabels',
  async (auth) => {
    const response = await axios.get(routes.apiLabels(), { headers: auth.getAuthHeader() });
    log(response);
    return response.data;
  },
);

export const fetchLabel = createAsyncThunk(
  'labels/fetchLabel',
  async (id, auth) => {
    const response = await axios.get(`${routes.apiLabels()}/${id}`, { headers: auth.getAuthHeader() });
    return response.data;
  },
);

const initialState = {
  labels: null,
  label: null,
  status: 'idle',
  error: null,
};

export const labelsSlice = createSlice({
  name: 'labels',
  initialState,
  reducers: {
    addLabels(state, { payload }) {
      state.labels = payload;
    },
    addLabel(state, { payload }) {
      state.labels.push(payload);
    },
    updateLabel(state, { payload }) {
      const index = state.labels
        .findIndex((label) => label.id.toString() === payload.id.toString());
      state.labels[index] = payload;
    },
    removeLabel(state, { payload }) {
      const id = payload;
      state.labels = state.labels.filter((label) => label.id.toString() !== id.toString());
    },
  },
  extraReducers: (builder) => {
    builder
      // get labels
      .addCase(fetchLabels.pending, (state) => {
        log('pending labels');
        state.status = 'loading';
        state.error = null;
      })
      .addCase(fetchLabels.rejected, (state, action) => {
        log('get labels failed', action.error);
        state.status = 'failed';
        state.error = action.error;
      })
      .addCase(fetchLabels.fulfilled, (state, action) => {
        state.status = 'idle';
        state.error = null;
        state.labels = action.payload;
      })

      // get label
      .addCase(fetchLabel.pending, (state) => {
        log('pending labels');
        state.status = 'loading';
        state.error = null;
      })
      .addCase(fetchLabel.rejected, (state, action) => {
        log('get labels failed', action.error);
        state.status = 'failed';
        state.error = action.error;
      })
      .addCase(fetchLabel.fulfilled, (state, action) => {
        state.status = 'idle';
        state.error = null;
        state.label = action.payload;
      });
  },
});

export const { actions } = labelsSlice;
export default labelsSlice.reducer;
