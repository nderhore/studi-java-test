package studi.test.app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import studi.test.app.pojo.Book;
import studi.test.app.service.BookService;
import studi.test.app.ws.BookController;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookControllerUnitTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;


    @Test
    void testGetBookById_Success() throws Exception {

        // construction de ma données
        Book mockBook = new Book();
        mockBook.setId(1L);
        mockBook.setTitle("1984");
        mockBook.setAuthor("George Orwell");
        mockBook.setPublicationYear(1949);

        when(bookService.getBookById(mockBook.getId())).thenReturn(mockBook);

        ResponseEntity<Book> response = bookController.getBookById(1L);


        //assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        Book responseBook = response.getBody();
        if(responseBook != null && responseBook.getAuthor() != null) {
            assertThat(responseBook.getAuthor()).isEqualTo("George Orwell");
        } else {
            fail();
        }
    }

    @Test
    void testGetBookById_NotFound(){
        ResponseEntity<Book> response = bookController.getBookById(1000L);
        assertThat(response.getStatusCode().value()).isEqualTo(404);

    }

    @Test
    void deleteBook_Success() throws Exception {
        Long bookId = 1L;
        ResponseEntity<Void> response = bookController.deleteBook(bookId);
        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }

    @Test
    void testCreateBook_Success() throws Exception {
        Book newBook = new Book();
        newBook.setTitle("The Hobbit");
        newBook.setAuthor("J.R.R. Tolkien");

        when(bookService.createBook(newBook)).thenReturn(newBook);

        //envoie de ma demande
        Book response = bookController.createBook(newBook);

        //verification
        assertThat(response.getAuthor()).isEqualTo("J.R.R. Tolkien");
        assertThat(response.getTitle()).isEqualTo("The Hobbit");


    }

    @Test
    void testCreateBook_Failure_TitleMissing() throws Exception {
        Book newBook = new Book();
        newBook.setAuthor("J.R.R. Tolkien");

        when(bookService.createBook(newBook)).thenThrow(new IllegalArgumentException("Title cannot be null"));

        try {
            bookController.createBook(newBook);
        } catch(Exception e) {
            assertThat(e).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Title cannot be null");
        }
    }

    @Test
    void testUpdateBook_Success() throws NullPointerException{
        Long bookId = 1L;
        Book updatedBook = new Book();
        updatedBook.setId(bookId);
        updatedBook.setTitle("The Fellowship of the Ring");
        updatedBook.setAuthor("J.R.R. Tolkien");

        when(bookService.updateBook(bookId, updatedBook)).thenReturn(updatedBook);

        ResponseEntity<Book> response = bookController.updateBook(bookId, updatedBook);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        if(response.getBody() != null) {
            assertThat(response.getBody().getTitle()).isEqualTo("The Fellowship of the Ring");
            assertThat(response.getBody().getAuthor()).isEqualTo("J.R.R. Tolkien");
        } else{
            fail();
        }
    }

    @Test
    void testUpdateBook_NotFound(){
        Long bookId = 999L;
        Book updatedBook = new Book();
        updatedBook.setId(bookId);
        updatedBook.setTitle("The Silmarillion");
        updatedBook.setAuthor("J.R.R. Tolkien");

        when(bookService.updateBook(bookId, updatedBook)).thenReturn(null);

        ResponseEntity<Book> response = bookController.updateBook(bookId, updatedBook);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }
}
