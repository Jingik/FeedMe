import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';

export const fetchFriendInfo = createAsyncThunk(
  'friendInfo/fetchFriendInfo',
  async ({ token, counterpartNickname }, { rejectWithValue }) => {
    try {
      // console.log({ token, counterpartNickname });

      const response = await axios({
        method: 'get',
        url: `https://i11b104.p.ssafy.io/api/friends/info`, 
        headers: {
          Authorization: `${token}`,
        },
        params: {
          counterpartNickname: counterpartNickname,
        }
      });

      // console.log(response.data);

      return response.data;
    } catch (error) {
      console.error(error);
      return rejectWithValue(error.response ? error.response.data : error.message); 
    }
  }
);

// 친구 삭제
export const deleteFriend = createAsyncThunk(
  'friendInfo/deleteFriend',
  async ({ token, counterpartNickname }, { rejectWithValue }) => {
    try {
      const response = await axios.delete(`https://i11b104.p.ssafy.io/api/friends`, {
        headers: {
          Authorization: `${token}`,
        },
        params: {
          counterpartNickname: counterpartNickname,
        },
      });
      return response.status;
    } catch (error) {
      return rejectWithValue(error.response ? error.response.data : error.message);
    }
  }
);


const friendInfoSlice = createSlice({
  name: 'friendInfo',
  initialState: {
    friendId: null,
    roomId: '',
    nickname: '',
    creatureNickname: '',
    creatureImg: '',
    level: 0,
    exp: 0,
    join: 0,
    status: 'idle',
    error: null,
  },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchFriendInfo.pending, (state) => {
        state.status = 'loading';
      })
      .addCase(fetchFriendInfo.fulfilled, (state, action) => {
        state.status = 'succeeded';
        const { friendId, roomId, nickname, creatureNickname, creatureImg, level, exp, join } = action.payload;
        state.friendId = friendId;
        state.roomId = roomId;
        state.nickname = nickname;
        state.creatureNickname = creatureNickname;
        state.creatureImg = creatureImg;
        state.level = level;
        state.exp = exp;
        state.join = join;
      })
      .addCase(fetchFriendInfo.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload;
      })
      .addCase(deleteFriend.pending, (state) => {
        state.status = 'loading';
      })
      .addCase(deleteFriend.fulfilled, (state) => {
        state.status = 'succeeded';
      })
      .addCase(deleteFriend.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload;
      });
  },
});

export default friendInfoSlice.reducer;
