package br.com.rmacario.itau.domain.customer.account.movement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface AccountMovementRepository extends JpaRepository<AccountMovement, Long> {

    /**
     * Busca uma pagina de movimentações financeiras da conta informada, ordenando em ordem
     * decrescente pelo ID da movimentação.
     *
     * @param pageable Informações da paginação que deverá ser realizada.
     * @return {@link Page} com as {@link AccountMovement} encontradas.
     */
    Page<AccountMovement> findByAccountNumberOrderByIdDesc(
            @Param("accountNumber") Long accountNumber, Pageable pageable);
}
