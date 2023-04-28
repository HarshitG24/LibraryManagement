import React, { useEffect, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { toast, ToastContainer } from "react-toastify";
import { Button } from "antd";
import {
  cartDeleteThunk,
  cartFindThunk,
} from "../../services/cart-thunks";
import { getAllBooksThunk } from "../../services/books-thunks.js";
import { createTransactionThunk } from "../../services/transaction-thunks.js";
import CartItem from "./CartItem";
import {clearCartReducer} from "../../reducers/cart-reducers";

const Cart = () => {
  const dispatch = useDispatch();


  const { profile } = useSelector((state) => state.user);
  const { cart } = useSelector((state) => state.cartData);
  const { allBooks } = useSelector((state) => state.booksData);
  const { error } = useSelector(state => state.transactionData)
  const [orderPlaced, setOrderPlaced] = useState(false);
  const inputRef = useRef(null);

  useEffect(() => {
    if (orderPlaced) {
      if (!error) {
          dispatch(clearCartReducer())
      toast.success("Loan request placed! :)", {
        position: "top-right",
        autoClose: 200,
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: false,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
      } else {
          toast.error('Loan request could not be placed!', {
              position: "top-right",
              autoClose: 200,
              hideProgressBar: false,
              closeOnClick: true,
              pauseOnHover: false,
              draggable: true,
              progress: undefined,
              theme: "colored",
          });
      }
    }
  }, [orderPlaced]);

  useEffect(() => {
    dispatch(getAllBooksThunk());
    if (profile.username) {
      dispatch(cartFindThunk(profile.username));
    }
    // return () => dispatch(clearOrderReducer())
  }, []);

  const handleCheckout = () => {
    const transaction = {
      transactionId: Date.now(),
      username: profile.username,
        bookIsbns: cart.books,
    };
    dispatch(createTransactionThunk({transaction}));
    setOrderPlaced(true);
  };

  const cartFull = cart && cart.books && cart.books.length > 0;

  debugger;
  return (
    <>
      <div className="pb-3 h5 border-bottom d-flex flex-row justify-content-between">
        <div>Loan Cart</div>
        {cartFull && (
          <Button
            danger
            onClick={() => dispatch(cartDeleteThunk(profile.username))}>
            Delete Cart
          </Button>
        )}
      </div>
      {!cartFull && (
        <div className="mb-5">
          {" "}
          <b>No books added to the cart</b>
        </div>
      )}
      {cartFull &&
        cart.books &&
        allBooks &&
        cart.books.map((cartBook, idx) => {
          debugger;
          const book = allBooks.find((book) => book.isbn === cartBook);
          return <CartItem key={idx} book={book} user={profile} />;
        })}
      {cartFull && (
        <>
          <Button
            size="large"
            onClick={handleCheckout}
            style={{
              background: "coral",
              color: "white",
              border: "coral",
              float: "right",
              marginRight: "65px",
            }}>
            Checkout
          </Button>
        </>
      )}
      <ToastContainer />
    </>
  );
};

export default Cart;
