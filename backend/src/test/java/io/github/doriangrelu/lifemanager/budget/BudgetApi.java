package io.github.doriangrelu.lifemanager.budget;

import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.CategoryResponse;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.ExpenseRequest;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.ExpenseResponse;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.MonthlyBudgetResponse;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.BlockingHttpClient;

import java.util.List;
import java.util.UUID;

/** Petit client de test pour l'API du budget : un appel = une méthode lisible. */
record BudgetApi(BlockingHttpClient http) {

    List<CategoryResponse> categories(String token, UUID household) {
        return http.retrieve(HttpRequest.GET(base(household) + "/expense-categories").bearerAuth(token),
                Argument.listOf(CategoryResponse.class));
    }

    UUID category(String token, UUID household, String name) {
        return categories(token, household).stream()
                .filter(category -> category.name().equals(name))
                .findFirst().orElseThrow().id();
    }

    void archive(String token, UUID household, UUID category) {
        http.exchange(HttpRequest.POST(base(household) + "/expense-categories/" + category + "/archive", "")
                .bearerAuth(token));
    }

    ExpenseResponse record(String token, UUID household, ExpenseRequest expense) {
        return http.retrieve(HttpRequest.POST(base(household) + "/expenses", expense).bearerAuth(token),
                ExpenseResponse.class);
    }

    ExpenseResponse correct(String token, UUID household, UUID expense, ExpenseRequest correction) {
        return http.retrieve(HttpRequest.PUT(base(household) + "/expenses/" + expense, correction).bearerAuth(token),
                ExpenseResponse.class);
    }

    void delete(String token, UUID household, UUID expense) {
        http.exchange(HttpRequest.DELETE(base(household) + "/expenses/" + expense).bearerAuth(token));
    }

    MonthlyBudgetResponse month(String token, UUID household, String month) {
        return http.retrieve(HttpRequest.GET(base(household) + "/budget/" + month).bearerAuth(token),
                MonthlyBudgetResponse.class);
    }

    private static String base(UUID household) {
        return "/api/households/" + household;
    }
}
