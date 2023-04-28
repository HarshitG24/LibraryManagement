import React, { useEffect, useState } from "react";
import { Button } from "antd";
import { markBookAsReturnedThunk } from "../../services/transaction-thunks.js";
import { toast, ToastContainer } from "react-toastify";
import { getBookByIsbnThunk } from "../../services/books-thunks.js";
import { useDispatch, useSelector } from "react-redux";
import { cartAddBookThunk } from "../../services/cart-thunks";
import { Link } from "react-router-dom";

const LoanedOrReturnedBook = ({ isbn, transaction, type }) => {
  const dispatch = useDispatch();
  const { profile } = useSelector((state) => state.user);
  const [book, setBook] = useState(null);
  const [reload, setReload] = useState(false);

  useEffect(() => {
    dispatch(getBookByIsbnThunk({ isbn }))
      .then((result) => setBook(result.payload))
      .catch((error) => console.error(error));
  }, [dispatch, isbn, reload]);

  const handleReturnBook = () => {
    dispatch(
      markBookAsReturnedThunk({
        transactionId: transaction.transactionId,
        isbn: isbn,
      })
    )
      .then(() => {
        toast.success(`Book ${isbn} returned successfully!`, {
          position: "top-right",
          autoClose: 200,
          hideProgressBar: false,
          closeOnClick: true,
          pauseOnHover: false,
          draggable: true,
          progress: undefined,
          theme: "colored",
        });
        setReload(!reload); // Toggle the reload state
      })
      .catch((error) => {
        console.error(error);
      });
    setReload(!reload); // Toggle the reload state
  };

  const handleAddToCart = () => {
    debugger
    dispatch(
        cartAddBookThunk({
                           username: profile.username,
                           isbn: book.isbn,
                         })
    ).then((response) => {
      if (response.payload.success) {
        toast.success("Book successfully added to Shopping cart!", {
          position: "bottom-right",
          autoClose: 200,
          hideProgressBar: true,
          closeOnClick: true,
          pauseOnHover: false,
          draggable: false,
          progress: undefined,
          theme: "colored",
        });
      } else {
        toast.error("Could not add book to Shopping cart. Try again!", {
          position: "bottom-right",
          autoClose: 200,
          hideProgressBar: true,
          closeOnClick: true,
          pauseOnHover: false,
          draggable: false,
          progress: undefined,
          theme: "colored",
        });
      }

    }).catch(error => {
      console.log(error)
    })
  };

  return (
    <>
      {book && (
        <div className="row pb-3 pt-3 border-bottom">
          <div className="col-3">
            <img
              src={book.image}
              className="card-img-top"
              style={{ height: "300px", width: "auto" }}
              alt={book.name}
            />
          </div>
          <div className="col-9">
            <Link
                to={`/books/${book.isbn}`}
                style={{textDecoration: "none"}}
            ><h6
              className="card-title mt-2"
              style={{ maxHeight: "80px", overflow: "hidden" }}>
              {book.name}
            </h6></Link>
            <p
              className="card-text"
              style={{ maxHeight: "70px", overflow: "hidden" }}>
              <div className="text-muted">{book.description}</div>
            </p>
            <p className="card-text">
              <div className="text-muted">ISBN: {book.isbn}</div>
            </p>
            <p className="card-text">
              <div className="text-muted">Author: {book.authorName}</div>
            </p>
            <p className="card-text">
              <div className="text-muted">Inventory: {book.inventory}</div>
            </p>

            {type === "LOAN" && (
              <Button onClick={() => handleReturnBook()} type="primary">
                Return Book
              </Button>
            )}
            {type === "RETURN" && (
              <Button onClick={() => handleAddToCart()} type="primary">
                Loan again (Add to cart)
              </Button>
            )}
          </div>
        </div>
      )}
      <ToastContainer />
    </>
  );
};

export default LoanedOrReturnedBook;
