import { createSlice } from "@reduxjs/toolkit";
import {
  markBookAsReturnedThunk,
  createTransactionThunk,
  getAllNotReturnedBooksThunk,
  getAllTransactionsForUser, getAllReturnedBooksThunk,
} from "../services/transaction-thunks";

const initialState = {
  loanedBooksByTransaction: [],
  returnedBooksByTransaction: [],
  allTransactions: [],
  error: null,
  loading: false,
};

const loanSlice = createSlice({
  name: "transaction",
  initialState,
  reducers: {},
  extraReducers: {
    [createTransactionThunk.pending]: (state) => {
      state.error = null;
    },
    [createTransactionThunk.fulfilled]: (state) => {
      state.error = null;
    },
    [createTransactionThunk.rejected]: (state, action) => {
      state.error = action.payload?.message || action.payload === '' || action.error
    },

    [markBookAsReturnedThunk.pending]: (state) => {
      state.error = null;
    },
    [markBookAsReturnedThunk.fulfilled]: (state, { payload }) => {
      state.error = null;
      state.loanedBooksByTransaction = state.loanedBooksByTransaction.map(transaction => {
        if (transaction.transactionId === payload.transactionId) {
          const updatedBookIsbns = transaction.bookIsbns.filter(isbn => isbn !== payload.isbn);
          return {
            ...transaction,
            bookIsbns: updatedBookIsbns,
          };
        }
        return transaction;
      });
    },
    [markBookAsReturnedThunk.rejected]: (state, action) => {
      state.error = action.payload?.message || action.payload === '' || action.error
    },

    [getAllNotReturnedBooksThunk.pending]: (state) => {
      state.loading = true;
      state.loan = [];
      state.error = null;
    },
    [getAllNotReturnedBooksThunk.fulfilled]: (state, { payload }) => {
      state.loading = false;
      state.loanedBooksByTransaction = payload;
      state.error = null;
    },
    [getAllNotReturnedBooksThunk.rejected]: (state, action) => {
      state.loading = false;
      state.loanedBooksByTransaction = [];
      state.error = action.error;
    },

    [getAllTransactionsForUser.pending]: (state) => {
      state.loading = true;
      state.transaction = [];
      state.error = null;
    },
    [getAllTransactionsForUser.fulfilled]: (state, { payload }) => {
      state.loading = false;
      state.transaction = payload;
      state.error = null;
    },
    [getAllTransactionsForUser.rejected]: (state, action) => {
      state.loading = false;
      state.transaction = [];
      state.error = action.error;
    },
    [getAllReturnedBooksThunk.pending]: (state) => {
      state.returnedBooksByTransaction = [];
      state.error = null;
    },
    [getAllReturnedBooksThunk.fulfilled]: (state, { payload }) => {
      state.returnedBooksByTransaction = payload;
      state.error = null;
    },
    [getAllReturnedBooksThunk.rejected]: (state, action) => {
      state.returnedBooksByTransaction = [];
      state.error = action.error;
    },
  },
});

export default loanSlice.reducer;
