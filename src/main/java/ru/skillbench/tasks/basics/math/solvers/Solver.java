package ru.skillbench.tasks.basics.math.solvers;

import ru.skillbench.tasks.basics.math.algebra.Matrix;
import ru.skillbench.tasks.basics.math.algebra.Vector;

import java.util.List;
import java.util.Objects;

/**
 * Решатель СЛАУ
 */
public class Solver {

    public static final String NUMBER_OF_ITERATION = "Число итераций: ";

    private Solver() {}

    private static final PolynomialRootsFinder POLYNOMIAL_ROOTS_FINDER = new PolynomialRootsFinder();

    public static Vector diagonalAlgorithm(Matrix matrix, Vector rightSide) {
        Vector x = new Vector();
        Vector y = new Vector();
        Vector a = new Vector();
        Vector b = new Vector();
        int n = matrix.sizeRows() - 1;
        y.add(matrix.getElement(0, 0));
        a.add(-matrix.getElement(0, 1)/y.getAt(0));
        b.add(rightSide.getAt(0)/y.getAt(0));
        for (int i = 1; i < n; i++) {
            y.add(matrix.getElement(i, i) + matrix.getElement(i, i-1)*a.getAt(i-1));
            a.add(-matrix.getElement(i, i+1)/y.getAt(i));
            b.add((rightSide.getAt(i) - matrix.getElement(i, i-1) * b.getAt(i-1))/y.getAt(i));
        }
        y.add(matrix.getElement(n, n) + matrix.getElement(n, n-1)*a.getAt(n-1));
        b.add((rightSide.getAt(n) - matrix.getElement(n, n-1)*b.getAt(n-1))/y.getAt(n));
        x.add(b.getAt(n));
        for (int i = 0; i < a.size(); i++) {
            x.add(x.getAt(x.size() - 1)*a.getAt(a.size() - i - 1) + b.getAt(b.size() - i - 2));
        }
        return x.inverse();
    }

    public static Vector simpleIterationMethod(Matrix matrix, Vector rightSide, Double epsilon) {
        List<Object> formed = getIterationForm(matrix, rightSide);
        Matrix copy = ((Matrix) formed.get(0)).clone();
        Matrix xOld = new Matrix(List.of((Vector) formed.get(1))).transpose();
        Matrix helper = new Matrix(List.of((Vector) formed.get(1))).transpose();
        int iteration = 0;
        double eps = 1;
        while (eps > epsilon) {
            Matrix xSave = xOld.clone();
            xOld = helper.sum(copy.multiply(xOld));
            Vector xOldVector = new Vector().inLineMatrix(xOld);
            Vector xSaveVector = new Vector().inLineMatrix(xSave);
            eps = xOldVector.sum(xSaveVector.multiplyByNumber(-1D)).normMax();
            iteration++;
        }
        System.out.println(NUMBER_OF_ITERATION + iteration);
        return new Vector().inLineMatrix(xOld);
    }

    public static Vector seidelMethod(Matrix matrix, Vector rightSide, Double epsilon) {
        List<Object> formed = getIterationForm(matrix, rightSide);
        Matrix alpha = ((Matrix) formed.get(0)).clone();
        Vector b = ((Vector) formed.get(1)).clone();
        Vector xOld = b.clone();
        Vector xNew;
        int iteration = 0;
        double eps = 1;
        while (eps > epsilon) {
            xNew = new Vector(b.size());
            for (int i = 0; i < alpha.sizeRows(); i++) {
                for (int j = 0; j < alpha.sizeColumns(); j++) {
                    if (j < i) {
                        xNew.setAt(i, xNew.getAt(i) + alpha.getElement(i, j) * xNew.getAt(j));
                    } else {
                        xNew.setAt(i, xNew.getAt(i) + alpha.getElement(i, j) * xOld.getAt(j));
                    }
                }
                xNew.setAt(i, xNew.getAt(i) + b.getAt(i));
            }
            eps = xOld.sum(xNew.multiplyByNumber(-1D)).normMax();
            iteration++;
            xOld = xNew.clone();
        }
        System.out.println(NUMBER_OF_ITERATION + iteration);
        return xOld;
    }

