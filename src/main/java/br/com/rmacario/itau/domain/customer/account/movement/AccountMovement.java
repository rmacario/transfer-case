package br.com.rmacario.itau.domain.customer.account.movement;

import br.com.rmacario.itau.domain.customer.account.Account;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
@Table(name = "ACCOUNT_MOVEMENT")
public class AccountMovement implements Serializable {

    private static final long serialVersionUID = 7418353662634200935L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MOVEMENT")
    Long id;

    @NonNull
    @Column(name = "NUM_VALUE", precision = 10, scale = 6, nullable = false)
    BigDecimal value;

    @Builder.Default
    @Column(name = "DAT_CREATION", nullable = false)
    ZonedDateTime createdAt = ZonedDateTime.now();

    @NonNull
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_ACCOUNT_OWNER", nullable = false, updatable = false)
    Account account;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "IND_MOVEMENT_TYPE", updatable = false, length = 50)
    MovementType type;

    @NonNull
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_ACCOUNT_ORIGIN", nullable = false, updatable = false)
    Account accountOrigin;

    @NonNull
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_ACCOUNT_TARGET", nullable = false, updatable = false)
    Account accountTarget;

    @Setter
    @Builder.Default
    @Column(name = "FLG_SUCCESS", nullable = false)
    Boolean success = Boolean.TRUE;
}
