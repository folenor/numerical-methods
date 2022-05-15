import math
import numpy as np
import matplotlib.pyplot as plt
from lab41 import rungeKutt
from lab42_shoot import shooting_method

PI = math.pi

target = '''
ОДУ: y'' - 2(1 + (tg(x)^2))y = 0
Краевая задача:
y(0) = 0
y(pi/6) = - sqrt(3 / 3)
Точное решение: y(x) = - tg(x)
'''

'''def func(x, y, y_der):
    return math.tan(x)*y_der - 2*y'''


def func(x, y, y_der):
    return -(2 * (x + 1) * y_der - 2 * y) / (x * (2 * x + 1))


def g(x, y, k):
    return k


def first_der(x, y, x0):
    i = 0
    while i < len(x) - 1 and x[i + 1] < x0:
        i += 1
    return (y[i + 1] - y[i]) / (x[i + 1] - x[i])


def check_finish(x, y, b, delta, gamma, y1, eps):
    y_der = first_der(x, y, b)
    return abs(delta * y[-1] + gamma * y_der - y1) > eps


def p(x):
    return 2*(x+1)/(x*(2*x+1))


'''def q(x):
    return 2'''


def q(x):
    return -2/(x*(2*x+1))


'''def pureFunction(x):
    return math.sin(x)+2-math.sin(x)*math.log((1+math.sin(x))/(1-math.sin(x)))'''


def pureFunction(x):
    return x + 1 + 1 / x


def f(x):
    return 0


def first(x, y, x0):
    i = 0
    while i < len(x) - 1 and x[i + 1] < x0:
        i += 1
    return (y[i + 1] - y[i]) / (x[i + 1] - x[i])


def stop(y, y1, eps):
    if abs(y[-1] - y1) > eps:
        return True
    else:
        return False


def newN(n_last, n, ans_last, ans, b, y1):
    x, y = ans_last[0], ans_last[1]
    phi_last = y[-1] - y1
    x, y = ans[0], ans[1]
    phi = y[-1] - y1
    return n - (n - n_last) / (phi - phi_last) * phi


def get_n(n_prev, n, ans_prev, ans, b, delta, gamma, y1):
    x, y = ans_prev[0], ans_prev[1]
    y_der = first_der(x, y, b)
    phi_n_prev = delta * y[-1] + gamma * y_der - y1
    x, y = ans[0], ans[1]
    y_der = first_der(x, y, b)
    phi_n = delta * y[-1] + gamma * y_der - y1
    return n - (n - n_prev) / (phi_n - phi_n_prev) * phi_n


def shootingMethod(a, b, alpha, beta, delta, gamma, y0, y1, h, eps):
    n_last = 3
    n = 0.8
    y_der = (y0 - alpha * n_last) / beta
    ans_last = rungeKutt(func, a, b, h, n_last, y_der)[:2]
    y_der = (y0 - alpha * n) / beta
    ans = rungeKutt(func, a, b, h, n, y_der)[:2]

    while check_finish(ans[0], ans[1], b, delta, gamma, y1, eps):
        n, n_last = get_n(n_last, n, ans_last, ans, b, delta, gamma, y1), n
        ans_last = ans
        y_der = (y0 - alpha * n) / beta
        ans = rungeKutt(func, a, b, h, y0, y_der)[:2]

    return ans


def tma(a, b, c, d, shape):
    p = [-c[0] / b[0]]
    q = [d[0] / b[0]]
    x = [0] * (shape + 1)
    for i in range(1, shape):
        p.append(-c[i] / (b[i] + a[i] * p[i - 1]))
        q.append((d[i] - a[i] * q[i - 1]) / (b[i] + a[i] * p[i - 1]))
    for i in reversed(range(shape)):
        x[i] = p[i] * x[i + 1] + q[i]
    return x[:-1]


def finiteDifferenceMethod(a, b, alpha, beta, delta, gamma, y0, y1, h):
    n = int((b - a) / h)
    x = [i for i in np.arange(a, b + h, h)]
    A = [0] + [1 - p(x[i]) * h / 2 for i in range(0, n - 1)] + [-gamma]
    B = [alpha * h - beta] + [q(x[i]) * h ** 2 - 2 for i in range(0, n - 1)] + [delta * h + gamma]

    C = [beta] + [1 + p(x[i]) * h / 2 for i in range(0, n - 1)] + [0]
    D = [y0 * h] + [f(x[i]) * h ** 2 for i in range(0, n - 1)] + [y1 * h]

    y = tma(A, B, C, D, len(A))
    return x, y


