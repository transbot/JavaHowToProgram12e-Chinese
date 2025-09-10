// 练习8.19: DeckOfCardsTest.java
// 洗牌和发牌演示，并评估一手牌的牌型

public class DeckOfCardsTest {
    // 程序执行入口
    public static void main(String[] args) {
        var myDeckOfCards = new DeckOfCards();
        myDeckOfCards.shuffle(); // 将牌随机排序

        // 发5张牌
        Card[] hand = new Card[5];
        for (int i = 0; i < 5; i++) {
            hand[i] = myDeckOfCards.dealCard();
        }

        // 显示发出的5张牌
        System.out.println("发出的5张牌:");
        for (int i = 0; i < hand.length; i++) {
            System.out.printf("%-19s", hand[i]);
            if ((i + 1) % 4 == 0) {
                System.out.println();
            }
        }
        System.out.println();

        // 评估牌型并输出
        String handType = myDeckOfCards.evaluateHand(hand);
        System.out.println("牌型: " + handType);
    } 
}

/**************************************************************************
 * (C) Copyright 1992-2025 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/