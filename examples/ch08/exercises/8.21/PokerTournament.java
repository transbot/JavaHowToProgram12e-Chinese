// 练习8.21，PokerTournament.java
// 梭哈锦标赛程序，进行20场比赛并统计胜负
import java.util.List;
import java.util.Scanner;

public class PokerTournament {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int playerWins = 0;
        int dealerWins = 0;
        int draws = 0;
        
        System.out.println("欢迎参加梭哈锦标赛！将进行20场比赛。");
        System.out.println("====================================");
        
        for (int game = 1; game <= 20; game++) {
            System.out.println("\n=== 第 " + game + " 场游戏 ===");
            
            DeckOfCards deck = new DeckOfCards();
            deck.shuffle();
            
            // 发两手牌：一手给玩家，一手给庄家
            Card[] playerHand = new Card[5];
            Card[] dealerHand = new Card[5];
            
            // 发牌
            for (int i = 0; i < 5; i++) {
                playerHand[i] = deck.dealCard();
                dealerHand[i] = deck.dealCard();
            }
            
            // 显示玩家的初始牌
            System.out.println("您的初始牌:");
            for (int i = 0; i < 5; i++) {
                System.out.println(i + ": " + playerHand[i]);
            }
            String playerInitialType = deck.evaluateHand(playerHand);
            System.out.println("您的初始牌型: " + playerInitialType);
            
            // 玩家选择要替换的牌
            List<Integer> playerCardsToReplace = deck.getPlayerCardSelection(playerHand);
            if (!playerCardsToReplace.isEmpty()) {
                System.out.println("您选择替换第 " + playerCardsToReplace + " 张牌");
                deck.replaceCards(playerHand, playerCardsToReplace);
                
                // 显示玩家换牌后的结果
                System.out.println("换牌后您的牌:");
                for (int i = 0; i < 5; i++) {
                    System.out.println(i + ": " + playerHand[i]);
                }
            } else {
                System.out.println("您选择不换牌。");
            }
            
            String playerFinalType = deck.evaluateHand(playerHand);
            System.out.println("您的最终牌型: " + playerFinalType);
            
            // 显示庄家换牌前的牌型
            String dealerInitialType = deck.evaluateHand(dealerHand);
            System.out.println("\n庄家初始牌型: " + dealerInitialType + " (牌面不可见)");
            
            // 庄家自动换牌
            int dealerCardsToDraw = deck.decideNumberOfCardsToDraw(dealerHand);
            List<Integer> dealerCardsToReplace = deck.selectCardsToReplace(dealerHand);
            
            if (!dealerCardsToReplace.isEmpty()) {
                System.out.println("庄家决定换 " + dealerCardsToReplace.size() + " 张牌");
                System.out.println("庄家选择替换第 " + dealerCardsToReplace + " 张牌");
                deck.replaceCards(dealerHand, dealerCardsToReplace);
            } else {
                System.out.println("庄家选择不换牌");
            }
            
            // 显示庄家换牌后的牌型
            String dealerFinalType = deck.evaluateHand(dealerHand);
            System.out.println("庄家最终牌型: " + dealerFinalType + " (牌面不可见)");
            
            // 比较两手牌
            String result = deck.compareHands(playerHand, dealerHand);
            System.out.println("\n比赛结果: " + result);
            
            // 显示双方最终牌型
            System.out.println("您的最终牌型: " + playerFinalType);
            System.out.println("庄家的最终牌型: " + dealerFinalType);
            
            // 统计胜负
            if (result.equals("玩家获胜")) {
                playerWins++;
            } else if (result.equals("庄家获胜")) {
                dealerWins++;
            } else {
                draws++;
            }
            
            // 显示当前统计
            System.out.println("\n当前统计 - 玩家胜: " + playerWins + 
                               ", 庄家胜: " + dealerWins + 
                               ", 平局: " + draws);
            
            // 如果还有比赛，询问是否继续
            if (game < 20) {
                System.out.println("\n按回车键继续下一场比赛...");
                scanner.nextLine();
            }
        }
        
        // 显示最终结果
        System.out.println("\n====================================");
        System.out.println("锦标赛结束！最终结果:");
        System.out.println("玩家获胜: " + playerWins + " 场");
        System.out.println("庄家获胜: " + dealerWins + " 场");
        System.out.println("平局: " + draws + " 场");
        
        if (playerWins > dealerWins) {
            System.out.println("恭喜！您赢得了锦标赛！");
        } else if (dealerWins > playerWins) {
            System.out.println("很遗憾，庄家赢得了锦标赛。");
        } else {
            System.out.println("锦标赛以平局结束！");
        }
    }
}