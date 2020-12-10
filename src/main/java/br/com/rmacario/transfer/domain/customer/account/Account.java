package br.com.rmacario.transfer.domain.customer.account;

import br.com.rmacario.transfer.domain.customer.Customer;
import br.com.rmacario.transfer.domain.customer.account.movement.AccountMovement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "ACCOUNT")
public class Account implements Serializable {

    private static final long serialVersionUID = 1372263091506918886L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ACCOUNT")
    Long id;

    @NonNull
    @Column(name = "NUM_ACCOUNT", unique = true, updatable = false, nullable = false)
    Long number;

    @NonNull
    @Column(name = "NUM_BALANCE", precision = 10, scale = 6, nullable = false)
    BigDecimal balance;

    @Setter
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CUSTOMER", nullable = false)
    Customer customer;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    List<AccountMovement> accountMovements;

    @Version @ToString.Exclude Integer version;

    public void subtractBalance(@NonNull final BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public void addBalance(@NonNull final BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
}
