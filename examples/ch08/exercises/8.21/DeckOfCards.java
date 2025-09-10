// 练习8.21，DeckOfCards.java
// 用于表示一副扑克牌的DeckOfCards类
import java.util.random.RandomGenerator;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
            return "Invalid hand size";
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
    
    // 获取牌型的权重（用于比较）
    public int getHandWeight(Card[] hand) {
        String handType = evaluateHand(hand);
        
        switch (handType) {
            case "同花顺": return 9;
            case "四条": return 8;
            case "葫芦": return 7;
            case "同花": return 6;
            case "顺子": return 5;
            case "三条": return 4;
            case "两对": return 3;
            case "一对": return 2;
            case "散牌": return 1;
            default: return 0;
        }
    }
    
    // 比较两手牌
    public String compareHands(Card[] hand1, Card[] hand2) {
        int weight1 = getHandWeight(hand1);
        int weight2 = getHandWeight(hand2);
        
        if (weight1 > weight2) {
            return "玩家获胜";
        } else if (weight2 > weight1) {
            return "庄家获胜";
        } else {
            // 牌型相同，需要比较牌面大小
            return compareSameTypeHands(hand1, hand2);
        }
    }
    
    // 比较相同牌型的两手牌
    private String compareSameTypeHands(Card[] hand1, Card[] hand2) {
        // 获取两手牌的点数值并排序
        int[] values1 = getSortedCardValues(hand1);
        int[] values2 = getSortedCardValues(hand2);
        
        // 根据不同牌型进行比较
        String handType = evaluateHand(hand1);
        
        switch (handType) {
            case "同花顺":
            case "顺子":
            case "同花":
            case "散牌":
                // 比较最高牌
                return compareHighCard(values1, values2);
                
            case "四条":
                // 比较四条的点数，再比较单张
                return compareFourOfAKind(values1, values2);
                
            case "葫芦":
                // 比较三条的点数，再比较对子
                return compareFullHouse(values1, values2);
                
            case "三条":
                // 比较三条的点数，再比较剩余牌
                return compareThreeOfAKind(values1, values2);
                
            case "两对":
                // 比较高对，再比较低对，最后比较单张
                return compareTwoPairs(values1, values2);
                
            case "一对":
                // 比较对子点数，再比较剩余牌
                return compareOnePair(values1, values2);
                
            default:
                return "平局";
        }
    }
    
    // 获取排序后的牌面值（从大到小）
    private int[] getSortedCardValues(Card[] hand) {
        int[] values = new int[hand.length];
        for (int i = 0; i < hand.length; i++) {
            values[i] = faceToValue(hand[i].face());
        }
        Arrays.sort(values);
        // 反转数组，使其从大到小排序
        for (int i = 0; i < values.length / 2; i++) {
            int temp = values[i];
            values[i] = values[values.length - 1 - i];
            values[values.length - 1 - i] = temp;
        }
        return values;
    }
    
    // 比较最高牌
    private String compareHighCard(int[] values1, int[] values2) {
        for (int i = 0; i < values1.length; i++) {
            if (values1[i] > values2[i]) {
                return "玩家获胜";
            } else if (values2[i] > values1[i]) {
                return "庄家获胜";
            }
        }
        return "平局";
    }
    
    // 比较四条
    private String compareFourOfAKind(int[] values1, int[] values2) {
        int four1 = getNOfAKindValue(values1, 4);
        int four2 = getNOfAKindValue(values2, 4);
        
        if (four1 > four2) {
            return "玩家获胜";
        } else if (four2 > four1) {
            return "庄家获胜";
        } else {
            // 四条相同，比较单张
            int single1 = getNOfAKindValue(values1, 1);
            int single2 = getNOfAKindValue(values2, 1);
            
            if (single1 > single2) {
                return "玩家获胜";
            } else if (single2 > single1) {
                return "庄家获胜";
            } else {
                return "平局";
            }
        }
    }
    
    // 比较葫芦
    private String compareFullHouse(int[] values1, int[] values2) {
        int three1 = getNOfAKindValue(values1, 3);
        int three2 = getNOfAKindValue(values2, 3);
        
        if (three1 > three2) {
            return "玩家获胜";
        } else if (three2 > three1) {
            return "庄家获胜";
        } else {
            // 三条相同，比较对子
            int pair1 = getNOfAKindValue(values1, 2);
            int pair2 = getNOfAKindValue(values2, 2);
            
            if (pair1 > pair2) {
                return "玩家获胜";
            } else if (pair2 > pair1) {
                return "庄家获胜";
            } else {
                return "平局";
            }
        }
    }
    
    // 比较三条
    private String compareThreeOfAKind(int[] values1, int[] values2) {
        int three1 = getNOfAKindValue(values1, 3);
        int three2 = getNOfAKindValue(values2, 3);
        
        if (three1 > three2) {
            return "玩家获胜";
        } else if (three2 > three1) {
            return "庄家获胜";
        } else {
            // 三条相同，比较剩余牌
            return compareHighCard(values1, values2);
        }
    }
    
    // 比较两对
    private String compareTwoPairs(int[] values1, int[] values2) {
        int[] pairs1 = getPairValues(values1);
        int[] pairs2 = getPairValues(values2);
        
        // 比较高对
        if (pairs1[0] > pairs2[0]) {
            return "玩家获胜";
        } else if (pairs2[0] > pairs1[0]) {
            return "庄家获胜";
        }
        
        // 高对相同，比较低对
        if (pairs1[1] > pairs2[1]) {
            return "玩家获胜";
        } else if (pairs2[1] > pairs1[1]) {
            return "庄家获胜";
        }
        
        // 两对都相同，比较单张
        int single1 = getSingleCardValue(values1);
        int single2 = getSingleCardValue(values2);
        
        if (single1 > single2) {
            return "玩家获胜";
        } else if (single2 > single1) {
            return "庄家获胜";
        } else {
            return "平局";
        }
    }
    
    // 比较一对
    private String compareOnePair(int[] values1, int[] values2) {
        int pair1 = getNOfAKindValue(values1, 2);
        int pair2 = getNOfAKindValue(values2, 2);
        
        if (pair1 > pair2) {
            return "玩家获胜";
        } else if (pair2 > pair1) {
            return "庄家获胜";
        } else {
            // 对子相同，比较剩余牌
            return compareHighCard(values1, values2);
        }
    }
    
    // 获取N张相同牌的值
    private int getNOfAKindValue(int[] values, int n) {
        int[] counts = new int[15]; // 索引1-14对应牌值1-14(A) - 修复：增加数组大小
        for (int value : values) {
            if (value >= 1 && value <= 14) { // 确保值在有效范围内
                counts[value]++;
            }
        }
        
        for (int i = 14; i >= 1; i--) { // 修复：从14开始而不是13
            if (counts[i] == n) {
                return i;
            }
        }
        
        return 0;
    }
    
    // 获取对子的值（用于两对）
    private int[] getPairValues(int[] values) {
        int[] counts = new int[15]; // 修复：增加数组大小
        int[] pairs = new int[2];
        int pairCount = 0;
        
        for (int value : values) {
            if (value >= 1 && value <= 14) { // 确保值在有效范围内
                counts[value]++;
            }
        }
        
        for (int i = 14; i >= 1; i--) { // 修复：从14开始而不是13
            if (counts[i] == 2) {
                pairs[pairCount++] = i;
                if (pairCount == 2) break;
            }
        }
        
        return pairs;
    }
    
    // 获取单张牌的值（用于两对）
    private int getSingleCardValue(int[] values) {
        int[] counts = new int[15]; // 修复：增加数组大小
        
        for (int value : values) {
            if (value >= 1 && value <= 14) { // 确保值在有效范围内
                counts[value]++;
            }
        }
        
        for (int i = 1; i <= 14; i++) { // 修复：检查到14而不是13
            if (counts[i] == 1) {
                return i;
            }
        }
        
        return 0;
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
            case "A": return 14; // A作为14，便于比较
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
    
    // 根据牌型质量决定换牌数量（策略函数）
    public int decideNumberOfCardsToDraw(Card[] hand) {
        String handType = evaluateHand(hand);
        switch (handType) {
            case "同花顺":
            case "四条":
            case "葫芦":
                return 0; // 不换牌
            case "同花":
            case "顺子":
                return 1; // 换一张牌
            case "三条":
                return 2; // 换两张牌
            case "两对":
                return 1; // 换一张牌（尝试改善为葫芦）
            case "一对":
                return 3; // 换三张牌
            case "散牌":
                return 3; // 换三张牌
            default:
                return 3;
        }
    }
    
    // 智能选择要替换的牌（庄家策略）
    public List<Integer> selectCardsToReplace(Card[] hand) {
        List<Integer> cardsToReplace = new ArrayList<>();
        String handType = evaluateHand(hand);
        
        switch (handType) {
            case "同花顺":
            case "四条":
            case "葫芦":
                // 不换任何牌
                break;
                
            case "同花":
            case "顺子":
                // 换一张最小的牌（尝试改善牌型）
                int minIndex = findLowestCardIndex(hand);
                cardsToReplace.add(minIndex);
                break;
                
            case "三条":
                // 换两张不是三条的牌
                int[] tripleValue = findNOfAKind(hand, 3);
                for (int i = 0; i < hand.length; i++) {
                    int value = faceToValue(hand[i].face());
                    if (value != tripleValue[0]) {
                        cardsToReplace.add(i);
                        if (cardsToReplace.size() == 2) break;
                    }
                }
                break;
                
            case "两对":
                // 换一张单牌（尝试凑成葫芦）
                int[] pairValues = findNOfAKind(hand, 2);
                int singleValue = findSingleCardValue(hand, pairValues);
                for (int i = 0; i < hand.length; i++) {
                    int value = faceToValue(hand[i].face());
                    if (value == singleValue) {
                        cardsToReplace.add(i);
                        break;
                    }
                }
                break;
                
            case "一对":
                // 换三张不是对子的牌
                int[] pairValue = findNOfAKind(hand, 2);
                for (int i = 0; i < hand.length; i++) {
                    int value = faceToValue(hand[i].face());
                    if (value != pairValue[0]) {
                        cardsToReplace.add(i);
                        if (cardsToReplace.size() == 3) break;
                    }
                }
                break;
                
            case "散牌":
                // 换三张最小的牌
                int[] lowestIndices = findLowestCardIndices(hand, 3);
                for (int index : lowestIndices) {
                    cardsToReplace.add(index);
                }
                break;
        }
        
        return cardsToReplace;
    }
    
    // 换牌操作：替换指定位置的牌
    public void replaceCards(Card[] hand, List<Integer> indicesToReplace) {
        for (int index : indicesToReplace) {
            Card newCard = dealCard();
            if (newCard != null && index >= 0 && index < hand.length) {
                hand[index] = newCard;
            }
        }
    }
    
    // 辅助方法：找到点数最小的牌的索引
    private int findLowestCardIndex(Card[] hand) {
        int minValue = 15; // 比A(14)大
        int minIndex = 0;
        
        for (int i = 0; i < hand.length; i++) {
            int value = faceToValue(hand[i].face());
            if (value < minValue) {
                minValue = value;
                minIndex = i;
            }
        }
        
        return minIndex;
    }
    
    // 辅助方法：找到N张点数最小的牌的索引
    private int[] findLowestCardIndices(Card[] hand, int n) {
        int[] values = new int[hand.length];
        Integer[] indices = new Integer[hand.length];
        
        for (int i = 0; i < hand.length; i++) {
            values[i] = faceToValue(hand[i].face());
            indices[i] = i;
        }
        
        // 按牌面值排序索引
        Arrays.sort(indices, (a, b) -> Integer.compare(values[a], values[b]));
        
        int[] result = new int[Math.min(n, hand.length)];
        for (int i = 0; i < result.length; i++) {
            result[i] = indices[i];
        }
        
        return result;
    }
    
    // 辅助方法：找到N张相同点数的牌的值
    private int[] findNOfAKind(Card[] hand, int n) {
        int[] counts = new int[15]; // 索引1-14对应牌值1-14(A)
        for (Card card : hand) {
            int value = faceToValue(card.face());
            if (value >= 1 && value <= 14) { // 确保值在有效范围内
                counts[value]++;
            }
        }
        
        List<Integer> result = new ArrayList<>();
        for (int i = 1; i <= 14; i++) {
            if (counts[i] == n) {
                result.add(i);
            }
        }
        
        // 转换为数组
        int[] arr = new int[result.size()];
        for (int i = 0; i < result.size(); i++) {
            arr[i] = result.get(i);
        }
        
        return arr;
    }
    
    // 辅助方法：找到单张牌的值（用于两对）
    private int findSingleCardValue(Card[] hand, int[] pairValues) {
        int[] counts = new int[15];
        for (Card card : hand) {
            int value = faceToValue(card.face());
            if (value >= 1 && value <= 14) { // 确保值在有效范围内
                counts[value]++;
            }
        }
        
        for (int i = 1; i <= 14; i++) {
            if (counts[i] == 1) {
                // 确保这个值不是对子的值
                boolean isPairValue = false;
                for (int pairValue : pairValues) {
                    if (i == pairValue) {
                        isPairValue = true;
                        break;
                    }
                }
                if (!isPairValue) {
                    return i;
                }
            }
        }
        
        return 0;
    }
    
    // 玩家选择要替换的牌
    public List<Integer> getPlayerCardSelection(Card[] hand) {
        Scanner scanner = new Scanner(System.in);
        List<Integer> selectedIndices = new ArrayList<>();
        
        System.out.println("\n您的牌:");
        for (int i = 0; i < hand.length; i++) {
            System.out.println(i + ": " + hand[i]);
        }
        
        System.out.println("请输入您想要替换的牌的编号(0-4)，用逗号分隔(如: 1,3,4)，如果不换牌请输入-1:");
        
        try {
            String input = scanner.nextLine();
            if (input.equals("-1")) {
                return selectedIndices; // 空列表表示不换牌
            }
            
            String[] indices = input.split(",");
            for (String indexStr : indices) {
                int index = Integer.parseInt(indexStr.trim());
                if (index >= 0 && index < hand.length) {
                    selectedIndices.add(index);
                }
            }
        } catch (Exception e) {
            System.out.println("输入无效，请重新开始游戏。");
        }
        
        return selectedIndices;
    }
} // 结束DeckOfCards类