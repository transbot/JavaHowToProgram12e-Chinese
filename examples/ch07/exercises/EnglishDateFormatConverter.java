import java.util.*;
import java.util.regex.*;

public class EnglishDateFormatConverter {
    // 英文月份名称数组
    private static final String[] ENGLISH_MONTHS = {
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    };

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("请输入包含英文日期的字符串:");
        System.out.println("支持的格式: MMDDYY (如011426), MM/DD/YY (如01/14/15), MM/DD/YYYY (如01/14/2015), Month DD, YYYY (如January 14, 2015)");
        String input = scanner.nextLine();
        
        // 匹配英文日期的多种格式
        List<String> dates = new ArrayList<>();
        Map<String, Integer> dateTypes = new HashMap<>(); // 存储日期类型: 1=MMDDYY, 2=MM/DD/YY, 3=MM/DD/YYYY, 4=Month DD, YYYY
        
        // 正则表达式模式 - 更新以支持更多格式
        Pattern pattern1 = Pattern.compile("\\b(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])(\\d{2})\\b"); // MMDDYY (6位数字)
        Pattern pattern2 = Pattern.compile("\\b(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])/(\\d{2})\\b"); // MM/DD/YY (两位年份)
        Pattern pattern3 = Pattern.compile("\\b(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])/(\\d{4})\\b"); // MM/DD/YYYY (四位年份)
        Pattern pattern4 = Pattern.compile("\\b(January|February|March|April|May|June|July|August|September|October|November|December) (0?[1-9]|[12][0-9]|3[01]), (\\d{4})\\b"); // Month DD, YYYY
        
        // 查找匹配
        findAndStoreMatches(pattern1, input, dates, dateTypes, 1);
        findAndStoreMatches(pattern2, input, dates, dateTypes, 2);
        findAndStoreMatches(pattern3, input, dates, dateTypes, 3);
        findAndStoreMatches(pattern4, input, dates, dateTypes, 4);
        
        if (dates.isEmpty()) {
            System.out.println("未找到任何日期格式");
            return;
        }
        
        System.out.println("\n找到的日期及其转换结果:");
        for (String date : dates) {
            int type = dateTypes.get(date);
            System.out.println("原始格式: " + date);
            
            // 转换为其他格式
            if (type != 1) {
                System.out.println("  转换为MMDDYY格式: " + convertToFormat1(date, type));
            }
            if (type != 2 && type != 3) {
                System.out.println("  转换为MM/DD/YY格式: " + convertToFormat2(date, type));
            }
            if (type != 3) {
                System.out.println("  转换为MM/DD/YYYY格式: " + convertToFormat3(date, type));
            }
            if (type != 4) {
                System.out.println("  转换为Month DD, YYYY格式: " + convertToFormat4(date, type));
            }
            System.out.println();
        }
        
