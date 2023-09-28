import java.io.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class SaveTradeInformation {
    public static void recordTradeInformation(List<Stock> stocks) {
        Scanner scanner = new Scanner(System.in);
        double unitPrice;

        try {
            String nextDate;
            while (true) {
                // 获取当前日期和时间，精确到分钟
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
                System.out.print("请输入当前年月(例如: 2023-09-22-12:53): ");
                nextDate = scanner.next();
                if (!isValidDateFormat(nextDate)) {
                    System.out.println("请输入标准时间");
                    continue;
                } else {
                    // 获取当前时间
                    Date currentDate = new Date();
                    Date tradeDate = dateFormat.parse(nextDate);
                    // 检查输入的时间是否在周一到周五的9:00到15:00之间
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(tradeDate);
                    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                    int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
                    // 检查输入的时间是否在未来
                    if (tradeDate.after(currentDate)) {
                        System.out.println("错误：输入的时间不能是未来时间！");
                        continue;
                    } else if (dayOfWeek < Calendar.MONDAY || dayOfWeek > Calendar.FRIDAY || hourOfDay < 9 || hourOfDay >= 15) {
                        System.out.println("错误：只能输入周一到周五的9:00到15:00之间的时间！");
                        continue;
                    } else {
                        break;
                    }
                }
            }


            System.out.print("名字: ");
            String productName = scanner.next();

            // 检查输入的股票代码是否存在于现有股票列表中
            boolean stockExists = false;
            for (Stock stock : stocks) {
                if (stock.getProductName().equalsIgnoreCase(productName)) {
                    stockExists = true;
                    break;
                }
            }
            if (!stockExists) {
                System.out.println("错误：指定的productName不存在于现有股票列表中！");
                return;
            }

            System.out.print("买卖区分（买/卖）: ");
            String buyOrSell = scanner.next();
            if (!buyOrSell.equalsIgnoreCase("买") && !buyOrSell.equalsIgnoreCase("卖")) {
                System.out.println("错误：只能输入'买'或'卖'！");
                return;
            }

            int quantity;

            while (true) {
                System.out.print("数量（多少股）: ");

                if (scanner.hasNextInt()) { // 检查输入是否为整数
                    quantity = scanner.nextInt();

                    if (quantity > 0) { // 检查输入是否为正整数
                        break; // 输入有效，退出循环
                    } else {
                        System.out.println("错误：请输入正整数！");
                    }
                } else {
                    System.out.println("错误：请输入有效整数！");
                    scanner.next(); // 清除无效输入
                }
            }

            while (true) {
                System.out.println("price");
                if (scanner.hasNextDouble()) {
                    ;// 检查输入是否为双精度浮点数
                    unitPrice = scanner.nextDouble();
                    if (unitPrice > 0) { // 检查输入是否为正数
                        break; // 输入有效，退出循环
                    } else {
                        System.out.println("错误：请输入正数！");
                    }
                } else {
                    System.out.println("错误：请输入有效数字！");
                    scanner.next(); // 清除无效输入
                }
            }


            // 创建一个DecimalFormat对象，并设置格式为"#.00"，表示小数点后两位
            DecimalFormat df = new DecimalFormat("#.00");

            String formattedPrice = df.format(unitPrice);


            // 创建 TradeInformation 对象并保存到CSV文件
            TradeInformation tradeInfo = new TradeInformation(nextDate,
                    productName, buyOrSell, String.valueOf(quantity), String.valueOf(formattedPrice));
            saveTradeInformationToCSV(tradeInfo, "/Users/wuyingtao/Desktop/step2/trade_information.csv");

            System.out.println("买卖情报录入成功！");
        } catch (Exception e) {
            System.out.println("录入买卖情报时发生错误：" + e.getMessage());
        }
    }

    private static void saveTradeInformationToCSV(TradeInformation tradeInfo, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            boolean hasHeader = false; // 标记文件是否已经有头部行

            // 检查文件是否已经包含头部行
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String firstLine = reader.readLine();
                if (firstLine != null && firstLine.startsWith("trade_date_time\tproduct_name\tbuy_or_sell\tquantity\tunit_price")) {
                    hasHeader = true;
                }
            }
            // 如果文件没有头部行，则添加头部行
            if (!hasHeader) {
                writer.write("trade_date_time\tproduct_name\tbuy_or_sell\tquantity\tunit_price\n");
            }
            // 使用制表符分隔字段并写入文件
            writer.printf("%s\t%s\t%s\t%s\t%s\n",
                    tradeInfo.getTradeDateTime(),
                    tradeInfo.getProductName(),
                    tradeInfo.getBuyOrSell(),
                    tradeInfo.getQuantity(),
                    tradeInfo.getUnitPrice());
            System.out.println("买卖情报已成功保存到 " + filePath);
        } catch (IOException e) {
            System.out.println("保存买卖情报到CSV文件时发生错误：" + e.getMessage());
        }
    }

    private static boolean isValidDateFormat(String date) {
        // 使用正则表达式检查日期格式
        String pattern = "\\d{4}-\\d{2}-\\d{2}-\\d{2}:\\d{2}";
        return date.matches(pattern);
    }

    private static boolean containsOnlyDigitsAndHyphen(String input) {
        // 使用正则表达式检查输入是否只包含数字和连字符
        String pattern = "^[0-9-]+$";
        return input.matches(pattern);
    }

}
