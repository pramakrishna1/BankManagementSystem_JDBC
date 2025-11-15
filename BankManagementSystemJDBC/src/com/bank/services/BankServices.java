package com.bank.services;

import com.bank.model.BankDetails;

import java.sql.*;

public class BankServices {
    private final String updateQuery = "update customer set amount = ? where accountNumber = ?";
    public Connection getConnection() throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con ;
        try {
            String username = System.getenv("USER");
            String password = System.getenv("PASSWORD");
            String url = System.getenv("URI");
            con = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return con;
    }

    public String showAccounts(){
        StringBuilder result = new StringBuilder();
        try(Connection con = getConnection()) {
            String selectQuery = "select * from customer";
            PreparedStatement ps = con.prepareStatement(selectQuery);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int id = rs.getInt("id");
                String accountNumber = rs.getString("accountNumber");
                String name = rs.getString("name");
                String phoneNo = rs.getString("phoneNumber");
                long amount = rs.getLong("amount");
                String address = rs.getString("address");
                result.append("[id: %d ,AccountNumber: %s ,Name: %s ,Phone Number: %s ,Amount: Rs.%d/- ,Address: %s ]\n".formatted(id,accountNumber,name, phoneNo, amount, address));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.printf("error while fetching data into DB %s%n", e.getMessage());
        }
        return result.toString();
    }

