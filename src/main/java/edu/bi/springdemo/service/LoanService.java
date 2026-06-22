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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class LoanService {

    private static final String STATUS_BORROW_REQUESTED = "BORROW_REQUESTED";
    private static final String STATUS_BORROW_APPROVED = "BORROW_APPROVED";
    private static final String STATUS_RETURN_REQUESTED = "RETURN_REQUESTED";
    private static final String STATUS_RETURN_APPROVED = "RETURN_APPROVED";

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

    public Loan borrowBook(CreateLoanDTO dto, String currentUsername) {
        validateLoanDto(dto);

        User currentUser = getCurrentUser(currentUsername);

        if (!"ROLE_READER".equals(currentUser.getRole())) {
            throw new AccessDeniedException("Only readers can request to borrow books");
        }

        if (!currentUser.getId().equals(dto.getUserId())) {
            throw new AccessDeniedException("Readers can request books only for themselves");
        }

        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new BookNotFoundException("Book with id " + dto.getBookId() + " not found"));

        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
            throw new BookUnavailableException("No available copies for book with id " + book.getBookId());
        }

        if (hasOpenLoanOrRequest(currentUser, book)) {
            throw new InvalidRequestException("You already have an active request or loan for this book");
        }

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUser(currentUser);
        loan.setLoanDate(dto.getLoanDate());
        loan.setDueDate(dto.getDueDate());
        loan.setReturnDate(null);
        loan.setStatus(STATUS_BORROW_REQUESTED);

        return loanRepository.save(loan);
    }

    public Loan approveBorrowRequest(Integer loanId, String currentUsername) {
        User currentUser = getCurrentUser(currentUsername);
        requireLibrarian(currentUser);

        Loan loan = getLoanEntity(loanId);
        String status = getLoanStatus(loan);

        if (!STATUS_BORROW_REQUESTED.equals(status)) {
            throw new InvalidRequestException("Only borrow requests can be approved");
        }

        Book book = loan.getBook();

        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
            throw new BookUnavailableException("No available copies for book with id " + book.getBookId());
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        loan.setStatus(STATUS_BORROW_APPROVED);

        return loanRepository.save(loan);
    }

    public Loan returnBook(Integer loanId, String currentUsername) {
        Loan loan = getLoanEntity(loanId);
        User currentUser = getCurrentUser(currentUsername);

        if (!"ROLE_READER".equals(currentUser.getRole())) {
            throw new AccessDeniedException("Only readers can request to return books");
        }

        if (!currentUser.getId().equals(loan.getUser().getId())) {
            throw new AccessDeniedException("Readers can request return only for their own loans");
        }

        if (loan.getReturnDate() != null) {
            throw new InvalidRequestException("This loan has already been returned");
        }

        String status = getLoanStatus(loan);

        if (STATUS_RETURN_REQUESTED.equals(status)) {
            throw new InvalidRequestException("Return request is already waiting for librarian approval");
        }

        if (!STATUS_BORROW_APPROVED.equals(status)) {
            throw new InvalidRequestException("Only approved borrowed books can be requested for return");
        }

        loan.setStatus(STATUS_RETURN_REQUESTED);

        return loanRepository.save(loan);
    }

    public Loan approveReturnRequest(Integer loanId, String currentUsername) {
        User currentUser = getCurrentUser(currentUsername);
        requireLibrarian(currentUser);

        Loan loan = getLoanEntity(loanId);
        String status = getLoanStatus(loan);

        if (!STATUS_RETURN_REQUESTED.equals(status)) {
            throw new InvalidRequestException("Only return requests can be approved");
        }

        if (loan.getReturnDate() != null) {
            throw new InvalidRequestException("This loan has already been returned");
        }

        loan.setReturnDate(LocalDate.now());
        loan.setStatus(STATUS_RETURN_APPROVED);

        Book book = loan.getBook();
        Long availableCopies = book.getAvailableCopies() == null ? 0 : book.getAvailableCopies();
        book.setAvailableCopies(availableCopies + 1);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    public Iterable<Loan> getAllLoans(String currentUsername) {
        User currentUser = getCurrentUser(currentUsername);

        if (!isLibrarian(currentUser)) {
            throw new AccessDeniedException("Only librarians can view all loans");
        }

        return loanRepository.findAll();
    }

    public Loan getLoanById(Integer loanId, String currentUsername) {
        Loan loan = getLoanEntity(loanId);
        User currentUser = getCurrentUser(currentUsername);

        if (!canAccessLoan(currentUser, loan)) {
            throw new AccessDeniedException("You can view only your own loans");
        }

        return loan;
    }

    public Iterable<Loan> getLoansForCurrentUser(String currentUsername) {
        User currentUser = getCurrentUser(currentUsername);
        return loanRepository.findByUser_Id(currentUser.getId());
    }

    public Iterable<Loan> getLoansByUser(Integer userId, String currentUsername) {
        if (userId == null || userId <= 0) {
            throw new InvalidRequestException("User id must be a positive number");
        }

        User currentUser = getCurrentUser(currentUsername);

        if (!isLibrarian(currentUser) && !currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("Readers can view only their own loans");
        }

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }

        return loanRepository.findByUser_Id(userId);
    }

    private void validateLoanDto(CreateLoanDTO dto) {
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
    }

    private Loan getLoanEntity(Integer loanId) {
        if (loanId == null || loanId <= 0) {
            throw new InvalidRequestException("Loan id must be a positive number");
        }

        return loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan with id " + loanId + " not found"));
    }

    private User getCurrentUser(String username) {
        if (username == null || username.isBlank()) {
            throw new AccessDeniedException("Authenticated username is missing");
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username '" + username + "' not found"));
    }

    private void requireLibrarian(User user) {
        if (!isLibrarian(user)) {
            throw new AccessDeniedException("Only librarians can approve loan requests");
        }
    }

    private boolean isLibrarian(User user) {
        return "ROLE_LIBRARIAN".equals(user.getRole());
    }

    private boolean canAccessLoan(User currentUser, Loan loan) {
        return isLibrarian(currentUser) || currentUser.getId().equals(loan.getUser().getId());
    }

    private String getLoanStatus(Loan loan) {
        if (loan.getStatus() != null && !loan.getStatus().isBlank()) {
            return loan.getStatus();
        }

        if (loan.getReturnDate() != null) {
            return STATUS_RETURN_APPROVED;
        }

        return STATUS_BORROW_APPROVED;
    }

    private boolean hasOpenLoanOrRequest(User user, Book book) {
        Iterable<Loan> userLoans = loanRepository.findByUser_Id(user.getId());

        for (Loan loan : userLoans) {
            boolean sameBook = loan.getBook() != null
                    && loan.getBook().getBookId() != null
                    && loan.getBook().getBookId().equals(book.getBookId());

            if (sameBook && !STATUS_RETURN_APPROVED.equals(getLoanStatus(loan))) {
                return true;
            }
        }

        return false;
    }
}