package com.liuyi.rad.endpoint.admin.demo;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class StockSystemUtil {
    public static String formattedSharesIssued = "shares_issued";

    public static List<Stock> readStocksFromCSV(String filename) {
        List<Stock> stocks = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\t");
                if (values.length == 4) {
                    String code = values[0];
                    String productName = values[1];
                    String market = values[2];
                    String sharesIssued = values[3];
                    stocks.add(new Stock(code, productName, market, sharesIssued));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stocks;
    }

    public static void showList(List<Stock> stocks) {
        // 展示股票信息
        System.out.println("|=====================================================================|");
        // 在for循环中，将SharesIssued列的输出格式化
        for (int i = 0; i < stocks.size(); i++) {
            if (i >= 1) {
                String sharesIssued = stocks.get(i).getSharesIssued();
                // 格式化SharesIssued并将其靠右并分隔成三个数字一组
                formattedSharesIssued = String.format("%,15d", Integer.parseInt(sharesIssued));
                String code = stocks.get(i).getCode();
                int padding = (10 - code.length()) / 2;
                String paddingSpaces = new String(new char[padding]).replace('\0', ' ');
                System.out.printf("| %s%s%s | %-25s | %-8s | %-15s | %n",
                        paddingSpaces,
                        code,
                        paddingSpaces,
                        stocks.get(i).getProductName().length() > 20 ? stocks.get(i).getProductName().substring(0, 10) + "..." : stocks.get(i).getProductName(),
                        stocks.get(i).getMarket().length() > 20 ? stocks.get(i).getMarket().substring(0, 10) + "..." : stocks.get(i).getMarket(),
                        formattedSharesIssued);
            } else {
                System.out.printf("|    %-8s| %-25s | %-8s | %-15s | %n",
                        stocks.get(i).getCode().length() > 20 ? stocks.get(i).getCode().substring(0, 10) + "..." : stocks.get(i).getCode(),
                        stocks.get(i).getProductName().length() > 20 ? stocks.get(i).getProductName().substring(0, 10) + "..." : stocks.get(i).getProductName(),
                        stocks.get(i).getMarket().length() > 20 ? stocks.get(i).getMarket().substring(0, 10) + "..." : stocks.get(i).getMarket(),
                        stocks.get(i).getSharesIssued().length() > 20 ? stocks.get(i).getSharesIssued().substring(0, 10) + "..." : stocks.get(i).getSharesIssued(),
                        formattedSharesIssued);
                System.out.println("|------------+---------------------------+----------+-----------------|");
            }
        }
        System.out.println("=======================================================================");
    }

    // 检查code是否为4位数字且不以0开头
    public static boolean isValidCode(String code) {
        if (code.length() == 4 && code.matches("[1-9][0-9]{3}")) {
            return true;
        }
        return false;
    }

    // 检查是否有重复的code或productName
    public static boolean isDuplicate(List<Stock> stocks, String code, String productName) {
        for (Stock stock : stocks) {
            if (stock.getCode().equals(code) || stock.getProductName().equals(productName)) {
                return true;
            }
        }
        return false;
    }

    // 检查Market是否为合法值
    public static boolean isValidMarket(String market) {
        return market.equalsIgnoreCase("PRIME") || market.equalsIgnoreCase("STANDARD") || market.equalsIgnoreCase("GROWTH");
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    // 保存股票信息到CSV文件
    public static void saveStockToCSV(Stock stock, String filePath) {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            boolean hasHeader = false; // 标记文件是否已经有头部行

            // 检查文件是否已经包含头部行
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String firstLine = reader.readLine();
                if (firstLine != null && firstLine.startsWith("code\tproduct_name\tmarket\tshares_issued")) {
                    hasHeader = true;
                }
            }

            // 如果文件没有头部行，则添加头部行
            if (!hasHeader) {
                writer.write("Code\tProductName\tMarket\tSharesIssued\n");
            }

            // 遍历股票列表并写入CSV文件
                writer.write(String.format("%s\t%s\t%s\t%s\n",
                        stock.getCode(), stock.getProductName(), stock.getMarket(), stock.getSharesIssued()));


            System.out.println("股票信息已成功保存到 " + filePath);
        } catch (IOException e) {
            System.out.println("保存股票信息到CSV文件时发生错误：" + e.getMessage());
        }
    }

    // 买卖情报录入
    public static void recordTradeInformation(List<Stock> stocks) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("请输入买卖情报：");

            // 获取当前日期和时间，精确到分钟
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm");

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

            System.out.print("数量（多少股）: ");
            int quantity = scanner.nextInt();
            if (quantity <= 0) {
                System.out.println("错误：数量必须为正整数！");
                return;
            }

            System.out.print("请输入当前年月(例如: 2023-09-22-12:53): ");
            String nextDate = scanner.next();
            // 获取当前时间
            Date currentDate = new Date();
            Date tradeDate = dateFormat.parse(nextDate);
            // 检查输入的时间是否在未来
            if (tradeDate.after(currentDate)) {
                System.out.println("错误：输入的时间不能是未来时间！");
                return;
            }
            // 检查输入的时间是否在周一到周五的9:00到15:00之间
            Calendar cal = Calendar.getInstance();
            cal.setTime(tradeDate);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);

            if (dayOfWeek < Calendar.MONDAY || dayOfWeek > Calendar.FRIDAY || hourOfDay < 9 || hourOfDay >= 15) {
                System.out.println("错误：只能输入周一到周五的9:00到15:00之间的时间！");
                return;
            }

            System.out.print("股票单价（一股的单价，小数点后两位）: ");
            double unitPrice = scanner.nextDouble();

            // 创建一个DecimalFormat对象，并设置格式为"#.00"，表示小数点后两位
            DecimalFormat df = new DecimalFormat("#.00");

