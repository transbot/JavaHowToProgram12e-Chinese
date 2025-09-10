// 练习8.19，PokerComparison.java
// 比较两手牌（各5张）哪一手更大

public class PokerComparison {
    public static void main(String[] args) {
        DeckOfCards deck = new DeckOfCards();
        deck.shuffle();
        
        // 发两手牌，每手5张
        Card[] hand1 = new Card[5];
        Card[] hand2 = new Card[5];
        
        System.out.println("第一手牌:");
        for (int i = 0; i < 5; i++) {
            hand1[i] = deck.dealCard();
            System.out.print(hand1[i] + " ");
        }
        String hand1Type = deck.evaluateHand(hand1);
        System.out.println("\n牌型: " + hand1Type);
        
        System.out.println("\n第二手牌:");
        for (int i = 0; i < 5; i++) {
            hand2[i] = deck.dealCard();
            System.out.print(hand2[i] + " ");
        }
        String hand2Type = deck.evaluateHand(hand2);
        System.out.println("\n牌型: " + hand2Type);
        
        // 比较两手牌
        String result = deck.compareHands(hand1, hand2);
        System.out.println("\n比较结果: " + result);
    }
}