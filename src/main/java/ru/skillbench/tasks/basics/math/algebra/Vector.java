package ru.skillbench.tasks.basics.math.algebra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Математический вектор
 */
public class Vector implements Cloneable{
    private List<Double> list;

    public static Vector arange(int start, int end) {
        return new Vector(IntStream.range(start, end).asDoubleStream().boxed().collect(Collectors.toList()));
    }

    public Vector(List<Double> list) {
        this.list = list;
    }

    public Vector() {
        this.list = new ArrayList<>();
    }

    public Vector(int i) {
        this.list = new ArrayList<>();
        for (int j = 0; j < i; j++) {
            list.add(0D);
        }
    }

    public List<Double> getList() {
        return list;
    }

    public void setList(List<Double> list) {
        this.list = list;
    }

    public Double getAt(int i) {
        return list.get(i);
    }

    public void setAt(int i, Double number) {
        list.set(i, number);
    }

    public int size(){
        return list.size();
    }

    public void add(Double value) {
        list.add(value);
    }

    public Vector sum(Vector list) {
        if (size() != list.size()) {
            throw new IllegalArgumentException("Error sum: vectors have different lengths");
        }
        return new Vector(IntStream.range(0, size())
            .mapToObj(i -> getAt(i) + list.getAt(i))
            .collect(Collectors.toList()));
    }

    public double norm() {
        double m = Math.abs(getAt(0));
        for (int i = 0; i < size(); i++) {
            if (Math.abs(getAt(i)) > m) {
                m = Math.abs(getAt(i));
            }
        }
        return m;
    }

    public Vector multiplyByNumber(Double number) {
        return new Vector(list.stream().map(element -> element * number).collect(Collectors.toList()));
    }

    public Double dotProduct(Vector argument) {
        if (size() != argument.size()) {
            throw new IllegalArgumentException("Error dotProduct: vectors have different lengths");
        }
        return IntStream.range(0, size())
            .mapToDouble(i -> getAt(i) * argument.getAt(i))
            .sum();
    }

    public void swap(int i, int j) {
        Collections.swap(getList(), i, j);
    }

    public void forEach(Consumer<Double> action) {
        list.forEach(action);
    }

    public Vector permutation(Vector transposition) {
        Vector permutationVector = new Vector(list.size());
        for (int i = 0; i < transposition.size(); i++) {
            permutationVector.setAt(i, list.get(i));
        }
        return permutationVector;
    }

    public Vector inverse() {
        Vector result = new Vector(size());
        for (int i = 0; i < size(); i++) {
            result.setAt(i, getAt(size() - i - 1));
        }
        return result;
    }

    public Vector inLineMatrix(Matrix matrix) {
        Vector result = new Vector();
        for (int i = 0; i < matrix.sizeRows(); i++) {
            for (int j = 0; j < matrix.sizeColumns(); j++) {
                result.add(matrix.getElement(i, j));
            }
        }
        return result;
    }

    @Override
    public Vector clone() {
        List<Double> clone = new ArrayList<>(list.size());
        clone.addAll(list);
        return new Vector(clone);
    }

    @Override
    public String toString() {
        return list.toString() + "\n";
    }
}
