package edu.bi.springdemo.repository;

import edu.bi.springdemo.entity.Loan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends CrudRepository<Loan, Integer> {

    List<Loan> findByUser_Id(Integer userId);
}