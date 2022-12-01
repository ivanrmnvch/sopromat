import java.util.ArrayList;
import java.util.List;

public class Main {
    private static float[] f = {1, 0, 1, 12};
    private static float[] m = {0, -10, 0, 0};
    private static float[] q = {0, -5, -5};
    private static float[] distance = {4, 4, 4};
    private static int[] xLine = {1, 1, 1};
    private static float[][] qx = new float[distance.length][2];
    private static float[][] mx = new float[distance.length][2];
    private static List<Integer> unknownF = new ArrayList<>();
    private static List<Integer> knownF = new ArrayList<>();
    private static List<Integer> unknownM = new ArrayList<>();
    private static List<Integer> knownM = new ArrayList<>();
    private static List<Integer> knownQ = new ArrayList<>();

    public static void main(String[] args) {
        start();
    }

    private static void start() {
        search(f, unknownF, knownF);
        search(m, unknownM, knownM);
        search(q, knownQ, knownQ);
        for (int i = 0; i < 2; i++) {
            if (unknownF.get(i) == Math.max(unknownF.get(0), unknownF.get(1)))
                f[unknownF.get(1)] = sumMomPoint(unknownF.get(0));
            else f[unknownF.get(0)] = sumMomPoint(unknownF.get(1));
        }
        System.out.println("Ra = " + f[0]);
        System.out.println("Rb = " + f[2]);
        System.out.println();
        knownF = new ArrayList<>();
        search(f, unknownF, knownF);
        qx();
        mx();
    }

    private static void search(float[] arr, List<Integer> unknown, List<Integer> known) {
        for (int i = 0; i < arr.length; i++) {
            if (Math.abs(arr[i]) == 1) unknown.add(i);
            else if (arr[i] != 0) known.add(i);
        }
    }

    private static float sumMomPoint(int point) {
        int coeff = coeff(point);
        float r = ((M() * coeff) + (Q(point) * coeff) + (F(point) * coeff)) / disUnknownF(point);
        return r;
    }

    private static float Q(int point) {
        float ql = 0;
        for (int i = 0; i < knownQ.size(); i++) {
            float dis = 0;
            if (point < knownQ.get(i)) {
                dis = dis(point, knownQ.get(i));
                ql += q[knownQ.get(i)] * distance[knownQ.get(i)] * ((distance[knownQ.get(i)] / 2) + dis);
            } else if (point == knownQ.get(i)) {
                ql += q[knownQ.get(i)] * distance[knownQ.get(i)] * (distance[knownQ.get(i)] / 2);
            } else if ((point - knownQ.get(i) == 1)) {
                ql += -q[knownQ.get(i)] * distance[knownQ.get(i)] * (distance[knownQ.get(i)] / 2);
            } else {
                dis = dis((knownQ.get(i) + 1), point);
                ql += -q[knownQ.get(i)] * distance[knownQ.get(i)] * ((distance[knownQ.get(i)] / 2) + dis);
            }
        }
        return ql;
    }

    private static float M() {
        float mom = 0;
        for (int i = 0; i < knownM.size(); i++) {
            mom += m[knownM.get(i)];
        }
        return mom;
    }

    private static float F(int point) {
        float fl = 0;
        for (int i = 0; i < knownF.size(); i++) {
            float dis = 0;
            if (point == knownF.get(i)) continue;
            else if (point < knownF.get(i)) {
                dis = dis(point, knownF.get(i));
                fl += f[knownF.get(i)] * dis;
            } else {
                dis = dis(knownF.get(i), point);
                fl += -f[knownF.get(i)] * dis;
            }
        }
        return fl;
    }

    private static float disUnknownF(int point) {
        float dis = 0;
        for (int i = 0; i < unknownF.size(); i++) {
            if (point == unknownF.get(i)) continue;
            else if (point < unknownF.get(i)) {
                dis = dis(point, unknownF.get(i));
            } else {
                dis = dis(unknownF.get(i), point);
            }
        }
        return dis;
    }

    private static float dis(int i, int count) {
        float dis = 0;
        for (; i < count; i++) dis += distance[i];
        return dis;
    }

    private static int coeff(int point) {
        int x = Math.max(unknownF.get(0), unknownF.get(1));
        if (point < x) return -1;
        else return 1;
    }

