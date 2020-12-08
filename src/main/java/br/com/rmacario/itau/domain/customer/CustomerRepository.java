package br.com.rmacario.itau.domain.customer;

import br.com.rmacario.itau.domain.customer.account.CustomerNotFoundException;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Realiza uma busca por {@link Customer} de forma paginada e ordenando por {@link
     * Customer#getId()}.
     *
     * @param pageable Informações da paginação que deverá ser realizada.
     * @return {@link Page} com os customers encontrados.
     */
    Page<Customer> findByOrderByIdAsc(Pageable pageable);

    /**
     * Busca um {@link Customer} pelo número de sua conta.
     *
     * @param accountNumber Número da conta.
     * @return {@link Optional} encapsulando o registro encontrado, ou vazio, caso a consulta não
     *     encontre registros.
     */
    Optional<Customer> findByAccountNumber(@Param("number") Long accountNumber);

    /**
     * Busca um {@link Customer} pelo número de sua conta.
     *
     * @param accountNumber Número da conta.
     * @return {@link Customer} encontrado.
     * @throws CustomerNotFoundException Caso nenhum registro seja encontrado.
     */
    default Customer findOneByAccountNumber(@NonNull Long accountNumber) {
        return findByAccountNumber(accountNumber)
                .orElseThrow(() -> new CustomerNotFoundException(accountNumber));
    }
}
