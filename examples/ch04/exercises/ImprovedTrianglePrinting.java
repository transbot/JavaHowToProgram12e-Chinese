// 练习4.10，ImprovedTrianglePrinting.java
// 改进的三角形打印程序 - 并排显示
public class ImprovedTrianglePrinting {
   public static void main(String[] args) {
      final int ROWS = 10;
      
      // 并排打印四个图案
      for (int i = 1; i <= ROWS; i++) {
         // 图案(a)
         for (int j = 1; j <= i; j++) {
            System.out.print('*');
         }
         for (int j = i + 1; j <= ROWS; j++) {
            System.out.print(' ');
         }
         
         System.out.print("   "); // 图案间分隔
         
         // 图案(b)
         for (int j = 1; j <= ROWS - i + 1; j++) {
            System.out.print('*');
         }
         for (int j = ROWS - i + 2; j <= ROWS; j++) {
            System.out.print(' ');
         }
         
         System.out.print("   "); // 图案间分隔
         
         // 图案(c)
         for (int j = 1; j < i; j++) {
            System.out.print(' ');
         }
         for (int j = i; j <= ROWS; j++) {
            System.out.print('*');
         }
         
         System.out.print("   "); // 图案间分隔
         
         // 图案(d)
         for (int j = 1; j <= ROWS - i; j++) {
            System.out.print(' ');
         }
         for (int j = ROWS - i + 1; j <= ROWS; j++) {
            System.out.print('*');
         }
         
         System.out.println();
      }
   }
}