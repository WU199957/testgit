import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class StockSystemLogin {
    public static void login(List<Stock> stocks) throws IOException {
        Scanner Sc = new Scanner(System.in);
        System.out.println("新規株式登録");
        String code = null;
        String productName = null;
        String market = null;
        String sharesIssued = null;

        //输入コード
        boolean validCode = false;
        while (!validCode) {
            System.out.print("銘柄コード＞");
            code = Sc.next();
            if (StockSystemLogin.isValidCode(code)&&isDuplicate(stocks,code)) {
                validCode = true;
            }
            else {
                System.out.println("输入错误");
                continue;
            }
        }
        //入力productname
//        while (true) {
//            System.out.print("銘柄名＞ ");
//            productName = Sc.next();
//            if (productName.isBlank()) {
//            System.out.println("銘柄名は空白にできません。再度入力してください。");
//            continue;
//            } else {
//                break;
//            }
//        }

        boolean validproductname=false;
        while (!validproductname) {
            System.out.println("銘柄名＞ ");
            productName = Sc.next();
            if (containsWhitespace(productName)) {
                System.out.println("銘柄名は空白にできません。再度入力してください。");
                continue;
            }
            else{
                validproductname=true;
            }
        }
//
//        System.out.print("銘柄名＞ ");
//        productName = Sc.next();

        //输入市场
        boolean validMarket=false;
        while (!validMarket){
            System.out.print("市场＞");
            market= Sc.next();
            market = market.toUpperCase();
            if(isValidMarket(market)){
                validMarket=true;
            }else {
                System.out.println("输入错误");
                continue;
            }
        }

        boolean validSharesIssued=false;
        while (!validSharesIssued){
            System.out.println("発行すみ株式数＞");
            sharesIssued= Sc.next();
            if(isValidSharesIssued(sharesIssued)){
                validSharesIssued=true;
            }else {
                System.out.println("请输入正确数字！");
                continue;
            }
        }
        Stock newStock=new Stock(code,productName,market,sharesIssued);
        stocks.add(newStock);
        StockSystemLogin.saveStockToCSV(newStock,"/Users/wuyingtao/Desktop/step2/output.csv");
    }



    public static boolean isValidCode (String code){
        if (code.length() == 4 && code.matches("[1-9][0-9]{3}")) {
            return true;
        }
        return false;
    }
    public static boolean isValidMarket(String Market){
        if(Market.equalsIgnoreCase("PRIME")||Market.equalsIgnoreCase("STANDARD")||Market.equalsIgnoreCase("GROWTH")){
            return true;
        }
        return false;
    }
    public static boolean isValidSharesIssued(String SharesIssued){
        try{
            Double.parseDouble(SharesIssued);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }
    public static boolean containsWhitespace(String str) {
        return Pattern.compile("\\s").matcher(str).find();
    }

    public static boolean isDuplicate(List<Stock> stocks, String code){
        for(Stock stock:stocks){
            if(stock.getCode().equals(code)){
                return false;
            }
        }
        return  true;
    }
    public static void saveStockToCSV(Stock stock,String filepath) throws IOException {
        try(FileWriter writer=new FileWriter(filepath,true)){
            writer.write(String.format("%s\t%s\t%s\t%s\n",
                    stock.getCode(),stock.getProductName(),stock.getMarket(),stock.getSharesIssued()));
            System.out.println("保存しました");
        }catch (IOException e) {
            System.out.println("保存股票信息到CSV文件时发生错误：" );
        }
    }
}