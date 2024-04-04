/**
 * @author <Duong Viet Hoang - S3962514>
 */

import View.MainMenu;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("COSC2440 - Further Programming Assignment 1");
        System.out.println("Name: Duong Viet Hoang");
        System.out.println("Student ID: s3962514");
        System.out.println("Instructor: Mr. Minh Vu Thanh & Mr. Tuan Phong Ngo");
        System.out.print("\n");
        System.out.println("\033[1m====================================================================\033[0m");
        System.out.println("\033[1m========= WELCOME TO THE INSURANCE CLAIM MANAGEMENT SYSTEM =========\033[0m");
        System.out.println("\033[1m====================================================================\033[0m");
        MainMenu mainMenu = new MainMenu();
        mainMenu.view();
    }

}

