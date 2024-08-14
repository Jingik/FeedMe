import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';

export const fetchFriendsList = createAsyncThunk(
  'friends/fetchFriendsList', 
  async (token, { rejectWithValue }) => {
    try {
      const response = await axios.get('http://localhost:8080/friends/chats', {
        headers: {
          Authorization: `${token}`,
        },
      });
      
      return response.data.map(friend => ({
        friendId: friend.id, // id를 friendId로 변경
        counterpartNickname: friend.nickname, // nickname을 counterpartNickname으로 변경
        avatar: friend.creatureImage, // creatureImage를 avatar로 변경
        isChecked: friend.isChecked, // 추가된 isChecked 필드
      }));
    } catch (error) {
      return rejectWithValue(error.response.data);
    }
  }
);

const friendsSlice = createSlice({
  name: 'friends',
  initialState: {
    list: [], 
    status: 'idle', 
    error: null, 
  },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchFriendsList.pending, (state) => {
        state.status = 'loading';
      })
      .addCase(fetchFriendsList.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.list = action.payload; // 가공된 데이터를 state.list에 저장
      })
      .addCase(fetchFriendsList.rejected, (state, action) => {
        state.status = 'failed'; 
        state.error = action.payload; 
      });
  },
});

export default friendsSlice.reducer;
