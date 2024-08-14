import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';

export const fetchFeedList = createAsyncThunk(
  'feedList/fetchFeedList',
  async (token, { rejectWithValue }) => {
    try {
      const response = await axios.get('http://localhost:8080/feed/recent/friends', {
        headers: {
          Authorization: `${token}`,
        },
      });
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response.data);
    }
  }
);

// 댓글 작성하기
export const postComment = createAsyncThunk(
  'feedList/postComment',
  async ({ token, feedId, content }, { rejectWithValue }) => {
    try {
      const response = await axios.post(`http://localhost:8080/feedComment/${feedId}`, 
      { content },
      {
        headers: {
          Authorization: `${token}`,
        },
      });
      return { feedId, comment: response.data };
    } catch (error) {
      return rejectWithValue(error.response.data);
    }
  }
);

// 댓글 삭제
export const deleteComment = createAsyncThunk(
  'feedList/deleteComment',
  async ({ token, feedComentId }, { rejectWithValue }) => {
    try {
      await axios.delete(`http://localhost:8080/feedComment/${feedComentId}`, {
        headers: {
          Authorization: `${token}`,
        },
      });
      return feedComentId;
    } catch (error) {
      return rejectWithValue(error.response.data);
    }
  }
);

// 댓글 수정
export const editComment = createAsyncThunk(
  'feedList/editComment',
  async ({ token, feedComentId, updatedComment }, { rejectWithValue }) => {
    try {
      const response = await axios.patch(`http://localhost:8080/feedComment/${feedComentId}`, 
      updatedComment,
      {
        headers: {
          Authorization: `${token}`,
        },
      });
      return { feedComentId, updatedComment: response.data };
    } catch (error) {
      return rejectWithValue(error.response.data);
    }
  }
);

// 게시글 삭제
export const deleteFeed = createAsyncThunk(
  'feedList/deleteFeed',
  async ({ token, feedId }, { rejectWithValue }) => {
    try {
      await axios.delete(`http://localhost:8080/feed/${feedId}`, {
        headers: {
          Authorization: `${token}`,
        },
      });
      return feedId;
    } catch (error) {
      return rejectWithValue(error.response.data);
    }
  }
);

// 게시글 수정
export const editFeed = createAsyncThunk(
  'feedList/editFeed',
  async ({ token, feedId, content }, { rejectWithValue }) => {
    try {
      const response = await axios.patch(`http://localhost:8080/feed/${feedId}`, 
      { content },
      {
        headers: {
          Authorization: `${token}`, 
        },
      });
      return { feedId, updatedFeed: response.data }; 
    } catch (error) {
      return rejectWithValue(error.response.data); 
    }
  }
);

// 좋아요
export const likePost = createAsyncThunk(
  'feedList/likePost',
  async ({ token, feedId }, { rejectWithValue }) => {
    try {
      const response = await axios.post(
        `http://localhost:8080/feed/${feedId}/like`,
        {},
        {
          headers: {
            Authorization: `${token}`,
          },
        }
      );
      
      if (response.status === 200) {
        return feedId; 
      } else {
        throw new Error('Failed to like/unlike the post');
      }
    } catch (error) {
      return rejectWithValue(error.response?.data || 'Server Error');
    }
  }
);

const feedListSlice = createSlice({
  name: 'feedList',
  initialState: {
    feeds: [],
    status: 'idle',
    error: null,
  },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchFeedList.pending, (state) => {
        state.status = 'loading';
      })
      .addCase(fetchFeedList.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.feeds = action.payload;
      })
      .addCase(fetchFeedList.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload;
      })
      .addCase(postComment.fulfilled, (state, action) => {
        const { feedId, comment } = action.payload;
        const feed = state.feeds.find(f => f.feedId === feedId);
        if (feed) {
          feed.comments.push(comment);
        }
      })
      .addCase(deleteComment.fulfilled, (state, action) => {
        const feedComentId = action.payload;
        state.feeds.forEach(feed => {
          const commentIndex = feed.comments.findIndex(comment => comment.feedComentId === feedComentId);
          if (commentIndex !== -1) {
            feed.comments.splice(commentIndex, 1);
          }
        });
      })
      .addCase(editComment.fulfilled, (state, action) => {
        const { feedComentId, updatedComment } = action.payload;
        state.feeds.forEach(feed => {
          const commentIndex = feed.comments.findIndex(comment => comment.feedComentId === feedComentId);
          if (commentIndex !== -1) {
            feed.comments[commentIndex] = updatedComment;
          }
        });
      })
      .addCase(deleteFeed.fulfilled, (state, action) => {
        const feedId = action.payload;
        state.feeds = state.feeds.filter(feed => feed.feedId !== feedId);
      })
      .addCase(editFeed.fulfilled, (state, action) => {
        const { feedId, updatedFeed } = action.payload;
        const index = state.feeds.findIndex(feed => feed.id === feedId); 
        if (index !== -1) {
          state.feeds[index] = updatedFeed; 
        }
      })
      .addCase(likePost.fulfilled, (state, action) => {
        const feedId = action.payload;
        const feed = state.feeds.find(feed => feed.feedId === feedId);
        if (feed) {
          feed.myLike = !feed.myLike; 
          feed.likes += feed.myLike ? -1 : 1;
        }
      })
    },
  });

export default feedListSlice.reducer;
