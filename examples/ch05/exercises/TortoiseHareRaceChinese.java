// 练习5.31，模拟：龟兔赛跑
public class TortoiseHareRaceChinese {
    public static void main(String[] args) throws Exception {
        // 初始化位置（都从第1格开始）
        int tortoisePos = 1;
        int harePos = 1;
        int totalPositions = 70;  // 总位置数
        
        // 随机数生成器
        java.util.Random random = new java.util.Random();
        
        // 比赛开始
        System.out.println("砰!!!!!");
        System.out.println("他们出发了!!!!!");
        System.out.println();  // 空行分隔
        
        // 控制比赛速度（毫秒）
        final int DELAY = 500;
        
        // 比赛主循环
        while (true) {
            // 让程序暂停一段时间，便于观察
            Thread.sleep(DELAY);
            
            // 更新乌龟位置
            tortoisePos = moveTortoise(tortoisePos, random);
            // 确保位置不会小于1
            if (tortoisePos < 1) tortoisePos = 1;
            
            // 更新兔子位置
            harePos = moveHare(harePos, random);
            // 确保位置不会小于1
            if (harePos < 1) harePos = 1;
            
            // 检查是否有获胜者
            boolean tortoiseWins = tortoisePos >= totalPositions;
            boolean hareWins = harePos >= totalPositions;
            
            if (tortoiseWins && hareWins) {
                // 平局，判定乌龟获胜
                printRaceTrack(tortoisePos, harePos, totalPositions);
                System.out.println("\n乌龟赢了!!!耶!!!");
                break;
            } else if (tortoiseWins) {
                printRaceTrack(tortoisePos, harePos, totalPositions);
                System.out.println("\n乌龟赢了!!!耶!!!");
                break;
            } else if (hareWins) {
                printRaceTrack(tortoisePos, harePos, totalPositions);
                System.out.println("\n兔子赢了。呸。");
                break;
            }
            
            // 显示当前赛道状态
            printRaceTrack(tortoisePos, harePos, totalPositions);
        }
    }
    
    // 移动乌龟的方法
    private static int moveTortoise(int currentPos, java.util.Random random) {
        int move;
        int i = random.nextInt(10) + 1; // 生成1-10的随机数
        
        if (i <= 5) {          // 50%概率：快速爬行，向右3格
            move = 3;
        } else if (i <= 7) {   // 20%概率：滑倒，向左6格
            move = -6;
        } else {               // 30%概率：慢速爬行，向右1格
            move = 1;
        }
        
        return currentPos + move;
    }
    
    // 移动兔子的方法
    private static int moveHare(int currentPos, java.util.Random random) {
        int move;
        int i = random.nextInt(10) + 1; // 生成1-10的随机数
        
        if (i <= 2) {          // 20%概率：睡大觉，原地不动
            move = 0;
        } else if (i <= 4) {   // 20%概率：大跳，向右9格
            move = 9;
        } else if (i <= 5) {   // 10%概率：大滑，向左12格
            move = -12;
        } else if (i <= 8) {   // 30%概率：小跳，向右1格
            move = 1;
        } else {               // 20%概率：小滑，向左2格
            move = -2;
        }
        
        return currentPos + move;
    }
    
    // 打印赛道状态的方法（每个位置显示一个汉字或两个半角空格）
    private static void printRaceTrack(int tortoisePos, int harePos, int totalPositions) {
        // 创建一个字符串数组表示每个位置
        String[] track = new String[totalPositions];
        for (int i = 0; i < totalPositions; i++) {
            track[i] = "  ";  // 初始化为两个半角空格
        }
        
        // 放置乌龟和兔子
        if (tortoisePos <= totalPositions && harePos <= totalPositions && tortoisePos == harePos) {
            // 位置相同且在赛道范围内，显示"痛"
            track[tortoisePos - 1] = "痛";
        } else {
            // 分别放置乌龟和兔子（在赛道范围内）
            if (tortoisePos <= totalPositions) {
                track[tortoisePos - 1] = "龟";
            }
            if (harePos <= totalPositions) {
                track[harePos - 1] = "兔";
            }
        }
        
        // 打印赛道（拼接所有位置）
        StringBuilder sb = new StringBuilder();
        for (String position : track) {
            sb.append(position);
        }
        System.out.println(sb.toString());
    }
}