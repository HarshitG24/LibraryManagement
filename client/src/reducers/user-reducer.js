import { createSlice } from "@reduxjs/toolkit";
import { userLoginThunk } from "../services/user-thunks.js";

const initialState = {
    lastAttempt: null,
    profile: null,
    type: null,
    error: null
}

const userSlice = createSlice(
    {
        name: 'user',
        initialState,
        reducers: {
            clearLogin: (state) => {
                state.profile = null;
                state.error = null;
            },
            logoutUser: (state) => {
                state.profile = null;
                state.error = null;
            }
        },
        extraReducers: {
            [userLoginThunk.fulfilled]:
                (state, { payload }) => {;
                    state.lastAttempt = Date.now();
                    state.profile = payload;
                    state.error = null;
                },
            [userLoginThunk.rejected]:
                (state, action) => {
                console.log(action)
                    state.lastAttempt = Date.now();
                    state.profile = null;
                    state.error = action.error;
                }
        },

    });

export default userSlice.reducer;
export const { logoutUser, clearLogin } = userSlice.actions;
