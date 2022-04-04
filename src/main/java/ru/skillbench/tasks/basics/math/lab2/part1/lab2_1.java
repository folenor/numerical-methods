package ru.skillbench.tasks.basics.math.lab2.part1;

import org.json.simple.parser.ParseException;
import ru.skillbench.tasks.basics.math.solvers.EquationSolver;
import ru.skillbench.tasks.basics.math.util.ObjectParser;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

/**
 * Лабораторная 2.1
 */
public class lab2_1 {
    public static void main(String[] args) throws IOException, ParseException {
        ObjectParser parser = new ObjectParser("lab2_1.json");
        Function<Double, Double> f = x -> Math.pow(4, x) - 5*x - 2;
        Function<Double, Double> derivative = x -> Math.pow(4, x)*Math.log(4) - 5;
        double left = parser.getParameter("left");
        double right = parser.getParameter("right");
        double epsilon = parser.getEpsilon();
        List<Object> result = EquationSolver.newtonMethod(left, right, epsilon, f, derivative);
        System.out.println("Iteration Newton : " + result.get(1));
        System.out.println("Result Newton: " + result.get(0));
        System.out.println("Check function value in point : " + f.apply((Double) result.get(0)));

        double q = 1 / derivative.apply(right);
        Function<Double, Double> phi = x -> x - q * f.apply(x);
        result = EquationSolver.simpleIteration(left, right, epsilon, phi);
        System.out.println("Iteration Simple : " + result.get(1));
        System.out.println("Result Simple: " + result.get(0));
        System.out.println("Check function value in point : " + f.apply((Double) result.get(0)));

    }
}
