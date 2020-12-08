package br.com.rmacario.itau.domain.customer.account.movement;

import org.springframework.data.jpa.repository.JpaRepository;

interface AccountMovementRepository extends JpaRepository<AccountMovement, Long> {}
