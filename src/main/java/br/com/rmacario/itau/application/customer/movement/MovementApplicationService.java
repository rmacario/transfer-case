package br.com.rmacario.itau.application.customer.movement;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import br.com.rmacario.itau.domain.customer.CustomerRepository;
import br.com.rmacario.itau.domain.customer.account.movement.AccountMovement;
import br.com.rmacario.itau.domain.customer.account.movement.AccountMovementRepository;
import br.com.rmacario.itau.domain.customer.account.movement.MovementDomainService;
import br.com.rmacario.itau.domain.customer.account.movement.MovementType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    private static final Integer PAGINATION_PAGE_SIZE = 10;

    MovementDomainService movementDomainService;

    CustomerRepository customerRepository;

    AccountMovementRepository accountMovementRepository;

    /**
     * Orquestra chamadas de transferências de fundos de modo a obter os insumos necessários para
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

    /**
     * Retorna uma {@link Page} com as movimentações financeiras relacionadas com o número da conta
     * informado.
     *
     * @param accountNumber Número da conta.
     * @param page Número da página que deve ser retornada.
     * @return {@Page} encapsulando as {@link AccountMovement} encontradas.
     */
    public Page<AccountMovement> findByAccountNumber(
            @NonNull final Long accountNumber, @NonNull final Integer page) {
        return accountMovementRepository.findByAccountNumberOrderByIdDesc(
                accountNumber, PageRequest.of(page, PAGINATION_PAGE_SIZE));
    }
}
