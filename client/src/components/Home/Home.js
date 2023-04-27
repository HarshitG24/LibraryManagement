import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Link } from "react-router-dom";
import { getAllBooksThunk } from "../../services/books-thunks.js";
import Card from "antd/es/card/Card";
import {Tag} from "antd";

const Home = () => {

    const dispatch = useDispatch();

    const { allBooks, loading } = useSelector(state => state.booksData)
    console.log(allBooks)

    useEffect(() => {
        dispatch(getAllBooksThunk())
    }, [])


    return (
        
        <div>
            {loading && <li className="list-group-item"> Loading... </li>}
                <div className="row mx-auto align-items-stretch">
                    <div className="mb-2 text-muted">Total Books: {allBooks.length}</div>

                    {allBooks
                        .map((book, idx) => <div className="col" key={idx}>
                            <Card style={{ height: "520px", minWidth: "200px", maxWidth: "300px" }}>
                                <img src={book.image} className="card-img-top" style={{ height: "300px", width: "auto" }} alt={book.title} />
                                <div className="card-body">
                                    <Link
                                        className="wd-user"
                                        to={`/books/${book.isbn}`}
                                        style={{ textDecoration: "none" }}
                                    >
                                        <h6 className="card-title mt-2" style={{ maxHeight: "80px", overflow: "hidden" }}>{book.name}</h6>
                                        <div><small style={{color: "black"}}>By {book.authorName}</small></div>
                                        <div style={{ height: "auto", maxHeight: "70px", overflow: "hidden" }}><small className="text-muted">{book.description}</small></div>
                                        {book.inventory < 1 ?
                                         <Tag color="red">Out of Stock</Tag> : <Tag color="green">In Stock</Tag>}
                                    </Link>
                                </div>
                            </Card>
                        </div>
                        )}
                </div>
        </div>
    );
}

export default Home;