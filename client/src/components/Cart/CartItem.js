import React, { useState } from "react";
import { useDispatch } from "react-redux";
import { Button, Tag } from "antd";
import { cartDeleteBookThunk } from "../../services/cart-thunks";

const CartItem = ({ book, user }) => {

    const dispatch = useDispatch();

    const handleDelete = () => {
        dispatch(cartDeleteBookThunk({
            username: user.username,
            isbn: book.isbn
        }))
    }

    return (<>
        {book && <li aria-current="true" className="list-group-item mb-4">
            <div className="row pb-2 mt-3 border-bottom">
                <div className="col-2 col-lg-2 col-md-3 d-none d-md-block align-self-center">
                    <img className="wd-post-image" style={{ width: "100%", height: "auto", maxHeight: '250px', maxWidth: '200px' }} src={`${book.image}`} alt="" />
                </div>

                <div className="col-10 col-lg-10 col-md-9">
                        <div className="col-10">
                            <div className="wd-light-text fw-bold">{book.name}</div>
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
    </>);
}

export default CartItem;