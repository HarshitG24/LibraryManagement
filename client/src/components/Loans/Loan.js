import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  getAllNotReturnedBooksThunk,
} from "../../services/transaction-thunks.js";
import LoanedOrReturnedBook from "../Book/LoanedOrReturnedBook";
import { Collapse } from "antd";
import { ToastContainer } from "react-toastify";

const { Panel } = Collapse;

const Loans = () => {
  const { loanedBooksByTransaction, loading } = useSelector(state => state.transactionData);
  const { profile } = useSelector((state) => state.user);

  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(getAllNotReturnedBooksThunk(profile.username));
    }, [dispatch, profile]);


  return (
    <div>
      {loading && <li className="list-group-item"> Loading... </li>}
      <div className="row mx-auto align-items-stretch">
        {loanedBooksByTransaction && <div className="mb-2 text-muted">
          Total Orders with unreturned books: {loanedBooksByTransaction.length}
        </div>}

        {loanedBooksByTransaction &&
         loanedBooksByTransaction.map((transaction, idx) => {
           return <Collapse className={"mb-3 ps-0 pe-0"}><Panel
               key={idx}
               header={
                 <div className="d-flex flex-row justify-content-between w-100">
                   <b>Loan Order #{transaction.transactionId}</b>
                   <b>Placed on: {new Date(transaction.transactionDate).toLocaleString()}</b>
                 </div>
               }
           >
             {transaction.bookIsbns.map((loanedBook, index) => {
               return (<><LoanedOrReturnedBook key={index} isbn={loanedBook} transaction={transaction} type={"LOAN"}/></>)
             })}
           </Panel></Collapse>
         })}
        <ToastContainer />
      </div>
    </div>
  );
};

export default Loans;
