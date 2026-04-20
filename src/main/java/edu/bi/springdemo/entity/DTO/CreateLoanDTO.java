package edu.bi.springdemo.entity.DTO;

import java.time.LocalDate;

public class CreateLoanDTO {

    private Integer bookId;
    private Integer userId;
    private LocalDate loanDate;
    private LocalDate dueDate;

    public CreateLoanDTO() {
    }

    public CreateLoanDTO(Integer bookId, Integer userId, LocalDate loanDate, LocalDate dueDate) {
        this.bookId = bookId;
        this.userId = userId;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}