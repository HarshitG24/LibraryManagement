import axios from "axios";
import { createAsyncThunk } from "@reduxjs/toolkit";

const CART_API = `http://localhost:${process.env.REACT_APP_SERVER_PORT}/cart`;

export const cartDeleteThunk = createAsyncThunk(
  "cart/delete",
  async (username, { rejectWithValue }) => {
    try {
      console.log("current port is: ", process.env.REACT_APP_SERVER_PORT);
      const response = await axios.delete(`${CART_API}/${username}`);
      return response.data;
    } catch (err) {
      return rejectWithValue(err.response.data);
    }
  }
);

export const cartFindThunk = createAsyncThunk(
  "cart/find",
  async (username, { rejectWithValue }) => {
    try {
      const response = await axios.get(`${CART_API}?username=${username}`);
      console.log(username);
      return response.data;
    } catch (err) {
      return rejectWithValue(err.response.data);
    }
  }
);

export const cartAddBookThunk = createAsyncThunk(
  "cart/add",
  async ({ username, isbn }, { rejectWithValue }) => {
    try {
      const response = await axios.post(`${CART_API}/addBook`, {
        username,
        isbn,
      });

      return response.data;
    } catch (err) {
      return rejectWithValue(err.response.data);
    }
  }
);

export const cartDeleteBookThunk = createAsyncThunk(
  "cart/deleteBook",
  async ({ username, isbn }, { rejectWithValue }) => {
    try {
      const response = await axios.delete(
        `${CART_API}/${username}/book/${isbn}`
      );
      return response.data;
    } catch (err) {
      return rejectWithValue(err.response.data);
    }
  }
);
