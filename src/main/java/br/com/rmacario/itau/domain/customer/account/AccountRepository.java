package br.com.rmacario.itau.domain.customer.account;

import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByNumber(@Param("number") Long accountNumber);

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("FROM Account a WHERE a.id = :id")
    Account findByIdAndLockEntity(@Param("id") Long accountId);
}
