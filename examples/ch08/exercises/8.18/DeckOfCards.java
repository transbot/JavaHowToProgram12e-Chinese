// 图8.8: DeckOfCards.java
// 用于表示一副扑克牌的DeckOfCards类
import java.util.random.RandomGenerator;
import java.util.Arrays;
import java.util.ArrayList;

public class DeckOfCards {
    // 随机数生成器
    private static final RandomGenerator randomNumbers = 
        RandomGenerator.getDefault();
    public static final int NUMBER_OF_CARDS = 52;    // 一副牌中有多少张牌（常量）
    private Card[] deck = new Card[NUMBER_OF_CARDS]; // Card引用数组
    private int currentCardIndex = 0;    // 下一张待发牌的索引 (0~51)

    // 构造函数初始化牌组
    public DeckOfCards() {
        String[] faces = {"A", "2", "3", "4", "5", "6",
            "7", "8", "9", "10", "J", "Q", "K"};    
        String[] suits = {"红桃♥", "方块♦", "梅花♣", "黑桃♠"};

        // 用Card对象填充牌组                  
        for (int count = 0; count < deck.length; ++count) {  
            deck[count] = new Card(faces[count % 13], suits[count / 13]);
        }
    } 

    // 使用单遍（one-pass）算法洗牌
    public void shuffle() {
        // 下次发牌从deck[0]开始
        currentCardIndex = 0; 

        // 遍历每张牌，随机选择另一张牌(0~51)并交换位置
        for (int first = 0; first < deck.length; ++first) {
            // 随机选择0~51之间的数字
            int second = randomNumbers.nextInt(NUMBER_OF_CARDS);

            // 交换当前牌与随机选择的牌
            Card temp = deck[first];   
            deck[first] = deck[second];
            deck[second] = temp;       
        } 
    } 

    // 发一张牌
    public Card dealCard() {
        // 检查是否还有牌可发
        if (currentCardIndex < deck.length) {
            return deck[currentCardIndex++]; // 返回数组中的当前牌
        } 
        else {
            return null; // 返回null表示所有牌已发完
        } 
    }

    // 评估一手牌（5张牌）的牌型
    public String evaluateHand(Card[] hand) {
        if (hand.length != 5) {
            return "一手牌的张数不正确";
        }

        // 检查是否同花
        boolean isFlush = isFlush(hand);
        // 检查是否顺子
        boolean isStraight = isStraight(hand);

        if (isFlush && isStraight) {
            return "同花顺";
        }

        // 计算点数的频率
        int[] faceCounts = new int[13]; // 对应A,2,3,...,K
        for (Card card : hand) {
            int index = faceToIndex(card.face());
            faceCounts[index]++;
        }

        // 统计各种点数的出现次数
        int pairs = 0;
        boolean hasThree = false;
        boolean hasFour = false;
        
        for (int count : faceCounts) {
            if (count == 2) {
                pairs++;
            } else if (count == 3) {
                hasThree = true;
            } else if (count == 4) {
                hasFour = true;
            }
        }

        if (hasFour) {
            return "四条";
        } else if (hasThree && pairs == 1) {
            return "葫芦";
        } else if (isFlush) {
            return "同花";
        } else if (isStraight) {
            return "顺子";
        } else if (hasThree) {
            return "三条";
        } else if (pairs == 2) {
            return "两对";
        } else if (pairs == 1) {
            return "一对";
        } else {
            return "散牌";
        }
    }

    // 辅助方法：检查是否同花
    private boolean isFlush(Card[] hand) {
        String firstSuit = hand[0].suit();
        for (int i = 1; i < hand.length; i++) {
            if (!hand[i].suit().equals(firstSuit)) {
                return false;
            }
        }
        return true;
    }

    // 辅助方法：检查是否顺子
    private boolean isStraight(Card[] hand) {
        // 将牌的点数转换为数值并排序
        int[] values = new int[hand.length];
        for (int i = 0; i < hand.length; i++) {
            values[i] = faceToValue(hand[i].face());
        }
        Arrays.sort(values);

        // 检查常规顺子（如5-6-7-8-9）
        boolean isRegularStraight = true;
        for (int i = 1; i < values.length; i++) {
            if (values[i] != values[i-1] + 1) {
                isRegularStraight = false;
                break;
            }
        }

        if (isRegularStraight) {
            return true;
        }

        // 检查特殊顺子：A-2-3-4-5（A作为1）
        if (values[0] == 1 && values[1] == 2 && values[2] == 3 && values[3] == 4 && values[4] == 5) {
            return true;
        }

        // 检查特殊顺子：10-J-Q-K-A（A作为14）
        if (values[0] == 10 && values[1] == 11 && values[2] == 12 && values[3] == 13 && values[4] == 14) {
            return true;
        }

        return false;
    }

    // 辅助方法：将点数字符串转换为数值
    private int faceToValue(String face) {
        switch (face) {
            case "A": return 1;
            case "J": return 11;
            case "Q": return 12;
            case "K": return 13;
            default:
                try {
                    return Integer.parseInt(face);
                } catch (NumberFormatException e) {
                    return -1; // 无效点数
                }
        }
    }
    
    // 辅助方法：将点数字符串转换为数组索引
    private int faceToIndex(String face) {
        switch (face) {
            case "A": return 0;
            case "J": return 10;
            case "Q": return 11;
            case "K": return 12;
            default:
                try {
                    return Integer.parseInt(face) - 1;
                } catch (NumberFormatException e) {
                    return -1; // 无效点数
                }
        }
    }
} // 结束DeckOfCards类

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