package br.com.rmacario.transfer.domain.customer;

import static javax.persistence.CascadeType.ALL;

import br.com.rmacario.transfer.domain.customer.account.Account;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "CUSTOMER")
public class Customer implements Serializable {

    private static final long serialVersionUID = -2190942176400307002L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CUSTOMER")
    Long id;

    @NonNull
    @Column(name = "NAM_CUSTOMER", nullable = false, length = 250)
    String name;

    @Builder.Default
    @Column(name = "DAT_CREATION", nullable = false)
    ZonedDateTime createdAt = ZonedDateTime.now();

    @NonNull
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "customer", cascade = ALL)
    Account account;

    /** Preenche o relacionamento da {@link Account} com o {@link Customer}. */
    public Customer bindAccount() {
        if (account != null) {
            account.setCustomer(this);
        }

        return this;
    }
}
