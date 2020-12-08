package br.com.rmacario.itau.domain.customer.account;

import java.util.Optional;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByNumber(@Param("number") Long accountNumber);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("a FROM Accounta a WHERE a.id = :id")
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "500")})
    Account findByIdAndLockEntity(@Param("id") Long accountId);
}
