import matplotlib.pyplot as plt
from NM_1_1 import LUP_solve
from NM_1_1 import LUP_decompose
from NM_1_1 import ge





t_LU

target = '''
i:        0        1        2     3       4       5
Xi:     0.0      1.7       3.4   5.1     6.8      8.5 
Yi:     0.0      3.0038  5.2439  7.3583  9.4077   11.415 
'''


def func(x, values):
    return sum([c * (x ** i) for i, c in enumerate(values)])


def sse(f, y):
    return round(sum([(f_i - y_i) ** 2 for f_i, y_i in zip(f, y)]), 5)


def mls(n, x, y):
    matrix = [[] for _ in range(n + 1)]
    size = len(matrix)
    for i in range(n + 1):
        for j in range(n + 1):
            matrix[i].append(sum([x_j ** (i + j) for x_j in x]))

    b = [0 for _ in range(n + 1)]
    for i in range(n + 1):
        b[i] = sum([y_j * (x_j ** i) for x_j, y_j in zip(x, y)])

    P = LUP_decompose(matrix, size)
    L, U = get_LU(matrix)
    new_b = LUP_solve(L, U, P, b, size)
    return [round(i, 5) for i in new_b]


def f_printer(coefs):
    n = len(coefs)
    f = f'F{n - 1}(x) = '
    for i in range(n):
        f += f'{coefs[i]}x^{i} + '
    f = f[:-2]
    return f


if __name__ == '__main__':
    x = [0.0, 1.7, 3.4, 5.1, 6.8, 8.5]
    y = [0.0, 3.0038, 5.2439, 7.3583, 9.4077, 11.415]
    F = []
    err = []
    coefs = []

    for degree in [1, 2]:
        print(f'Degree = {degree}')
        coefs.append(mls(degree, x, y))
        print(f_printer(coefs[degree - 1]))
        F.append([func(i, coefs[degree - 1]) for i in x])
        err.append(sse(F[degree - 1], y))

    plt.scatter(x, y, color='r')
    plt.plot(x, F[0], color='m', label='Function')
    plt.plot(x, F[1], color='g', label='LessSquareMethod')
    plt.legend(loc='best')
    plt.grid()
    plt.savefig('3_3.png')
    plt.show()

    k = 1
    for i in err:
        print(f'Error of F{k} = {i}')
        k += 1