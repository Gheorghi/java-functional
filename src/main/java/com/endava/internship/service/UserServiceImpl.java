package com.endava.internship.service;

import com.endava.internship.domain.Privilege;
import com.endava.internship.domain.User;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserServiceImpl implements UserService {
    Comparator<User> compareByAge = Comparator.comparing(User::getAge).reversed();
    Comparator<User> compareByLastName = Comparator.comparing((User::getLastName));

    @Override
    public List<String> getFirstNamesReverseSorted(List<User> users) {
        return users.stream()
                .sorted(Comparator.comparing(User::getFirstName).reversed())
                .map(User::getFirstName)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> sortByAgeDescAndNameAsc(final List<User> users) {
        return users.stream()
                .sorted(compareByAge.thenComparing(compareByLastName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Privilege> getAllDistinctPrivileges(final List<User> users) {
        return users.stream()
                .flatMap(user -> user.getPrivileges().stream().distinct())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> getUpdateUserWithAgeHigherThan(final List<User> users, final int age) {
        return users.stream().filter(user -> user.getAge() > age).findFirst();
    }

    @Override
    public Map<Integer, List<User>> groupByCountOfPrivileges(final List<User> users) {
        return users.stream().collect(Collectors.groupingBy(user -> user.getPrivileges().size()));
    }

    @Override
    public double getAverageAgeForUsers(final List<User> users) {
        return users.stream().mapToDouble(User::getAge).average().orElse(-1);
    }

    @Override
    public Optional<String> getMostFrequentLastName(final List<User> users) {
        return users.stream().
                collect(Collectors.groupingBy(User::getLastName, Collectors.counting()))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() >= 2)
                .reduce((e1, e2) -> e1.getValue() > e2.getValue() ? e1 : e2)
                .map(Map.Entry::getKey);
    }

    @Override
    public List<User> filterBy(final List<User> users, final Predicate<User>... predicates) {
        return users.stream()
                .filter(Stream.of(predicates).reduce(x -> true, Predicate::and))
                .collect(Collectors.toList());
    }

    @Override
    public String convertTo(final List<User> users, final String delimiter, final Function<User, String> mapFun) {
        return users.stream().map(mapFun).collect(Collectors.joining(delimiter));
    }

    @Override
    public Map<Privilege, List<User>> groupByPrivileges(List<User> users) {
        return users.stream()
                .flatMap(user -> user.getPrivileges().stream()
                        .map(privilege -> new AbstractMap.SimpleEntry<>(privilege, user)))
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
    }

    @Override
    public Map<String, Long> getNumberOfLastNames(final List<User> users) {
        return users.stream().collect(Collectors.groupingBy(User::getLastName, Collectors.counting()));
    }
}
