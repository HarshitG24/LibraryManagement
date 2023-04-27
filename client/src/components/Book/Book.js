import React, {useEffect, useState} from "react";
import {useParams} from "react-router";
import {getBookByIsbnThunk} from "../../services/books-thunks";
import {useDispatch, useSelector} from "react-redux";
import {toast, ToastContainer} from "react-toastify";
import {cartAddBookThunk, cartDeleteBookThunk} from "../../services/cart-thunks";
import {Tag} from "antd";

// import fs from "fs";

// Reload the book using api call to have up to date inventory
const Book = () => {
    const dispatch = useDispatch();
    const {isbn} = useParams();

    const {currentBook} = useSelector((state) => state.booksData);
    const [book, setBook] = useState({...currentBook});
    const { cart } = useSelector((state) => state.cartData);
    const {profile, type} = useSelector((state) => state.user);
    const isLoggedIn = () => profile && Object.keys(profile).length > 0;

    useEffect(() => {
        dispatch(getBookByIsbnThunk({isbn}));
    }, []);

    useEffect(() => {
        setBook({...currentBook});
    }, [currentBook]);

    const handleAddToCart = () => {
        dispatch(
            cartAddBookThunk({
                                 username: profile.username,
                                 isbn: book.isbn,
                             })
        ).then((response) => {
            if (response.payload.success) {
                toast.success("Book successfully added to Shopping cart!", {
                    position: "bottom-right",
                    autoClose: 500,
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
                    autoClose: 500,
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

    const handleRemoveFromCart = () => {
        debugger
        dispatch(
            cartDeleteBookThunk({
                                    username: profile.username,
                                    isbn: book.isbn,
                                })
        ).then((response) => {
            if (response.payload.success) {

                toast.success("Book successfully removed from Shopping cart!", {
                    position: "bottom-right",
                    autoClose: 500,
                    hideProgressBar: true,
                    closeOnClick: true,
                    pauseOnHover: false,
                    draggable: false,
                    progress: undefined,
                    theme: "colored",
                })
            } else {
                toast.error("Could not remove book from Shopping cart. Try again!", {
                    position: "bottom-right",
                    autoClose: 500,
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
        });
    };

    debugger

    return (
        <div className="container">
            {!book ? (
                <li className="list-group-item"> Loading... </li>
            ) : (
                 <div className="card-body">
                     {/* Title */}
                     <h4 className="card-title mb-2">{book.name}</h4>

                     {/* Book Tags & Author */}
                     <div className="mb-3">
                         {isLoggedIn() && type === "buyer" && (
                             <p className="card-text text-muted">Sold by {book.authorName}</p>
                         )}
                     </div>

                     {/* Image */}
                     {book.image && (
                         <div className="mb-3">
                             <img
                                 src={book.image}
                                 style={{width: "auto", height: "300px"}}
                                 className="card-img-top mt-2 mb-1"
                                 alt={book.name}
                             />
                         </div>
                     )}

                     {/* ISBN */}
                     <div className="card-text mb-3"><b>ISBN:</b> {book.isbn}</div>

                     {/* Description */}
                     <div className="mb-3">
                         <h5>Description</h5>
                         <p className="card-text">{book.description}</p>
                     </div>

                     {/* Author Name */}
                     <div className="mb-3">
            <span>
              <b>Author: </b>
            </span>
                         <span className="card-text text-muted">{book.authorName}</span>
                     </div>

                     {/* Stock Level */}
                     <div className="mb-3">
            <span>
              <b>Inventory Level: </b>
            </span>
                         <span className="card-text text-muted">{book.inventory}</span>
                     </div>
                     <div className="mb-3">
                         {book.inventory < 1 ? (
                             <Tag color="red">Out of Stock</Tag>
                         ) : (
                              <Tag color="green">In Stock</Tag>
                          )}
                     </div>
                     {/* Add to Cart */}
                     <div className="mb-3">
                         {
                             (cart && cart.books && cart.books.includes(book.isbn)) ? <button
                                                                                        className="btn btn-outline-danger"
                                                                                        style={{display: "inline"}}
                                                                                        disabled={!isLoggedIn()}
                                                                                        onClick={handleRemoveFromCart}>
                                                                                        Remove from Cart
                                                                                    </button> :
                             <button
                                 className="btn btn-outline-success"
                                 style={{display: "inline"}}
                                 disabled={!isLoggedIn() || book.inventory < 1}
                                 onClick={handleAddToCart}>
                                 Add to Cart (Loan)
                             </button>}
                         {!isLoggedIn() && (
                             <div>
                                 <i style={{color: "red"}}>Login to loan book</i>
                             </div>
                         )}
                     </div>
                     <br/>
                 </div>
             )}
            <ToastContainer/>
        </div>
    );
};

export default Book;
