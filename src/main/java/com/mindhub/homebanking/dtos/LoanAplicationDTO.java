package com.mindhub.homebanking.dtos;

public class LoanAplicationDTO {
    private Long loanId;
    private Double amount;
    private Integer payments;
    private String toAccountNumber;

//    para recibir datos desde el front por body se usa este dto con parametros puntuales y jackson necesita este constructor vacio,
//    no es necesario tener un cosntructor con los atributos por parametros
    public LoanAplicationDTO() {

    }

    public Long getLoanId() {
        return loanId;
    }

    public Double getAmount() {
        return amount;
    }
    public Integer getPayments() {
        return payments;
    }
    public String getToAccountNumber() {
        return toAccountNumber;
    }
}
