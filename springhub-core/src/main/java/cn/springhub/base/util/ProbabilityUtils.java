package cn.springhub.base.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 概率问题
 * @author AI
 * @date 2024/12/7 17:57
 * @version 1.0
**/
public class ProbabilityUtils {

    /**
     * 在一个 n x n 的国际象棋棋盘上，一个骑士从单元格 (row, column) 开始，并尝试进行 k 次移动。行和列是 从 0 开始 的，所以左上单元格是 (0,0) ，右下单元格是 (n - 1, n -
     * 象棋骑士 走日字
     * @param n
     * @param k
     * @param row
     * @param column
     * @return
     */
    public static double knightProbability(int n, int k, int row, int column) {
        if (k == 0) {
            return 1D;
        }

        return getNextPosition(row, column, n , k - 1) / Math.pow(8, k);
    }

    /**
     * 获取下一步的位置
     * @param row
     * @param column
     */
    public static int getNextPosition(int row, int column, int n, int k) {
        List<Integer[]> result = new ArrayList<>() {{
           add(new Integer[]{ row + 2, column + 1 });
           add(new Integer[]{ row + 2, column - 1 });
           add(new Integer[]{ row - 2, column + 1 });
           add(new Integer[]{ row - 2, column - 1 });
           add(new Integer[]{ row + 1, column + 2 });
           add(new Integer[]{ row - 1, column + 2 });
           add(new Integer[]{ row + 1, column - 2 });
           add(new Integer[]{ row - 1, column - 2 });
        }};

        int count = 0;
        for (Integer[] integers : result) {
            if (!inTable(integers[0], integers[1], n)) {
                // 不在了
                continue;
            }

            count += (k == 0 ? 1 : getNextPosition(integers[0], integers[1], n, k - 1));
        }

        return count;
    }

    /**
     * 判断是否还在棋盘上
     * @param row
     * @param column
     * @param n
     */
    private static boolean inTable(int row, int column, int n) {
        if (row >= n || column >= n || row < 0 || column < 0) {
            return false;
        }

        return true;
    }

    // 定义骑士可以移动的所有方向。骑士在国际象棋中以"L"形移动：两格横向加一格纵向，或两格纵向加一格横向。
    static int[][] dirs = {{-2, -1}, {-2, 1}, {2, -1}, {2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}};

    public static double knightProbability1(int n, int k, int row, int column) {
        // 创建一个三维数组dp用于动态规划。dp[step][i][j]表示从(i,j)出发，在走了step步之后仍然留在棋盘上的概率。
        double[][][] dp = new double[k + 1][n][n];

        // 初始化dp数组。当step为0时，即没有移动的情况下，骑士肯定还在原地，所以所有位置的概率都初始化为1。
        for (int step = 0; step <= k; step++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (step == 0) {
                        dp[step][i][j] = 1;
                    } else {
                        // 对于每一步，遍历所有可能的移动方向。
                        for (int[] dir : dirs) {
                            // 计算新的位置ni和nj。
                            int ni = i + dir[0], nj = j + dir[1];

                            // 如果新位置仍在棋盘内，则将前一步骤中到达该位置的概率除以8（因为有8个可能的方向）累加到当前位置。
                            if (ni >= 0 && ni < n && nj >= 0 && nj < n) {
                                dp[step][i][j] += dp[step - 1][ni][nj] / 8;
                            }
                        }
                    }
                }
            }
        }
        // 返回从(row, column)出发，在走了k步之后仍然留在棋盘上的概率。
        return dp[k][row][column];
    }

    public static void main(String[] args) {
        System.out.println(knightProbability1(8, 30, 6, 4));
    }
}
