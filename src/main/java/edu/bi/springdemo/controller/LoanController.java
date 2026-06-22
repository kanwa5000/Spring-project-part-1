package edu.bi.springdemo.controller;

import edu.bi.springdemo.entity.Loan;
import edu.bi.springdemo.entity.DTO.CreateLoanDTO;
import edu.bi.springdemo.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/borrow")
    @ResponseStatus(HttpStatus.CREATED)
    public Loan borrowBook(@RequestBody CreateLoanDTO dto, Authentication authentication) {
        return loanService.borrowBook(dto, authentication.getName());
    }

    @PutMapping("/{loanId}/approve-borrow")
    public Loan approveBorrowRequest(@PathVariable Integer loanId, Authentication authentication) {
        return loanService.approveBorrowRequest(loanId, authentication.getName());
    }

    @PutMapping("/{loanId}/return")
    public Loan returnBook(@PathVariable Integer loanId, Authentication authentication) {
        return loanService.returnBook(loanId, authentication.getName());
    }

    @PutMapping("/{loanId}/approve-return")
    public Loan approveReturnRequest(@PathVariable Integer loanId, Authentication authentication) {
        return loanService.approveReturnRequest(loanId, authentication.getName());
    }

    @GetMapping
    public Iterable<Loan> getAllLoans(Authentication authentication) {
        return loanService.getAllLoans(authentication.getName());
    }

    @GetMapping("/my")
    public Iterable<Loan> getMyLoans(Authentication authentication) {
        return loanService.getLoansForCurrentUser(authentication.getName());
    }

    @GetMapping("/user/{userId}")
    public Iterable<Loan> getLoansByUser(@PathVariable Integer userId, Authentication authentication) {
        return loanService.getLoansByUser(userId, authentication.getName());
    }

    @GetMapping("/{loanId}")
    public Loan getLoanById(@PathVariable Integer loanId, Authentication authentication) {
        return loanService.getLoanById(loanId, authentication.getName());
    }
}