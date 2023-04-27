import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Link } from "react-router-dom";
import { logoutUser } from "../../reducers/user-reducer";
import 'react-toastify/dist/ReactToastify.css';

const Navbar = () => {

    const dispatch = useDispatch();
    const { profile } = useSelector(state => state.user);
    const [user, setUser] = useState("anon");

    useEffect(() => {
        if (profile !== undefined && profile !== null && profile !== {}) { setUser("user") }
        else {
            setUser("anon")
        }
    }, [profile])

    const handleLogout = () => dispatch(logoutUser())

    const navLinks = [
        { title: "Home", path: "/", show: ["anon", "user"] },
        { title: "Cart", path: "/cart", show: ["user"] },
        { title: "Login/Register", path: "/login", show: ["anon"] },
        { title: "My Loans", path: "/loans", show: ["user"] },
        { title: "My Returns", path: "/returns", show: ["user"] },
        { title: "Profile", path: "/profile", show: ["seller", "user"] },
        { title: "Logout", path: "/", show: ["user"], onClickHandler: handleLogout },
    ]

    return (
        <div className="navbar navbar-light bg-light d-flex flex-row justify-content-between" style={{ borderBottom: "1px solid coral" }}>
            <Link className="navbar-brand ms-5" to="">
                Online Library
            </Link>

            <div className="d-flex flex-row">

                {navLinks
                    .filter((link) => link.show.includes(user))
                    .map((link) => (
                        <li className="nav nav-item ps-2 pe-2" key={link.title}>
                            <Link
                                className="btn btn-light wd-round-btn"
                                style={{ color: 'coral', textDecoration: 'none' }}
                                to={link.path}
                                onClick={link.onClickHandler}
                            >
                                {link.title}
                            </Link>
                        </li>
                    ))}
            </div>
        </div>

    );
}

export default Navbar;