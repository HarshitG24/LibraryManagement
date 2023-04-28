package com.example.distributedsystems.distributed.systems.dsalgo.paxos;

/**
 * The enum to express all the scenarios where paxos needs to be used
 */
public enum PaxosScenario {
    CHECKOUT, // when user clicks on checkout in cart page
    LOAN, // when user adds a book to cart
    RETURN, // when user returns a book
    DELETE_BOOK, // when user deletes a book from cart
    DELETE_CART // when user deletes the whole cart
}