def show(ans, exact, h):
    n = len(ans)
    for i in range(n):
        plt.subplot(n, 1, i + 1)
        plt.subplots_adjust(wspace=0.1, hspace=0.6)
        plt.scatter(ans[i]["Shooting"]["x"], ans[i]["Shooting"]["y"], color='r', alpha=0.4, label='Shooting method')
        plt.plot(ans[i]["Shooting"]["x"], ans[i]["Shooting"]["y"], color='r', alpha=0.4)
        plt.scatter(ans[i]["FD"]["x"], ans[i]["FD"]["y"], color='b', alpha=0.4, label='Finite difference method')
        plt.plot(ans[i]["FD"]["x"], ans[i]["FD"]["y"], color='b', alpha=0.4)
        plt.scatter(exact[i][0], exact[i][1], color='g', alpha=0.4, label='Exact solution')
        plt.plot(exact[i][0], exact[i][1], color='g', alpha=0.4)

        plt.legend(loc='best')
        plt.title('h{0} = '.format(i + 1) + str(h[i]))
        plt.xlabel('x')
        plt.ylabel('y')
        plt.grid(True)
    plt.savefig('ShootAndFD.png')
    plt.show()


def rungeRomberg(ans, exact):
    k = ans[0]['h'] / ans[1]['h']
    Y1 = [yi for xi, yi in zip(ans[0]['Shooting']['x'], ans[0]['Shooting']['y']) if xi in ans[1]['Shooting']['x']]
    Y2 = [yi for xi, yi in zip(ans[1]['Shooting']['x'], ans[1]['Shooting']['y']) if xi in ans[0]['Shooting']['x']]
    shoot_err = [y1 + (y2 - y1) / (k ** 2 - 1) for y1, y2 in zip(Y1, Y2)]
    X_ex = [xi for xi in ans[0]['Shooting']['x'] if xi in ans[1]['Shooting']['x']]
    Y_ex = [pureFunction(i) for i in X_ex]
    for i in range(len(shoot_err)):
        shoot_err[i] = abs(shoot_err[i] - Y_ex[i])

    Y1 = [yi for xi, yi in zip(ans[0]['FD']['x'], ans[0]['FD']['y']) if xi in ans[1]['FD']['x']]
    Y2 = [yi for xi, yi in zip(ans[1]['FD']['x'], ans[1]['FD']['y']) if xi in ans[0]['FD']['x']]
    fd_err = [y1 + (y2 - y1) / (k ** 2 - 1) for y1, y2 in zip(Y1, Y2)]
    X_ex = [xi for xi in ans[0]['FD']['x'] if xi in ans[1]['FD']['x']]
    Y_ex = [pureFunction(i) for i in X_ex]
    for i in range(len(fd_err)):
        fd_err[i] = abs(fd_err[i] - Y_ex[i])

    return {'Shooting': shoot_err, 'FD': fd_err}


def sse(f, y):
    return round(sum([(f_i - y_i) ** 2 for f_i, y_i in zip(f, y)]), 5)


if __name__ == '__main__':
    print(target)

    '''a = 0
    b = PI / 6
    alpha = 1
    delta = 1
    gamma = 0
    beta = 0
    y0 = 2
    y1 = pureFunction(b)
    step = PI / 30
    eps = 1e-5'''

    a = 1
    b = 3

    alpha = 0
    delta = 1
    gamma = -1
    beta = 1

    y0 = 0
    y1 = 31 / 9
    step = 0.1
    eps = 1e-5

    print(f'Interval: [{a}, {b}]')
    print(f'y0 = {y0}, y1 = {y1}')
    print()

    res = []
    res2 = []
    ans = []
    steps = [step, step / 2]
    i = 0

    for h in steps:
        print(f'Current step: {h}')
        print('Shooting method')
        res.append(shooting_method(func, a, b, alpha, beta, delta, gamma, y0, y1, h, eps))
        for x, y in zip(res[i][0], res[i][1]):
            print(f'x: {round(x, 5)}, y: {round(y, 5)}')
        print()

        print('Finite difference method')
        res2.append(finiteDifferenceMethod(a, b, alpha, beta, delta, gamma, y0, y1, h))
        for x, y in zip(res2[i][0], res2[i][1]):
            print(f'x: {round(x, 5)}, y: {round(y, 5)}')
        print()

        ans.append({
            "h": h,
            "Shooting": {'x': res[i][0], 'y': res[i][1]},
            "FD": {'x': res2[i][0], 'y': res2[i][1]}
        })

        i += 1

    exact = []
    for h in steps:
        x_ex = [i for i in np.arange(a, b + h, h)]
        y_ex = [pureFunction(i) for i in x_ex]
        exact.append((x_ex, y_ex))

    err = rungeRomberg(ans, exact)
    print("All errors")
    print('Shooting method runge error: {}'.format(err['Shooting']))
    print('Finite difference method runge error: {}'.format(err['FD']))
    show(ans, exact, steps)