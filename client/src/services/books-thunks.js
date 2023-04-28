import axios from "axios";
import { createAsyncThunk } from "@reduxjs/toolkit";

const BOOKS_API = `http://localhost:${process.env.REACT_APP_SERVER_PORT}/book`;

export const getAllBooksThunk = createAsyncThunk("books/all", async () => {
  const response = await axios.get(`${BOOKS_API}`);
  return response.data;
});

export const getBookByIsbnThunk = createAsyncThunk(
  "books/isbn",
  async ({ isbn }, { rejectWithValue }) => {
    try {
      const response = await axios.get(`${BOOKS_API}/${isbn}`);
      return response.data;
    } catch (err) {
      return rejectWithValue(err.response.data);
    }
  }
);

export const createBookThunk = createAsyncThunk(
  "books/create",
  async (book) => {
    const response = await axios.post(BOOKS_API, book);
    return response.data;
  }
);

export const updateBookInventoryByIsbnThunk = createAsyncThunk(
  "books/updateInventory",
  async ({ isbn, inventory }) => {
    const response = await axios.put(`${BOOKS_API}/${isbn}`, inventory);
    return response.data;
  }
);
