package com.sisindia.ai.mtrainer.android;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.droidcommons.base.BaseViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.inject.Inject;

public class AppTestViewModel extends BaseViewModel {

    public ObservableField<String> testText = new ObservableField<>();

    @Inject
    public AppTestViewModel(@NonNull Application application) {
        super(application);
    }

    public void setTestText(String testText) {
        this.testText.set(testText);
    }

    // 0 1 1 2 3 5 8 13
    static int[] fibSequence;

    public static void main(String[] args) {
//        System.out.println("Sum 8 -> " + fact(4));
//        System.out.println(removeDuplicate("aaaaaaaaaabbbbbbbbbbbbbcccccccccccccdddddddddddddfffffffffffffffffeeeeeeeeeeeeeeeeeeeoooooooooooooooopppppppppppppppqqqqqqqqqqq"));
        // System.out.println(removeSpace("Bh as kar   Singh  Man  hju jji oo  "));
        // System.out.println(reverseString("zyxvu"));
        //System.out.println(isPalindrome("aba"));
        //Scanner in = new Scanner(System.in);
        //int n = Integer.parseInt(in.nextLine().trim());
        //fibSequence = new int[n];
        //fibSequence(n);
        //System.out.println(Arrays.toString(fibSequence));
        //binarySearch();
       // findDuplicate("Bhaskar aaaaa bbbbbb")
    }

    private static void binarySearch() {
        List<Integer> list = new ArrayList<>();
        list.add(5);
        list.add(90);
        list.add(6);
        list.add(1);
        list.add(10);
        Collections.sort(list);
        System.out.print("Sorted -> " + list);
        search(10, list);
    }

    private static void search(int key, List<Integer> list) {
        int index = Collections.binarySearch(list, key);
        System.out.print("Index : " + index);
    }

/*    private static int fib(int n) {
        if(n <= 1)
            return 0;
        else if(n == 2)
            return 1;
        else
            return fib(n -1) + fib(n-2);
    }

    private static void fibSequence(int n) {
        if(n==1)
            System.out.print(0);
        else if(n ==2)
            System.out.print("0 1");
        else {
            int first = 0;
            int second = 1;
            int tmp = 0;
            System.out.print("0 1");
            for (int i = 2; i < n; i++) {
                tmp = second;
                second = first + second;
                first = tmp;
                System.out.print(" " + second);
            }
        }
    }

    private static int wordCount(String src) {
        StringTokenizer st = new StringTokenizer(src);
        return st.countTokens();
    }

    private static int wordCount1(String src) {
        int count = 0;
        src = src.trim();
        if(!src.isEmpty()) {
            count++;
            char charLastSeen = src.charAt(0);
            if(src.length() > 1)
                for (int i = 1; i < src.length(); i++) {
                    if (charLastSeen == ' ') {
                        if (src.charAt(i) != ' ')
                            count++;
                        charLastSeen = src.charAt(i);
                    }
                }
        }
        return count;
    }

    private static void binarySearch() {
        List<Integer> list = new ArrayList<>();
        list.add(5);
        list.add(90);
        list.add(6);
        list.add(1);
        list.add(10);
        Collections.sort(list);
        System.out.print("Sorted -> " + list);
        search(10, list);
    }

    private static void search(int key, List<Integer> list) {
        int index = Collections.binarySearch(list, key);
        System.out.print("Index : " + index);
    }

    private static int fact(int n) {
        if(n == 0)
            return 1;
        return n*fact(n-1);
    }

    private static String removeSpace(String src) {
        String[] items = src.split(" ");
        StringBuilder sb = new StringBuilder();
        for(String s : items) {
            sb.append(s.trim());
        }
        return sb.toString();
    }

    private static String removeDuplicate(String src) {
        HashSet<Character> hashSet = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<src.length(); i++) {
            Character ch = src.charAt(i);
            if(hashSet.add(ch))
                sb.append(ch);
        }
        return sb.toString();
    }

    private static String reverseString(String src) {
        StringBuilder sb = new StringBuilder(src.length());
        for (int i = (src.length() -1); i>=0; i--)
            sb.append(src.charAt(i));
        return sb.toString();
    }

    private static boolean isPalindrome(String src) {
        if(src == null)
            return false;
        if(src.length() < 2)
            return true;
        boolean result = true;
        for(int i=0,j=src.length()-1; i<j; i++,j--) {
            if(src.charAt(i) != src.charAt(j)) {
                result = false;
                break;
            }
        }
        return result;
    }

    private static void findDuplicate(String src) {
        HashMap<Character, Integer> map = new HashMap<>(src.length());
        for(int i=0; i<src.length();i++) {
            char ch = src.charAt(i);
            if(map.containsKey(ch)) {
                int value =  map.get(ch) + 1;
                map.put(ch, value);
            } else {
                map.put(ch, 1);
            }
        }
        for(Map.Entry<Character, Integer> entry : map.entrySet())
            if(entry.getValue() > 1)
                System.out.println(entry.getKey() + "Value : " + entry.getValue());

            List<Map.Entry<Character, Integer>> mapList = new ArrayList<>(map.entrySet());
            Collections.sort(mapList, (first, second) -> first.getValue() - second.getValue());
            System.out.println("Max : " + mapList.get(map.size()-1).getKey());
    }*/

}
