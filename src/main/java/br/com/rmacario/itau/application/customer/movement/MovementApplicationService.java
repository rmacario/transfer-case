package br.com.rmacario.itau.application.customer.movement;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import br.com.rmacario.itau.domain.customer.CustomerRepository;
import br.com.rmacario.itau.domain.customer.account.movement.AccountMovement;
import br.com.rmacario.itau.domain.customer.account.movement.MovementDomainService;
import br.com.rmacario.itau.domain.customer.account.movement.MovementType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Serviço que orquestra chamadas aos casos de uso que envolvem movimentações financeiras.
 *
 * @author rmacario
 */
@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = PACKAGE, onConstructor_ = @Autowired)
public class MovementApplicationService {

    MovementDomainService movementDomainService;

    CustomerRepository customerRepository;

    /**
     * Orquestra chamadas de transferências de valores de modo a obter os insumos necessários para
     * que a lógica e regras de negócio possam ser aplicadas pelo serviço de domínio.
     *
     * @param solicitation Informações sobre a transferência que será realizada.
     */
    public void requestTransferFunds(@NonNull final TransferFundsSolicitation solicitation) {
        final var customerOrigin =
                customerRepository.findOneByAccountNumber(solicitation.getAccountOrigin());
        final var customerTarget =
                customerRepository.findOneByAccountNumber(solicitation.getAccountTarget());

        final var accountMovement =
                AccountMovement.builder()
                        .value(solicitation.getAmount())
                        .account(customerOrigin.getAccount())
                        .accountOrigin(customerOrigin.getAccount())
                        .accountTarget(customerTarget.getAccount())
                        .type(MovementType.TRANSFER)
                        .build();

        movementDomainService.transferFunds(accountMovement);
    }
}
