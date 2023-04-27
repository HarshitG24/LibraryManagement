import axios from 'axios'
import { createAsyncThunk } from "@reduxjs/toolkit"

const USER_API = 'http://localhost:8080/user';

export const userRegisterThunk = createAsyncThunk('user/createUser',
    async (user, { rejectWithValue }) => {
        try {
            const response = await axios.post(`${USER_API}/createUser`, user);
            return response.data;
        } catch (err) {
            console.log(err.response.data)
            return rejectWithValue(err.response.data);
        }

    })

export const userLoginThunk = createAsyncThunk('user/login',
                                               async ({ username, password }) => {
                                                   const response = await axios
                                                       .get(`${USER_API}/authenticate?username=${username}&password=${password}`);
                                                   return response.data;
                                               })

