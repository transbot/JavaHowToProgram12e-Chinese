// 练习14.25，InvoiceStreamTest

import java.util.Arrays;
import java.util.Comparator;

public class InvoiceStreamTest {

    public static void main(String[] args) {
        // 1. 初始化Invoice对象数组
        Invoice[] invoices = {
            new Invoice("83", "电动砂光机", 7, 57.98),
            new Invoice("24", "电锯", 18, 99.99),
            new Invoice("7", "大锤", 11, 21.50),
            new Invoice("77", "锤子", 76, 11.99),
            new Invoice("39", "割草机", 3, 79.50),
            new Invoice("68", "螺丝刀", 106, 6.99),
            new Invoice("56", "曲线锯", 21, 11.00),
            new Invoice("3", "扳手", 34, 7.50)
        };

        // a) 使用流按partDescription对Invoice对象进行排序
        System.out.println("a) 按零件描述排序：");
        Arrays.stream(invoices)
              .sorted(Comparator.comparing(Invoice::partDescription))
              .forEach(System.out::println);

        // b) 使用流按pricePerItem对Invoice对象进行排序
        System.out.println("\nb) 按单价排序：");
        Arrays.stream(invoices)
              .sorted(Comparator.comparing(Invoice::pricePerItem))
              .forEach(System.out::println);

        // c) 使用流将每个Invoice对象映射为其partDescription和quantity，并按quantity对结果进行排序
        // 为了清晰地表示映射后的结果，我们创建一个临时的record
        record PartInfo(String description, int quantity) {}
        System.out.println("\nc) 映射零件描述和数量，并按数量排序：");
        Arrays.stream(invoices)
              .sorted(Comparator.comparing(Invoice::quantity)) // 按数量排序
              .map(invoice -> String.format("描述: %-10s  数量: %d", 
                                            invoice.partDescription(), invoice.quantity()))
              .forEach(System.out::println);


        // d) 使用流将每个Invoice对象映射为其partDescription和发票总额，并按发票总额对结果进行排序
        System.out.println("\nd) 映射零件描述和发票总额，并按总额排序：");
        Arrays.stream(invoices)
              .sorted(Comparator.comparing(Invoice::getInvoiceAmount)) // 按发票总额排序
              .map(invoice -> String.format("描述: %-10s  发票总额: $%,.2f",
                                            invoice.partDescription(), invoice.getInvoiceAmount()))
              .forEach(System.out::println);

        // e) 修改d小题，筛选出发票总额在$200～$500范围内的记录
        System.out.println("\ne) 筛选$200-$500之间的发票，按总额排序：");
        Arrays.stream(invoices)
              .filter(invoice -> {
                  double amount = invoice.getInvoiceAmount();
                  return amount >= 200 && amount <= 500;
              })
              .sorted(Comparator.comparing(Invoice::getInvoiceAmount))
              .map(invoice -> String.format("描述: %-10s  发票总额: $%,.2f",
                                            invoice.partDescription(), invoice.getInvoiceAmount()))
              .forEach(System.out::println);


        // f) 查找partDescription包含单词“锯”的任一Invoice对象
        System.out.println("\nf) 查找第一个描述中包含“锯”的发票：");
        Arrays.stream(invoices)
              .filter(invoice -> invoice.partDescription().contains("锯"))
              .findFirst() // 查找第一个匹配项
              .ifPresent(System.out::println); // 如果存在，则打印
    }
}

/**
 * Invoice记录类，用于存储发票信息。
 * 这是一个不可变的数据类。
 */
record Invoice(String partNumber, String partDescription, int quantity, double pricePerItem) {

    // 带有数据校验的紧凑构造函数
    public Invoice {
        if (quantity < 0) { // 验证数量
            throw new IllegalArgumentException("数量必须 >= 0");
        }

        if (pricePerItem < 0.0) { // 验证单价
            throw new IllegalArgumentException("商品单价必须 >= 0");
        }
    }

    // 返回发票总金额
    public double getInvoiceAmount() {
        return quantity() * pricePerItem(); // 计算总费用
    }

    // 为了方便打印，重写toString方法
    @Override
    public String toString() {
        return String.format("%-6s %-10s %-5d $%,-8.2f",
                partNumber, partDescription, quantity, pricePerItem);
    }
}