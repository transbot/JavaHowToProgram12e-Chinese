// 练习4.14，PiCalculationWithMachin.java
// 核心功能：基于马青公式（Machin Formula）计算圆周率π的值
// 马青公式优势：相比收敛极慢的莱布尼茨级数（需数十万项才能达5位小数精度），
// 马青公式通过反正切函数组合（π = 16*arctan(1/5) - 4*arctan(1/239)）实现快速收敛，
// 仅需少量项（通常10项内）即可达到"3.14159"级精度，20万项可轻松实现百万位小数级精度

import java.math.BigDecimal;       // 用于高精度数值计算（避免double/float的精度丢失）
import java.math.MathContext;      // 控制数值计算的精度和舍入模式
import java.math.RoundingMode;     // 定义舍入规则（如四舍五入）

// 主类：封装马青公式计算π的所有逻辑
public class PiCalculationWithMachin {
   // 程序入口：初始化参数、执行计算、输出结果
   public static void main(String[] args) {
      // 目标计算精度：π的小数位数（此处设为10000位，可根据需求调整）
      int decimalPlaces = 10000;
      // 数学上下文：设置计算精度（比目标多10位，避免中间计算误差累积）+ 舍入模式（四舍五入）
      MathContext mc = new MathContext(decimalPlaces + 10, RoundingMode.HALF_UP);
      
      // 控制台提示：告知用户当前计算的目标精度
      System.out.println("使用马青公式计算π到" + decimalPlaces + "位小数...");
      
      // 变量：记录首次计算出"3.14159"开头π值的项数（初始-1表示未找到）
      int firstOccurrenceTerms = -1;
      // 变量：存储当前迭代计算出的π值（初始化为0）
      BigDecimal pi = BigDecimal.ZERO;
      
      // 核心迭代：通过增加arctan级数的项数，逐步提升π的计算精度
      // 循环范围1-100项：因马青公式收敛快，100项足以远超10000位小数精度
      for (int terms = 1; terms <= 100; terms++) {
          // 计算arctan(1/5)：传入分母5、当前项数terms、计算精度（目标+10位）
          BigDecimal arctan5 = arctan(5, terms, decimalPlaces + 10);
          // 计算arctan(1/239)：传入分母239、当前项数terms、计算精度
          BigDecimal arctan239 = arctan(239, terms, decimalPlaces + 10);
          
          // 应用马青公式计算π：16*arctan(1/5) - 4*arctan(1/239)
          // 注：所有运算指定MathContext，确保精度和舍入规则统一
          pi = BigDecimal.valueOf(16).multiply(arctan5, mc)  // 16 * arctan(1/5)
                 .subtract(BigDecimal.valueOf(4).multiply(arctan239, mc), mc);  // 减去 4*arctan(1/239)
          
          // 检查：是否首次达到"3.14159"开头的精度（未找到时才执行）
          if (firstOccurrenceTerms == -1) {
              // 将当前π值保留6位小数（向下舍入，避免进位干扰判断），转为字符串
              String piStr = pi.setScale(6, RoundingMode.DOWN).toString();
              // 判断是否以"3.14159"开头（满足题目要求的精度）
              if (piStr.startsWith("3.14159")) {
                  firstOccurrenceTerms = terms;  // 记录当前项数
                  // 输出首次达标信息
                  System.out.println("首次计算出3.14159开头的π值，使用项数: " + firstOccurrenceTerms);
                  System.out.println("当前近似值: " + piStr);
              }
          }
          
          // 提前终止条件：项数超过20且已达到目标精度（避免无效迭代）
          if (terms > 20 && checkConvergence(pi, decimalPlaces)) {
              System.out.println("在第" + terms + "项时已达到所需精度");
              break;  // 退出循环，结束迭代
          }
      }
      
      // 最终高精度计算：调用专门方法，确保达到目标小数位数（10000位）
      pi = computePiMachinHighPrecision(decimalPlaces);
      
      // 输出结果1：π的前50位（避免完整10000位输出时初期信息过载）
      System.out.println("\n最终π值（前50位）:");
      String piStr = pi.toString();  // 将高精度π值转为字符串
      // 截取前50位（若总长度不足50则取全部，避免索引越界）
      System.out.println(piStr.substring(0, Math.min(50, piStr.length())));
      
      // 输出结果2：实际计算的总小数位数（字符串长度 - 2：减去"3."的长度）
      System.out.println("\n总小数位数: " + (piStr.length() - 2));
      
      // 输出结果3：再次确认首次达标项数（若已找到）
      if (firstOccurrenceTerms != -1) {
          System.out.println("首次出现3.14159开头的项数: " + firstOccurrenceTerms);
      }
      
      // 输出结果4：完整的10000位π值（按格式化展示，便于阅读）
      System.out.println("\n完整的10000位π值:");
      displayPiValue(piStr, decimalPlaces);
   }
   
