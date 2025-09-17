// 练习11.6，StudentPoll.java
// 修改图6.7的StudentPoll类，从文件中读取学生问卷调查结果并分析
// 你必须创建一个名为surveyresponses.txt的文本文件，
// 该文件包含学生的打分结果（1-5），每个分数占一行。
// 将该文件与StudentPoll类放在同一目录中

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

// 修改后的StudentPoll类
// 从文件中读取学生问卷调查结果并分析
public class StudentPoll {
    public static void main(String[] args) {
        // 使用ArrayList动态存储读取的分数，因为不知道文件中有多少数据
        ArrayList<Integer> responseList = new ArrayList<>();
        
        try {
            // 创建Scanner对象读取文件
            Scanner fileScanner = new Scanner(new File("surveyresponses.txt"));
            
            // 逐行读取文件内容，直到文件结束
            while (fileScanner.hasNextInt()) {
                int response = fileScanner.nextInt();
                responseList.add(response);
            }
            fileScanner.close(); // 关闭文件扫描器
            
        } catch (FileNotFoundException e) {
            System.out.println("错误：未找到surveyresponses.txt文件");
            System.out.println("请确保文件与程序在同一目录下");
            return; // 退出程序
        }
        
        // 将ArrayList转换为数组
        int[] responses = new int[responseList.size()];
        for (int i = 0; i < responseList.size(); i++) {
            responses[i] = responseList.get(i);
        }
        
        int[] frequency = new int[6]; // 频次统计数组，索引0-5
        
        // 遍历responses数组，统计每个分数的频次
        for (int answer = 0; answer < responses.length; ++answer) {
            int rating = responses[answer];
            // 只统计有效分数（1-5），跳过无效分数
            if (rating >= 1 && rating <= 5) {
                ++frequency[rating];
            }
        }
        
        System.out.printf("%s%10s%n", "分数", "频次");
        
        // 输出每个分数对应的频次
        for (int rating = 1; rating < frequency.length; ++rating) {
            System.out.printf("%4d%12d%n", rating, frequency[rating]);
        }
        
        // 显示分析结果摘要
        System.out.println("\n分析完成！");
        System.out.println("总参与调查学生数: " + responses.length);
        
        int validResponses = 0;
        for (int i = 1; i < frequency.length; i++) {
            validResponses += frequency[i];
        }
        System.out.println("有效打分数量: " + validResponses);
        System.out.println("无效打分数量: " + (responses.length - validResponses));
    }
}