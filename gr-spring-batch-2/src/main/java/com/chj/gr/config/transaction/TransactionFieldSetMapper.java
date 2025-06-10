package com.chj.gr.config.transaction;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.boot.context.properties.bind.BindException;

import com.chj.gr.entity.Transaction;

public class TransactionFieldSetMapper implements FieldSetMapper<Transaction> {
    @Override
    public Transaction mapFieldSet(FieldSet fieldSet) throws BindException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(fieldSet.readString("transactionId"));
        String amountStr = fieldSet.readString("amount");
        transaction.setAmount(new java.math.BigDecimal(amountStr)); // Will throw NumberFormatException if invalid
        transaction.setStatus(fieldSet.readString("status"));
        transaction.setTransactionDate(fieldSet.readString("transactionDate"));
//        transaction.setTransactionDate(fieldSet.readDate("transactionDate", "yyyy-MM-dd'T'HH:mm:ss"));
        transaction.setDescription(fieldSet.readString("description"));
        return transaction;
    }
}