   /**
    * 辅助方法：格式化展示π值（每10位分组，每100位换行，提升可读性）
    * @param piStr 完整的π值字符串（格式如"3.1415926535..."）
    * @param decimalPlaces 目标小数位数（用于校验，确保展示完整）
    */
   private static void displayPiValue(String piStr, int decimalPlaces) {
      // 提取小数部分：去掉"3."前缀（从索引2开始截取）
      String decimalPart = piStr.substring(2);
      
      // 格式化参数：每100位换一行，每10位加一个空格
      int digitsPerLine = 100;   // 每行显示的小数位数
      int digitsPerGroup = 10;   // 每组显示的小数位数（用于空格分隔）
      
      // 先输出π的整数部分"3."
      System.out.print("3.");
      // 遍历小数部分的每一位，按规则格式化输出
      for (int i = 0; i < decimalPart.length(); i++) {
          // 条件1：每100位换行（跳过第0位，避免首行前空）
          if (i > 0 && i % digitsPerLine == 0) {
              System.out.println();  // 换行
          }
          // 条件2：每10位加一个空格（跳过第0位，避免首位前空）
          if (i > 0 && i % digitsPerGroup == 0) {
              System.out.print(" ");  // 加空格
          }
          // 输出当前位的数字
          System.out.print(decimalPart.charAt(i));
      }
      // 输出结束后换行，避免控制台内容粘连
      System.out.println();
   }
   
   /**
    * 核心方法：用泰勒级数计算arctan(1/x)的值（arctan是反正切函数）
    * 泰勒级数公式：arctan(1/x) = (1/x) - (1/(3*x³)) + (1/(5*x⁵)) - (1/(7*x⁷)) + ... 
    * （交错级数，符号随项数奇偶变化，分母为2n+1，x的幂次为2n+1）
    * @param inverseX 分母x（即计算arctan(1/x)中的x）
    * @param terms 迭代计算的项数（项数越多，精度越高）
    * @param precision 计算精度（确保中间结果的精度不丢失）
    * @return 高精度的arctan(1/x)计算结果
    */
   private static BigDecimal arctan(int inverseX, int terms, int precision) {
      // 初始化数学上下文：指定计算精度和舍入模式
      MathContext mc = new MathContext(precision, RoundingMode.HALF_UP);
      // 将分母x转为BigDecimal类型（便于高精度运算）
      BigDecimal x = BigDecimal.valueOf(inverseX);
      // 计算1/x（即arctan级数的首项）
      BigDecimal reciprocal = BigDecimal.ONE.divide(x, mc);
      
      // 结果变量：存储arctan的累加结果（初始为0）
      BigDecimal result = BigDecimal.ZERO;
      // 当前项变量：初始为级数首项（1/x），后续每轮迭代更新为前一项/(x²)
      BigDecimal term = reciprocal;
      
      // 迭代计算每一项：从第0项到第terms-1项
      for (int n = 0; n < terms; n++) {
          // 计算当前项的实际值：term / (2n+1)（分母为级数的项数系数）
          BigDecimal currentTerm = term.divide(BigDecimal.valueOf(2 * n + 1), mc);
          // 交错累加：偶数项（n%2==0）加当前项，奇数项减当前项
          if (n % 2 == 0) {
              result = result.add(currentTerm, mc);  // 加项
          } else {
              result = result.subtract(currentTerm, mc);  // 减项
          }
          // 更新下一项：当前项 / (x²)（因级数每一项的x幂次比前一项多2，即除以x²）
          term = term.divide(x.multiply(x), mc);
      }
      
      // 返回最终的arctan(1/x)计算结果
      return result;
   }
   
