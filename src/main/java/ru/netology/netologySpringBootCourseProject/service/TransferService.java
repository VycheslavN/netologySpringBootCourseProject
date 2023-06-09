package ru.netology.netologySpringBootCourseProject.service;

import org.springframework.stereotype.Service;
import ru.netology.netologySpringBootCourseProject.exceptions.InvalidTransactionExceptions;
import ru.netology.netologySpringBootCourseProject.log.LogBuilder;
import ru.netology.netologySpringBootCourseProject.log.TransferLog;
import ru.netology.netologySpringBootCourseProject.model.*;
import ru.netology.netologySpringBootCourseProject.repository.TransferRepository;

import java.math.BigDecimal;

@Service
public class TransferService {
    private final TransferRepository transferRepository;
    //1%
    static final int COMMISSION = 100;
    static final String SECRET_CODE = "0000";
    private final TransferLog transferLog;

    public TransferService(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
        this.transferLog = TransferLog.getInstance();
    }

    public String transferMoneyCardToCard(CardTransfer cardTransfer) throws InvalidTransactionExceptions {
        Card cardFrom = new Card(
                cardTransfer.getCardFromNumber(),
                cardTransfer.getCardFromValidTill(),
                cardTransfer.getCardFromCVV()
        );

        String cardToNumber = cardTransfer.getCardToNumber();
        Amount amount = new Amount(cardTransfer.getAmount().getValue(),
                cardTransfer.getAmount().getCurrency());
        if (cardFrom.getCardNumber().equals(cardToNumber)) {
            throw new InvalidTransactionExceptions("Карта для перевода и получения совпадает!\n" +
                    "Проверьте входные данные ещё раз");
        }

        BigDecimal balanceFrom = transferRepository.getMapStorage().get(cardFrom.getCardNumber()).getAmount().getValue();
        Amount commission = new Amount(amount.getValue().divide(BigDecimal.valueOf(COMMISSION)), amount.getCurrency());
        BigDecimal sumResult = commission.getValue().add(amount.getValue());

        // пишем проверку баланса и перевод денег
        LogBuilder logBuilder = new LogBuilder()
                //.setOperationId(operationId)
                .setCardNumberFrom(cardFrom.getCardNumber())
                .setCardNumberTo(cardToNumber)
                .setAmount(amount)
                .setCommission(commission);
        if (balanceFrom.compareTo(sumResult) >= 0) {
            logBuilder.setResult("ЗАПРОС НА ПЕРЕВОД");
            transferLog.log(logBuilder);
        } else {
            logBuilder.setResult("НЕДОСТАТОЧНО СРЕДСТВ ДЛЯ ОПЕРАЦИИ");
            transferLog.log(logBuilder);
            throw new InvalidTransactionExceptions(logBuilder.getResult());
        }
        return cardToNumber;
    }


    private String operationWithMoney(Verification verification, Operation operation) throws InvalidTransactionExceptions {
        if (verification.getCode().equals(operation.getSecretCode())) {
            System.out.println("СЕКРЕТНЫЙ КОД СОВПАДАЕТ");
            BigDecimal balanceFrom = transferRepository.getMapStorage().get(operation.getCardFromNumber()).getAmount().getValue();
            BigDecimal sumResult = operation.getCommission().getValue().add(operation.getAmount().getValue());
            LogBuilder logBuilder = new LogBuilder()
                    .setOperationId(verification.getOperationId())
                    .setCardNumberFrom(operation.getCardFromNumber())
                    .setCardNumberTo(operation.getCardToNumber())
                    .setAmount(operation.getAmount())
                    .setCommission(operation.getCommission());
            if (balanceFrom.compareTo(sumResult) >= 0) {
                //устанавливаем новый баланс на нашу исходную карту
                transferRepository.getMapStorage().get(operation.getCardFromNumber())
                        .getAmount()
                        .setValue(
                                balanceFrom.subtract(sumResult)
                        );
                BigDecimal balanceTo = transferRepository.getMapStorage().get(operation.getCardToNumber()).getAmount().getValue();

                //устанавливаем новый баланс на карту перевода
                transferRepository.getMapStorage().get(operation.getCardToNumber())
                        .getAmount()
                        .setValue(balanceTo.add(operation.getAmount().getValue()
                        ));
                logBuilder.setResult(String.format("ТРАНЗАКЦИЯ ПРОШЛА УСПЕШНО! ВАШ БАЛАНС СОСТАВЛЯЕТ: %.2f %s",
                        transferRepository.getMapStorage().get(operation.getCardFromNumber()).getAmount().getValue().divide(new BigDecimal(100)),
                        operation.getAmount().getCurrency()));
                transferLog.log(logBuilder);
                //todo удалить заглушку и сделать для всех операций удаление, когда будем получать с front id операции
                if (verification.getOperationId() != null) {
                    transferRepository.deleteWaitOperation(verification.getOperationId());
                }
                return "Успешная транзакция №" + verification.getOperationId();
            } else {
                logBuilder.setResult("НЕДОСТАТОЧНО СРЕДСТВ ДЛЯ ОПЕРАЦИИ");
                transferLog.log(logBuilder);
                throw new InvalidTransactionExceptions(logBuilder.getResult());
            }
        } else {
            throw new InvalidTransactionExceptions("Такой операции нет");
        }
    }
}