    public static List<Object> rotationMethod(Matrix matrix, Double epsilon) {
        int size = matrix.sizeRows();
        Matrix clone = matrix.clone();
        Matrix eigenvectors = Matrix.identityMatrix(size);
        int a = 0;
        while (true) {
            a++;
            Matrix uMatrix = Matrix.identityMatrix(size);
            List<Integer> indexes = indexesOfMax(clone);
            Integer iMax = indexes.get(0);
            Integer jMax = indexes.get(1);
            double phi;
            if (Objects.equals(clone.getElement(iMax, iMax), clone.getElement(jMax, jMax))) {
                phi = Math.PI/4;
            } else {
                phi = 0.5 * Math.atan((2 * clone.getElement(iMax, jMax)) / (clone.getElement(iMax, iMax) - clone.getElement(jMax, jMax)));
            }
            uMatrix.setElement(iMax, iMax, Math.cos(phi));
            uMatrix.setElement(jMax, jMax, Math.cos(phi));
            uMatrix.setElement(iMax, jMax, -Math.sin(phi));
            uMatrix.setElement(jMax, iMax, Math.sin(phi));

            eigenvectors = eigenvectors.multiply(uMatrix);
            Matrix u = uMatrix.transpose();
            clone = u.multiply(clone).multiply(uMatrix);
            double epsilonCalculated = 0D;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < i; j++) {
                    epsilonCalculated += clone.getElement(i, j) * clone.getElement(i, j);
                }
            }
            epsilonCalculated = Math.sqrt(epsilonCalculated);
            if (epsilonCalculated < epsilon) {
                System.out.println(NUMBER_OF_ITERATION + a);
                break;
            }
        }
        Vector eigenvalues = clone.getDiagonal();
        return List.of(eigenvalues, eigenvectors);
    }

    public static List<Object> eigenValuesQR(Matrix matrix, double epsilon) {
        Vector result = new Vector();
        int i = 0;
        Matrix aIteration = matrix.clone();
        while (i < matrix.sizeRows()) {
            List<Object> eigenvalue = getEigenValue(aIteration, epsilon, i);
            if (Boolean.TRUE.equals(eigenvalue.get(1))) {
                List<Double> vars = (List<Double>) eigenvalue.get(0);
                vars.forEach(result::add);
                i += 2;
            } else {
                List<Double> vars = (List<Double>) eigenvalue.get(0);
                vars.forEach(result::add);
                i += 1;
            }
            aIteration = (Matrix) eigenvalue.get(2);
        }
        return List.of(result, i);
    }

    public static List<Object> getEigenValue(Matrix matrix, double epsilon, int i) {
        Matrix aIteration = matrix.clone();
        while (true) {
            List<Matrix> decompose = getQR(aIteration);
            Matrix qMatrix = decompose.get(0);
            Matrix rMatrix = decompose.get(1);
            aIteration = rMatrix.multiply(qMatrix);
            if (aIteration.transpose().getAt(i).getSubVectorFrom(i+1).norm() <= epsilon) {
                return List.of(List.of(aIteration.getElement(i, i)), Boolean.FALSE, aIteration);
            } else if (aIteration.transpose().getAt(i).getSubVectorFrom(i+2).norm() <= epsilon &&
            finishIterationQRCondition(aIteration, epsilon, i)) {
                return List.of(List.of(getRoots(aIteration, i)),
                    Boolean.TRUE,
                    aIteration);
            }
        }
    }

    private static boolean finishIterationQRCondition(Matrix matrix, double epsilon, int i) {
        List<Matrix> decompose = getQR(matrix);
        Matrix qMatrix = decompose.get(0);
        Matrix rMatrix = decompose.get(1);
        Matrix aNext = rMatrix.multiply(qMatrix);
        Double[] lambda1 = getRoots(matrix, i);
        Double[] lambda2 = getRoots(aNext, i);
        return Math.abs(lambda1[0] - lambda2[0]) <= epsilon &&
            Math.abs(lambda1[1] - lambda2[1]) <= epsilon;
    }

    private static Double[] getRoots(Matrix matrix, int i) {
        int size = matrix.sizeRows();
        Double a11= matrix.getElement(i, i);
        Double a12 = i + 1 < size ? matrix.getElement(i, i+1) : 0D;
        Double a21 = i + 1 < size ? matrix.getElement(i+1, i) : 0D;
        double a22 = i + 1 < size ? matrix.getElement(i + 1, i+1) : 0D;
        return POLYNOMIAL_ROOTS_FINDER.solve(1, -a11 - a22, a11 * a22 - a12 * a21);
    }

    public static List<Matrix> getQR(Matrix matrix) {
        int size = matrix.sizeRows();
        Matrix qMatrix = Matrix.identityMatrix(size);
        Matrix aIteration = matrix.clone();
        for (int i = 0; i < size - 1; i++) {
            Vector column = aIteration.transpose().getAt(i);
            Matrix hMatrix = householder(column, aIteration.sizeRows(), i);
            qMatrix = qMatrix.multiply(hMatrix);
            aIteration = hMatrix.multiply(aIteration);
        }
        return List.of(qMatrix, aIteration);
    }

    private static Matrix householder(Vector column, int size, int k) {
        Vector v = new Vector(size);
        Vector a = column.clone();
        v.setAt(k, a.getAt(k) + sign(a.getAt(k))*a.getSubVectorFrom(k).norm());
        for (int i = k+1; i < size; i++) {
            v.setAt(i, a.getAt(i));
        }
        Matrix vMatrix = new Matrix(List.of(v)).transpose();
        return Matrix.identityMatrix(size)
            .sum(vMatrix
                .multiply(vMatrix.transpose())
                .multiplyByNumber(-2D / (vMatrix.transpose().multiply(vMatrix).getElement(0, 0))));
    }

    private static int sign(Double number) {
        return number > 0 ? 1: number < 0 ? -1 : 0;
    }


    private static List<Integer> indexesOfMax(Matrix matrix) {
        int iMax = 0;
        int jMax = 0;
        double aMax = 0D;
        for (int i = 0; i < matrix.sizeRows(); i++) {
            for (int j = i + 1; j < matrix.sizeRows(); j++) {
                if (Math.abs(matrix.getElement(i, j)) > aMax) {
                    aMax = Math.abs(matrix.getElement(i, j));
                    iMax = i;
                    jMax = j;
                }
            }
        }
        return List.of(iMax, jMax);
    }

    private static List<Object> getIterationForm(Matrix matrix, Vector rightSide) {
        Matrix formed = new Matrix(matrix.sizeRows(), matrix.sizeColumns());
        Vector formedVector = new Vector(rightSide.size());
        int counter = -1;
        for (int i = 0; i < matrix.sizeRows(); i++) {
            if (matrix.getElement(i, i) == 0) {
                counter = i;
                break;
            }
        }
        if (counter != -1) {
            matrix.swapRows(counter, counter-1);
            rightSide.swap(counter, counter-1);
        }
        for (int i = 0; i < matrix.sizeRows(); i++) {
            formedVector.setAt(i, rightSide.getAt(i)/matrix.getElement(i, i));
            for (int j = 0; j < matrix.sizeColumns(); j++) {
                formed.setElement(i, j, -matrix.getElement(i,j)/ matrix.getElement(i, i));
            }
            formed.setElement(i, i, 0D);
        }
        return List.of(formed, formedVector);
    }
}
