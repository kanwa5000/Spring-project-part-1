package edu.bi.springdemo.service;

import edu.bi.springdemo.entity.Book;
import edu.bi.springdemo.entity.exception.BookAlreadyExistsException;
import edu.bi.springdemo.entity.exception.BookNotFoundException;
import edu.bi.springdemo.entity.exception.InvalidRequestException;
import edu.bi.springdemo.repository.BookRepository;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book addBook(Book book) {
        validateBook(book);

        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new BookAlreadyExistsException("Book with ISBN '" + book.getIsbn() + "' already exists");
        }

        return bookRepository.save(book);
    }

    public Book updateBook(Integer id, Book updatedBook) {
        if (id == null || id <= 0) {
            throw new InvalidRequestException("Book id must be a positive number");
        }

        validateBook(updatedBook);

        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with id " + id + " not found"));

        if (!existingBook.getIsbn().equals(updatedBook.getIsbn()) &&
                bookRepository.existsByIsbn(updatedBook.getIsbn())) {
            throw new BookAlreadyExistsException("Book with ISBN '" + updatedBook.getIsbn() + "' already exists");
        }

        existingBook.setIsbn(updatedBook.getIsbn());
        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setPublisher(updatedBook.getPublisher());
        existingBook.setYearPublished(updatedBook.getYearPublished());
        existingBook.setAvailableCopies(updatedBook.getAvailableCopies());

        return bookRepository.save(existingBook);
    }

    public void deleteBook(Integer id) {
        if (id == null || id <= 0) {
            throw new InvalidRequestException("Book id must be a positive number");
        }

        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Book with id " + id + " not found");
        }

        bookRepository.deleteById(id);
    }

    public Iterable<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Integer id) {
        if (id == null || id <= 0) {
            throw new InvalidRequestException("Book id must be a positive number");
        }

        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with id " + id + " not found"));
    }

    public Iterable<Book> searchBooks(String phrase) {
        if (phrase == null || phrase.isBlank()) {
            throw new InvalidRequestException("Search phrase cannot be blank");
        }

        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrIsbnContainingIgnoreCase(
                phrase, phrase, phrase
        );
    }

    private void validateBook(Book book) {
        if (book == null) {
            throw new InvalidRequestException("Book body cannot be null");
        }

        if (book.getIsbn() == null || book.getIsbn().isBlank()) {
            throw new InvalidRequestException("ISBN cannot be blank");
        }

        if (book.getTitle() == null || book.getTitle().isBlank()) {
            throw new InvalidRequestException("Title cannot be blank");
        }

        if (book.getAuthor() == null || book.getAuthor().isBlank()) {
            throw new InvalidRequestException("Author cannot be blank");
        }

        if (book.getAvailableCopies() == null || book.getAvailableCopies() < 0) {
            throw new InvalidRequestException("Available copies cannot be null or negative");
        }

        if (book.getYearPublished() != null && book.getYearPublished() < 0) {
            throw new InvalidRequestException("Year published cannot be negative");
        }
    }
}