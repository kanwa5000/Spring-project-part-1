package edu.bi.springdemo.service;

import edu.bi.springdemo.entity.Book;
import edu.bi.springdemo.entity.DTO.CreateLoanDTO;
import edu.bi.springdemo.entity.Loan;
import edu.bi.springdemo.entity.User;
import edu.bi.springdemo.entity.exception.BookNotFoundException;
import edu.bi.springdemo.entity.exception.BookUnavailableException;
import edu.bi.springdemo.entity.exception.InvalidRequestException;
import edu.bi.springdemo.entity.exception.LoanNotFoundException;
import edu.bi.springdemo.entity.exception.UserNotFoundException;
import edu.bi.springdemo.repository.BookRepository;
import edu.bi.springdemo.repository.LoanRepository;
import edu.bi.springdemo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public LoanService(LoanRepository loanRepository,
                       BookRepository bookRepository,
                       UserRepository userRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public Loan borrowBook(CreateLoanDTO dto) {
        if (dto == null) {
            throw new InvalidRequestException("Loan body cannot be null");
        }

        if (dto.getBookId() == null || dto.getBookId() <= 0) {
            throw new InvalidRequestException("Book id must be a positive number");
        }

        if (dto.getUserId() == null || dto.getUserId() <= 0) {
            throw new InvalidRequestException("User id must be a positive number");
        }

        if (dto.getLoanDate() == null) {
            throw new InvalidRequestException("Loan date must be provided");
        }

        if (dto.getDueDate() == null) {
            throw new InvalidRequestException("Due date must be provided");
        }

        if (dto.getDueDate().isBefore(dto.getLoanDate())) {
            throw new InvalidRequestException("Due date cannot be earlier than loan date");
        }

        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new BookNotFoundException("Book with id " + dto.getBookId() + " not found"));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User with id " + dto.getUserId() + " not found"));

        if (!"ROLE_READER".equals(user.getRole())) {
            throw new InvalidRequestException("Only users with ROLE_READER can borrow books");
        }

        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
            throw new BookUnavailableException("No available copies for book with id " + book.getBookId());
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUser(user);
        loan.setLoanDate(dto.getLoanDate());
        loan.setDueDate(dto.getDueDate());

        return loanRepository.save(loan);
    }

    public Loan returnBook(Integer loanId) {
        if (loanId == null || loanId <= 0) {
            throw new InvalidRequestException("Loan id must be a positive number");
        }

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan with id " + loanId + " not found"));

        if (loan.getReturnDate() != null) {
            throw new InvalidRequestException("This loan has already been returned");
        }

        loan.setReturnDate(LocalDate.now());

        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    public Iterable<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public Loan getLoanById(Integer loanId) {
        if (loanId == null || loanId <= 0) {
            throw new InvalidRequestException("Loan id must be a positive number");
        }

        return loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan with id " + loanId + " not found"));
    }

    public Iterable<Loan> getLoansByUser(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new InvalidRequestException("User id must be a positive number");
        }

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }

        return loanRepository.findByUser_Id(userId);
    }
}