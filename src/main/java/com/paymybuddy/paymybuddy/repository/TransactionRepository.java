package com.paymybuddy.paymybuddy.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paymybuddy.paymybuddy.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer>{
    //List<Transaction> findBySenderIdOrReceiverIdOrderByDateCreatedDesc(int senderId, int receiverId);

    Page<Transaction> findBySenderIdOrReceiverIdOrderByDateCreatedDesc(
        int senderId, int receiverId, Pageable pageable);
}