        scanner.close();
    }
    
    // 查找并存储匹配的日期
    private static void findAndStoreMatches(Pattern pattern, String input, List<String> dates, Map<String, Integer> dateTypes, int type) {
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String date = matcher.group();
            dates.add(date);
            dateTypes.put(date, type);
        }
    }
    
    // 转换为MMDDYY格式
    private static String convertToFormat1(String date, int originalType) {
        int month, day, year;
        
        if (originalType == 2) {
            // MM/DD/YY格式
            String[] parts = date.split("/");
            month = Integer.parseInt(parts[0]);
            day = Integer.parseInt(parts[1]);
            year = Integer.parseInt(parts[2]) % 100; // 取后两位
        } else if (originalType == 3) {
            // MM/DD/YYYY格式
            String[] parts = date.split("/");
            month = Integer.parseInt(parts[0]);
            day = Integer.parseInt(parts[1]);
            year = Integer.parseInt(parts[2]) % 100; // 取后两位
        } else if (originalType == 4) {
            // Month DD, YYYY格式
            String[] parts = date.split(" ");
            month = getMonthNumber(parts[0]);
            day = Integer.parseInt(parts[1].replace(",", ""));
            year = Integer.parseInt(parts[2]) % 100; // 取后两位
        } else {
            return date; // 已经是目标格式
        }
        
        return String.format("%02d%02d%02d", month, day, year);
    }
    
    // 转换为MM/DD/YY格式
    private static String convertToFormat2(String date, int originalType) {
        int month, day, year;
        
        if (originalType == 1) {
            // MMDDYY格式
            month = Integer.parseInt(date.substring(0, 2));
            day = Integer.parseInt(date.substring(2, 4));
            year = Integer.parseInt(date.substring(4, 6)); // 已经是两位年份
        } else if (originalType == 3) {
            // MM/DD/YYYY格式
            String[] parts = date.split("/");
            month = Integer.parseInt(parts[0]);
            day = Integer.parseInt(parts[1]);
            year = Integer.parseInt(parts[2]) % 100; // 取后两位
        } else if (originalType == 4) {
            // Month DD, YYYY格式
            String[] parts = date.split(" ");
            month = getMonthNumber(parts[0]);
            day = Integer.parseInt(parts[1].replace(",", ""));
            year = Integer.parseInt(parts[2]) % 100; // 取后两位
        } else {
            return date; // 已经是目标格式
        }
        
        return String.format("%02d/%02d/%02d", month, day, year);
    }
    
    // 转换为MM/DD/YYYY格式
    private static String convertToFormat3(String date, int originalType) {
        int month, day, year;
        
        if (originalType == 1) {
            // MMDDYY格式
            month = Integer.parseInt(date.substring(0, 2));
            day = Integer.parseInt(date.substring(2, 4));
            year = 2000 + Integer.parseInt(date.substring(4, 6)); // 假设是20XX年
        } else if (originalType == 2) {
            // MM/DD/YY格式
            String[] parts = date.split("/");
            month = Integer.parseInt(parts[0]);
            day = Integer.parseInt(parts[1]);
            year = 2000 + Integer.parseInt(parts[2]); // 假设是20XX年
        } else if (originalType == 4) {
            // Month DD, YYYY格式
            String[] parts = date.split(" ");
            month = getMonthNumber(parts[0]);
            day = Integer.parseInt(parts[1].replace(",", ""));
            year = Integer.parseInt(parts[2]);
        } else {
            return date; // 已经是目标格式
        }
        
        return String.format("%02d/%02d/%04d", month, day, year);
    }
    
    // 转换为Month DD, YYYY格式
    private static String convertToFormat4(String date, int originalType) {
        int month, day, year;
        
        if (originalType == 1) {
            // MMDDYY格式
            month = Integer.parseInt(date.substring(0, 2));
            day = Integer.parseInt(date.substring(2, 4));
            year = 2000 + Integer.parseInt(date.substring(4, 6)); // 假设是20XX年
        } else if (originalType == 2) {
            // MM/DD/YY格式
            String[] parts = date.split("/");
            month = Integer.parseInt(parts[0]);
            day = Integer.parseInt(parts[1]);
            year = 2000 + Integer.parseInt(parts[2]); // 假设是20XX年
        } else if (originalType == 3) {
            // MM/DD/YYYY格式
            String[] parts = date.split("/");
            month = Integer.parseInt(parts[0]);
            day = Integer.parseInt(parts[1]);
            year = Integer.parseInt(parts[2]);
        } else {
            return date; // 已经是目标格式
        }
        
        return String.format("%s %d, %04d", ENGLISH_MONTHS[month - 1], day, year);
    }
    
    // 获取月份数字
    private static int getMonthNumber(String monthName) {
        for (int i = 0; i < ENGLISH_MONTHS.length; i++) {
            if (ENGLISH_MONTHS[i].equals(monthName)) {
                return i + 1;
            }
        }
        return 1; // 默认值
    }
}