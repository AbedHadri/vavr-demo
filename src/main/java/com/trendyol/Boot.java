package com.trendyol;

import com.trendyol.model.Destination;
import com.trendyol.model.Source;
import io.vavr.Tuple;
import io.vavr.collection.*;
import io.vavr.concurrent.Future;
import io.vavr.control.Either;
import io.vavr.control.Option;

import java.util.Optional;

import static io.vavr.Predicates.*;
import static io.vavr.Patterns.*;
import static io.vavr.API.*;

public class Boot {
    public static void main(String[] args) {

        /////////////////////
        // options
        //////////////////////
        Optional<Integer> maybeInteger = Optional.ofNullable(1);
        Option<Integer> maybeVavrInteger = Option.of(1);
        Option<Integer> someInteger = Some(1);
        Option<Integer> none = None();

        // not not to use Some of null!!!
        // Options are serializable unlike java's optionals!!!!!!!

        Option<Integer> maybeTransformedInteger = someInteger.map(x -> x + 1);
        maybeTransformedInteger.toJavaOptional();

        ///////////////////////////////
        // collections
        //////////////////////
        List<Integer> integers = List.of(1, 3, 3, 4, 5);

        Integer foldedResult =
                integers
                        .distinct()
                        .map(x -> x + 1)
                        .fold(0, (acc, i) -> {
                            if (acc < 10) {
                                return acc + i;
                            } else {
                                return acc;
                            }
                        });


        ////////////////////////////////
        // pattern matching
        ///////////////////////////////
        String s = Match(222).of(
                Case($(1), "one"),
                Case($(2), "two"),
                Case($(), "unknown")
        );

        String s1 = Match(foldedResult).of(
                Case($(isIn(1, 2, 0, -1)), "two/one"),
                Case($(3), "three"),
                Case($(), "unknown")
        );

        Option<String> maybeS = Match(foldedResult).option(
                Case($(1), "one"),
                Case($(2), "two")
        );

        Number c = 1;

        Number result = Match(c).of(
                Case($(instanceOf(Double.class)), x -> x + 1),
                Case($(instanceOf(Long.class)), x -> x + 2));


        //////////////////////////////
        // tuples
        /////////////////////////////
        Tuple.of(1, 2L, "", "");

        List<Source> users = List.of(
                new Source(1, "user1", "user1"),
                new Source(2, "user2", "user2"),
                new Source(3, "user3", "user3 111111"));

        users
                .map(user -> Tuple.of(new Destination(user.getName(), user.getSurname()), user.getName() + user.getSurname()))
                .filter(x -> x._2.length() > 12)
                .map(destFullNameTuple -> {
                    doSthWithFullName(destFullNameTuple._2);
                    return destFullNameTuple;
                })
                .map(x -> {
                    System.out.println(x._2);
                    return x._1;
                });

        ////////////////////////
        // for comprehension
        ////////////////////////
        For(
                List.of(1, 2),
                Option(1),
                Either.right(1),
                Future.successful(1),
                Future.successful(1)
        ).yield(Tuple::of)
                .map(x -> x._2 + x._1 + x._3)
                .getOrElse(0);


    }


    private static void doSthWithFullName(String f) {

    }


    private static Either<Throwable, Integer> either(String f) {
        /////////////////////////
        // try
        ///////////////////////

        return Try(Boot::doSthDangerous)
                .recover(x -> {
                    System.out.println("failed and im in fallback method");
                    return -1;
                })

                .onSuccess(z -> {
                    System.out.println("successful");
                }).onFailure(error -> {
            System.out.println(error.getMessage());
        }).toEither();
    }

    private static Integer doSthDangerous() throws Exception {
        return 1;
    }

    private static Integer doSthNormal() {
        return 1;
    }

}

