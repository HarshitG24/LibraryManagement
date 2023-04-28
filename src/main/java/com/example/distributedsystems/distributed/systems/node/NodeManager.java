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
import com.example.distributedsystems.distributed.systems.repository.CartBookInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


/**
 * A service responsible for managing the current node in the distributed system.
 */
@Service
public class NodeManager {
    /**
     * The logger object for NodeManager class.
     */
    private static final Logger logger = LoggerFactory.getLogger(NodeManager.class);

    /**
     * The server port that this node is running on.
     */
    @Value("${server.port}")
    private int serverPort;

    /**
     * The RestTemplate object used to make HTTP requests to other nodes.
     */
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * The repository object for books.
     */
    private BookInterface bookRepository;
    /**
     * The repository object for carts.
     */
    private CartInterface cartRepository;

    /**
     * The repository object for cart books.
     */
    private CartBookInterface cartBookRepository;

    /**
     * The repository object for transactions.
     */
    private TransactionInterface transactionRepository;

    /**
     * The repository object for users.
     */
    private UserInterface userRepository;

    /**
     * The IP address of this node.
     */
    private final String address = InetAddress.getLoopbackAddress().getHostAddress();

    /**
     * The NodeRegistry object that maintains the list of active nodes in the distributed system.
     */
    @Autowired
    private NodeRegistry nodeRegistry;

    /**
     * The VectorTimestampService object that manages vector timestamps in the distributed system.
     */
    @Autowired
    private VectorTimestampService vectorTimestampService;

    /**
     * Sets the Book repository.
     *
     * @param bookRepository The Book repository to set.
     */
    @Autowired
    public void setBookRepository(BookInterface bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Sets the Cart repository.
     *
     * @param cartRepository The Cart repository to set.
     */
    @Autowired
    public void setCartRepository(CartInterface cartRepository) {
        this.cartRepository = cartRepository;
    }

    /**
     * Sets the CartBook repository.
     *
     * @param cartBookRepository The CartBook repository to set.
     */
    @Autowired
    public void setCartBookRepository(CartBookInterface cartBookRepository) {
        this.cartBookRepository = cartBookRepository;
    }

    /**
     * Sets the Transaction repository.
     *
     * @param transactionRepository The Transaction repository to set.
     */
    @Autowired
    public void setTransactionRepository(TransactionInterface transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Sets the User repository.
     *
     * @param userRepository The User repository to set.
     */
    @Autowired
    public void setUserRepository(UserInterface userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Starts the node, registers it with the NodeRegistry, synchronizes data from an existing node if available,
     * and initializes the VectorTimestampService for the new node.
     */
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

    /**
     * Updates vector timestamps for all active nodes to add the current node.
     */
    private void updateVectorTimestampsForAllNodesExceptCurrent() {
        String currentNodeAddress = getNodeAddress();
        vectorTimestampService.updateVectorTimestampsForCurrentActiveNodes();
        for (String nodeAddress : nodeRegistry.getActiveNodes()) {
            if (!nodeAddress.equals(currentNodeAddress)) {
                restTemplate.postForEntity(nodeAddress + "/vectorTimestamps/update", null, String.class);
            }
        }
    }

    /**
     * Initializes data in the node's database if no existing node is present.
     * Adds users data from a json file and books data from another json file.
     */
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

    /**
     * Synchronizes the data from any existing node.
     * @param nodeAddress exiting node address
     */
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

    /**
     * Returns an existing node.
     *
     * @return existing node address
     */
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

    /**
     * Returns current node address
     * @return current node address
     */
    public String getNodeAddress() {
        return "http://" + address + ":" + serverPort;
    }
}
