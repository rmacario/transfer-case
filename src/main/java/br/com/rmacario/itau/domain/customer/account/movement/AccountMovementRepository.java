package br.com.rmacario.itau.domain.customer.account.movement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountMovementRepository extends JpaRepository<AccountMovement, Long> {

    /**
     * Busca uma pagina de movimentações financeiras da conta informada, ordenando em ordem
     * decrescente pelo ID da movimentação.
     *
     * @param pageable Informações da paginação que deverá ser realizada.
     * @return {@link Page} com as {@link AccountMovement} encontradas.
     */
    @Query(
            value =
                    "FROM AccountMovement am JOIN FETCH am.account a JOIN FETCH am.accountTarget "
                            + "WHERE a.number=:accountNumber",
            countQuery =
                    "SELECT count(am) FROM AccountMovement am WHERE am.account.number=:accountNumber")
    Page<AccountMovement> findByAccountNumberOrderByIdDesc(
            @Param("accountNumber") Long accountNumber, Pageable pageable);
}
