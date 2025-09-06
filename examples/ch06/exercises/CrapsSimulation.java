// 练习6.10，CrapsSimulation.java
// 编写一个程序，模拟100万次双骰子游戏
// 计算并显示每轮获胜和失败的次数及概率

import java.util.Random;

public class CrapsSimulation {
    // 游戏状态枚举
    private enum Status { CONTINUE, WON, LOST }
    
    // 随机数生成器
    private static final Random random = new Random();
    
    public static void main(String[] args) {
        // 模拟100万次游戏
        int totalGames = 1000000;
        
        // 记录每轮获胜和失败的次数
        int[] winsByRound = new int[22]; // 索引0不使用，1-20表示第1-20轮，21表示20轮之后
        int[] lossesByRound = new int[22];
        
        // 记录总获胜次数和总轮数
        int totalWins = 0;
        long totalRounds = 0;
        
        // 模拟游戏
        for (int i = 0; i < totalGames; i++) {
            int rounds = playCrapsGame(winsByRound, lossesByRound);
            totalRounds += rounds;
        }
        
        // 计算总获胜次数
        for (int i = 1; i <= 21; i++) {
            totalWins += winsByRound[i];
        }
        
        // 计算获胜概率
        double winProbability = (double) totalWins / totalGames * 100;
        
        // 计算平均局长
        double averageRounds = (double) totalRounds / totalGames;
        
        // 输出结果
        System.out.println("双骰子游戏模拟结果 (" + totalGames + " 局)");
        System.out.println("================================================");
        System.out.printf("%-10s%-15s%-15s%-15s\n", "轮次", "获胜局数", "失败局数", "累计概率");
        System.out.println("================================================");
        
        // 计算累计概率
        int cumulativeWins = 0;
        int cumulativeLosses = 0;
        for (int i = 1; i <= 21; i++) {
            cumulativeWins += winsByRound[i];
            cumulativeLosses += lossesByRound[i];
            double cumulativeProbability = (double) cumulativeWins / (cumulativeWins + cumulativeLosses) * 100;
            
            String roundLabel = i <= 20 ? "第" + i + "轮" : "20轮后";
            System.out.printf("%-10s%-15d%-15d%15.2f%%\n", 
                             roundLabel, winsByRound[i], lossesByRound[i], cumulativeProbability);
        }
        
        System.out.println("================================================");
        System.out.printf("总获胜概率: %.2f%%\n", winProbability);
        System.out.printf("平均局长: %.2f 轮\n", averageRounds);
        System.out.println("================================================");
        
        // 回答问题
        System.out.println("问题解答:");
        System.out.println("1. 双骰子游戏的获胜概率约为 " + String.format("%.2f", winProbability) + "%");
        System.out.println("2. 双骰子游戏的平均局长约为 " + String.format("%.2f", averageRounds) + " 轮");
        System.out.println("3. 获胜概率随着游戏局长的增加而 " + 
                          (doesProbabilityIncrease(winsByRound, lossesByRound) ? "提高" : "降低或保持不变"));
        System.out.println("4. 双骰子游戏是赌场中最公平的游戏之一，这意味着玩家的获胜机会接近50%，");
        System.out.println("   赌场的优势很小，通常只有约1.4%的优势。");
    }
    
    // 玩一局双骰子游戏并记录结果
    private static int playCrapsGame(int[] winsByRound, int[] lossesByRound) {
        int myPoint = 0;
        Status gameStatus;
        int round = 1;
        
        // 第一轮掷骰
        int sumOfDice = rollDice();
        
        // 根据第一次掷出的点数和决定游戏状态
        switch (sumOfDice) {
            case 7:
            case 11:
                gameStatus = Status.WON;
                break;
            case 2:
            case 3:
            case 12:
                gameStatus = Status.LOST;
                break;
            default:
                gameStatus = Status.CONTINUE;
                myPoint = sumOfDice;
                break;
        }
        
        // 继续游戏直到获胜或失败
        while (gameStatus == Status.CONTINUE) {
            round++;
            sumOfDice = rollDice();
            
            if (sumOfDice == myPoint) {
                gameStatus = Status.WON;
            } else if (sumOfDice == 7) {
                gameStatus = Status.LOST;
            }
        }
        
        // 记录结果
        int resultIndex = round <= 20 ? round : 21;
        if (gameStatus == Status.WON) {
            winsByRound[resultIndex]++;
        } else {
            lossesByRound[resultIndex]++;
        }
        
        return round;
    }
    
    // 掷骰子
    private static int rollDice() {
        int die1 = random.nextInt(6) + 1;
        int die2 = random.nextInt(6) + 1;
        return die1 + die2;
    }
    
    // 检查获胜概率是否随着轮数增加而提高
    private static boolean doesProbabilityIncrease(int[] winsByRound, int[] lossesByRound) {
        double prevProbability = 0;
        
        for (int i = 1; i <= 20; i++) {
            int roundWins = winsByRound[i];
            int roundLosses = lossesByRound[i];
            int roundTotal = roundWins + roundLosses;
            
            if (roundTotal > 0) {
                double probability = (double) roundWins / roundTotal * 100;
                if (i > 1 && probability < prevProbability) {
                    return false;
                }
                prevProbability = probability;
            }
        }
        
        return true;
    }
}