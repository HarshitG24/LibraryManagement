package com.example.distributedsystems.distributed.systems.node;

import com.example.distributedsystems.distributed.systems.model.Book;
import com.example.distributedsystems.distributed.systems.model.cart.Cart;
import com.example.distributedsystems.distributed.systems.model.cart.CartBook;
import com.example.distributedsystems.distributed.systems.model.cart.CartBookId;
import com.example.distributedsystems.distributed.systems.model.cart.CartDTO;
import com.example.distributedsystems.distributed.systems.model.transaction.Transaction;
import com.example.distributedsystems.distributed.systems.model.user.User;
import com.example.distributedsystems.distributed.systems.repository.BookInterface;
import com.example.distributedsystems.distributed.systems.repository.CartInterface;
import com.example.distributedsystems.distributed.systems.repository.TransactionInterface;
import com.example.distributedsystems.distributed.systems.repository.UserInterface;
import com.example.distributedsystems.distributed.systems.service.CartBookInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
public class NodeManager {
    @Value("${server.port}")
    private int serverPort;

    private final RestTemplate restTemplate = new RestTemplate();

    private BookInterface bookRepository;
    private CartInterface cartRepository;
    private CartBookInterface cartBookRepository;
    private TransactionInterface transactionRepository;
    private UserInterface userRepository;

    @Autowired
    private NodeRegistry nodeRegistry;

    public NodeManager( ) {

    }

    @Autowired
    public void setBookRepository(BookInterface bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Autowired
    public void setCartRepository(CartInterface cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Autowired
    public void setCartBookRepository(CartBookInterface cartBookRepository) {
        this.cartBookRepository = cartBookRepository;
    }

    @Autowired
    public void setTransactionRepository(TransactionInterface transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Autowired
    public void setUserRepository(UserInterface userRepository) {
        this.userRepository = userRepository;
    }

    public void startNode() {
        // Register the node with the central registry
        String nodeAddress = getNodeAddress();
        nodeRegistry.registerNode(nodeAddress);
        System.out.println("Active nodes for port " + serverPort + ": " + nodeRegistry.getActiveNodes());

        // Synchronize data from an existing node
        String existingNodeAddress = getAnExistingNodeAddress();
        if (existingNodeAddress != null) {
            synchronizeDataFromNode(existingNodeAddress);
        }
    }

    public void stopNode() {
        // Deregister the node from the central registry
        String nodeAddress = getNodeAddress();
        nodeRegistry.unregisterNode(nodeAddress);
    }


    public void synchronizeDataFromNode(String nodeAddress) {

        // User
        ResponseEntity<List<User>> userResponse = restTemplate.exchange(
                nodeAddress + "/user",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>() {}
        );
        List<User> userData = userResponse.getBody();

        // Book
        ResponseEntity<List<Book>> bookResponse = restTemplate.exchange(
                nodeAddress + "/book",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Book>>() {}
        );
        List<Book> bookData = bookResponse.getBody();

        // Cart
        ResponseEntity<List<CartDTO>> cartResponse = restTemplate.exchange(
                nodeAddress + "/cart/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CartDTO>>() {}
        );
        List<CartDTO> cartData = cartResponse.getBody();
        System.out.println("CartData response: " + cartData);


        // CartBook
        ResponseEntity<List<CartBookId>> cartBookResponse = restTemplate.exchange(
                nodeAddress + "/cart/cartBooks/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CartBookId>>() {}
        );
        List<CartBookId> cartBookData = cartBookResponse.getBody();

        // Transaction
        ResponseEntity<List<Transaction>> transactionResponse = restTemplate.exchange(
                nodeAddress + "/transaction",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Transaction>>() {}
        );
        List<Transaction> transactionData = transactionResponse.getBody();

        // Populate the new node's database with the fetched data
        // User
        if (userData != null) {
            userRepository.saveAll(userData);
        }

        // Book
        if (bookData != null) {
            bookRepository.saveAll(bookData);
        }

        // Cart and CartBook data
        if (cartData != null && cartBookData != null) {
            for (CartDTO cartDTO: cartData) {
                Cart cart = new Cart(cartDTO.getUsername());
                cartRepository.save(cart);
                for (CartBookId cartBookId : cartBookData) {
                    Book book = bookRepository.getBookByIsbn(cartBookId.getBookId());
                    CartBook cartBook = new CartBook(cart, book);
                    cartBookRepository.save(cartBook);
                }
            }
        }

//        // CartBook
//        if (cartBookData != null) {
//            cartBookRepository.saveAll(cartBookData);
//        }

        // Transaction
        if (transactionData != null) {
            transactionRepository.saveAll(transactionData);
        }

    }

    public String getAnExistingNodeAddress() {
        Set<String> activeNodes = nodeRegistry.getActiveNodes();
        String currentNodeAddress = getNodeAddress();
        for (String address : activeNodes) {
            if (!address.equals(currentNodeAddress)) {
                return address;
            }
        }
        return null;
    }

    public String getNodeAddress() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            return "http://" + address.getHostAddress() + ":" + serverPort;
        } catch (UnknownHostException ex) {
            // Handle exception
            return null;
        }
    }
}
