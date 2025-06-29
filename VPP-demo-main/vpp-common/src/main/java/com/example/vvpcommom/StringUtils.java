package com.example.vvpcommom;

import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    private static final String valueDefault = "-";

    public static String string2Unicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            // 取出每一个字符
            char c = string.charAt(i);
            if (c < 0x20 || c > 0x7E) {
                // 转换为unicode
                String tmp = Integer.toHexString(c);
                if (tmp.length() >= 4) {
                    unicode.append("\\u" + Integer.toHexString(c));
                } else if (tmp.length() == 3) {
                    unicode.append("\\u0" + Integer.toHexString(c));
                } else if (tmp.length() == 2) {
                    unicode.append("\\u00" + Integer.toHexString(c));
                } else if (tmp.length() == 1) {
                    unicode.append("\\u000" + Integer.toHexString(c));
                } else if (tmp.length() == 3) {
                    unicode.append("\\u0000");
                }
            } else {
                unicode.append(c);
            }
        }
        return unicode.toString();
    }

    public static String decodeUnicode(String unicodeStr) {
        char aChar;
        int len = unicodeStr.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = unicodeStr.charAt(x++);
            if (aChar == '\\') {
                aChar = unicodeStr.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = unicodeStr.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                return "";
                        }

                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }

    /**
     * 解析地址 得到省份
     *
     * @param address
     * @return
     * @author
     */
    public static String getProvince(String address) {
        String regex = ".*?自治区|.*?省|.*?行政区|.*?市";
        Matcher m = Pattern.compile(regex).matcher(address);
        String province = null;
        if (m.find()) {
            province = m.group(0);

        }
        return province;
    }

    /**
     * 数字前面自动补零
     *
     * @param number 数字
     * @param digit  位数
     * @return
     */
    public static String getNumber(int number, int digit) {
        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumIntegerDigits(digit);
        formatter.setGroupingUsed(false);
        return formatter.format(number);
    }

    public static void main(String[] args) {
        int num = 12;
        System.out.println(StringUtils.getNumber(num, 2));
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * 判断字符串是否支持转换为数字
     * 正则表达式
     *
     * @param string 字符串
     * @return
     */
    public static boolean isNumber(String string) {
        if (string == null)
            return false;
        Pattern pattern = Pattern.compile("^-?\\d+(\\.\\d+)?$");
        return pattern.matcher(string).matches();
    }

    public static double convertBaseLineValueToDouble(String baseValue) {
        if (StringUtils.isEmpty(baseValue)
                || valueDefault.equals(baseValue)
                || !StringUtils.isNumber(baseValue)) {
            return 0d;
        }
        return Double.parseDouble(baseValue);
    }
}