// 使用format方法将unitPrice格式化为保留两位小数的字符串
            String formattedPrice = df.format(unitPrice);

            // 创建 TradeInformation 对象并保存到CSV文件
            TradeInformation tradeInfo = new TradeInformation(nextDate,
                    productName, buyOrSell, String.valueOf(quantity), String.valueOf(formattedPrice));
            saveTradeInformationToCSV(tradeInfo, "/Users/wuyingtao/Projects/aa/demo/trade_information.csv");

            System.out.println("买卖情报录入成功！");
        } catch (Exception e) {
            System.out.println("录入买卖情报时发生错误：" + e.getMessage());
        }
    }

    // 保存买卖情报到CSV文件，使用制表符分隔字段
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

    // 读取保存买卖情报的CSV文件
    public static void readTradeInformationCSV(String filePath) {
        List<TradeInformation> tradeInfoList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t"); // 使用制表符分割字段
                if (parts.length == 5) {
                    String tradeDateTimeStr = parts[0]; // 获取日期时间字符串
                    String productName = parts[1];
                    String buyOrSell = parts[2];
                    String quantity = parts[3];
                    String unitPrice = parts[4];

                    TradeInformation tradeInfo = new TradeInformation(tradeDateTimeStr, productName, buyOrSell, quantity, unitPrice);
                    tradeInfoList.add(tradeInfo);
                }
            }
            sortTradeInformationByDateTime(tradeInfoList);
            printTradeInformation(tradeInfoList);
        } catch (IOException e) {
            System.out.println("读取CSV文件时发生错误：" + e.getMessage());
        }
    }


    // 对买卖情报列表按取引日期时间降序排列
    public static void sortTradeInformationByDateTime(List<TradeInformation> tradeInfoList) {
        List<TradeInformation> newTradeInfoList = tradeInfoList.subList(1, tradeInfoList.size());
        newTradeInfoList.sort((info1, info2) -> {
            // 比较日期时间字符串，注意反转顺序以实现降序排列
            return info2.getTradeDateTime().compareTo(info1.getTradeDateTime());
        });
    }

    // 打印买卖情报
    public static void printTradeInformation(List<TradeInformation> tradeInfoList) {
        for (TradeInformation tradeInfo : tradeInfoList) {
            System.out.printf("%-20s | %-15s|%-13s \t| %-13s \t| %-13s %n",
                    tradeInfo.getTradeDateTime(),
                    tradeInfo.getProductName(),
                    tradeInfo.getBuyOrSell(),
                    tradeInfo.getQuantity(),
                    tradeInfo.getUnitPrice());
        }
    }

    public static void login(List<Stock> stocks){
        Scanner scanner = new Scanner(System.in);

        // 股票信息登录
        System.out.print("请输入用户名：");
        String username = scanner.next();
        System.out.print("请输入密码：");
        scanner.next();
        System.out.println("登录成功，欢迎 " + username + "！");
        boolean validInput = false; // 标记是否输入有效信息
            System.out.println("请输入股票信息：");
            String code = null;
            String productName = null;
            String market = null;
            String sharesIssued = null;

            // 输入Code
            boolean validCode = false;
            while (!validCode) {
                System.out.print("Code (4位数字且不以0开头): ");
                code = scanner.next();
                // 检查code是否为4位数字且不以0开头
                if (StockSystemUtil.isValidCode(code)) {
                    validCode = true;
                } else {
                    System.out.println("错误：Code必须是4位数字且不以0开头！");
                }
            }

            // 输入ProductName
            System.out.print("ProductName: ");
            productName = scanner.next();

            // 输入Market
            boolean validMarket = false;
            while (!validMarket) {
                System.out.print("Market (PRIME/STANDARD/GROWTH): ");
                market = scanner.next();
                // 检查Market是否为合法值
                if (StockSystemUtil.isValidMarket(market)) {
                    validMarket = true;
                } else {
                    System.out.println("错误：Market只能是PRIME、STANDARD或GROWTH其中之一！");
                }
            }

            // 输入SharesIssued
            boolean validSharesIssued = false;
            while (!validSharesIssued) {
                System.out.print("SharesIssued: ");
                sharesIssued = scanner.next();
                // 检查SharesIssued是否为数字
                if (StockSystemUtil.isNumeric(sharesIssued)) {
                    validSharesIssued = true;
                } else {
                    System.out.println("错误：SharesIssued必须是数字！");
                }
            }

            // 检查是否有重复的code或productName
            if (StockSystemUtil.isDuplicate(stocks, code, productName)) {
                System.out.println("错误：Code或ProductName已存在！");

                //continue; // 重新循环要求用户输入
            }

            // 输入有效，退出循环
            validInput = true;
            // 创建新的Stock对象并添加到列表中
            Stock newStock = new Stock(code, productName, market, sharesIssued);
            stocks.add(newStock);
            StockSystemUtil.saveStockToCSV(newStock, "/Users/wuyingtao/Projects/aa/demo/output.csv");
            System.out.println("股票信息录入成功！");
        }
    }


