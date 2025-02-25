package studi.test.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import studi.test.app.pojo.Book;
import studi.test.app.repository.BookRepository;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookRepositoryTest {

    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testBook")
            .withUsername("testUser")
            .withPassword("testPassword");

    @Autowired
    private BookRepository bookRepository;

    private Book book;

    @BeforeAll
    static void setUp() {
        postgreSQLContainer.start();
        System.setProperty("spring.datasource.url",postgreSQLContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgreSQLContainer.getUsername());
        System.setProperty("spring.datasource.password", postgreSQLContainer.getPassword());
    }

    @Test
    void testBookCreationAndRetrieval(){
        // Création et sauvegarde d'un livre
        Book book = new Book("Spring boot avec testContainers","José",2025);
        Book savedBook = bookRepository.save(book);

        //verifier que l'ID est présent
        assertThat(savedBook.getId()).isNotNull();

        Book retrievedBook = bookRepository.findById(savedBook.getId()).orElse(null);
        this.book = retrievedBook;
        assertThat(retrievedBook).isNotNull();
        assertThat(retrievedBook.getTitle()).isEqualTo("Spring boot avec testContainers");
    }

    @Test
    void testUpdateBook(){
        this.book.setTitle("José et les developpeurs");
        this.bookRepository.save(this.book);
        Book retrievedBook = this.bookRepository.findById(this.book.getId()).orElse(null);
        assertThat(retrievedBook).isNotNull();
        assertThat(retrievedBook.getTitle()).isEqualTo("José et les developpeurs");
    }

    @Test
    void testDeleteBook(){
        this.bookRepository.deleteById(this.book.getId());
        Book retrievedBook = this.bookRepository.findById(this.book.getId()).orElse(null);
        assertThat(retrievedBook).isNull();
    }


}
