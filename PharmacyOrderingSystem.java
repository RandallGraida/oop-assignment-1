/*
* ==== Name ====
* Carlos Randall S. Graida
*
* ==== Course ====
* B.S. Information Technology
*
* ==== Year ====
* 2nd
*
* ==== Section ====
* BSIT 2-2
*
* ==== Name of File ====
* PharmacyOrderingSystem.java
*/


import java.util.Scanner;

public class PharmacyOrderingSystem {
    // Parallel Arrays for Data Model
    static String[] productCodes = {"P001", "P002", "P003", "P004", "P005", "P006", "P007", "P008"}; // (e.g., "P001" ...)
    static String[] productNames = {
        "Paracetamol 500mg (10s)", 
        "Amoxicillin 500mg (10s)", 
        "Ibuprofen 200mg (10s)", 
        "Vitamin C 500mg (30s)", 
        "Cetirizine 10mg (10s)", 
        "Loperamide 2mg (10s)", 
        "Mefenamic Acid 500mg (10s)", 
        "Multivitamins (30s)"
    };

    /*
    * The 'prices', 'stock', and 'cartQty' variables are index-aligned and
    * parallel to productNames and productCodes.
    */
    static double[] prices = {55.00, 120.00, 75.50, 150.00, 85.00, 45.00, 68.00, 210.00};
    static int[] stock = {50, 30, 40, 100, 20, 15, 25, 60};
    static int[] cartQty = new int[8];

    // Scanner object to capture and read user input from the console
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean exit = false;

