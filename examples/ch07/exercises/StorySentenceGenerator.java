// 练习7.3，StorySentenceGenerator.java
// 编写一个程序，使用随机数生成来创建武侠风格的中文句子。
// 程序需使用4个String数组，分别名为subjects（主体）、locations（地点）、actions（动作）和objects（客体）。
// 按照"主体+在+地点+动作+客体"的顺序从每个数组中随机选择一个词语来组成一个句子。
// 每选出一个词语，就将其与前面的词语连接起来（中文不需要空格）。
// 最终输出的句子应以句号结尾。程序应生成并显示20个句子。

import java.util.Random;

public class StorySentenceGenerator {
    public static void main(String[] args) {
        // 定义四个数组
        String[] subjects = {"张无忌", "赵敏", "周芷若", "谢逊", "张三丰", "殷素素", "杨逍", "范遥", "小昭", "殷离"};
        String[] locations = {"冰火岛", "光明顶", "武当山", "少林寺", "家里", "峨眉山", "灵蛇岛", "武当派", "明教总坛", "万安寺"};
        String[] actions = {"训练", "攻打", "防御", "揉", "洗", "揍","刷", "研究", "修炼", "打造", "挥舞", "参悟"};
        String[] objects = {"狗狗", "猫猫", "帅帅", "衣服", "玄铁指环", "倚天剑", "屠龙刀", "圣火令", "九阳真经", "乾坤大挪移"};
        
        Random random = new Random();
        
        System.out.println("随机生成的武侠故事句子：");
        System.out.println("==========================");
        
        // 生成并显示20个句子
        for (int i = 0; i < 20; i++) {
            // 从每个数组中随机选择一个词语
            String subject = subjects[random.nextInt(subjects.length)];
            String location = locations[random.nextInt(locations.length)];
            String action = actions[random.nextInt(actions.length)];
            String object = objects[random.nextInt(objects.length)];
            
            // 按照指定格式构建句子
            String sentence = subject + "在" + location + action + object + "。";
            
            // 输出句子
            System.out.println((i + 1) + ". " + sentence);
        }
    }
}