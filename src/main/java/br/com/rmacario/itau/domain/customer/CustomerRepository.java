package br.com.rmacario.itau.domain.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Realiza uma busca por {@link Customer} de forma paginada e ordenando por {@link
     * Customer#getId()}.
     *
     * @param pageable Informações da paginação que deverá ser realizada.
     * @return {@link Page} com os customers encontrados.
     */
    Page<Customer> findByOrderByIdAsc(Pageable pageable);
}