    private static void qx() {
        for (int i = 0; i < distance.length; i++) {
            for (int j = 0; j < 2; j++) {
                qx[i][j] = fq(i, xLine[i], j);
                System.out.print(qx[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static float fq(int dis, int axis, int xn) {
        float resf = 0;
        float resq = 0;
        if (axis == -1) {
            for (int j = 0; j < dis + 1; j++) {
                resf += f[j] > 0 ? Math.abs(f[j]) : Math.abs(f[j]) * -1;
                if (dis == j && xn == 0)
                    resq += 0;
                else resq += q[j] > 0 ? Math.abs(q[j]) * distance[j] : Math.abs(q[j]) * distance[j] * -1;
            }
        } else {
            int k = dis;
            for (; k < distance.length; k++) {
                resf += f[k + 1] > 0 ? Math.abs(f[k + 1]) * -1 : Math.abs(f[k + 1]);
                if (dis == k && xn == 0)
                    resq += 0;
                else resq += q[k] > 0 ? Math.abs(q[k]) * distance[k] * -1 : Math.abs(q[k]) * distance[k];
            }
        }
        return (resf + resq);
    }

    private static void mx() {
        for (int i = 0; i < distance.length; i++) {
            for (int j = 0; j < 2; j++) {
                mx[i][j] = mfq(i, xLine[i], j);
                System.out.print(mx[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static float mfq(int dis, int axis, int xn) {
        float resm = 0;
        float resq = 0;
        float resf = 0;
        float[] indexF = indexArr(f);
        float[] indexQ = indexArr(q);
        if (axis == -1) {
            for (int i = 0; i < dis + 1; i++) {
                if (dis == indexF[i] && xn != 0)
                    resf += f[i] > 0 ? Math.abs(f[i]) * distance[i] : (Math.abs(f[i]) * distance[i]) * -1;
                else if (dis != indexF[i] && xn == 0)
                    resf += f[i] > 0 ? Math.abs(f[i]) * (dis(i, dis + 1) - distance[i]) : Math.abs(f[i]) * (dis(i, dis + 1) - distance[i]) * -1;
                else if (dis != indexF[i] && xn != 0)
                    resf += f[i] > 0 ? Math.abs(f[i]) * dis(i, dis + 1) : Math.abs(f[i]) * dis(i, dis + 1) * -1;
                if (dis == indexQ[i] && xn != 0)
                    resq += q[i] > 0 ? Math.abs(q[i]) * distance[i] * (distance[i] / 2) :
                            Math.abs(q[i]) * distance[i] * (distance[i] / 2) * -1;
                else if (dis != indexQ[i] && xn == 0)
                    resq += q[i] > 0 ? Math.abs(q[i]) * distance[i] * (distance[i] / 2 + dis(i + 1, dis)) :
                            Math.abs(q[i]) * distance[i] * (distance[i] / 2) * -1;
                else if (dis != indexQ[i] && xn != 0)
                    resq += q[i] > 0 ? Math.abs(q[i]) * distance[i] * (distance[i] / 2 + dis(i + 1, dis + 1)) :
                            Math.abs(q[i]) * distance[i] * (distance[i] / 2 + dis(i + 1, dis + 1)) * -1;
                resm += m[i] * -1;
            }
        } else {
            int i = dis;
            for (; i < distance.length; i++) {
                if ((indexF[i + 1] - dis) == 1 && xn != 0)
                    resf += f[i + 1] > 0 ? Math.abs(f[i + 1]) * distance[i] * -1 : (Math.abs(f[i + 1]) * distance[i]);
                else if ((indexF[i + 1] - dis) != 1 && xn == 0)
                    resf += f[i + 1] > 0 ? Math.abs(f[i + 1]) * (dis(dis, i + 1) - distance[dis]) * -1 : Math.abs(f[i + 1]) * (dis(dis, i + 1) - distance[dis]);
                else if ((indexF[i + 1] - dis) != 1 && xn != 0)
                    resf += f[i + 1] > 0 ? Math.abs(f[i + 1]) * dis(dis, i + 1) * -1 : Math.abs(f[i + 1]) * dis(dis, i + 1);
                if (dis == indexQ[i] && xn != 0)
                    resq += q[i] > 0 ? Math.abs(q[i]) * distance[i] * (distance[i] / 2) * -1 :
                            Math.abs(q[i]) * distance[i] * (distance[i] / 2);
                else if (dis != indexQ[i] && xn == 0)
                    resq += q[i] > 0 ? Math.abs(q[i]) * distance[i] * (distance[i] / 2 + dis(dis + 1, i)) * -1 :
                            Math.abs(q[i]) * distance[i] * (distance[i] / 2 + dis(dis + 1, i));
                else if (dis != indexQ[i] && xn != 0)
                    resq += q[i] > 0 ? Math.abs(q[i]) * distance[i] * (distance[i] / 2 + dis(dis, i)) * -1 :
                            Math.abs(q[i]) * distance[i] * (distance[i] / 2 + dis(dis, i));
            }
            int j = dis + 1;
            for (; j < distance.length; j++) {
                resm += m[j] * -1;
            }
        }
        return resm + resf + resq;
    }

    private static float[] indexArr(float[] array) {
        float[] arr = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 0) arr[i] = -1;
            else arr[i] = i;
        }
        return arr;
    }
}