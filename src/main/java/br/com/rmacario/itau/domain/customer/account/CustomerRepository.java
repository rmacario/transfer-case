package br.com.rmacario.itau.domain.customer.account;

import br.com.rmacario.itau.domain.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {}
