package io.github.doriangrelu.lifemanager.architecture;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.simpleNameEndingWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;

/**
 * Vérifie automatiquement les règles de dépendance de docs/architecture.md.
 */
class ArchitectureTest {

    private static final String ROOT = "io.github.doriangrelu.lifemanager";

    /** Domaines métiers : à compléter à chaque nouveau domaine. */
    private static final List<String> DOMAINS = List.of("identity", "household", "budget", "vehicle", "shopping", "reminder", "bills");

    private static final JavaClasses CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(ROOT);

    @Test
    void domain_layer_is_pure_java() {
        classes().that().resideInAPackage(ROOT + "..domain..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage("java..", ROOT + "..domain..")
                .because("le métier ne dépend d'aucun framework")
                .allowEmptyShould(true)
                .check(CLASSES);
    }

    @Test
    void application_layer_does_not_depend_on_infrastructure() {
        noClasses().that().resideInAPackage(ROOT + "..application..")
                .should().dependOnClassesThat().resideInAPackage(ROOT + "..infrastructure..")
                .allowEmptyShould(true)
                .check(CLASSES);
    }

    @Test
    void controllers_only_use_input_ports() {
        noClasses().that().resideInAPackage(ROOT + "..infrastructure.in..")
                .should().dependOnClassesThat().resideInAPackage(ROOT + "..application.usecase..")
                .because("les adaptateurs entrants passent par les interfaces de port/in")
                .allowEmptyShould(true)
                .check(CLASSES);
    }

    @Test
    void kernel_and_platform_ignore_business_domains() {
        noClasses().that().resideInAnyPackage(ROOT + ".kernel..", ROOT + ".platform..")
                .should().dependOnClassesThat().resideInAnyPackage(domainPackages(DOMAINS))
                .allowEmptyShould(true)
                .check(CLASSES);
    }

    @Test
    void domains_only_see_each_other_through_contracts() {
        for (String domain : DOMAINS) {
            List<String> others = DOMAINS.stream().filter(other -> !other.equals(domain)).toList();
            DescribedPredicate<JavaClass> otherDomainInternals = resideInAnyPackage(domainPackages(others))
                    .and(not(resideInAnyPackage(contractPackages(others))));
            noClasses().that().resideInAPackage(ROOT + "." + domain + "..")
                    .should().dependOnClassesThat(otherDomainInternals)
                    .allowEmptyShould(true)
                    .check(CLASSES);
        }
    }

    @Test
    void contracts_are_only_consumed_by_event_listeners() {
        for (String domain : DOMAINS) {
            noClasses().that().resideOutsideOfPackages(
                            ROOT + "." + domain + "..", ROOT + "..infrastructure.in.event..")
                    .should().dependOnClassesThat().resideInAPackage(ROOT + "." + domain + ".contract..")
                    .allowEmptyShould(true)
                    .check(CLASSES);
        }
    }

    /**
     * Use cases sans foyer à contrôler : déclenchés par un événement ou une tâche planifiée, ou ne portant que
     * sur le demandeur lui-même (son compte, ses foyers, l'adhésion par code). Tout autre use case est appelé
     * avec un foyer venu de l'adresse : il DOIT vérifier que le demandeur en est membre.
     */
    private static final Set<String> USE_CASES_WITHOUT_HOUSEHOLD_CHECK = Set.of(
            "AdvanceChargeSchedulesUseCase", "RegisterHouseholdMemberUseCase", "CreateDefaultCategoriesUseCase",
            "CreateDefaultShoppingListsUseCase", "RecordChargeExpenseUseCase", "RecordChargeIncomeUseCase",
            "CreateHouseholdUseCase",
            "JoinHouseholdUseCase", "ListMembershipsUseCase", "ListMyHouseholdsUseCase", "UpdateMemberProfileUseCase",
            "GetCurrentUserUseCase", "SyncUserAccountUseCase", "ForgetVehicleDeadlinesUseCase",
            "TrackChargeDeadlineUseCase", "TrackVehicleDeadlinesUseCase",
            "ForgetHouseholdMemberUseCase", "ForgetHouseholdUseCase", "PurgeReminderHistoryUseCase");

    @Test
    void use_cases_check_household_membership() {
        // Les services *Access (et les *Recorder qui les appellent avant tout fichier) font le cloisonnement.
        DescribedPredicate<JavaClass> accessCheck = simpleNameEndingWith("Access").or(simpleNameEndingWith("Recorder"));
        DescribedPredicate<JavaClass> exempted = DescribedPredicate.describe("internes (sans foyer à contrôler)",
                javaClass -> USE_CASES_WITHOUT_HOUSEHOLD_CHECK.contains(javaClass.getSimpleName()));
        classes().that().resideInAPackage(ROOT + "..application.usecase..")
                .and().haveSimpleNameEndingWith("UseCase")
                .and(not(exempted))
                .should().dependOnClassesThat(accessCheck)
                .because("un foyer reçu dans l'adresse doit appartenir au demandeur (cloisonnement des foyers)")
                .check(CLASSES);
    }

    @Test
    void business_and_persistence_never_use_decimal_numbers() {
        noFields().that().areDeclaredInClassesThat().resideInAnyPackage(
                        ROOT + "..domain..", ROOT + "..application..", ROOT + "..infrastructure.out.persistence..")
                .should().haveRawType(double.class).orShould().haveRawType(Double.class)
                .orShould().haveRawType(float.class).orShould().haveRawType(Float.class)
                .orShould().haveRawType(BigDecimal.class)
                .because("les montants sont des centimes entiers (voir docs/architecture.md)")
                .allowEmptyShould(true)
                .check(CLASSES);
    }

    private static String[] domainPackages(List<String> domains) {
        return domains.stream().map(domain -> ROOT + "." + domain + "..").toArray(String[]::new);
    }

    private static String[] contractPackages(List<String> domains) {
        return domains.stream().map(domain -> ROOT + "." + domain + ".contract..").toArray(String[]::new);
    }
}
