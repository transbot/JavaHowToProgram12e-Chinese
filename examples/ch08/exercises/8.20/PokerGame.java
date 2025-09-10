// 练习8.20，PokerGame.java
// 模拟庄家换牌并比较两手牌
import java.util.List;
import java.util.ArrayList;
public class PokerGame {
    public static void main(String[] args) {
        DeckOfCards deck = new DeckOfCards();
        deck.shuffle();
        
        // 发两手牌：一手给玩家，一手给庄家（暗牌）
        Card[] playerHand = new Card[5];
        Card[] dealerHand = new Card[5];
        
        System.out.println("玩家的牌:");
        for (int i = 0; i < 5; i++) {
            playerHand[i] = deck.dealCard();
            System.out.print(playerHand[i] + " ");
        }
        String playerHandType = deck.evaluateHand(playerHand);
        System.out.println("\n玩家牌型: " + playerHandType);
        
        // 庄家的牌是暗牌，但程序可以访问
        System.out.println("\n庄家的牌（初始）:");
        for (int i = 0; i < 5; i++) {
            dealerHand[i] = deck.dealCard();
            System.out.print("?? "); // 模拟暗牌，玩家看不到
        }
        String dealerHandType = deck.evaluateHand(dealerHand);
        System.out.println("\n庄家初始牌型: " + dealerHandType + " (玩家不可见)");
        
        // 程序评估庄家的牌并决定换牌数量
        int cardsToDraw = deck.decideNumberOfCardsToDraw(dealerHand);
        System.out.println("\n庄家决定换 " + cardsToDraw + " 张牌");
        
        // 智能选择要替换的牌
        List<Integer> cardsToReplace = deck.selectCardsToReplace(dealerHand);
        System.out.println("庄家选择替换第 " + cardsToReplace + " 张牌");
        
        // 庄家换牌
        deck.replaceCards(dealerHand, cardsToReplace);
        String newDealerHandType = deck.evaluateHand(dealerHand);
        System.out.println("庄家换牌后牌型: " + newDealerHandType + " (玩家不可见)");
        
        // 显示玩家和庄家的最终牌型（用于演示）
        System.out.println("\n玩家的最终牌型: " + playerHandType);
        System.out.println("庄家的最终牌型: " + newDealerHandType);
        
        // 比较两手牌
        String result = deck.compareHands(playerHand, dealerHand);
        System.out.println("\n比较结果: " + result);
        
        // 显示庄家换牌后的牌（仅用于演示）
        System.out.println("\n庄家换牌后的牌（仅用于演示）:");
        for (Card card : dealerHand) {
            System.out.print(card + " ");
        }
    }
}