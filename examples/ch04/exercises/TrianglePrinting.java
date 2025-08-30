// 练习4.9，TrianglePrinting.java
// 三角形打印程序
public class TrianglePrinting {
   public static void main(String[] args) {
      // 图案(a)
      for (int i = 1; i <= 10; i++) {
         for (int j = 1; j <= i; j++) {
            System.out.print('*');
         }
         System.out.println();
      }
      
      System.out.println();
      
      // 图案(b)
      for (int i = 10; i >= 1; i--) {
         for (int j = 1; j <= i; j++) {
            System.out.print('*');
         }
         System.out.println();
      }
      
      System.out.println();
      
      // 图案(c)
      for (int i = 10; i >= 1; i--) {
         for (int j = 1; j <= 10 - i; j++) {
            System.out.print(' ');
         }
         for (int j = 1; j <= i; j++) {
            System.out.print('*');
         }
         System.out.println();
      }
      
      System.out.println();
      
      // 图案(d)
      for (int i = 1; i <= 10; i++) {
         for (int j = 1; j <= 10 - i; j++) {
            System.out.print(' ');
         }
         for (int j = 1; j <= i; j++) {
            System.out.print('*');
         }
         System.out.println();
      }
   }
}