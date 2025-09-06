// 练习6.5，DuplicateElimination.java
// 编写一个程序，提示用户输入5个10~100的整数
// 如果数字已经存在，则不将其存储在数组中
// 显示所有不重复的输入数字
    

import java.util.Scanner;

public class DuplicateElimination {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int[] uniqueNumbers = new int[5]; // 最小长度的数组（5）
        int count = 0; // 记录当前唯一数字的数量

        for (int i = 0; i < 5; i++) {
            System.out.print("请输入10~100的一个整数: ");
            
            // 验证输入是否为整数
            while (!input.hasNextInt()) {
                System.out.print("输入无效，请输入整数: ");
                input.next(); // 清除无效输入
            }
            
            int num = input.nextInt();
            
            // 验证输入是否在有效范围内
            while (num < 10 || num > 100) {
                System.out.print("无效输入，请重新输入10~100的整数: ");
                
                // 再次验证输入是否为整数
                while (!input.hasNextInt()) {
                    System.out.print("输入无效，请输入整数: ");
                    input.next(); // 清除无效输入
                }
                
                num = input.nextInt();
            }

            boolean isDuplicate = false;
            // 检查是否为重复数字
            for (int j = 0; j < count; j++) {
                if (uniqueNumbers[j] == num) {
                    isDuplicate = true;
                    break;
                }
            }

            // 如果不是重复数字，则添加到数组
            if (!isDuplicate) {
                uniqueNumbers[count] = num;
                count++;
            }

            // 显示当前所有唯一数字
            System.out.print("当前唯一数字: ");
            for (int k = 0; k < count; k++) {
                System.out.print(uniqueNumbers[k] + " ");
            }
            System.out.println();
        }
        input.close();
    }
}