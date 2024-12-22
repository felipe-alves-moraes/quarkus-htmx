package com.fmoraes.errorhandling;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class NumbersApiGateway {

    private static final RandomGenerator RANDOM_GENERATOR = RandomGeneratorFactory.getDefault().create();

    private final NumbersApi numbersApi;

    public NumbersApiGateway(
        @RestClient final NumbersApi numbersApi) {
        this.numbersApi = numbersApi;
    }

    public String getFact(int number) {
        final var randomNumber = RANDOM_GENERATOR.nextInt(3);
        return switch (randomNumber) {
            case 0 -> numbersApi.getFact(number);
            case 1 -> {
                try {
                    Thread.sleep(5000);
                    yield  numbersApi.getFact(number);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }
            default -> throw new RuntimeException("unable to get a fact about the number: " + number);
        };
    }
}
