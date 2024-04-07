//package codeKMean;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
public class SequentialKMeans {
    private static final Random random = new Random();
    private double[][] dataPoints;
    private double[][] centroids;
    private int[] assignments;
    private int numClusters;
    public SequentialKMeans(int numClusters) {
        this.numClusters = numClusters;
    }
    public void loadData(String filePath) throws IOException {
        List<double[]> dataList = new ArrayList<>();
        int lineCount = 0;
        int skippedLines = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineCount++;
                try {
                    String[] values = line.split(",");
                    double[] dataPoint = new double[values.length];
                    for (int i = 0; i < values.length; i++) {
                        dataPoint[i] = Double.parseDouble(values[i]);
                    }
                    dataList.add(dataPoint);
                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid or incomplete data at line " + lineCount);
                    skippedLines++;
                    continue;
                }
            }
        }

        dataPoints = dataList.toArray(new double[0][]);
        System.out.println("Loaded " + dataList.size() + " data points from " + filePath);
        if (skippedLines > 0) {
            System.out.println("Skipped " + skippedLines + " lines due to invalid data.");
        }
    }
    public void initializeCentroids() {
        if (numClusters > dataPoints.length) {
            throw new IllegalArgumentException("Number of clusters cannot exceed number of data points.");
        }
        centroids = new double[numClusters][];
        Set<Integer> usedIndexes = new HashSet<>();
        for (int i = 0; i < numClusters; i++) {
            int index;
            do {
                index = random.nextInt(dataPoints.length);
            } while (!usedIndexes.add(index)); 
            centroids[i] = dataPoints[index].clone();
        }
    }
    public void assignClusters() {
        assignments = new int[dataPoints.length];
        for (int i = 0; i < dataPoints.length; i++) {
            double minDistance = Double.MAX_VALUE;
            for (int j = 0; j < numClusters; j++) {
                double distance = calculateDistance(dataPoints[i], centroids[j]);
                if (distance < minDistance) {
                    minDistance = distance;
                    assignments[i] = j;
                }
            }
        }
    }
    public void updateCentroids() {
        double[][] newCentroids = new double[numClusters][dataPoints[0].length];
        int[] clusterSizes = new int[numClusters];
        // Tính tổng các điểm và số lượng điểm trong mỗi cụm
        for (int i = 0; i < dataPoints.length; i++) {
            int cluster = assignments[i];
            clusterSizes[cluster]++;
            for (int j = 0; j < dataPoints[i].length; j++) {
                newCentroids[cluster][j] += dataPoints[i][j];
            }
        }
        // Tính trung bình cho mỗi cụm, và tái khởi tạo tâm cho cụm rỗng nếu cần
        for (int i = 0; i < numClusters; i++) {
            if (clusterSizes[i] > 0) {
                for (int j = 0; j < newCentroids[i].length; j++) {
                    newCentroids[i][j] /= clusterSizes[i];
                }
            } else {
                // Tái khởi tạo tâm cụm cho cụm rỗng bằng cách chọn một điểm ngẫu nhiên từ tập dữ liệu
                int randomIndex = random.nextInt(dataPoints.length);
                newCentroids[i] = dataPoints[randomIndex].clone();
            }
        }
        // Cập nhật tâm cụm mới
        centroids = newCentroids;
    }
    
    public void run(String filePath) throws IOException {
        loadData(filePath);
        initializeCentroids();
        boolean changed;
        int totalIterations = 0; // Thêm biến đếm số lần lặp
        do {
            assignClusters();
            double[][] oldCentroids = centroids.clone();
            updateCentroids();
            changed = checkCentroidsChanged(oldCentroids);
            totalIterations++; // Tăng biến đếm sau mỗi lần lặp
        } while (changed);
        System.out.println("Số lần lăp: " + totalIterations); // In tổng số lần lặp khi thuật toán kết thúc
    }

    private double calculateDistance(double[] point1, double[] point2) {
        double sum = 0;
        for (int i = 0; i < point1.length-1; i++) {
            sum += Math.pow(point1[i] - point2[i], 2);
        }
        return Math.sqrt(sum);
    }
    
  
    private boolean checkCentroidsChanged(double[][] oldCentroids) {
        for (int i = 0; i < numClusters; i++) {
            if (!java.util.Arrays.equals(oldCentroids[i], centroids[i])) {
                return true;
            }
        }
        return false;
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of clusters: ");
        int numClusters = scanner.nextInt();
        scanner.close();

        SequentialKMeans kmeans = new            
        SequentialKMeans(numClusters);
        long startTime = System.currentTimeMillis();
        try {
            kmeans.run("C:\\Users\\Admin\\Downloads\\data_csv.csv"); 
            for (int i = 0; i < kmeans.centroids.length; i++) {
                System.out.println("Centroid " + (i + 1) + ": " + java.util.Arrays.toString(kmeans.centroids[i]));
            }
            long endTime = System.currentTimeMillis();
            System.out.println("Execution time: " + (endTime - startTime) + " milliseconds");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
