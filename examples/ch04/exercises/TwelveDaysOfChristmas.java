// 练习4.20，TwelveDaysOfChristmas.java

/*  如果没有break语句，每次在switch中发现匹配时，该case和后续case的语句都会执行，
    直到遇到一个break语句，或者到达switch的终点。这称为“穿透”或“直通”（falling through）到后续case。
    利用上述特性编写一个程序，使用循环和switch语句打印《圣诞节的十二天》这首歌的歌词。
    其中一个switch语句用于打印日期（“第一天”“第二天”等），另一个独立的switch语句则用于打印每节诗的其余部分。
*/

// 十二天圣诞节歌歌词打印程序 - 英文版和中文版
// 中英文版歌词：https://zh.wikipedia.org/zh-hans/圣诞节的十二天。
public class TwelveDaysOfChristmas {
   public static void main(String[] args) {
      System.out.println("英文版歌词:");
      System.out.println("==========");
      printEnglishLyrics();
      
      System.out.println("\n中文版歌词:");
      System.out.println("==========");
      printChineseLyrics();
   }
   
   // 打印英文版歌词
   public static void printEnglishLyrics() {
      for (int day = 1; day <= 12; day++) {
         System.out.print("On the ");
         printEnglishDay(day);
         System.out.println(" of Christmas,");
         System.out.println("my true love sent to me");
         
         // 使用switch穿透特性打印礼物
         switch (day) {
            case 12:
               System.out.println("Twelve drummers drumming,");
            case 11:
               System.out.println("Eleven pipers piping,");
            case 10:
               System.out.println("Ten lords a-leaping,");
            case 9:
               System.out.println("Nine ladies dancing,");
            case 8:
               System.out.println("Eight maids a-milking,");
            case 7:
               System.out.println("Seven swans a-swimming,");
            case 6:
               System.out.println("Six geese a-laying,");
            case 5:
               System.out.println("Five golden rings,");
            case 4:
               System.out.println("Four calling birds,");
            case 3:
               System.out.println("Three French hens,");
            case 2:
               System.out.println("Two turtle doves,");
            case 1:
               if (day == 1) {
                  System.out.println("A partridge in a pear tree.");
               } else {
                  System.out.println("And a partridge in a pear tree." + (day == 12 ? "!" : "."));
               }
         }
         System.out.println();
      }
   }
   
   // 打印中文版歌词
   public static void printChineseLyrics() {
      for (int day = 1; day <= 12; day++) {
         System.out.print("圣诞节的");
         printChineseDay(day);
         System.out.println("，");
         System.out.println("我的真爱送给我");
         
         // 使用switch穿透特性打印礼物
         switch (day) {
            case 12:
               System.out.println("十二个打鼓的鼓手，");
            case 11:
               System.out.println("十一个吹笛的吹笛手，");
            case 10:
               System.out.println("十个跳跃的先生，");
            case 9:
               System.out.println("九个跳舞的女士，");
            case 8:
               System.out.println("八个挤奶的侍女，");
            case 7:
               System.out.println("七只游泳的天鹅，");
            case 6:
               System.out.println("六只下蛋的鹅，");
            case 5:
               System.out.println("五个金戒指，");
            case 4:
               System.out.println("四只鸣叫的鸟，");
            case 3:
               System.out.println("三只法国母鸡，");
            case 2:
               System.out.println("两只斑鸠，");
            case 1:
               if (day == 1) {
                  System.out.println("一只洋梨树上的鹧鸪。");
               } else {
                  System.out.println("和一只洋梨树上的鹧鸪" + (day == 12 ? "！" : "。"));
               }
         }
         System.out.println();
      }
   }
   
   // 打印英文日期
   public static void printEnglishDay(int day) {
      switch (day) {
         case 1: System.out.print("first"); break;
         case 2: System.out.print("second"); break;
         case 3: System.out.print("third"); break;
         case 4: System.out.print("fourth"); break;
         case 5: System.out.print("fifth"); break;
         case 6: System.out.print("sixth"); break;
         case 7: System.out.print("seventh"); break;
         case 8: System.out.print("eighth"); break;
         case 9: System.out.print("ninth"); break;
         case 10: System.out.print("tenth"); break;
         case 11: System.out.print("eleventh"); break;
         case 12: System.out.print("twelfth"); break;
      }
   }
   
   // 打印中文日期
   public static void printChineseDay(int day) {
      switch (day) {
         case 1: System.out.print("第一天"); break;
         case 2: System.out.print("第二天"); break;
         case 3: System.out.print("第三天"); break;
         case 4: System.out.print("第四天"); break;
         case 5: System.out.print("第五天"); break;
         case 6: System.out.print("第六天"); break;
         case 7: System.out.print("第七天"); break;
         case 8: System.out.print("第八天"); break;
         case 9: System.out.print("第九天"); break;
         case 10: System.out.print("第十天"); break;
         case 11: System.out.print("第十一天"); break;
         case 12: System.out.print("第十二天"); break;
      }
   }
}