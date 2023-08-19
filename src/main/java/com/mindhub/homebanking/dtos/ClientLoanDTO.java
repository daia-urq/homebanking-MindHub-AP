package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.ClientLoan;

public class ClientLoanDTO {
    private Long id;
    private Long loan_id;
    private String name;
    private  Double amount;
    private Integer payments;

    public ClientLoanDTO(ClientLoan clientLoan) {
        this.id = clientLoan.getId();
        this.loan_id = clientLoan.getLoan().getId();
        this.name = clientLoan.getLoan().getName();
        this.amount = clientLoan.getAmount();
        this.payments = clientLoan.getPayments();
    }

    public Long getId() {
        return id;
    }


    public Long getLoan_id() {
        return loan_id;
    }



    public String getName() {
        return name;
    }


    public Double getAmount() {
        return amount;
    }


    public Integer getPayments() {
        return payments;
    }

}
