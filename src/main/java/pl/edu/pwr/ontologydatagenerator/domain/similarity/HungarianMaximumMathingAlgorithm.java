package pl.edu.pwr.ontologydatagenerator.domain.similarity;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;

@Slf4j
@UtilityClass
public class HungarianMaximumMathingAlgorithm {

    public static double calculateNormalizedSumForMaximumMatching(double[][] inputArray) {
        return calculateSumForMaximumMatching(inputArray) / Math.max(inputArray.length, inputArray[0].length);
    }

    public static double calculateSumForMaximumMatching(double[][] inputArray) {
        return calculateSumForMatching(transposeArrayIfNecessary(inputArray), Type.MAX);
    }

    public static double calculateSumForMinimumMatching(double[][] inputArray) {
        return calculateSumForMatching(transposeArrayIfNecessary(inputArray), Type.MIN);
    }

    private static double[][] transposeArrayIfNecessary(double[][] inputArray) {
        if (inputArray.length > inputArray[0].length) {
            return transpose(inputArray);
        }
        return inputArray;
    }

    private static double[][] transpose(double[][] array) {
        double[][] transposedArray = new double[array[0].length][array.length];
        for (int i = 0; i < transposedArray.length; i++) {
            for (int j = 0; j < transposedArray[i].length; j++) {
                transposedArray[i][j] = array[j][i];
            }
        }
        return transposedArray;
    }

    private static double calculateSumForMatching(double[][] inputArray, Type type) {
        int[][] assignment = calculateMatchingElementsMatrix(inputArray, type);
        double sum = 0;
        for (int[] row : assignment) {
            sum = sum + inputArray[row[0]][row[1]];
        }
        return sum;
    }

    private static int[][] calculateMatchingElementsMatrix(double[][] array, Type type) {
        double[][] cost = deepCopy(array);
        if (type == Type.MAX)
        {
            double maxWeight = getMaxValue(cost);
            for (int i = 0; i < cost.length; i++)
            {
                for (int j = 0; j < cost[i].length; j++) {
                    cost[i][j] = (maxWeight - cost[i][j]);
                }
            }
        }
        double maxCost = getMaxValue(cost);
        int[][] mask = new int[cost.length][cost[0].length];
        int[] rowCoveringVector = new int[cost.length];
        int[] columnCoveringVector = new int[cost[0].length];
        int[] zeroRC = new int[2];
        int step = 1;
        boolean done = false;
        while (!done)
        {
            switch (step) {
                case 1 -> step = step1(cost);
                case 2 -> step = step2(cost, mask, rowCoveringVector, columnCoveringVector);
                case 3 -> step = step3(mask, columnCoveringVector);
                case 4 -> step = step4(step, cost, mask, rowCoveringVector, columnCoveringVector, zeroRC);
                case 5 -> step = step5(mask, rowCoveringVector, columnCoveringVector, zeroRC);
                case 6 -> step = step6(cost, rowCoveringVector, columnCoveringVector, maxCost);
                case 7 -> done = true;
            }
        }
        int[][] assignment = new int[array.length][2];
        for (int i = 0; i < mask.length; i++) {
            for (int j = 0; j < mask[i].length; j++) {
                if (mask[i][j] == 1) {
                    assignment[i][0] = i;
                    assignment[i][1] = j;
                }
            }
        }
        return assignment;
    }

    public static double[][] deepCopy(double[][] original) {
        return Arrays.stream(original)
                .map(double[]::clone)
                .toArray(double[][]::new);
    }

    public static double getMaxValue(double[][] array) {
        return Arrays.stream(array)
                .flatMapToDouble(Arrays::stream)
                .max()
                .orElse(0);
    }

    public static int step1(double[][] cost) {
        double minval;
        for (int i = 0; i < cost.length; i++) {
            minval = cost[i][0];
            for (int j = 0; j < cost[i].length; j++)
            {
                if (minval > cost[i][j]) {
                    minval = cost[i][j];
                }
            }
            for (int j = 0; j < cost[i].length; j++)
            {
                cost[i][j] = cost[i][j] - minval;
            }
        }
        return 2;
    }

    public static int step2(double[][] cost, int[][] mask, int[] rowCover, int[] colCover) {
        for (int i = 0; i < cost.length; i++) {
            for (int j = 0; j < cost[i].length; j++) {
                if ((cost[i][j] == 0) && (colCover[j] == 0) && (rowCover[i] == 0)) {
                    mask[i][j] = 1;
                    colCover[j] = 1;
                    rowCover[i] = 1;
                }
            }
        }
        clearCovers(rowCover, colCover);
        return 3;
    }

    public static int step3(int[][] mask, int[] colCover) {
        for (int[] ints : mask) {
            for (int j = 0; j < ints.length; j++) {
                if (ints[j] == 1) {
                    colCover[j] = 1;
                }
            }
        }
        int count = 0;
        for (int i : colCover) {
            count = count + i;
        }

        int step;
        if (count >= mask.length)
        {
            step = 7;
        } else {
            step = 4;
        }
        return step;
    }

