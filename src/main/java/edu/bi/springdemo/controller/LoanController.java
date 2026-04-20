package edu.bi.springdemo.controller;

import edu.bi.springdemo.entity.Loan;
import edu.bi.springdemo.entity.DTO.CreateLoanDTO;
import edu.bi.springdemo.service.LoanService;
import org.springframework.http.HttpStatus;
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
    public Loan borrowBook(@RequestBody CreateLoanDTO dto) {
        return loanService.borrowBook(dto);
    }

    @PutMapping("/{loanId}/return")
    public Loan returnBook(@PathVariable Integer loanId) {
        return loanService.returnBook(loanId);
    }

    @GetMapping
    public Iterable<Loan> getAllLoans() {
        return loanService.getAllLoans();
    }

    @GetMapping("/{loanId}")
    public Loan getLoanById(@PathVariable Integer loanId) {
        return loanService.getLoanById(loanId);
    }

    @GetMapping("/user/{userId}")
    public Iterable<Loan> getLoansByUser(@PathVariable Integer userId) {
        return loanService.getLoansByUser(userId);
    }
}