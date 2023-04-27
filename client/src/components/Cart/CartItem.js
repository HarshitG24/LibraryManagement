import React, {useEffect, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import { Button, Tag } from "antd";
import { cartDeleteBookThunk } from "../../services/cart-thunks";
import {toast, ToastContainer} from "react-toastify";
import {Link} from "react-router-dom";

const CartItem = ({ book, user }) => {

    const dispatch = useDispatch();
    const { error } = useSelector(state => state.cartData);
    const [bookDeleted, setBookDeleted] = useState(false);

    useEffect(() => {
        if (bookDeleted) {
            if (!error) {
                toast.success(`Book ${book.isbn} successfully deleted from Shopping cart!`, {
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
                toast.error("Could not delete book from Shopping cart. Try again!", {
                    position: "bottom-right",
                    autoClose: 500,
                    hideProgressBar: true,
                    closeOnClick: true,
                    pauseOnHover: false,
                    draggable: false,
                    progress: undefined,
                    theme: "colored",
                });
                setBookDeleted(false);
            }
        }
    }, [])
    const handleDelete = () => {
        dispatch(cartDeleteBookThunk({
            username: user.username,
            isbn: book.isbn
        }))
        setBookDeleted(true);
    }

    return (<>
        {book && <li aria-current="true" className="list-group-item mb-4">
            <div className="row pb-2 mt-3 border-bottom">
                <div className="col-2 col-lg-2 col-md-3 d-none d-md-block align-self-center">
                    <img className="wd-post-image" style={{ width: "100%", height: "auto", maxHeight: '250px', maxWidth: '200px' }} src={`${book.image}`} alt="" />
                </div>

                <div className="col-10 col-lg-10 col-md-9">
                        <div className="col-10">
                            <Link
                                to={`/books/${book.isbn}`}
                                style={{textDecoration: "none"}}
                            ><div className="wd-light-text fw-bold">{book.name}</div></Link>
                            <div className="small mt-1 mb-2">{book.description}</div>

                            <div className="small">ISBN: {book.isbn}</div>
                            <div className="small mb-2">Author: {book.authorName}</div>


                            {book.inventory < 1 ?
                                <Tag color="red">Out of Stock</Tag> : <Tag color="green">In Stock</Tag>}
                            <span className="small">Inventory: {book.inventory}</span>
                            <div className="mt-5 mb-0 pb-1"><Button danger onClick={handleDelete}>Delete</Button></div>
                        </div>
                </div>
            </div>
        </li>}
        <ToastContainer />
    </>);
}

export default CartItem;