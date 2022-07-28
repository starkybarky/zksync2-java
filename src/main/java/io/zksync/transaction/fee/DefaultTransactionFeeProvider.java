package io.zksync.transaction.fee;

import io.zksync.methods.request.Transaction;
import io.zksync.methods.response.ZksEstimateFee;
import io.zksync.protocol.ZkSync;
import io.zksync.protocol.core.Token;
import io.zksync.protocol.exceptions.JsonRpcResponseException;
import io.zksync.transaction.Execute;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.math.BigInteger;

@AllArgsConstructor
public class DefaultTransactionFeeProvider implements ZkTransactionFeeProvider {

    private ZkSync zksync;
    private Token feeToken;

    @Override
    @SneakyThrows
    public <T extends io.zksync.transaction.Transaction> Fee getFee(T transaction) {
        return getFee(new io.zksync.methods.request.Transaction((Execute) transaction));
    }

    @SneakyThrows
    @Override
    public Fee getFee(Transaction transaction) {
        ZksEstimateFee estimateFee = this.zksync.zksEstimateFee(transaction).send();

        if (estimateFee.hasError()) {
            throw new JsonRpcResponseException(estimateFee);
        }

        return estimateFee.getResult();
    }

    @Override
    public Token getFeeToken() {
        return this.feeToken;
    }

    @Override
    public BigInteger getGasPrice(String contractFunc) {
        return zksync.ethGasPrice(feeToken.getL2Address()).sendAsync().join().getGasPrice();
    }

    @Override
    public BigInteger getGasPrice() {
        return zksync.ethGasPrice(feeToken.getL2Address()).sendAsync().join().getGasPrice();
    }

    @Override
    public BigInteger getGasLimit(String contractFunc) {
        return null;
    }

    @Override
    public BigInteger getGasLimit() {
        return null;
    }

    @Override
    public BigInteger getGasLimit(Transaction transaction) {
        return zksync.ethEstimateGas(transaction).sendAsync().join().getAmountUsed();
    }
}
