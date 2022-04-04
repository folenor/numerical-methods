package ru.skillbench.tasks.basics.math.solvers;

import ru.skillbench.tasks.basics.math.algebra.Matrix;
import ru.skillbench.tasks.basics.math.algebra.Vector;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Решатель нелинейных уравнений
 */
public class EquationSolver {
    private EquationSolver() {}

    public static List<Object> newtonMethod(double left, double right, double epsilon, Function<Double, Double> function,
        Function<Double, Double> derivative) {
        double xPrevious = right;
        double x = 0;
        int count = 0;
        while (true) {
            count++;
            x = xPrevious - function.apply(xPrevious) / derivative.apply(xPrevious);
            double eps = Math.abs(function.apply(x) - function.apply(xPrevious));
            if (eps <= epsilon) {
                break;
            }
            xPrevious = x;
        }
        return List.of(x, count);
    }

    public static List<Object> simpleIteration(double left, double right, double epsilon, Function<Double, Double> function) {
        double xPrevious = right;
        double x = 0;
        int count = 0;
        while (true) {
            count++;
            x = function.apply(xPrevious);
            double eps = Math.abs(x - xPrevious);
            if (eps <= epsilon) {
                break;
            }
            xPrevious = x;
        }
        return List.of(x, count);
    }

    public static List<Object> newtonSystem(double left1, double right1, double left2, double right2,
        BiFunction<Double, Double, Double> f1, BiFunction<Double, Double, Double> f2,
        BiFunction<Double, Double, Double> derFirstByX, BiFunction<Double, Double, Double> derFirstByY,
        BiFunction<Double, Double, Double> derSecondByX, BiFunction<Double, Double, Double> derSecondByY, double epsilon) {
        double xPrevious = right1;
        double yPrevious = right2;
        double x = 0;
        double y = 0;
        int count = 0;
        while (true) {
            count++;
            Vector a11 = new Vector(f1.apply(xPrevious, yPrevious), derFirstByY.apply(xPrevious, yPrevious)); //первая строка A1
            Vector a12 = new Vector(f2.apply(xPrevious, yPrevious), derSecondByY.apply(xPrevious, yPrevious)); //вторая строка A1
            Matrix A1 = new Matrix(List.of(a11, a12));
            Vector a21 = new Vector(derFirstByX.apply(xPrevious, yPrevious), f1.apply(xPrevious, yPrevious)); //первая строка A2
            Vector a22 = new Vector(derSecondByX.apply(xPrevious, yPrevious), f2.apply(xPrevious, yPrevious)); //вторая строка A2
            Matrix A2 = new Matrix(List.of(a21, a22));
            Vector j1 = new Vector(derFirstByX.apply(xPrevious, yPrevious), derFirstByY.apply(xPrevious, yPrevious));
            Vector j2 = new Vector(derSecondByX.apply(xPrevious, yPrevious), derSecondByY.apply(xPrevious, yPrevious));
            Matrix J = new Matrix(List.of(j1, j2));
            x = xPrevious - A1.luDeterminant() / J.luDeterminant();
            y = yPrevious - A2.luDeterminant() / J.luDeterminant();
            double eps = Math.max(Math.abs(x - xPrevious), Math.abs(y-yPrevious));
            if (eps <= epsilon) {
                break;
            }
            xPrevious = x;
            yPrevious = y;
        }
        return List.of(x, y, count);
    }

    public static List<Object> simpleSystem(double left1, double right1, double left2, double right2, double epsilon,
        BiFunction<Double, Double, Double> phi1, BiFunction<Double, Double, Double> phi2) {
        double xPrevious = right1;
        double yPrevious = right2;
        double x = 0;
        double y = 0;
        int count = 0;
        while (true) {
            count++;
            x = phi1.apply(xPrevious, yPrevious);
            y = phi2.apply(xPrevious, yPrevious);
            double eps = Math.max(Math.abs(x - xPrevious), Math.abs(y-yPrevious));
            if (eps <= epsilon) {
                break;
            }
            xPrevious = x;
            yPrevious = y;
        }
        return List.of(x, y, count);
    }
}