        while (!exit) {
            displayMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    listProducts();
                    break;
                case "2":
                    searchProduct();
                    break;
                case "3":
                    addToCart();
                    break;
                case "4":
                    viewCart();
                    break;
                case "5":
                    checkout();
                    break;
                case "6":
                    reportsAndRestock();
                    break;
                case "0":
                    exit = true;
                    System.out.println("Exiting... Thank you for using Botika ni Sinta!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // --- A. Main Menu ---
    static void displayMainMenu() {
        System.out.println("\n========================================");
        System.out.println("      BOTIKA NI SINTA ORDERING TOOL     ");
        System.out.println("========================================");
        System.out.println("1) List products");
        System.out.println("2) Search product");
        System.out.println("3) Add to cart (by product code)");
        System.out.println("4) View cart");
        System.out.println("5) Checkout");
        System.out.println("6) Reports & Restock");
        System.out.println("0) Exit");
        System.out.print("Choice: ");
    }

    // --- C. Inventory Listing ---
    static void listProducts() {
        printInventoryHeader();
        for (int i = 0; i < productCodes.length; i++) {
            printProductRow(i);
        }
    }

    // --- D. Keyword Search ---
    static void searchProduct() {
        System.out.print("Enter keyword: ");
        String keyword = scanner.nextLine().toLowerCase();
        boolean found = false;

        printInventoryHeader();
        for (int i = 0; i < productNames.length; i++) {
            if (productNames[i].toLowerCase().contains(keyword)) {
                printProductRow(i);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No matches.");
        }
    }

    // --- E. Add to Cart ---
    static void addToCart() {
        System.out.print("Enter product code: ");
        String code = scanner.nextLine().trim();
        int index = indexOfCode(code);

        if (index == -1) {
            System.out.println("Error: Product code not found.");
            return;
        }

        int available = stock[index] - cartQty[index];
        if (available <= 0) {
            System.out.println("Error: Item out of stock.");
            return;
        }

        System.out.println("Product: " + productNames[index] + " | Available: " + available);
        int qty = readInt("Enter quantity to add: ", 1, available);

        cartQty[index] += qty;
        System.out.println("Successfully added " + qty + " units to cart.");
    }

    // --- F. View Cart & Subtotal ---
    static void viewCart() {
        double subtotal = computeSubtotal();
        if (subtotal == 0) {
            System.out.println("\nYour cart is empty.");
            return;
        }

        printCartHeader();
        for (int i = 0; i < productCodes.length; i++) {
            if (cartQty[i] > 0) {
                printCartRow(i);
            }
        }
        System.out.println("----------------------------------------------------------------------");
        System.out.printf("SUBTOTAL: %49.2f\n", subtotal);
    }

    // --- G. Checkout & Receipt ---
    static void checkout() {
        double subtotal = computeSubtotal();
        if (subtotal == 0) {
            System.out.println("\nYour cart is empty. Nothing to checkout.");
            return;
        }

        viewCart();
        System.out.print("\nEnter discount code (SENIOR20, PWD15, or NONE): ");
        String dCode = scanner.nextLine().trim().toUpperCase();
        double discountRate = 0;

        if (dCode.equals("SENIOR20")) {
            discountRate = 0.20;
        } else if (dCode.equals("PWD15")) {
            discountRate = 0.15;
        } else if (!dCode.equals("NONE") && !dCode.isEmpty()) {
            System.out.println("Note: Invalid discount code applied (0%).");
        }

        double discountAmount = subtotal * discountRate;
        double total = subtotal - discountAmount;

        System.out.println("\n----------- FINAL RECEIPT -----------");
        viewCart();
        System.out.printf("Discount (%d%%): %44.2f\n", (int)(discountRate*100), -discountAmount);
        System.out.println("----------------------------------------------------------------------");
        System.out.printf("TOTAL AMOUNT: %45.2f\n", total);

        System.out.print("\nProceed with purchase? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();

        if (confirm.equals("Y")) {
            for (int i = 0; i < productCodes.length; i++) {
                stock[i] -= cartQty[i];
                cartQty[i] = 0;
            }
            System.out.println("Purchase successful! Stock updated.");
        } else {
            System.out.println("Checkout cancelled.");
        }
    }

    // --- H. Reports & Restock ---
    static void reportsAndRestock() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Reports & Restock ---");
            System.out.println("1) Low stock report (<5)");
            System.out.println("2) Restock item");
            System.out.println("0) Back");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                printInventoryHeader();
                boolean found = false;
                for (int i = 0; i < stock.length; i++) {
                    if (stock[i] < 5) {
                        printProductRow(i);
                        found = true;
                    }
                }
                if (!found) System.out.println("No low stock items.");
            } else if (choice.equals("2")) {
                System.out.print("Enter product code to restock: ");
                String code = scanner.nextLine().trim();
                int index = indexOfCode(code);
                if (index != -1) {
                    int amount = readInt("Enter restock quantity: ", 1, 1000);
                    stock[index] += amount;
                    System.out.println("Stock updated.");
                } else {
                    System.out.println("Error: Code not found.");
                }
            } else if (choice.equals("0")) {
                back = true;
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    // --- Helper Methods ---
    static int indexOfCode(String code) {
        for (int i = 0; i < productCodes.length; i++) {
            if (productCodes[i].equalsIgnoreCase(code)) return i;
        }
        return -1;
    }

    static double computeSubtotal() {
        double total = 0;
        for (int i = 0; i < productCodes.length; i++) {
            total += cartQty[i] * prices[i];
        }
        return total;
    }

    static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(input);
                if (val >= min && val <= max) return val;
                System.out.println("Error: Please enter a value between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid input. Please enter a number.");
            }
        }
    }

    static void printInventoryHeader() {
        System.out.printf("\n%-4s | %-6s | %-25s | %-8s | %-6s\n", "Idx", "Code", "Name", "Price", "Stock");
        System.out.println("---------------------------------------------------------------");
    }

    static void printProductRow(int i) {
        System.out.printf("%-4d | %-6s | %-25s | %8.2f | %6d\n", i, productCodes[i], productNames[i], prices[i], stock[i]);
    }

    static void printCartHeader() {
        System.out.printf("\n%-6s | %-25s | %-8s | %-5s | %-10s\n", "Code", "Name", "Price", "Qty", "LineTotal");
        System.out.println("----------------------------------------------------------------------");
    }

    static void printCartRow(int i) {
        double lineTotal = prices[i] * cartQty[i];
        System.out.printf("%-6s | %-25s | %8.2f | %-5d | %10.2f\n", productCodes[i], productNames[i], prices[i], cartQty[i], lineTotal);
    }
}
