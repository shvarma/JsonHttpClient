package com;



import java.io.*;
import java.util.*;

public class stringOprs {

    public static void main(String[] args) {
    	

        Scanner in = new Scanner(System.in);
        int N = in.nextInt();
        for (int i = 1; i < 11; i++)
        	System.out.println(N + " X " + i + " = " + (N*i));

    	
        
//        Scanner sc=new Scanner(System.in);
//        String A=sc.next();
//        String B=sc.next();
//       
//        System.out.println(A.length() + B.length());     
//        
//        List<String> strs = new ArrayList<String>();
//        strs.add(A);
//        strs.add(B);
//        Collections.sort(strs);
//        if (strs.get(0).equals(A))
//        	System.out.println("No");
//        else
//        	System.out.println("Yes");
//        
//        System.out.println(A.replaceFirst(String.valueOf(A.charAt(0)), String.valueOf(Character.toUpperCase(A.charAt(0)))) + " " 
//        +  B.replaceFirst(String.valueOf(B.charAt(0)), String.valueOf(Character.toUpperCase(B.charAt(0)))));
    }
}

