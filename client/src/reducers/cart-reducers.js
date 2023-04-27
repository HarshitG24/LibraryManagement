import { createSlice } from "@reduxjs/toolkit";
import {
    cartDeleteBookThunk,
    cartDeleteThunk,
    cartFindThunk,
    cartAddBookThunk
} from "../services/cart-thunks";

const initialState = {
    cart: {},
    error: null
}

const shoppingCartSlice = createSlice(
    {
        name: 'cart',
        initialState,
        reducers: {
            clearCartReducer: (state) => {
                state.cart = {};
                state.error = null;
            },
        },
        extraReducers: {
            [cartFindThunk.pending]:
                (state) => {
                    state.cart = {}
                    state.error = null
                },
            [cartFindThunk.fulfilled]:
                (state, { payload }) => {
                    state.cart = payload
                    state.error = null
                },
            [cartFindThunk.rejected]:
                (state, action) => {
                    state.cart = {}
                    state.error = action.error
                },
            [cartDeleteThunk.fulfilled]:
                (state, { payload }) => {
                    state.error = null
                    state.cart = {}
                },
            [cartDeleteThunk.rejected]:
                (state, action) => {
                    state.error = action.error
                },
            [cartAddBookThunk.fulfilled]:
                (state, { payload }) => {
                    state.error = null
                    if (state.cart.books === undefined || state.cart.books.length === 0) {
                        state.cart.books = []
                        state.cart.books.push(payload.isbn)
                    } else {
                        state.cart.books.push(payload.isbn)
                    }
                },
            [cartAddBookThunk.rejected]:
                (state, action) => {
                    state.error = action.payload?.message || action.payload === '' || action.error
                },
            [cartDeleteBookThunk.fulfilled]:
                (state, { payload }) => {
                    state.error = null
                    const index = state.cart.books.indexOf(payload.isbn);
                    if (index > -1) {
                        state.cart.books.splice(index, 1);
                    }
                },
            [cartDeleteBookThunk.rejected]:
                (state, action) => {
                    state.error = action.payload?.message || action.payload === '' || action.error
                }
        }
    });

export const { clearCartReducer } = shoppingCartSlice.actions;
export default shoppingCartSlice.reducer;
