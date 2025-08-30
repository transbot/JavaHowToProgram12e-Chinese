// 练习4.15，OptimizedPythagoreanTriples.java
// OptimizedPythagoreanTriples.java
public class OptimizedPythagoreanTriples {
   public static void main(String[] args) {
      final int MAX_SIDE = 500;
      int count = 0;
      
      System.out.println("边长不大于500的毕达哥拉斯三元组:");
      System.out.println("边1\t边2\t斜边");
      System.out.println("----------------------------");
      
      // 优化：避免重复，让side2从side1开始，斜边从最大直角边开始
      for (int side1 = 1; side1 <= MAX_SIDE; side1++) {
         for (int side2 = side1; side2 <= MAX_SIDE; side2++) {
            for (int hypotenuse = Math.max(side1, side2) + 1; hypotenuse <= MAX_SIDE; hypotenuse++) {
               if (side1 * side1 + side2 * side2 == hypotenuse * hypotenuse) {
                  System.out.printf("%d\t%d\t%d%n", side1, side2, hypotenuse);
                  count++;
               }
            }
         }
      }
      
      System.out.println("总共找到 " + count + " 个三元组");
   }
}