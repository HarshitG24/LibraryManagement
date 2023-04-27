import React from "react";
import { BrowserRouter } from "react-router-dom";
import { Route, Routes } from "react-router";
import { useSelector } from "react-redux";
import "./App.css";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import Login from "./components/Login/Login.js";
import Register from "./components/Register/Register";
import Navbar from "./components/Navbar/Navbar";
import Home from "./components/Home/Home.js";
import Book from "./components/Book/Book.js";
import Cart from "./components/Cart/Cart";
import Profile from "./components/Profile/Profile";
import Return from "./components/Return/Return";
import Loans from "./components/Loans/Loan";
// import dotenv from "dotenv";

// dotenv.config();

function App() {
  const port = process.env.REACT_APP_SERVER_PORT;
  console.log("port is: ", port);
  return (
    <div style={{ width: "100%" }}>
      <BrowserRouter>
        <Navbar />
        <div className="m-5" style={{ minHeight: "50vh" }}>
          <Routes>
            <Route path="/books/:isbn" element={<Book />} />
            <Route index element={<Home />} />
            <Route path="/cart" element={<Cart />} />
            <Route path="/loans" element={<Loans />} />
            <Route path="/profile" element={<Profile />} />
            <Route path="/register" element={<Register />} />
            <Route path="/login" element={<Login />} />
            <Route path="/returns" element={<Return />} />
            <Route path="*" element={<div>INVALID PAGE</div>} />
          </Routes>
        </div>
      </BrowserRouter>
    </div>
  );
}

export default App;
