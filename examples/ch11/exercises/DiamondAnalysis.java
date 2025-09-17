// 练习11.13，DiamondAnalysis.java
// 使用Jackson库分析diamonds.csv数据集  

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiamondAnalysis {
    // 表示数据集中每行字段的record类
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DiamondRecord(
        String index,          // 第一列是索引（不显示）
        Double carat,          // 克拉重量
        String cut,            // 切工
        String color,          // 颜色
        String clarity,        // 净度
        Double depth,          // 深度
        Double table,          // 台宽比
        Integer price,         // 价格
        Double x,              // x尺寸
        Double y,              // y尺寸
        Double z               // z尺寸
    ) {}

    public static void main(String[] args) throws Exception {
        var mapper = new CsvMapper(); // 读取CSV记录

        // 要求Jackson根据CSV列名，将每个列名都映射到一个对象
        CsvSchema schema = CsvSchema.emptySchema().withHeader(); 

        // 创建MappingIterator，它知道如何将文件记录读入DiamondRecord对象
        MappingIterator<DiamondRecord> iterator = 
            mapper.readerFor(DiamondRecord.class).with(schema).readValues(
                Path.of("diamonds.csv").toFile());

        // 将数据集加载到DiamondRecord对象列表中
        List<DiamondRecord> diamonds = iterator.readAll();

        // 创建列表，用正确的类型代表每一列
        var caratList = new ArrayList<Double>();
        var cutList = new ArrayList<String>();
        var colorList = new ArrayList<String>();
        var clarityList = new ArrayList<String>();
        var depthList = new ArrayList<Double>();
        var tableList = new ArrayList<Double>();
        var priceList = new ArrayList<Integer>();
        var xList = new ArrayList<Double>();
        var yList = new ArrayList<Double>();
        var zList = new ArrayList<Double>();

        // 填充列列表
        for (DiamondRecord record : diamonds) {
            caratList.add(record.carat());
            cutList.add(record.cut());
            colorList.add(record.color());
            clarityList.add(record.clarity());
            depthList.add(record.depth());
            tableList.add(record.table());
            priceList.add(record.price());
            xList.add(record.x());
            yList.add(record.y());
            zList.add(record.z());
        }

        // 显示前7行数据（不显示索引列）
        System.out.println("前7行数据:");
        System.out.printf("%-6s%-8s%-8s%-10s%-8s%-9s%-10s%-8s%-8s%-8s%n", 
            "克拉", "切工", "颜色", "净度", "深度", "台宽比", "价格", "x", "y", "z");
        
        for (int i = 0; i < 7; ++i) {
            DiamondRecord record = diamonds.get(i);
            System.out.printf("%-8.2f%-10s%-10s%-12s%-10.2f%-12.2f%-12d%-8.2f%-8.2f%-8.2f%n", 
                record.carat(), record.cut(), record.color(), 
                record.clarity(), record.depth(), record.table(), record.price(), 
                record.x(), record.y(), record.z());
        }

        // 显示最后7行数据（不显示索引列）
        System.out.println("\n最后7行数据:");
        System.out.printf("%-6s%-8s%-8s%-10s%-8s%-9s%-10s%-8s%-8s%-8s%n", 
            "克拉", "切工", "颜色", "净度", "深度", "台宽比", "价格", "x", "y", "z");
        
        int count = diamonds.size();
        for (int i = count - 7; i < count; ++i) {
            DiamondRecord record = diamonds.get(i);
            System.out.printf("%-8.2f%-10s%-10s%-12s%-10.2f%-12.2f%-12d%-8.2f%-8.2f%-8.2f%n", 
                record.carat(), record.cut(), record.color(), 
                record.clarity(), record.depth(), record.table(), record.price(), 
                record.x(), record.y(), record.z());
        }

        // 为数值列计算描述性统计数据
        System.out.println("\n数值列的描述性统计:");
        
        // 克拉重量
        calculateStats("克拉重量", caratList);
        
        // 深度
        calculateStats("深度", depthList);
        
        // 台宽比
        calculateStats("台宽比", tableList);
        
        // 价格
        calculateStats("价格", priceList);
        
        // x尺寸
        calculateStats("x尺寸", xList);
        
        // y尺寸
        calculateStats("y尺寸", yList);
        
        // z尺寸
        calculateStats("z尺寸", zList);

        // 为分类数据列计算统计信息
        System.out.println("\n分类数据列的统计:");
        
        // 切工
        calculateCategoricalStats("切工", cutList);
        
        // 颜色
        calculateCategoricalStats("颜色", colorList);
        
        // 净度
        calculateCategoricalStats("净度", clarityList);
    }

    // 计算数值列的描述性统计
    private static void calculateStats(String columnName, List<? extends Number> data) {
        // 转换为Double列表以便计算
        List<Double> values = new ArrayList<>();
        for (Number num : data) {
            values.add(num.doubleValue());
        }
        
        // 排序以便找到中位数
        Collections.sort(values);
        
        int size = values.size();
        double min = values.get(0);
        double max = values.get(size - 1);
        
        // 计算中位数
        double median;
        if (size % 2 == 0) {
            median = (values.get(size / 2 - 1) + values.get(size / 2)) / 2.0;
        } else {
            median = values.get(size / 2);
        }
        
        // 计算平均值
        double sum = 0.0;
        for (double value : values) {
            sum += value;
        }
        double mean = sum / size;
        
        System.out.printf("%s: 最小值=%.2f, 最大值=%.2f, 中位数=%.2f, 平均值=%.2f%n",
            columnName, min, max, median, mean);
    }

    // 计算分类数据列的统计（不使用HashMap）
    private static void calculateCategoricalStats(String columnName, List<String> data) {
        int count = data.size();
        
        // 创建唯一值列表和计数列表
        List<String> uniqueValues = new ArrayList<>();
        List<Integer> valueCounts = new ArrayList<>();
        
        // 计算唯一值和计数
        for (String value : data) {
            boolean found = false;
            for (int i = 0; i < uniqueValues.size(); i++) {
                if (uniqueValues.get(i).equals(value)) {
                    valueCounts.set(i, valueCounts.get(i) + 1);
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                uniqueValues.add(value);
                valueCounts.add(1);
            }
        }
        
        int uniqueCount = uniqueValues.size();
        
        // 找到众数（出现次数最多的值）
        String mode = null;
        int maxCount = 0;
        for (int i = 0; i < uniqueValues.size(); i++) {
            if (valueCounts.get(i) > maxCount) {
                maxCount = valueCounts.get(i);
                mode = uniqueValues.get(i);
            }
        }
        
        System.out.printf("%s: 计数=%d, 唯一值数量=%d, 众数=%s (出现%d次)%n",
            columnName, count, uniqueCount, mode, maxCount);
    }
}