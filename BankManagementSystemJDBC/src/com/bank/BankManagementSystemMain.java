package com.bank;

import com.bank.model.BankDetails;
import com.bank.services.BankServices;

import java.util.Scanner;

public class BankManagementSystemMain {

    public static void main(String[] args) {
        BankServices bankService = new BankServices();
        System.out.println("Welcome to Bank");
        Scanner sc = new Scanner(System.in);
        boolean trigger = true;
        while(trigger) {
            BankDetails details = new BankDetails();
            System.out.println("""
                    Enter 0 to show all accounts
                    Enter 1 to create new account
                    Enter 2 to get account details
                    Enter 3 to check balance
                    Enter 4 to withdraw amount
                    Enter 5 to credit amount
                    Enter 6 to transfer money
                    Enter 7 to update name
                    Enter 8 to update Phone number
                    Enter 9 to exit""");
            int choice = sc.nextInt();
            sc.nextLine();
            if (choice == 0) {
                System.out.println(bankService.showAccounts());
            } else if (choice == 1) {
                System.out.println("Enter name:");
                String name = sc.nextLine();
                details.setName(name);
                System.out.println("Enter phone number:");
                String phoneNo = sc.nextLine();
                details.setPhoneNumber(phoneNo);
                System.out.println("Enter address:");
                String address = sc.nextLine();
                details.setAddress(address);
                System.out.println("Enter amount:");
                long amount = sc.nextLong();
                details.setAmount(amount);
                String result = bankService.createAccount(details);
                System.out.println(result);
            }else if(choice == 2){
                System.out.println("Enter account number");
                String accNo = sc.nextLine();
                System.out.println(bankService.getAccountDetails(accNo));
            }else if(choice == 3){
                System.out.println("Enter account number");
                String accNo = sc.nextLine();
                long balance = bankService.checkBalance(accNo);
                if(balance >=0) {
                    System.out.printf("your current balance Rs.%d/- %n ", balance);
                }else{
                    System.out.println("Pls enter correct Account Number");
                }
            }else if(choice == 4){
                System.out.println("Enter account number");
                String accNo = sc.nextLine();
                System.out.println("Enter amount you want to withdraw");
                long amount = sc.nextLong();
                System.out.println(bankService.withdraw(accNo, amount));
            }else if(choice == 5){
                System.out.println("Enter account number");
                String accNo = sc.nextLine();
                System.out.println("Enter amount you want to credit");
                long amount = sc.nextLong();
                System.out.println(bankService.credit(accNo, amount));
            }else if(choice == 6){
                System.out.println("Enter sender account number");
                String senderAccNo = sc.nextLine();
                System.out.println("Enter receiver account number");
                String receiverAccNo = sc.nextLine();
                System.out.println("Enter the amount to transfer");
                long amount = sc.nextLong();
                System.out.println(bankService.transferAmount(senderAccNo, receiverAccNo, amount));
            }else if(choice == 7){
                System.out.println("Enter account number");
                String accNo = sc.nextLine();
                System.out.println("Enter new name");
                String name = sc.nextLine();
                int result = bankService.updateName(accNo,name);
                if(result > 0){
                    System.out.println("Name updated successfully");
                }else{
                    System.out.println("Pls enter correct account number");
                }
            }else if(choice == 8){
                System.out.println("Enter account number");
                String accNo = sc.nextLine();
                System.out.println("Enter new phone number");
                String phoneNo = sc.nextLine();
                int result = bankService.updatePhoneNo(accNo,phoneNo);
                if(result > 0){
                    System.out.println("Phone number updated successfully");
                }else if(result == 0){
                    System.out.println("pls enter valid phone number");
                } else{
                    System.out.println("Pls enter correct account number");
                }
            }
            else if(choice == 9){
                trigger = false;
            }
        }
        sc.close();
    }
}
