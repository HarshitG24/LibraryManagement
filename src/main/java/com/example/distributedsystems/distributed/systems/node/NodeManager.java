package com.example.distributedsystems.distributed.systems.node;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.example.distributedsystems.distributed.systems.dsalgo.vectortimestamps.VectorTimestampService;
import com.example.distributedsystems.distributed.systems.model.Book;
import com.example.distributedsystems.distributed.systems.model.cart.Cart;
import com.example.distributedsystems.distributed.systems.model.cart.CartBook;
import com.example.distributedsystems.distributed.systems.model.cart.CartBookId;
import com.example.distributedsystems.distributed.systems.model.cart.CartDTO;
import com.example.distributedsystems.distributed.systems.model.transaction.Transaction;
import com.example.distributedsystems.distributed.systems.model.user.CreateUserRequest;
import com.example.distributedsystems.distributed.systems.model.user.User;
import com.example.distributedsystems.distributed.systems.repository.BookInterface;
import com.example.distributedsystems.distributed.systems.repository.CartInterface;
import com.example.distributedsystems.distributed.systems.repository.TransactionInterface;
import com.example.distributedsystems.distributed.systems.repository.UserInterface;
import com.example.distributedsystems.distributed.systems.service.CartBookInterface;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;



@Service
public class NodeManager {
    private static final Logger logger = LoggerFactory.getLogger(NodeManager.class);

    @Value("${server.port}")
    private int serverPort;

    private final RestTemplate restTemplate = new RestTemplate();

    private BookInterface bookRepository;
    private CartInterface cartRepository;
    private CartBookInterface cartBookRepository;
    private TransactionInterface transactionRepository;
    private UserInterface userRepository;
    private final String address = InetAddress.getLoopbackAddress().getHostAddress();

    @Autowired
    private NodeRegistry nodeRegistry;

    @Autowired
    private VectorTimestampService vectorTimestampService;

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

        // Register the node
        String nodeAddress = getNodeAddress();
        logger.info("Registering node");
        UUID lifecycleListenerId = nodeRegistry.registerNode(nodeAddress);
        logger.info("Current active nodes for port " + serverPort + ": " + nodeRegistry.getActiveNodes());

        // Synchronize data from an existing node
        String existingNodeAddress = getAnExistingNodeAddress();
        if (existingNodeAddress != null) {
            synchronizeDataFromNode(existingNodeAddress);
        } else {
            initializeData();
        }
        // Initialize the vector timestamp for the current new node
        updateVectorTimestampsForAllNodesExceptCurrent();
    }

    private void updateVectorTimestampsForAllNodesExceptCurrent() {
        String currentNodeAddress = getNodeAddress();
        vectorTimestampService.updateVectorTimestampsForCurrentActiveNodes();
        for (String nodeAddress : nodeRegistry.getActiveNodes()) {
            if (!nodeAddress.equals(currentNodeAddress)) {
                restTemplate.postForEntity(nodeAddress + "/vectorTimestamps/update", null, String.class);
            }
        }
    }

    public void initializeData() {
        //Adding users data
        List<User> existingUsers = (List<User>) userRepository.findAll();
        if (!existingUsers.iterator().hasNext()){
            try {
                Gson gson = new Gson();
                Reader reader = new FileReader("src/main/java/com/example/distributedsystems/distributed/systems/data/users.json");
                CreateUserRequest[] createUserRequests = gson.fromJson(reader, CreateUserRequest[].class);
                List<User> users = new ArrayList<>();
                for (CreateUserRequest request : createUserRequests) {
                    User.Address address = new User.Address(request.getAddress1(), request.getAddress2(), request.getCity(), request.getState(), request.getZipcode());
                    User user = new User(request.getFirstName(), request.getLastName(), request.getEmail(), request.getPassword(), request.getUsername(), request.getPhone(), address);
                    users.add(user);
                }
                logger.info("Initializing User data");
                userRepository.saveAll(users);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }

        //Adding books data
        try {
            Gson gson = new Gson();
            Reader reader = new FileReader("src/main/java/com/example/distributedsystems/distributed/systems/data/books.json");
            Type listType = new TypeToken<List<Book>>() {}.getType();
            List<Book> books = gson.fromJson(reader, listType);
            logger.info("Initializing Book data");
            bookRepository.saveAll(books);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void synchronizeDataFromNode(String nodeAddress) {

        logger.info("Synchronizing data from existing node: " + nodeAddress);
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
            System.out.println("UserData: " + userData);
            userRepository.saveAll(userData);
        } else {
            System.out.println("user null");
        }

        // Book
        if (bookData != null) {
            System.out.println("Book Data: " + bookData);
            bookRepository.saveAll(bookData);
        } else {
            System.out.println("book null");
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

        // Transaction
        if (transactionData != null) {
            transactionRepository.saveAll(transactionData);
        }
        logger.info("Data Synchronization complete!");
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
        return "http://" + address + ":" + serverPort;
    }
}