    public BankDetails getAccountDetails(String accountNo){
        BankDetails bankDetails = new BankDetails();
        try(Connection con = getConnection()) {
            String selectQuery = "select * from customer where accountNumber = ?";
            PreparedStatement ps = con.prepareStatement(selectQuery);
            ps.setString(1, accountNo);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                bankDetails.setId(rs.getInt("id"));
                bankDetails.setAccNo(rs.getString("accountNumber"));
                bankDetails.setName(rs.getString("name"));
                bankDetails.setPhoneNumber(rs.getString("phoneNumber"));
                bankDetails.setAmount(rs.getLong("amount"));
                bankDetails.setAddress(rs.getString("address"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.printf("error while fetching data into DB %s%n", e.getMessage());
        }
        return bankDetails;
    }

    private String createAccountNumber(){
        StringBuilder accountNo = new StringBuilder();
        for(int i = 0; i <10; i++) {
            int randomNumber = (int) (Math.random() * 10);
            accountNo.append(randomNumber);
        }
        return accountNo.toString();
    }
    public String createAccount(BankDetails details)  {
        String result = "";
        String phoneNumber = details.getPhoneNumber();
        if(phoneNumber.isBlank() && !(phoneNumber.matches("\\d{10}"))){
           result = "Please give valid Phone number";
        }else {
            String name = details.getName();
            String address = details.getAddress();
            if (!(name.isBlank() &&  address.isBlank())) {
                String accountNumber = createAccountNumber();
                try(Connection con = getConnection()) {
                    String insertQuery = "insert into customer(accountNumber,name,phoneNumber,amount,address) values(?,?,?,?,?)";
                    PreparedStatement ps = con.prepareStatement(insertQuery);
                    ps.setString(1,accountNumber);
                    ps.setString(2,name);
                    ps.setString(3, phoneNumber);
                    ps.setLong(4, details.getAmount());
                    ps.setString(5, address);
                    int i = ps.executeUpdate();
                    if(i == 1){
                    result = "Account created successfully Account Number: %s".formatted(accountNumber);
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    System.out.printf("error while inserting data into DB %s%n", e.getMessage());
                }

            }else{
                result = "Please give Name and Address";
            }
        }
        return result;
    }

    public long checkBalance(String accountNumber){
        long result = 0;
        try(Connection con = getConnection()) {
            String insertQuery = "select amount from customer where accountNumber = ?";
            PreparedStatement ps = con.prepareStatement(insertQuery);
            ps.setString(1,accountNumber);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                result = rs.getLong("amount");
            }else{
                result = -1;
            }

        } catch (SQLException | ClassNotFoundException e) {
            System.out.printf("error while fetching amount into DB %s%n", e.getMessage());
        }
        return result;
    }

    public String withdraw(String accountNumber, long amount){
            try (Connection con = getConnection()) {
                long accountAmount = checkBalance(accountNumber);
                if(accountAmount >=0) {
                    if(accountAmount > amount) {
                        PreparedStatement ps = con.prepareStatement(updateQuery);
                        long updatedAmount = accountAmount - amount;
                        ps.setLong(1, updatedAmount);
                        ps.setString(2, accountNumber);
                        int result = ps.executeUpdate();
                        if (result == 1) {
                            return "Withdrawn successfully, your current balance Rs.%d/-".formatted(updatedAmount);
                        }
                    }else{
                        return "insufficient balance";
                    }
                }else {
                    return "pls give correct account number";
                }
            } catch (SQLException | ClassNotFoundException e) {
                System.out.printf("error while fetching amount into DB %s%n", e.getMessage());
            }

        return "";
    }

    public String credit(String accountNumber, long amount){
        try (Connection con = getConnection()) {
            long accountAmount = checkBalance(accountNumber);
            if(accountAmount >=0) {
                    PreparedStatement ps = con.prepareStatement(updateQuery);
                    long updatedAmount = accountAmount + amount;
                    ps.setLong(1, updatedAmount);
                    ps.setString(2, accountNumber);
                    int result = ps.executeUpdate();
                    if (result == 1) {
                        return "Credited successfully, your current balance Rs.%d/-".formatted(updatedAmount);
                    }
            }else {
                return "pls give correct account number";
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.printf("error while fetching amount into DB %s%n", e.getMessage());
        }

        return "";
    }

    public String transferAmount(String senderAccountNo, String receiverAccountNo, long amount){
        String withdrawResult = withdraw(senderAccountNo, amount);
        if(withdrawResult.contains("successfully")){
            String creditResult = credit(receiverAccountNo, amount);
            if(creditResult.contains("successfully")){
                return "amount transferred successfully";
            }else{
                credit(senderAccountNo, amount);
                return creditResult +" for Receiver Account";
            }
        }else{
            return withdrawResult + " for Sender Account";
        }
    }

    public int updateName(String accountNo, String updatedName){
        BankDetails dbBankDetails = getAccountDetails(accountNo);
        if(dbBankDetails.getAccNo() != null){
            if(updatedName.matches("[A-Za-z]+")) {
                try (Connection con = getConnection()) {
                    String nameUpdateQuery = "update customer set name = ? where accountNumber = ?";
                    PreparedStatement ps = con.prepareStatement(nameUpdateQuery);
                    ps.setString(1, updatedName);
                    ps.setString(2, accountNo);
                    return ps.executeUpdate();
                } catch (SQLException | ClassNotFoundException e) {
                    System.out.printf("error while updating name into DB %s%n", e.getMessage());
                }
            }else{
                return 0;
            }
        }
        return -1;
    }

    public int updatePhoneNo(String accountNo, String updatedPhoneNo){
        BankDetails dbBankDetails = getAccountDetails(accountNo);
        if(dbBankDetails.getAccNo() != null){
            if(updatedPhoneNo.matches("\\d{10}")) {
                try (Connection con = getConnection()) {
                    String phoneUpdateQuery = "update customer set phoneNumber = ? where accountNumber = ?";
                    PreparedStatement ps = con.prepareStatement(phoneUpdateQuery);
                    ps.setString(1, updatedPhoneNo);
                    ps.setString(2, accountNo);
                    return ps.executeUpdate();
                } catch (SQLException | ClassNotFoundException e) {
                    System.out.printf("error while updating phone number into DB %s%n", e.getMessage());
                }
            }else{
                return 0;
            }
        }
        return -1;
    }

    public int updateAddress(String accountNo, String updatedAddress){
        BankDetails dbBankDetails = getAccountDetails(accountNo);
        if(dbBankDetails.getAccNo() != null){
            try (Connection con = getConnection()) {
                String addressUpdateQuery = "update customer set address = ? where accountNumber = ?";
                PreparedStatement ps = con.prepareStatement(addressUpdateQuery);
                ps.setString(1, updatedAddress);
                ps.setString(2, accountNo);
                return ps.executeUpdate();
            } catch (SQLException | ClassNotFoundException e) {
                System.out.printf("error while updating address into DB %s%n", e.getMessage());
            }
        }
        return -1;
    }


}
