package ru.skillbench.tasks.basics.math.solvers;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BrentSolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Решатель полиномов
 */
public class PolynomialRootsFinder {
    private final BrentSolver solver = new BrentSolver();

    public Double[] solve(double... coefficients) {
        UnivariateFunction function = x -> coefficients[0] * x * x + coefficients[1] * x + coefficients[2];
        double start = -10000D;
        double interval = 0.01D;
        List<Double> result = new ArrayList<>();
        while (start < 10000D) {
            start += interval;
            if(Math.signum(function.value(start)) != Math.signum(function.value(start+interval))) {
                result.add(solver.solve(1000, function, start, start+interval));
            }
        }
        return result.toArray(new Double[0]);
    }
}