   /**
    * 高精度计算方法：基于马青公式，动态迭代直到达到目标小数精度
    * 与main方法中的循环不同：此方法不限制固定项数，而是通过"当前项绝对值小于精度阈值"终止迭代
    * @param decimalPlaces 目标小数位数（如10000位）
    * @return 达到目标精度的π值（BigDecimal类型）
    */
   private static BigDecimal computePiMachinHighPrecision(int decimalPlaces) {
      // 数学上下文：精度比目标多10位，避免中间误差累积
      MathContext mc = new MathContext(decimalPlaces + 10, RoundingMode.HALF_UP);
      
      // -------------------------- 第一步：计算arctan(1/5) --------------------------
      BigDecimal arctan5 = BigDecimal.ZERO;  // 存储arctan(1/5)结果
      // 初始项：1/5（arctan(1/5)级数的首项）
      BigDecimal term5 = BigDecimal.ONE.divide(BigDecimal.valueOf(5), mc);
      // x²：5²=25（用于更新下一项）
      BigDecimal xSq5 = BigDecimal.valueOf(25);
      int n = 0;  // 项数计数器（从第0项开始）
      
      // 动态迭代：直到当前项的绝对值小于精度阈值（目标精度+5位，确保最终结果达标）
      while (true) {
          // 计算当前项：term5 / (2n+1)
          BigDecimal currentTerm = term5.divide(BigDecimal.valueOf(2 * n + 1), mc);
          // 交错累加（偶数项加，奇数项减）
          if (n % 2 == 0) {
              arctan5 = arctan5.add(currentTerm, mc);
          } else {
              arctan5 = arctan5.subtract(currentTerm, mc);
          }
          
          // 更新下一项：当前项 / 25（即term5 = term5 / x²）
          term5 = term5.divide(xSq5, mc);
          // 终止条件：当前项的绝对值 < 10^-(decimalPlaces+5)（精度足够小）
          if (currentTerm.abs().compareTo(BigDecimal.ONE.movePointLeft(decimalPlaces + 5)) < 0) {
              break;  // 退出循环，arctan(1/5)计算完成
          }
          n++;  // 项数加1，进入下一轮迭代
      }
      
      // -------------------------- 第二步：计算arctan(1/239) --------------------------
      BigDecimal arctan239 = BigDecimal.ZERO;  // 存储arctan(1/239)结果
      // 初始项：1/239（arctan(1/239)级数的首项）
      BigDecimal term239 = BigDecimal.ONE.divide(BigDecimal.valueOf(239), mc);
      // x²：239²=57121（用于更新下一项）
      BigDecimal xSq239 = BigDecimal.valueOf(239 * 239);
      n = 0;  // 重置项数计数器
      
      // 动态迭代：逻辑同arctan(1/5)，直到精度达标
      while (true) {
          // 计算当前项：term239 / (2n+1)
          BigDecimal currentTerm = term239.divide(BigDecimal.valueOf(2 * n + 1), mc);
          // 交错累加
          if (n % 2 == 0) {
              arctan239 = arctan239.add(currentTerm, mc);
          } else {
              arctan239 = arctan239.subtract(currentTerm, mc);
          }
          
          // 更新下一项：当前项 / 57121（即term239 = term239 / x²）
          term239 = term239.divide(xSq239, mc);
          // 终止条件：当前项的绝对值 < 10^-(decimalPlaces+5)
          if (currentTerm.abs().compareTo(BigDecimal.ONE.movePointLeft(decimalPlaces + 5)) < 0) {
              break;  // 退出循环，arctan(1/239)计算完成
          }
          n++;  // 项数加1
      }
      
      // -------------------------- 第三步：应用马青公式计算最终π --------------------------
      // 公式：π = 16*arctan(1/5) - 4*arctan(1/239)
      // 最后设置为目标小数位数（四舍五入），确保结果精度符合要求
      return BigDecimal.valueOf(16).multiply(arctan5, mc)
             .subtract(BigDecimal.valueOf(4).multiply(arctan239, mc), mc)
             .setScale(decimalPlaces, RoundingMode.HALF_UP);
   }
   
   /**
    * 辅助方法：检查当前π值是否已达到目标精度（收敛判断）
    * @param pi 当前计算的π值
    * @param targetPrecision 目标小数精度（如10000位）
    * @return true：已收敛（达到精度）；false：未收敛
    */
   private static boolean checkConvergence(BigDecimal pi, int targetPrecision) {
      // 参考值：π的已知高精度前20位（用于对比误差）
      BigDecimal reference = new BigDecimal("3.14159265358979323846");
      // 计算当前π值与参考值的绝对误差
      BigDecimal difference = pi.subtract(reference).abs();
      // 误差阈值：10^-targetPrecision（即目标精度对应的最小误差）
      // 若绝对误差 < 阈值，说明已收敛；否则未收敛
      return difference.compareTo(BigDecimal.ONE.movePointLeft(targetPrecision)) < 0;
   }
}