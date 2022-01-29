/* eslint-disable no-param-reassign */
import { createSlice } from '@reduxjs/toolkit';
// import { fetchUsers } from './usersSlice.js';
// import { actions as usersActions } from './usersSlice.js';

import getLogger from '../lib/logger.js';

const log = getLogger('slice notifications');
log.enabled = true;

const initialState = {
  messages: [],
};

export const notificationsSlice = createSlice({
  name: 'notifications',
  initialState,
  reducers: {
    addMessage(state, { payload }) {
      state.messages = payload;
    },
    addMessages(state, { payload }) {
      state.messages = payload;
    },
    clean(state) {
      state.messages = [];
    },
  },
});

export const { actions } = notificationsSlice;
export default notificationsSlice.reducer;
