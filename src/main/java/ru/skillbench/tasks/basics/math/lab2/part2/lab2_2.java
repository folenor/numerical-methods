package ru.skillbench.tasks.basics.math.lab2.part2;

import org.json.simple.parser.ParseException;
import ru.skillbench.tasks.basics.math.solvers.EquationSolver;
import ru.skillbench.tasks.basics.math.util.ObjectParser;

import java.io.IOException;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Системы нелинейных уравнений
 */
public class lab2_2 {
    public static void main(String[] args) throws IOException, ParseException {
        ObjectParser parser = new ObjectParser("lab2_2.json");
        double left1 = parser.getParameter("left1");
        double right1 = parser.getParameter("right1");
        double left2 = parser.getParameter("left2");
        double right2 = parser.getParameter("right2");
        double epsilon = parser.getEpsilon();
        BiFunction<Double, Double, Double> f1 = (x1, x2) -> 3*x1 - Math.cos(x2);
        BiFunction<Double, Double, Double> f2 = (x1, x2) -> 3*x2 - Math.exp(x1);
        BiFunction<Double, Double, Double> f1X = (x1, x2) -> 3D;
        BiFunction<Double, Double, Double> f1Y = (x1, x2) -> Math.sin(x2);
        BiFunction<Double, Double, Double> f2X = (x1, x2) -> -Math.exp(x1);
        BiFunction<Double, Double, Double> f2Y = (x1, x2) -> 3D;
        List<Object> result = EquationSolver.newtonSystem(left1, right1, left2, right2, f1, f2, f1X, f1Y, f2X, f2Y, epsilon);
        System.out.println("Iteration Newton : " + result.get(2));
        System.out.println("Point : (" + result.get(0) + ", " + result.get(1) + ")");
        System.out.println("Check f1 : " + f1.apply((Double) result.get(0), (Double) result.get(1)));
        System.out.println("Check f2 : " + f2.apply((Double) result.get(0), (Double) result.get(1)));

        BiFunction<Double, Double, Double> phi1 = (x1, x2) -> Math.cos(x2) / 3;
        BiFunction<Double, Double, Double> phi2 = (x1, x2) -> Math.exp(x1) / 3;
        result = EquationSolver.simpleSystem(left1, right1, left2, right2, epsilon, phi1, phi2);
        System.out.println("Iteration Simple : " + result.get(2));
        System.out.println("Point : (" + result.get(0) + ", " + result.get(1) + ")");
        System.out.println("Check f1 : " + f1.apply((Double) result.get(0), (Double) result.get(1)));
        System.out.println("Check f2 : " + f2.apply((Double) result.get(0), (Double) result.get(1)));
    }
}
