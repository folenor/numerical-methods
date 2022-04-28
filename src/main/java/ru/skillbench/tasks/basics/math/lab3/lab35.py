import numpy as np
import math

target = '''
y = x / ((2 * x + 7) * (3 * x + 4))
x0 = -2
x_k = 2
h1 = 0.5
h2 = 0.25
'''


def func(x):
    return 1 / (256 - math.pow(x, 4))


def get_x(x0, x, step):
    return [i for i in np.arange(x0, x + step, step)]


def get_y(x):
    return [func(i) for i in x]


def rectangle(x, h):
    return h * sum([func((x[i] + x[i + 1]) / 2) for i in range(len(x) - 1)])


def trapeze(x, h):
    y = get_y(x)
    return h * (y[0] / 2 + sum([y[i] for i in range(1, len(y) - 2)]) + y[len(y) - 1] / 2)


def simpson(x, h):
    y = get_y(x)
    return h / 3 * (y[0] +
                    sum([4 * y[i] for i in range(1, len(y) - 1, 2)]) +
                    sum([2 * y[i] for i in range(2, len(y) - 2, 2)]) +
                    y[len(y) - 1])


def runge_Romberg(res, true_value):
    k = res[1]['h'] / res[0]['h']
    val_rec = [res[0]['rec'] + (res[0]['rec'] - res[1]['rec']) / (k ** 2 - 1),
               abs(res[0]['rec'] + (res[0]['rec'] - res[1]['rec']) / (k ** 2 - 1) - true_value)]

    val_trp = [res[0]['trp'] + (res[0]['trp'] - res[1]['trp']) / (k ** 2 - 1),
               abs(res[0]['trp'] + (res[0]['trp'] - res[1]['trp']) / (k ** 2 - 1) - true_value)]

    val_smp = [res[0]['smp'] + (res[0]['smp'] - res[1]['smp']) / (k ** 4 - 1),
               abs(res[0]['smp'] + (res[0]['smp'] - res[1]['smp']) / (k ** 4 - 1) - true_value)]
    return {'rec': val_rec, 'trp': val_trp, 'smp': val_smp}


if __name__ == '__main__':
    x0 = -2
    x = 2
    h = [1.0, 0.5]
    true_value = 0.015827
    res = []
    for h_i in h:
        print('Current h =', h_i)
        X = get_x(x0, x, h_i)
        print(f'x = {X}')
        y = get_y(X)
        print(f'y = {y}')

        print('Прямоугольник:')
        res_rec = rectangle(X, h_i)
        print(f'Значение = {res_rec}')

        print('Трапеция:')
        res_trp = trapeze(X, h_i)
        print(f'Значение = {res_trp}')

        print('Симпсон:')
        res_smp = simpson(X, h_i)
        print(f'Значение = {res_smp}')
        print()

        res.append({"h": h_i,
                    "rec": res_rec,
                    "trp": res_trp,
                    "smp": res_smp})

    err = runge_Romberg(res, true_value)

    print('Ошибки:')
    print(f'Истинное значение: {true_value}')
    print('Прямоугольник уточненное значение: {}, прямоугольник абсолютная ошибка: {}'.format(err['rec'][0], err['rec'][1]))
    print('Трапеция уточненное значение: {}, прямоугольник абсолютная ошибка {}'.format(err['trp'][0], err['trp'][1]))
    print('Симпсон уточненное значение: {}, прямоугольник абсолютная ошибка {}'.format(err['smp'][0], err['smp'][1]))