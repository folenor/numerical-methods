import numpy as np


def shooting_method(f, a, b, alpha, beta, delta, gamma, y0, y1, h, eps):
    n_prev, n = 1.0, 0.8
    y_der = (y0 - alpha * n_prev) / beta
    ans_prev = rungeKutt(f, a, b, h, n_prev, y_der)[:2]
    y_der = (y0 - alpha * n) / beta
    ans = rungeKutt(f, a, b, h, n, y_der)[:2]

    while check_finish(ans[0], ans[1], b, delta, gamma, y1, eps):
        n, n_prev = get_n(n_prev, n, ans_prev, ans, b, delta, gamma, y1), n
        ans_prev = ans
        y_der = (y0 - alpha * n) / beta
        ans = rungeKutt(f, a, b, h, n, y_der)[:2]

    return ans

def check_finish(x, y, b, delta, gamma, y1, eps):
    y_der = first_der(x, y, b)
    return abs(delta * y[-1] + gamma * y_der - y1) > eps

def first_der(x, y, x0):
    i = 0
    while i < len(x) - 1 and x[i + 1] < x0:
        i += 1
    return (y[i + 1] - y[i]) / (x[i + 1] - x[i])

def get_n(n_prev, n, ans_prev, ans, b, delta, gamma, y1):
    x, y = ans_prev[0], ans_prev[1]
    y_der = first_der(x, y, b)
    phi_n_prev = delta * y[-1] + gamma * y_der - y1
    x, y = ans[0], ans[1]
    y_der = first_der(x, y, b)
    phi_n = delta * y[-1] + gamma * y_der - y1
    return n - (n - n_prev) / (phi_n - phi_n_prev) * phi_n

def g(x, y, k):
    return k

def rungeKutt(f, a, b, h, y0, y_der):
    n = int((b - a) / h)
    x = [np.round(i, 4) for i in np.arange(a, b + h, h)]
    y = [y0]
    k = [y_der]
    for i in range(n):
        K1 = h * g(x[i], y[i], k[i])
        L1 = h * f(x[i], y[i], k[i])
        K2 = h * g(x[i] + 0.5 * h, y[i] + 0.5 * K1, k[i] + 0.5 * L1)
        L2 = h * f(x[i] + 0.5 * h, y[i] + 0.5 * K1, k[i] + 0.5 * L1)
        K3 = h * g(x[i] + 0.5 * h, y[i] + 0.5 * K2, k[i] + 0.5 * L2)
        L3 = h * f(x[i] + 0.5 * h, y[i] + 0.5 * K2, k[i] + 0.5 * L2)
        K4 = h * g(x[i] + h, y[i] + K3, k[i] + L3)
        L4 = h * f(x[i] + h, y[i] + K3, k[i] + L3)
        y.append(y[i] + (K1 + 2 * K2 + 2 * K3 + K4) / 6)
        k.append(k[i] + (L1 + 2 * L2 + 2 * L3 + L4) / 6)
    return x, y, k