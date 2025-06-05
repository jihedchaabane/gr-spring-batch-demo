package com.chj.gr.processors;

import org.springframework.batch.item.ItemProcessor;

import com.chj.gr.entity.Transaction;

/**
 * Processeur pour transformer les données de transaction.
 * Exemple : Valider et mettre à jour le statut.
 */
public class TransactionItemProcessor implements ItemProcessor<Transaction, Transaction> {

    @Override
    public Transaction process(Transaction transaction) throws Exception {
        // Exemple de transformation : Mettre le statut en majuscules
        transaction.setStatus(transaction.getStatus().toUpperCase());
        // Ajouter une description si vide
        if (io.micrometer.common.util.StringUtils.isEmpty(transaction.getDescription())) {
            transaction.setDescription("Processed transaction");
        }
        return transaction;
    }
}