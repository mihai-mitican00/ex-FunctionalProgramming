package com.homework.exercises;

import com.homework.utils.Address;
import com.homework.utils.Employee;
import com.homework.utils.Employees;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.OrderWith;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;


@SuppressWarnings("all")
public class Pack_4_Streams_Difficult {

    private static final List<Employee> EMPLOYEES = Employees.allEmployees();

    @Test
    public void exercise_1_findFirst() {
        // find whether there are two employees with the same first name and surname and return the name
        // the solution has to be a single statement, complexity O(n^2) is acceptable

        String result = null;

        //TODO write your code here

        result = EMPLOYEES.stream()
                .filter(emp1 -> EMPLOYEES.stream().anyMatch(emp2 -> !emp1.equals(emp2)
                        && emp1.getFirstName().equals(emp2.getFirstName())
                        && emp1.getSurname().equals(emp2.getSurname()))
                )
                .map(e -> e.getFirstName() + " " + e.getSurname()).findFirst().get();

        assertThat(result, sameBeanAs("Holly Davies"));
    }

    @Test
    public void exercise_2_groupingBy_counting() {
        // find the total number of groups of at least 5 employees living close to each other
        // consider all employees with the same 2 first characters of the home address post code a single group
        // you can collect to map and then stream over it, however the solution has to be a single statement

        long result = 0;

        //TODO write your code here

        result = EMPLOYEES.stream()
                .collect(Collectors.groupingBy(emp -> emp.getHomeAddress().getPostCode().substring(0, 2), Collectors.toList()))
                .values().stream().filter(value -> value.size() >= 5).count();

        assertThat(result, sameBeanAs(110L));
    }

    @Test
    public void exercise_3_flatMap() {
        // find the total number of all different home and correspondence addresses

        long result = 0;

        //TODO write your code here

        result = EMPLOYEES.stream()
                .flatMap(emp -> Stream.of(emp.getHomeAddress(), emp.getCorrespondenceAddress().get()))
                .distinct()
                .count();

        assertThat(result, sameBeanAs(1820L));
    }

    @Test
    public void exercise_4_groupingBy_summingInt() {
        // find how much in total each company pays (annually) to their employees, order result by amount
        // you can convert the salaries to ints using BigDecimal#intValue method
        // you can collect to map and then stream over it, however the solution has to be a single statement

        DecimalFormat decimalFormat = new DecimalFormat("??#,###.00");
        List<String> result = null;

        //TODO write your code here

        result = EMPLOYEES.stream()
                .collect(Collectors.groupingBy(emp -> emp.getCompany().getName(), Collectors.summingInt(emp -> emp.getSalary().intValue())))
                .entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .map(entry -> entry.getKey() + " - " + decimalFormat.format(entry.getValue())).collect(Collectors.toList());


        assertThat(result, sameBeanAs(asList(
                "Anglo American - ??12,119,153.00",
                "HSBC - ??11,469,144.00",
                "Royal Bank of Scotland Group - ??11,127,807.00",
                "BP - ??10,925,088.00",
                "AstraZeneca - ??10,507,305.00",
                "HBOS - ??10,428,819.00",
                "Royal Dutch Shell - ??10,100,098.00",
                "Barclays plc - ??10,071,534.00",
                "Vodafone Group - ??10,029,401.00",
                "GlaxoSmithKline - ??9,499,235.00"
        )));
    }

    @Test
    public void exercise_5_patternCompileSplitAsStream() {
        // count the instances of words and output a list of formatted strings
        // output the strings sorted lexicographically by name
        // you can use collect twice
        // as always, a single statement solution is expected
        // hint: look at Pattern.compile(regex).splitAsStream(string)

        String string = "dog" + "\n" + "bird" + "\n" + "cat" + "\n" + "cat" + "\n" + "dog" + "\n" + "cat";
        List<String> result = null;

        //TODO write your code
        result = Pattern.compile("\n").splitAsStream(string)
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()))
                .entrySet().stream().sorted((entry1, entry2) -> entry1.getKey().compareTo(entry2.getKey()))
                .map(entry -> entry.getKey() + " - " + entry.getValue()).collect(Collectors.toList());

        assertThat(result, sameBeanAs(asList(
                "bird - 1",
                "cat - 3",
                "dog - 2"
        )));
    }

    @Test
    public void exercise_6_mapToLong() {
        // the rows and columns of the chess board are assigned arbitrary numbers (instead of letters and digits)
        // the value of the square is a multiplication of the numbers assigned to corresponding row and column
        // e.g. square A1 has a value 6,432 * 6,199 = 39,871,968
        // calculate the sum of values of all squares
        int[] rows = {6432, 8997, 8500, 7036, 9395, 9372, 9715, 9634};
        int[] columns = {6199, 9519, 6745, 8864, 8788, 7322, 7341, 7395};
        long result = 0;

        //TODO write your code here
        result = IntStream.range(0, rows.length)
                .mapToLong(i -> IntStream.range(0,columns.length)
                                         .mapToLong(j -> rows[i] * columns[j])
                                         .sum())
                .sum();

        assertThat(result, sameBeanAs(4294973013L));
    }

    @Test
    public void exercise_7_randomLongs_concat_toArray() {
        // concatenate two random streams of numbers (seed is fixed for testing purposes),
        // then revert the sign of the negative ones
        // then sort them and pick 10 middle elements (hint: you can use skip and limit)
        // then do modulo 1000 (remainder of division by 1000)
        // and finally collect the result into an array
        LongStream longs = new Random(0).longs(10);
        IntStream ints = new Random(0).ints(10);
        int[] result = null;


        //TODO write your code here
        result = LongStream.concat(longs, ints.asLongStream())
                .map(Math::abs)
                .sorted()
                .skip(5)
                .limit(10)
                .mapToInt(value -> (int)(value%1000))
                .toArray();

        assertThat(result, sameBeanAs(new long[]{106, 266, 402, 858, 313, 688, 303, 137, 766, 896}));
    }

}