    public static int step4(int step, double[][] cost, int[][] mask, int[] rowCover, int[] colCover, int[] zeroRC) {
        int[] rowCol = new int[2];
        boolean done = false;
        while (!done) {
            rowCol = findUncoveredZero(rowCol, cost, rowCover, colCover);
            if (rowCol[0] == -1) {
                done = true;
                step = 6;
            } else {
                mask[rowCol[0]][rowCol[1]] = 2;
                boolean starInRow = false;
                for (int j = 0; j < mask[rowCol[0]].length; j++) {
                    if (mask[rowCol[0]][j] == 1)
                    {
                        starInRow = true;
                        rowCol[1] = j;
                    }
                }
                if (starInRow) {
                    rowCover[rowCol[0]] = 1;
                    colCover[rowCol[1]] = 0;
                } else {
                    zeroRC[0] = rowCol[0];
                    zeroRC[1] = rowCol[1];
                    done = true;
                    step = 5;
                }
            }
        }
        return step;
    }

    public static int[] findUncoveredZero(int[] rowCol, double[][] cost, int[] rowCover, int[] colCover) {
        rowCol[0] = -1;
        rowCol[1] = 0;
        int i = 0;
        boolean done = false;
        while (!done) {
            int j = 0;
            while (j < cost[i].length) {
                if (cost[i][j] == 0 && rowCover[i] == 0 && colCover[j] == 0) {
                    rowCol[0] = i;
                    rowCol[1] = j;
                    done = true;
                }
                j = j + 1;
            }
            i = i + 1;
            if (i >= cost.length) {
                done = true;
            }
        }
        return rowCol;
    }

    public static int step5(int[][] mask, int[] rowCover, int[] colCover, int[] zeroRC) {
        int count = 0;
        int[][] path = new int[(mask[0].length * mask.length)][2];
        path[count][0] = zeroRC[0];
        path[count][1] = zeroRC[1];
        boolean done = false;
        while (!done) {
            int r = findStarInCol(mask, path[count][1]);
            if (r >= 0) {
                count = count + 1;
                path[count][0] = r;
                path[count][1] = path[count - 1][1];
            } else {
                done = true;
            }

            if (!done) {
                int c = findPrimeInRow(mask, path[count][0]);
                count = count + 1;
                path[count][0] = path[count - 1][0];
                path[count][1] = c;
            }
        }
        convertPath(mask, path, count);
        clearCovers(rowCover, colCover);
        erasePrimes(mask);
        return 3;
    }

    public static int findStarInCol(int[][] mask, int col) {
        int r = -1;
        for (int i = 0; i < mask.length; i++) {
            if (mask[i][col] == 1) {
                r = i;
            }
        }
        return r;
    }

    public static int findPrimeInRow(int[][] mask, int row) {
        int c = -1;
        for (int j = 0; j < mask[row].length; j++) {
            if (mask[row][j] == 2) {
                c = j;
            }
        }
        return c;
    }

    public static void convertPath(int[][] mask, int[][] path, int count) {
        for (int i = 0; i <= count; i++) {
            if (mask[(path[i][0])][(path[i][1])] == 1) {
                mask[(path[i][0])][(path[i][1])] = 0;
            } else {
                mask[(path[i][0])][(path[i][1])] = 1;
            }
        }
    }

    public static void erasePrimes(int[][] mask) {
        for (int i = 0; i < mask.length; i++) {
            for (int j = 0; j < mask[i].length; j++) {
                if (mask[i][j] == 2) {
                    mask[i][j] = 0;
                }
            }
        }
    }

    public static void clearCovers(int[] rowCover, int[] colCover) {
        Arrays.fill(rowCover, 0);
        Arrays.fill(colCover, 0);
    }

    public static int step6(double[][] cost, int[] rowCover, int[] colCover, double maxCost) {
        double minval = findSmallest(cost, rowCover, colCover, maxCost);
        for (int i = 0; i < rowCover.length; i++) {
            for (int j = 0; j < colCover.length; j++) {
                if (rowCover[i] == 1) {
                    cost[i][j] = cost[i][j] + minval;
                }
                if (colCover[j] == 0) {
                    cost[i][j] = cost[i][j] - minval;
                }
            }
        }
        return 4;
    }

    public static double findSmallest(double[][] cost, int[] rowCover, int[] colCover, double maxCost) {
        double minValue = maxCost;
        for (int i = 0; i < cost.length; i++)
        {
            for (int j = 0; j < cost[i].length; j++) {
                if (rowCover[i] == 0 && colCover[j] == 0 && (minValue > cost[i][j])) {
                    minValue = cost[i][j];
                }
            }
        }
        return minValue;
    }

    enum Type {
        MAX,
        MIN
    }

}
