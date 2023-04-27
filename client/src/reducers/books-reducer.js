import { createSlice } from "@reduxjs/toolkit";
import {
    createBookThunk,
    getAllBooksThunk, getBookByIsbnThunk,
    updateBookInventoryByIsbnThunk
} from "../services/books-thunks";

const initialState = {
    allBooks: [],
    loading: false,
    error: null,
    currentBook: null,
    newBookCreation: {
        book: null,
        complete: false,
        error: false
    }
}

const booksSlice = createSlice(
    {
        name: 'books',
        initialState,
        reducers: {
            clearNewBookCreation: (state) => {
                state.newBookCreation.complete = false
                state.newBookCreation.error = false
                state.newBookCreation.book = null
            },
        },
        extraReducers: {
            [getAllBooksThunk.pending]:
                (state) => {
                    state.currentBook = null
                    state.loading = true
                    state.allBooks = []
                    state.error = null
                },
            [getAllBooksThunk.fulfilled]:
                (state, { payload }) => {
                    state.loading = false
                    state.allBooks = payload
                    state.error = null
                },
            [getAllBooksThunk.rejected]:
                (state, action) => {
                    state.loading = false
                    state.allBooks = []
                    state.error = action.error
                },
            [getBookByIsbnThunk.fulfilled]:
                (state, { payload }) => {
                    state.loading = false
                    state.currentBook = payload
                },
            [updateBookInventoryByIsbnThunk.fulfilled]:
                (state, { payload }) => {
                    state.loading = false
                    if (payload) {
                        state.currentBook = payload
                    }
                },
            [createBookThunk.pending]:
                (state, { payload }) => {
                    state.newBookCreation.book = null
                    state.newBookCreation.complete = false
                    state.newBookCreation.error = false
                },
            [createBookThunk.fulfilled]:
                (state, { payload }) => {
                    state.newBookCreation.book = payload
                    state.newBookCreation.complete = true
                    state.newBookCreation.error = false
                    state.allBooks.push(payload)
                },
            [createBookThunk.rejected]:
                (state, { payload }) => {
                    state.newBookCreation.book = null
                    state.newBookCreation.complete = true
                    state.newBookCreation.error = true
                }
        }
    });

export default booksSlice.reducer;
export const { updateAppliedFilters, clearNewBookCreation, clearAppliedFilters } = booksSlice.actions;