package io.github.doriangrelu.lifemanager.budget.application.port.in;

import io.github.doriangrelu.lifemanager.budget.application.port.in.command.CorrectIncomeCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.IncomeView;

/** Corrige une rentrée d'argent. */
public interface CorrectIncome {

    IncomeView correct(CorrectIncomeCommand command);
}
